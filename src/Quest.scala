/**
 * Quest - the main game loop
 *
 * Copyright (c) 2009-2011 by the de Waal family. All Rights reserved
 */
import org.lwjgl.opengl.GL11


/*
                       1 unit ==
                      1<<12 ticks
                       |<---->|
 tiles-> +------+------+------+
         |      |      |      |
         (tx,ty)|      |      |
  screen -> +--------------+  | --
         |  |   |      |   |  |  ^
         +--|---+------+---|--+  |
         |  |   |      |   |  |  |
         |  |   |      |   |  |  |
         |  |   |      |   |  |  | HEIGHT
         |  |   |      |   |  |  |  in ticks
         +--|---+------+---|--+  |
         |  |   |      |   |  |  |
         |  |   |      |   |  |  v
         |  +--------------+  | --
         |      |      |      |
         +------+------+------+
            |              |
            |<------------>|
             WIDTH in ticks


  game coordinates in x.12 fixed coordinates
  world coordinates set up so 1 unit equals 1 tile
  it is scaled so you see the same amount of tiles
    regardless of resolution
  we draw only the tiles that are visible
  (tx,ty) defines the top-left coord

*/

class Quest(_data: Array[Symbol]) extends Controller {
  //
  // constants
  //
  val TILESIZE_SRC  = 128       // 128 pixels in artwork space
  val LOGTIX        = 12        // fixed point precision
  val FX1           = int2fixed(1)
  val WIDTH_TILES   = 10        // # of tiles to fit in width
  val WIDTH         = int2fixed(WIDTH_TILES)
  val HEIGHT        = fixedMulDiv(WIDTH,int2fixed(Main.screenHeight),int2fixed(Main.screenWidth))
  val HEIGHT_TILES  = fixed2int(HEIGHT+FX1-1)
  val dx = +1 ::  0 :: -1 :: 0 :: Nil
  val dy =  0 :: +1 ::  0 :: -1 :: Nil

  //
  // state
  //
  val rg = scala.util.Random
  var tx = float2fixed(0.5f)
  var ty = 0//float2fixed(0.25f)
  var data = _data

  //
  // utilities
  //
  def int2fixed(i: Int)     = i<<LOGTIX
  def fixed2int(f: Int)     = f>>LOGTIX
  def fixedMulDiv(a: Int,b: Int,c: Int) = ((a.toLong*b.toLong)/c).toInt
  def float2fixed(f: Float) = (f*(1<<LOGTIX)).toInt
  def fixed2float(f: Int)   = f.toFloat / (1<<LOGTIX)
  def fixedTrunc(f: Int)    = f & -(1<<LOGTIX)
  def fixedFrac(f: Int)     = f & ((1<<LOGTIX)-1)

  //
  // controller implementation
  //
  def initialize {
    // set up gl
    GL11.glMatrixMode(GL11.GL_PROJECTION)
    GL11.glLoadIdentity()
    GL11.glOrtho(0,WIDTH,HEIGHT,0,1,-1)
    GL11.glMatrixMode(GL11.GL_MODELVIEW)
    GL11.glLoadIdentity();
  }

  def destroy = 0

  def update {
    tx += fixedMulDiv(FX1,FX1,int2fixed(100))
    ty += fixedMulDiv(FX1,FX1,int2fixed(150))
  }

  def render {
      // Clear the screen and depth buffer
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT)

    // draw grid
    val ix = fixed2int(tx)
    val ox = -fixedFrac(tx)
    val iy = fixed2int(ty)
    val oy = -fixedFrac(ty)

    GL11.glBegin(GL11.GL_QUADS)
    for (ry <- 0 to HEIGHT_TILES) {
      val y = oy+int2fixed(ry)
      for (rx <- 0 to WIDTH_TILES) {
        val x = ox+int2fixed(rx)

        if (((rx^ry^ix^iy)&1)==0) GL11.glColor3f(1,1,1)
        else GL11.glColor3f(0,0,0)

        GL11.glVertex2i(x,    y)
        GL11.glVertex2i(x+FX1,y)
        GL11.glVertex2i(x+FX1,y+FX1)
        GL11.glColor3f(1,0,0)
        GL11.glVertex2i(x,    y+FX1)
      }
    }
    GL11.glEnd
  }
}