/*
 * Created on 2010/11/07
 * Copyright (c) 2010-2011, Wei-ju Wu.
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

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}

class VectorTest extends JUnit4(VectorSpec)
object VectorSpecRunner extends ConsoleRunner(VectorSpec)

object VectorSpec extends Specification {
  var objectSystem : ObjectSystem = null
  var functionSetMapper : IntrinsicFunctionSetMapper = null
  var vmState : TadsVMState = null

  "Vector" should {
    doBefore {
      objectSystem = new ObjectSystem
      functionSetMapper = new IntrinsicFunctionSetMapper
      vmState = new TadsVMState(objectSystem, functionSetMapper)
    }
    "be created" in {
      val vector = new Vector(new T3ObjectId(1), vmState, false)
      vector.metaClass.name must_== "vector"
      vector.size must_== 0
    }
    "append an element" in {
      val vector = new Vector(new T3ObjectId(1), vmState, false)
      val value = new T3Integer(4711)
      vector.append(value)
      vector.size must_== 1
      vector.valueAtIndex(new T3Integer(1)) must_== value
    }
    "insert an element at position 1" in {
      val vector = new Vector(new T3ObjectId(1), vmState, false)
      val value0 = new T3Integer(0)
      val value1 = new T3Integer(1)
      vector.append(value0)
      vector.insertAt(1, value1)
      vector.size must_== 2
      vector.valueAtIndex(new T3Integer(1)) must_== value1
      vector.valueAtIndex(new T3Integer(2)) must_== value0
    }
    "insert an element at the end" in {
      val vector = new Vector(new T3ObjectId(1), vmState, false)
      val value0 = new T3Integer(0)
      val value1 = new T3Integer(1)
      vector.append(value0)
      vector.insertAt(2, value1)
      vector.size must_== 2
      vector.valueAtIndex(new T3Integer(1)) must_== value0
      vector.valueAtIndex(new T3Integer(2)) must_== value1
    }
    "determine indexOf()" in {
      val vector = new Vector(new T3ObjectId(1), vmState, false)
      val value0 = new T3Integer(0)
      val value1 = new T3Integer(1)
      val value2 = new T3Integer(2)
      val value3 = new T3Integer(3)
      val value4 = new T3Integer(4)
      val value5 = new T3Integer(5)
      val value6 = new T3Integer(6)
      vector.append(value0)
      vector.append(value1)
      vector.append(value2)
      vector.append(value3)
      vector.append(value4)
      vector.append(value5)
      vector.indexOf(value0) must_== 1
      vector.indexOf(value5) must_== 6
      vector.indexOf(value2) must_== 3
      // not found
      vector.indexOf(value6) must_== 0
    }
  }
}
