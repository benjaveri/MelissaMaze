/*
 * Copyright (c) 2009-2014 by the De Waal family. All Rights reserved.
 */
package com.bronzecastle.MelissaMaze

import org.lwjgl.opengl.GL11

class Prepare extends Controller {
  //
  // constants
  //
  val tileSize = 2
  val width  = Main.screenWidth/tileSize // for now
  val height = Main.screenHeight/tileSize // for now

  //
  // controller
  //
  def initialize() {
    val rx = Main.screenWidth.toDouble / tileSize
    val ry = Main.screenHeight.toDouble / tileSize

    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity()
    GL11.glOrtho(0,rx,ry,0,1,-1);
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity()
  }

  def destroy() {}

  //val rg = new SecureRandom
  var x = width / 2
  var y = height / 2
  var done = false
  val steps = width*height/60/20

  def update() {}

  def render() {}
  /*{
      // Clear the screen and depth buffer
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

      // draw maze
      GL11.glBegin(GL11.GL_QUADS);
      for (y <- 0 until height) {
        for (x <- 0 until width) {
          data(x+y*width) match {
            case 'empty => GL11.glColor3f(1,1,1)
            case 'block => GL11.glColor3f(0,0,0)
            case 'edge  => GL11.glColor3f(.75f,.25f,.25f)
            case 'mark  => GL11.glColor3f(.25f,.75f,.25f)
          }
          GL11.glVertex2d(x,  y);
          GL11.glVertex2d(x+1,y);
          GL11.glVertex2d(x+1,y+1);
          GL11.glVertex2d(x,  y+1);
        }
      }
      GL11.glEnd
  }*/
}
