/*
 * Created on 2010/10/06
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

// treat Java collections like Scala collections
// until we really understand the object system, we iterate in a similar
// way as the reference
// implementation (by ascending key order), so we need sorted maps.
// unfortunately, the Scala TreeMap is immutable, which might be fine for
// a lot of operations, but in an IF story, we might have frequent updates,
// which are potentially slow with an immutable structure.
// Later, the order will not matter, so we can replace this with a
// Scala HashMap
import scala.collection.JavaConversions._
import java.util.TreeMap
import org.zmpp.base._

trait TadsObject {
  def id: TadsObjectId
  def isTransient: Boolean
  def isInstanceOf(objectId: Int): Boolean

  def findProperty(propertyId: Int): Property
  def valueAtIndex(index: Int): TadsValue
  def setValueAtIndex(index: Int,
                      newValue: TadsValue): TadsValue
}

abstract class AbstractTadsObject(val id: TadsObjectId) extends TadsObject {
  var isTransient = false
  def isInstanceOf(objectId: Int): Boolean = false
  def findProperty(propertyId: Int): Property = {
    throw new UnsupportedOperationException("findProperty() not implemented: " +
                                          getClass.getName)
  }
  def valueAtIndex(index: Int): TadsValue = {
    throw new UnsupportedOperationException("valueAtIndex() not implemented")
  }
  def setValueAtIndex(index: Int, newValue: TadsValue): TadsValue = {
    throw new UnsupportedOperationException("setValueAtIndex() not implemented")
  }
}

class Property(val id: Int, val valueType: Int, val value: Int,
               val definingObject: Int) {
  override def toString = {
    "Property (id = %d type: %d value: %d def. obj: %d)".format(
      id, valueType, value, definingObject)
  }
}

// The abstract interface to a TADS3 meta class.
trait MetaClass {
  def name: String
  def createFromStack(id: TadsObjectId, vmState: TadsVMState,
                      argc: Int): TadsObject
  def createFromImage(objectManager: ObjectManager,
                      imageMem: Memory, objectId: Int, objDataAddr: Int,
                      numBytes: Int, isTransient: Boolean): TadsObject
  def supportsVersion(version: String): Boolean
}

// Define all system meta classes that we know so far
// These are the current meta classes that are provided by the reference
// implementation. Apparently, the technical manual only mentions four of
// them, which makes it hard to implement a VM without looking at the
// QTads' source code.
// Instead of complaining, I'll just see how they are implemented in QTads and
// document it by myself
abstract class SystemMetaClass extends MetaClass {
  def createFromStack(id: TadsObjectId, vmState: TadsVMState,
                      argc: Int): TadsObject = {
    throw new UnsupportedOperationException("createFromStack not yet " +
                                            "supported in " +
                                            "metaclass '%s'".format(name))
  }
  def createFromImage(objectManager: ObjectManager,
                      imageMem: Memory, objectId: Int, objDataAddr: Int,
                      numBytes: Int, isTransient: Boolean): TadsObject = {
    throw new UnsupportedOperationException("createFromImage not yet " +
                                            "supported in " +
                                            "metaclass '%s'".format(name))
  }
  def supportsVersion(version: String) = true
}
class StringMetaClass extends SystemMetaClass {
  def name = "string"
}
class ListMetaClass   extends SystemMetaClass {
  def name = "list"
}

class IntClassModMetaClass extends SystemMetaClass {
  def name = "int-class-mod"
}
class RootObjectMetaClass extends SystemMetaClass {
  def name = "root-object"
}
class CollectionMetaClass extends SystemMetaClass {
  def name = "collection"
}
class IteratorMetaClass extends SystemMetaClass {
  def name = "iterator"
}
class IndexedIteratorMetaClass extends SystemMetaClass {
  def name = "indexed-iterator"
}
class CharacterSetMetaClass extends SystemMetaClass {
  def name = "character-set"
}
class ByteArrayMetaClass extends SystemMetaClass {
  def name = "bytearray"
}
class WeakRefLookupTableMetaClass extends SystemMetaClass {
  def name = "weakreflookuptable"
}
class LookupTableIteratorMetaClass extends SystemMetaClass {
  def name = "lookuptable-iterator"
}
class FileMetaClass extends SystemMetaClass {
  def name = "file"
}

// Predefined symbols that the image defines. Can be accessed by the VM through
// the unique name
object PredefinedSymbols {
  val Constructor             = "Constructor"
  val Destructor              = "Destructor"
  val ExceptionMessage        = "exceptionMessage"
  val FileNotFoundException   = "File.FileNotFoundException"
  val FileCreationExcetpion   = "File.FileCreationException"
  val FileOpenException       = "File.FileOpenException"
  val FileIOException         = "File.FileIOException"
  val FileSyncException       = "File.FileSyncException"
  val FileClosedException     = "File.FileClosedException"
  val FileModeException       = "File.FileModeException"
  val FileSafetyException     = "File.FileSafetyException"
  val GpFirstTokenIndex       = "GrammarProd.firstTokenIndex"
  val GpLastTokenIndex        = "GrammarProd.lastTokenIndex"
  val GpTokenList             = "GrammarProd.tokenList"
  val GpTokenMatchList        = "GrammarProd.tokenMatchList"
  val GpGrammarAltInfo        = "GrammarProd.GrammarAltInfo"
  val GpGrammarAltTokInfo     = "GrammarProd.GrammarAltTokInfo"
  val IfcCalcHash             = "IfcComparator.calcHash"
  val IfcMatchValues          = "IfcComparator.matchValues"
  val LastProp                = "LastProp"
  val MainRestore             = "mainRestore"
  val ObjectCallProp          = "ObjectCallProp"
  val PropNotDefined          = "propNotDefined"
  val RuntimeError            = "RuntimeError"
  val T3StackInfo             = "T3StackInfo"
  val UnknownCharSetException = "CharacterSet.UnknownCharSetException"
}

object ObjectSystem {
  // map the unique meta class names to the system meta classes
  // when initializing the game, this map can be used to map the image
  // identifiers for metaclass dependencies to the actual meta classes that
  // the ZMPP TADS3 VM supports
  val MetaClasses = Map(
    "tads-object"          -> new GenericObjectMetaClass,
    "string"               -> new StringMetaClass,
    "list"                 -> new ListMetaClass,
    "vector"               -> new VectorMetaClass,
    "lookuptable"          -> new LookupTableMetaClass,
    "dictionary2"          -> new Dictionary2MetaClass,
    "grammar-production"   -> new GrammarProductionMetaClass,
    "anon-func-ptr"        -> new AnonFuncPtrMetaClass,
    "int-class-mod"        -> new IntClassModMetaClass,
    "root-object"          -> new RootObjectMetaClass,
    "intrinsic-class"      -> new IntrinsicClassMetaClass,
    "collection"           -> new CollectionMetaClass,
    "iterator"             -> new IteratorMetaClass,
    "indexed-iterator"     -> new IndexedIteratorMetaClass,
    "character-set"        -> new CharacterSetMetaClass,
    "bytearray"            -> new ByteArrayMetaClass,
    "regex-pattern"        -> new RegexPatternMetaClass,
    "weakreflookuptable"   -> new WeakRefLookupTableMetaClass,
    "lookuptable-iterator" -> new LookupTableIteratorMetaClass,
    "file"                 -> new FileMetaClass,
    "string-comparator"    -> new StringComparatorMetaClass,
    "bignumber"            -> new BigNumberMetaClass)
}

// The object manager handles instantiation and management of objects.
// It is an extension of the VM state, because it represents the current state
// of all objects in the system.
// This version of the object manager has to be
// 1. reset before loading a new file
// 2. during loading the image, add metaclass dependencies as they come in
// 3. then load the classes as they come in
class ObjectManager(vmState: TadsVMState) {
  private var _maxObjectId       = 0
  private val _objectCache       = new TreeMap[Int, TadsObject]
  private val _metaClassMap      = new TreeMap[Int, MetaClass]

  private def image : TadsImage = vmState.image

  def addMetaClassDependency(index: Int, nameString: String) {
    val name = nameString.split("/")(0)
    val version = if (nameString.split("/").length == 2) nameString.split("/")(1)
                      else "000000"
    _metaClassMap(index) = ObjectSystem.MetaClasses(name)
  }

  def addMetaClassPropertyId(index: Int, propertyId: Int) {
    // TODO: Add property ids to the specified meta class
    // (we should first find out what it means)
  }
  def addStaticObject(imageMem: Memory, objectId: Int, metaClassIndex: Int,
                      objAddr: Int, numBytes: Int, isTransient: Boolean) {
    val obj = _metaClassMap(metaClassIndex).createFromImage(this, imageMem,
                                                            objectId, objAddr,
                                                            numBytes, isTransient)
    _objectCache(objectId) = obj
  }

  def reset {
    _metaClassMap.clear
    _objectCache.clear
  }

  // create a unique object id
  // we assume that a VM runs single-threaded and we never reuse ids
  private def newId = {
    _maxObjectId += 1
    _maxObjectId
  }

  def createFromStack(argc: Int, metaClassId: Int) = {
    val id = new TadsObjectId(newId)
    val obj = _metaClassMap(metaClassId).createFromStack(id, vmState, argc)
    _objectCache(id.value) = obj
    //printf("CREATED OBJECT WITH ID: %d\n", id)
    id
  }

  def printMetaClasses {
    for (i <- _metaClassMap.keys) {
      printf("ID: %d NAME: %s\n", i, _metaClassMap(i).name)
    }
  }

  // **********************************************************************
  // **** Query Functions
  // **********************************************************************

  def objectWithId(id: Int) = {
    if (_objectCache.contains(id)) _objectCache(id)
    else {
      throw new ObjectNotFoundException
    }
  }

  // Enumeration of objects, this is the 
  def firstObject(getInstances: Boolean, getClasses: Boolean,
                  classId: Int = TadsObjectId.ObjectIdInvalid): TadsObject = {
    throw new UnsupportedOperationException("firstObject() not yet supported")
  }
  def nextObject(getInstances: Boolean, getClasses: Boolean,
                 previousObject: Int = TadsObjectId.ObjectIdInvalid,
                 classId: Int = TadsObjectId.ObjectIdInvalid): TadsObject = {
    throw new UnsupportedOperationException("previousObject() not yet supported")
  }
}
