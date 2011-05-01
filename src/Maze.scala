/**
 * maze implementation
 *
 * Copyright (c) 2009-2011 by the de Waal family. All Rights reserved
 */
import java.security.SecureRandom
import org.lwjgl.opengl.{GL11, Display, DisplayMode}
import org.newdawn.slick.opengl.{TextureLoader, Texture}

class Maze extends Controller {
  val tileSize = 2
  val width  = Main.screenWidth/tileSize // for now
  val height = Main.screenHeight/tileSize // for now
  val data: Array[Symbol] = new Array[Symbol](width*height)

  def initialize {
    val rx = Main.screenWidth.toDouble / tileSize
    val ry = Main.screenHeight.toDouble / tileSize

    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity
    GL11.glOrtho(0,rx,ry,0,1,-1);
    GL11.glMatrixMode(GL11.GL_MODELVIEW);

    for (y <- 0 until height) {
      for (x <- 0 until width) {
        data(x+width*y) = if ((x==0) || (y==0) || (x==width/2) || (x==(width-1)) || (y==(height-1))) 'edge else 'block
      }
    }
  }

  def destroy = 0

  //val rg = new SecureRandom
  val rg = scala.util.Random
  val dx = +1 ::  0 :: -1 :: 0 :: Nil
  val dy =  0 :: +1 ::  0 :: -1 :: Nil
  var x = width / 2
  var y = height / 2
  var done = false
  val steps = width*height/60/20
  def update {
    // function to count # of block symbols surrounding a position
    def countBlocks(x: Int,y: Int): Int = {
      def fn(dir: Int) = if (data(x+dx(dir)+width*(y+dy(dir)))=='block) 1 else 0
      var sum = 0
      (0 to 3).foreach(sum += fn(_))
      sum
    }

    // one step - either advance in a valid random direction, or backtrack
    def step {
      if (done) return

      data(x+width*y) = 'mark
      val dirs = rg.shuffle((0 to 3).toList)
      for (dir <- dirs) {
        val cx = x + dx(dir)
        val cy = y + dy(dir)
        if (data(cx+width*cy)=='block) {
          val cb = countBlocks(cx,cy)
          if (cb == 3) {
            // advance
            x = cx
            y = cy
            return
          }
        }
      }

      data(x+width*y) = 'empty
      for (dir <- 0 to 3)
      {
        val cx = x + dx(dir)
        val cy = y + dy(dir)
        if (data(cx+width*cy)=='mark) {
          // backtrack
          x = cx
          y = cy
          return
        }
      }

      // cant advance or backtrack, must be done
      done = true
    }

    if (!done) for (i <- 0 until steps) step
  }

  def render {
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
  }
}