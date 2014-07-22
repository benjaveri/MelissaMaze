/*
 * Copyright (c) 2009-2014 by the De Waal family. All Rights reserved.
 */

package com.bronzecastle.MelissaMaze.menu

import com.bronzecastle.MelissaMaze.MainGame
import org.newdawn.slick.{Color, Graphics}

class MenuItem(val text: String,selectHandler: ()=>Unit) {
  def draw(g: Graphics,x: Int,y: Int,selected: Boolean) {
    if (selected) {
      MainGame.font.drawString(x, y, text, if (MainGame.period(512)) Color.white else Color.lightGray)
    } else {
      MainGame.font.drawString(x, y, text, Color.gray)
    }
  }

  def select() { selectHandler() }
}
