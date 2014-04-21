package org.scalax.semweb.shex.parser

import scala.util.parsing.combinator._
import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec

trait StateParser extends Parsers {

  
def repState[T,S](s: S, 
                  p: S => Parser[(T,S)]
		  		 ): Parser[(List[T],S)] = 
	  rep1State(s,p) | success((List(),s))
  
// Curried repState
def repS[T,S](p: S => Parser[(T,S)])
             (s:S): Parser[(List[T],S)] = 
	  repState(s,p)

def rep1sepOptState[T,S](s : S, 
		  				p : S => Parser[(T,S)], 
		  				q : => Parser[Any]): Parser[(List[T],S)] =
    p(s) >> { s1 => repState(s1._2, arrowOptState(p,q)) ^^ {
       case (ls,s2) => (s1._1::ls.flatten,s2)
       } 
    }

def arrowOptState[T,S](p : S => Parser[(T,S)],q: Parser[Any])
		  			    (s : S) : Parser[(Option[T],S)] =
    q ~> opt(p(s)) ^^ { 
      case None => (None,s)
      case Some((t,s1)) => (Some(t),s1)
	} 

def rep1sepState[T,S](s : S, 
		  				p : S => Parser[(T,S)], 
		  				q : => Parser[Any]): Parser[(List[T],S)] =
    p(s) >> { s1 => repState(s1._2, arrowState(p,q)) ^^ {
       case (ls,s2) => (s1._1::ls,s2)} 
  }

def chainl1State[T,S]
		(p: S => Parser[(T,S)], 
    	 q: S => Parser[((T, T) => T,S)])
    	(s:S): Parser[(T,S)]
    = chainl1State(p, p, q)(s)

def chainl1State[T, U, S](
    first: S => Parser[(T,S)], 
    p: S => Parser[(U,S)], 
    q: S => Parser[((T, U) => T,S)])
    (s: S): Parser[(T,S)]
    = seqState(first, repS(seqState(q,p)))(s) ^^ 
      {
        // x's type annotation is needed to deal with changed type inference due to SI-5189
        case ((x ~ xs, s1)) => (xs.foldLeft(x:T) { case (a, f ~ b) => f(a,b)},s1)
      }
        

    
def optState[T,S](p:S => Parser[(T,S)])(s:S): Parser[(Option[T],S)] =
   ( p(s) ^^ ( x  => (Some(x._1),x._2))  
   | success((None,s)) 
   )

def seqState[T,U,S](p: S => Parser[(T,S)],
		  			q: S => Parser[(U,S)])
		  		   (s:S) : Parser[(T ~ U,S)] = {
    p(s) >> { s1 => q(s1._2) ^^ { case (u,s2) => (new ~(s1._1,u), s2)} }
}

def arrowState[T,S](p : S => Parser[(T,S)],
		  			q: Parser[Any])
		  		   (s : S) : Parser[(T,S)] =
	q ~> p(s)

def rep1State[T,S]
	   (s: S, p: S => Parser[(T,S)]): Parser[(List[T],S)] = 
    	rep1State(s, p, p)

def rep1State[T,S](s : S,
  			       first: S => Parser[(T,S)], 
  			       p0: S => Parser[(T,S)]
  			       ): 
          Parser[(List[T],S)] = 
   Parser { in =>
    lazy val p = p0 // lazy argument
    val elems : ListBuffer[T] = new ListBuffer[T]

    def continue(s: S)
    			(in: Input): ParseResult[(List[T],S)] = {
      val p0 = p    // avoid repeatedly re-evaluating by-name parser

      @tailrec 
      def applyp(s0:S)(in0: Input): 
    	  			ParseResult[(List[T],S)] = p0(s0)(in0) match 
    	  			{
        case Success(x, rest) => elems += x._1 ; applyp(x._2)(rest)
        case e @ Error(_, _)  => e  // still have to propagate error
        case _                => Success((elems.toList,s0), in0)
      }

      applyp(s)(in)
    }

    first(s)(in) match {
      case Success(x, rest) => elems += x._1 ; continue(x._2)(rest)
      case ns: NoSuccess    => ns
    }
  }	  	  

}