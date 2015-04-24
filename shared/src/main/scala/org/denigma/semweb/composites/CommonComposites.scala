package org.denigma.semweb.composites

import prickle._

import scala.util.Try

/**
  implicit conversions for different types
  */

trait  CommonComposites{

  implicit lazy val config = PConfig.Default.copy(areSharedObjectsSupported=false)

  implicit def listPickler[T](implicit pickler: Pickler[T]):Pickler[List[T]] = new Pickler[List[T]] {
    def pickle[P](value: List[T], state: PickleState)(implicit config: PConfig[P]): P = {
      Pickler.resolvingSharingCollection[P](value, value.map(e => Pickle(e, state)), state, config)
    }
  }

  implicit def listUnpickler[T](implicit unpickler: Unpickler[T]): Unpickler[List[T]] =  new Unpickler[List[T]] {
    def unpickle[P](pickle: P, state: collection.mutable.Map[String, Any])(implicit config: PConfig[P]): Try[List[T]] = {
      unpickleSeqish[T, List[T], P](x => x.toList, pickle, state)
    }
  }

  def unpickleSeqish[T, S, P](f: Seq[T] => S, pickle: P, state: collection.mutable.Map[String, Any])
                             (implicit config: PConfig[P],
                              u: Unpickler[T]): Try[S] = {

    import config._
    readObjectField(pickle, prefix + "ref").transform(
      (p: P) => {
        readString(p).flatMap(ref => Try(state(ref).asInstanceOf[S]))
      },
      _ => readObjectField(pickle, prefix + "elems").flatMap(p => {
        readArrayLength(p).flatMap(len => {
          val seq = (0 until len).map(index => u.unpickle(readArrayElem(p, index).get, state).get)
          val result = f(seq)
          Unpickler.resolvingSharing(result, pickle, state, config)
          Try(result)
        })
      }
      ))
  }

  //implicit lazy val nonePickler = CompositePickler[None.type ]


  //for missing common classes
}
