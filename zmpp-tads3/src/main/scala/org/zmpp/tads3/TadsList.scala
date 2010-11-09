/*
 * Created on 2010/10/22
 * Copyright (c) 2010, Wei-ju Wu.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of Wei-ju Wu nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.zmpp.tads3

import org.zmpp.base._
import scala.collection.JavaConversions._
import java.util.ArrayList

/*
 * Lists are stored in the image as
 * length n (ushort)
 * n * size(DATAHOLDER)
 * Very similar to Vector
 */
class TadsList(id: TadsObjectId, vmState: TadsVMState)
extends TadsCollection(id, vmState) {
  private val _container = new ArrayList[TadsValue]
  override def metaClass: MetaClass = objectSystem.listMetaClass
  override def toString = "List object"
  def size = _container.size
  def addElement(value: TadsValue) {
    _container.add(value)
  }
  override def valueAtIndex(index: Int): TadsValue = _container(index - 1)
  override def setValueAtIndex(index: Int, newValue: TadsValue): TadsObjectId = {
    val oldValue = _container(index - 1)
    _container(index - 1) = newValue
    id // return this object
  }
  def createIterator(argc: Int): TadsValue = {
    println("createIterator()")
    val iter = objectSystem.indexedIteratorMetaClass.createIterator(this)
    iter.id
  }
}

class ListMetaClass extends MetaClass {
  def name = "list"
  override def superMeta = objectSystem.metaClassForName("collection")

  val FunctionVector = Array(undef _, subset _, map _, len _,
                             sublist _, intersect _, indexOf _,
                             car _, cdr _, indexWhich _, forEach _,
                             valWhich _, lastIndexOf _, lastIndexWhich _,
                             lastValWhich _, countOf _, countWhich _,
                             getUnique _, appendUnique _, append _,
                             sort _, prepend _, insertAt _, removeElementAt _,
                             removeRange _, forEachAssoc _)

  def undef(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("undefined")
  }
  def subset(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("subset")
  }
  def map(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("map")
  }
  def len(obj: TadsObject, argc: Int): TadsValue = {
    new TadsInteger(obj.asInstanceOf[TadsList].size)
  }
  def sublist(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("sublist")
  }
  def intersect(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("intersect")
  }
  def indexOf(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("indexOf")
  }
  def car(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("car")
  }
  def cdr(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("cdr")
  }
  def indexWhich(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("indexWhich")
  }
  def forEach(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("forEach")
  }
  def valWhich(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("valWhich")
  }
  def lastIndexOf(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("lastIndexOf")
  }
  def lastIndexWhich(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("lastIndexWhich")
  }
  def lastValWhich(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("lastValWhich")
  }
  def countOf(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("countOf")
  }
  def countWhich(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("countWhich")
  }
  def getUnique(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("getUnique")
  }
  def appendUnique(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("appendUnique")
  }
  def append(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("append")
  }
  def sort(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("sort")
  }
  def prepend(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("prepend")
  }
  def insertAt(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("insertAt")
  }
  def removeElementAt(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("removeElementAt")
  }
  def removeRange(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("removeRange")
  }
  def forEachAssoc(obj: TadsObject, argc: Int): TadsValue = {
    throw new UnsupportedOperationException("forEachAssoc")
  }

  def createListConstant(id: TadsObjectId, offset: TadsListConstant) = {
    import TadsConstants._
    val poolOffset = offset.value
    val len = vmState.image.constantDataShortAt(poolOffset)
    printf("List offset = %s, len: %d\n", offset, len)
    val list = new TadsList(id, vmState)
    for (i <- 0 until len) {
      val valueAddr = poolOffset + 1 + SizeDataHolder * i
      val valueType = vmState.image.constantDataByteAt(valueAddr)
      val value = TypeIds.valueForType(valueType,
                                       vmState.image.constantDataIntAt(
                                         valueAddr + 1))
      list.addElement(TadsValue.create(valueType, value))
    }
    list
  }

  override def callMethodWithIndex(obj: TadsObject, index: Int,
                                   argc: Int): TadsValue = {
    FunctionVector(index)(obj, argc)
  }
}
