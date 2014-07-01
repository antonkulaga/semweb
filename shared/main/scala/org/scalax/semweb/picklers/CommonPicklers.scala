package org.scalax.semweb.picklers

import org.scalajs.spickling.PicklerRegistry._

import org.scalajs.spickling._
import scala.collection.immutable.Nil
import org.scalajs.spickling.PicklerRegistry
import java.util.Date

/**
 * Picklers for some common classes like list
 */
trait CommonPicklers {


  self:PicklerRegistry=>

  implicit object DatePickler extends Pickler[Date] {
    def pickle[P](value: Date)(implicit registry: PicklerRegistry,
                               builder: PBuilder[P]): P = {
      builder.makeObject {

        "date"-> builder.makeObject("time" ->   builder.makeNumber(value.getTime))
      }
    }
  }

  implicit object DateUnpickler extends Unpickler[Date] {
    def unpickle[P](pickle: P)(implicit registry: PicklerRegistry,
                               reader: PReader[P]): Date = {
      val tp = reader.readObjectField(pickle, "date")

      new Date(Math.round(reader.readNumber(reader.readObjectField(tp,"time"))))

    }
  }

  implicit object ConsPickler extends Pickler[::[_]] {
    def pickle[P](value: ::[_])(implicit registry: PicklerRegistry,
                                builder: PBuilder[P]): P = {
      builder.makeArray(value.map(v=>registry.pickle(v)): _*)
    }
  }

  implicit object ConsUnpickler extends Unpickler[::[_]] {
    def unpickle[P](pickle: P)(implicit registry: PicklerRegistry,
                               reader: PReader[P]): ::[_] = {
      val len = reader.readArrayLength(pickle)
      assert(len > 0)
      ((0 until len).toList map { index =>
        registry.unpickle(reader.readArrayElem(pickle, index))
      }).asInstanceOf[::[Any]]
    }
  }
  def registerCommon() = {
    //
    // Utils
    register(Nil)
    register[::[Any]]
    register(None)
    register[Date]

    //register[(_,_)]

  }




}
