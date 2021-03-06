/**
 * Created on 2011/11/07
 * Copyright (c) 2010-2014, Wei-ju Wu.
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
package org.zmpp.iff

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.io._

@RunWith(classOf[JUnitRunner])
class QuetzalCompressionSpec extends FlatSpec with Matchers {

  "QuetzalCompression" should "compress an array with no changes" in {
    val originalBytes: Array[Byte] = Array(1, 2, 3, 4)
    val saveBytes: Array[Byte] = Array(1, 2, 3, 4)
    val compressed = QuetzalCompression.compressDiffBytes(originalBytes, saveBytes, 4)
    compressed.length should be (0)
  }
  it should "compress an array with one change at the end" in {
    val originalBytes = new Array[Byte](300)
    val saveBytes = new Array[Byte](300)
    for (i <- 0 until 300) originalBytes(i) = (i % 256).asInstanceOf[Byte]
    System.arraycopy(originalBytes, 0, saveBytes, 0, 300)
    saveBytes(299) = 0x28
    val compressed = QuetzalCompression.compressDiffBytes(originalBytes, saveBytes, 300)
    compressed.length should be (5)
    compressed(0) should be (0)
    (compressed(1) & 0xff) should be (255)
    compressed(2) should be (0)
    compressed(3) should be (42)
    compressed(4) should be (3)
  }

  it should "decompress an array with no changes encoded in zero-runs" in {
    val originalBytes: Array[Byte] = Array(1, 2, 3, 4)
    val targetBytes: Array[Byte] = new Array[Byte](4)
    val compressed: Array[Byte] = Array(0, 3)
    val result = QuetzalCompression.decompressDiffBytes(compressed, originalBytes,
                                                        targetBytes, 4)
    result.length should be (4)
    result(0) should be (1)
    result(1) should be (2)
    result(2) should be (3)
    result(3) should be (4)
  }
  it should "decompress an array with no changes encoded in an empty array" in {
    val originalBytes: Array[Byte] = Array(1, 2, 3, 4)
    val targetBytes: Array[Byte] = new Array[Byte](4)
    val compressed: Array[Byte] = Array()
    val result = QuetzalCompression.decompressDiffBytes(compressed, originalBytes,
                                                        targetBytes, 4)
    result.length should be (4)
    result(0) should be (1)
    result(1) should be (2)
    result(2) should be (3)
    result(3) should be (4)
  }

  it should "decompress an array with one change at the end" in {
    val originalBytes = new Array[Byte](300)
    val targetBytes = new Array[Byte](300)
    for (i <- 0 until 300) originalBytes(i) = (i % 256).asInstanceOf[Byte]
    val compressed: Array[Byte] = Array(0, 255.asInstanceOf[Byte], 0, 42, 3)
    val result = QuetzalCompression.decompressDiffBytes(compressed, originalBytes,
                                                        targetBytes, 300)
    result(299) should be (0x28)
  }

  it should "compress real data" in {
    val original = readBytes("originalmem.txt")
    val current = readBytes("currentmem.txt")
    val expected = Array(
      0, 0, 32, 0, 47, 1, 0, 255, 0, 255, 0, 255, 0, 43, 64, 0, 255, 0, 30,
      64, 0, 117, 32, 0, 61, 32, 0, 2, 200, 0, 190, 64, 0, 7, 64, 0, 209, 68,
      0, 26, 200, 0, 255, 0, 255, 0, 232, 121, 114, 0, 255, 0, 255, 0, 39, 18,
      52, 0, 104, 19, 164, 0, 255, 0, 255, 0, 220, 17, 251, 0, 152, 21, 234,
      0, 141, 18, 138, 0, 255, 0, 255, 0, 215, 18, 55, 0, 11, 26, 65, 0, 255,
      0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255,
      0, 255, 0, 202, 21, 205, 0, 216, 30, 18, 0, 123, 68, 0, 2, 2, 0, 58, 4,
      0, 23, 34, 163, 0, 93, 255, 255, 0, 6, 4, 0, 51, 52, 73, 0, 16, 11, 84,
      139, 0, 46, 1, 0, 60, 200, 0, 28, 23, 0, 0, 1, 0, 46, 1, 0, 75, 119, 148,
      0, 0, 4, 0, 6, 1, 84, 139, 4, 1, 0, 236, 1, 84, 139, 4, 1, 0, 255, 0, 222,
      115, 97, 118, 101, 0, 237, 115, 97, 118, 101, 0, 118, 1, 0, 0, 4, 0, 0, 5,
      0, 0, 185, 39, 26, 0, 16, 129, 39, 26, 0, 15, 84, 139, 4, 1)
    val compressed = QuetzalCompression.compressDiffBytes(original, current, original.length)
    compressed.length should be (expected.length)
    for (i <- 0 until compressed.length) {
      (compressed(i) & 0xff) should be (expected(i))
    }
  }

  it should "decompress real data" in {
    val original = readBytes("originalmem.txt")
    val current = readBytes("currentmem.txt")
    val target = new Array[Byte](original.length)
    val compressed = QuetzalCompression.compressDiffBytes(original, current, original.length)
    val decompressed = QuetzalCompression.decompressDiffBytes(compressed, original, target,
                                                              original.length)
    decompressed should be (target)
    for (i <- 0 until original.length) {
      (target(i) & 0xff) should be (current(i) & 0xff)
    }
  }

  def readBytes(filename: String) = {
    val out = new ByteArrayOutputStream
    val in = new BufferedReader(new InputStreamReader(
      getClass.getClassLoader.getResourceAsStream(filename)))
    var line = in.readLine
    while (line != null) {
      if (line.trim.length > 0) {
        val num = line.trim.toInt
        out.write(num)
        line = in.readLine
      }
    }
    in.close
    out.toByteArray
  }
}
