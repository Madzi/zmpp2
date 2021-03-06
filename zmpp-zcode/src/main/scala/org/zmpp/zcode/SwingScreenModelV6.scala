/*
 * Created on 2010/08/07
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
package org.zmpp.zcode

import javax.swing._
import java.awt.{Dimension,Font}
import java.io.{FileInputStream, FileOutputStream}

sealed trait V6WindowState {
}

class V6TextGrid extends V6WindowState {
}
class V6TextWindow extends V6WindowState {
}
class V6GraphicsWindow extends V6WindowState {
}

class V6Window {
  private var currentState: V6WindowState = null
  def setCurrentState(state: V6WindowState) {
    currentState = state
  }
}

/*
 * Implementation of the V6 screen model using Swing components.
 * The V6 screen model is pixel-based, so rendering is entirely done
 * in a custom way, making this much more difficult and more restrictive.
 */
class SwingScreenModelV6 extends JComponent
with OutputStream with InputStream with SwingScreenModel {
  var vm: Machine = null
  val windows = Array.ofDim[V6Window](8)
  (0 until 7).foreach{ i => windows(i) = new V6Window }

  private var selected  = true
  val fixedFont         = new Font("Courier New", Font.PLAIN, 14)
  setPreferredSize(new Dimension(640, 480))

  def capabilities = List(SupportsColors,    SupportsBoldFont,    SupportsItalicFont,
                          SupportsFixedFont, SupportsTimedInput,  SupportsSound,
                          SupportsPictures,  SupportsScreenSplit, SupportsMouse,
                          SupportsMenus)
  def activeWindow = {
    throw new UnsupportedOperationException("Not supported yet")
  }

  // OutputStream
  def isSelected = selected
  def select(flag: Boolean) = selected = flag
  def putChar(c: Char) {
    //println("@put_char: '%c'".format(c))
  }
  def flush { }
  def flushInterruptOutput { }
  def cancelInput { }

  // InputStream
  def readLine: Int = {
    println("@read_line (TODO)")
    0
  }

  // ScreenModel
  def initUI {
    // now the top window "knows" how large the screen is, so we can set
    // the dimensions and font sizes to the VM
    val g = getGraphics
    val fm = g.getFontMetrics(fixedFont)
    vm.setFontSizeInUnits(fm.charWidth('0'), fm.getHeight)
    vm.setScreenSizeInUnits(getWidth, getHeight)

    println("Screen size (units): " + vm.screenSizeInUnits)
    println("Font size (units): " + vm.fontSizeInUnits)
  }
  
  def connect(aVm: Machine) {
    vm = aVm
  }
  def setColour(foreground: Int, background: Int, window: Int) {
    printf("@set_colour %d %d %d (TODO)\n", foreground, background, window)
  }
  def setFont(font: Int): Int = {
    printf("@set_font %d (TODO)\n", font)
    1
  }
  def eraseLine(value: Int) {
    printf("@erase_line %d not implemented yet (TODO)\n", value)
  }
  def setTextStyle(aStyle: Int) {
    printf("@set_text_style %d (TODO)\n", aStyle)
  }
  def eraseWindow(windowId: Int) {
    printf("@erase_window %d (TODO)\n", windowId)
    windowId match {
      case -1 => println("reset screen")
      case -2 => println("clear window, no unsplit")
      case _ => println("clear selected window")
    }
  }
  def setWindow(windowId: Int) {
    printf("@set_window %d (TODO)\n", windowId)
  }
  def splitWindow(lines: Int) {
    printf("@split_window (%d units) (TODO)\n", lines)
  }
  def cursorPosition: (Int, Int) = {
    throw new UnsupportedOperationException("getCursorPosition() not yet implemented in screen model")
  }
  def setCursorPosition(line: Int, column: Int) {
    printf("@set_cursor %d %d (TODO)\n", line, column)
  }
  def updateStatusLine { }
  def screenOutputStream = this
  def keyboardStream     = this
  def bufferMode(flag: Int) {
    printf("@buffer_mode %d (TODO)\n", flag)
  }

  // SwingStreamModel
  def readChar { }

  def requestSaveFile {
    val fileChooser = new JFileChooser
    fileChooser.setDialogTitle("Save Game As...")
    val outputStream = if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      new FileOutputStream(fileChooser.getSelectedFile)
    } else null
    vm.resumeWithSaveStream(outputStream)
    ExecutionControl.executeTurn(vm, this)
  }
  def requestRestoreFile {
    val fileChooser = new JFileChooser
    fileChooser.setDialogTitle("Restore Game From...")
    val inputStream = if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      new FileInputStream(fileChooser.getSelectedFile)
    } else null
    vm.resumeWithRestoreStream(inputStream)
    ExecutionControl.executeTurn(vm, this)
  }
}
