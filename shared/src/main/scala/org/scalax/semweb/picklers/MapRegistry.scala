package org.scalax.semweb.picklers

import org.scalajs.spickling._
import scala.collection.immutable._


/**
 * There are some problems with pickling maps and sets in an ordinary way, that is why we have to make our own pickle registry
 */
trait MapRegistry extends PicklerRegistry {


  override def pickle[P](data: Any)(implicit builder: PBuilder[P], registry: PicklerRegistry = this): P = data match  {
    case null=>  builder.makeNull()


    case obj:Map[_,_]=>
      val array =  obj.map{case (key,value)=> builder.makeObject("key"->registry.pickle(key),"value"->registry.pickle(value))     }
      builder.makeObject("map"->builder.makeArray(array.toSeq:_*)  )

    case obj:Set[_]=>    builder.makeObject("set"->builder.makeArray(obj.map(v=>registry.pickle(v)).toSeq: _*)  )

    case obj:Vector[_]=>    builder.makeObject("vector"->builder.makeArray(obj.map(v=>registry.pickle(v)).toVector: _*)  )

    //    case seq: Seq[_] =>    builder.makeObject("seq" -> builder.makeArray(     seq.map(v=>registry.pickle(v)): _*))


    case (one,two,three,four)=>
      builder.makeObject {
        "tuple4"-> builder.makeObject("_1" -> registry.pickle(one), "_2" -> registry.pickle(two),"_3" -> registry.pickle(three),"_4" -> registry.pickle(four))
      }

    case (one,two,three)=>
      builder.makeObject {
        "tuple3"-> builder.makeObject("_1" -> registry.pickle(one), "_2" -> registry.pickle(two),"_3" -> registry.pickle(three))
      }

    case (key,value)=>
      builder.makeObject {
        "tuple2"-> builder.makeObject("_1" -> registry.pickle(key), "_2" -> registry.pickle(value))
      }

    case Some(value)=>
      builder.makeObject {
        "some"-> builder.makeObject("value" -> registry.pickle(value))
      }

    case other=>

      PicklerRegistry.pickle(other)(builder,registry)

  }

  protected def unpickleMap[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,Map[_,_]] =  if (reader.isNull(pickle))  null else
  {
    val mp = reader.readObjectField(pickle, "map")
    if (reader.isNull(mp) || reader.isUndefined(mp)) Left(pickle)
    else {
      val l = reader.readArrayLength(mp)
      Right((0 until l).map { i =>
        val el = reader.readArrayElem(mp, i)
        val key = registry.unpickle(reader.readObjectField(el, "key"))
        val value = registry.unpickle(reader.readObjectField(el, "value"))
        key -> value
      }.toMap)
    }
  }

  protected def unpickleSet[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,Set[_]] = {
    val st = reader.readObjectField(pickle, "set")
    if (reader.isNull(st) || reader.isUndefined(st)) Left(pickle)
    else {
      val l = reader.readArrayLength(st)
      Right((0 until l).map { i =>  registry.unpickle(reader.readArrayElem(st, i))   }.toSet)
    }
  }

  protected def unpickleSeq[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,Seq[_]] = {
    val seqData = reader.readObjectField(pickle, "seq")
    if (reader.isUndefined(seqData)) Left(pickle)
    else Right( (0 until reader.readArrayLength(seqData)).map( i => registry.unpickle(reader.readArrayElem(seqData, i))).toSeq )
  }

  protected def unpickleVector[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,Vector[_]] = {
    val seqData = reader.readObjectField(pickle, "vector")
    if (reader.isUndefined(seqData)) Left(pickle)
    else Right( (0 until reader.readArrayLength(seqData)).map( i => registry.unpickle(reader.readArrayElem(seqData, i))).toVector )
  }



  protected  def unpickleTuple2[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,(_,_)] ={
    val tp = reader.readObjectField(pickle, "tuple2")
    if(reader.isNull(tp) || reader.isUndefined(tp)) Left(pickle) else {
      ( registry.unpickle(reader.readObjectField(tp,"_1")) , registry.unpickle(reader.readObjectField(tp,"_2"))  )
      match {
        case (one,two) => Right((one,two))
        case other=>Left(pickle)
      }
    }
  }

  protected  def unpickleTuple3[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,(_,_,_)] ={
    val tp = reader.readObjectField(pickle, "tuple3")
    if(reader.isNull(tp) || reader.isUndefined(tp)) Left(pickle) else {
      ( registry.unpickle(reader.readObjectField(tp,"_1")) , registry.unpickle(reader.readObjectField(tp,"_2")),registry.unpickle(reader.readObjectField(tp,"_3"))  )
      match {
        case (one,two,three) => Right((one,two,three))
        case other=>Left(pickle)
      }
    }
  }

  protected  def unpickleTuple4[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,(_,_,_,_)] ={
    val tp = reader.readObjectField(pickle, "tuple4")
    if(reader.isNull(tp) || reader.isUndefined(tp)) Left(pickle) else {
      ( registry.unpickle(reader.readObjectField(tp,"_1")) , registry.unpickle(reader.readObjectField(tp,"_2")),registry.unpickle(reader.readObjectField(tp,"_3")),registry.unpickle(reader.readObjectField(tp,"_4"))  )
      match {
        case (one,two,three,four) => Right((one,two,three,four))
        case other=>Left(pickle)
      }
    }
  }

  protected  def unpickleSome[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,Option[_]] ={
    val tp = reader.readObjectField(pickle, "some")
    if(reader.isNull(tp) || reader.isUndefined(tp)) Left(pickle) else {
      Right(Some(registry.unpickle(reader.readObjectField(tp,"value"))))
    }

  }

  protected  def unpickleNull[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,Null] ={
    if(reader.isNull(pickle)) Right(null) else Left(pickle)
  }


  override def unpickle[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this): Any =
  {
    unpickleNull(pickle).right.getOrElse{
      this.unpickleMap(pickle).right.getOrElse{
        this.unpickleSet(pickle).right.getOrElse{
          this.unpickleVector(pickle).right.getOrElse{
            this.unpickleSeq(pickle).right.getOrElse{
              this.unpickleSeq(pickle).right.getOrElse{
                this.unpickleSome(pickle).right.getOrElse{
                  this.unpickleTuple2(pickle).right.getOrElse{
                    this.unpickleTuple3(pickle).right.getOrElse {
                      this.unpickleTuple4(pickle).right.getOrElse {
                        PicklerRegistry.unpickle(pickle)(reader, registry)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}