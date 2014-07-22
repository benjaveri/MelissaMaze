/*
 * Copyright (c) 2009-2014 by the De Waal family. All Rights reserved.
 */

package com.bronzecastle.MelissaMaze.menu

import com.bronzecastle.MelissaMaze.MainGame
import org.newdawn.slick.{Graphics, Input}

class Menu(val left: Int,val top: Int,esc: Int,val items: Seq[MenuItem]) {
  var sel = 0

  def draw(g: Graphics) {
    var y = top
    val dy = (3 * MainGame.fontHeight) / 2
    for (index <- 0 until items.size) {
      val item = items(index)
      val selected = index == sel
      item.draw(g,left,y,selected)
      y += dy
    }
  }

  def keyPressed(key: Int, c: Char) {
    key match {
      case Input.KEY_UP => sel = (sel + items.size - 1) % items.size
      case Input.KEY_DOWN => sel = (sel + 1) % items.size
      case Input.KEY_ENTER => items(sel).select()
      case Input.KEY_ESCAPE => items(esc).select()
      case _ => {}
    }
  }

  def keyReleased(key: Int, c: Char) {

  }
}
