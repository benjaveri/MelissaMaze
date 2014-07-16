/*
 * Copyright (c) 2009-2014 by the De Waal family. All Rights reserved.
 */
package com.bronzecastle.MelissaMaze

import java.io.FileInputStream
import org.lwjgl.opengl.GL11
import org.newdawn.slick.opengl.TextureLoader

/*
  this is the main part of the game (the quest)


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

class Quest(_maze: Maze) extends Controller {
  //
  // constants
  //
  val TILESIZE_TILE  = 128       // 128 pixels in artwork space
  val TILESIZE_FULL = 2048      // full width of artword texture
  val LOGTIX        = 12        // fixed point precision
  val FX1           = int2fixed(1)
  val WIDTH_TILES   = 10        // # of tiles to fit in width
  val WIDTH         = int2fixed(WIDTH_TILES)
  val HEIGHT        = fixedMulDiv(WIDTH,int2fixed(Main.screenHeight),int2fixed(Main.screenWidth))
  val HEIGHT_TILES  = fixed2int(HEIGHT+FX1-1)

  //
  // tables
  //
  val dx = +1 ::  0 :: -1 :: 0 :: Nil
  val dy =  0 :: +1 ::  0 :: -1 :: Nil
  val tc = Map(
    'empty        -> tcIndex(0),
    'thisway      -> tcIndex(1),
    'mark         -> tcIndex(2),
    'track        -> tcIndex(2),
    'edge         -> tcIndex(3),
    'block        -> tcIndex(4),
    'sword        -> tcIndex(5),
    'treasure     -> tcIndex(6),
    'prisoner     -> tcIndex(7),
    'monster0     -> tcIndex(8),
    'monster1     -> tcIndex(9),
    'monster2     -> tcIndex(10),
    'monster3     -> tcIndex(11),
    'man          -> tcIndex(12),
    'man_sword    -> tcIndex(13),
    'man_Treasure -> tcIndex(14)
  )

  //
  // state
  //
  val rg = scala.util.Random
  var tx = 0
  var ty = 0
  val maze = _maze
  val data = _maze.data
  val mazeWidth = _maze.width
  val mazeHeight = _maze.height

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
  def tcIndex(i: Int) = {
    val xtick = 1.0f / TILESIZE_FULL;
    val ytick = 1.0f / TILESIZE_TILE;
    val xtile = TILESIZE_TILE.toFloat * xtick;
    // we sample slightly inside the texture to avoid wrapping artifacts
    (
      i*xtile + xtick,
      ytick,
      (i+1)*xtile - xtick,
      1.0f-ytick
    )
  }

  //
  // controller implementation
  //
  def initialize() {
//    // set up gl transforms
//    GL11.glMatrixMode(GL11.GL_PROJECTION)
//    GL11.glLoadIdentity()
//    GL11.glOrtho(0,WIDTH,HEIGHT,0,1,-1)
//    GL11.glMatrixMode(GL11.GL_MODELVIEW)
//    GL11.glLoadIdentity();
//
//    // load textures
//    val texture = TextureLoader.getTexture("JPEG",new FileInputStream("art/000large.jpg"))
//    GL11.glEnable(GL11.GL_TEXTURE_2D);
//    GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_S,GL11.GL_REPEAT)
//    GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_T,GL11.GL_REPEAT)
//    GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_LINEAR)
//    GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_LINEAR)
//    GL11.glColor3f(1,1,1)
//    texture.bind()
//
//    // create symbol to texcoord map
//
  }

  def destroy() {}

  def update() {
    tx += fixedMulDiv(FX1,FX1,int2fixed(100))
    ty += fixedMulDiv(FX1,FX1,int2fixed(150))
  }

  def render() {
//      // Clear the screen and depth buffer
//    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT)
//
//    // draw grid
//    val ix = fixed2int(tx)
//    val ox = -fixedFrac(tx)
//    val iy = fixed2int(ty)
//    val oy = -fixedFrac(ty)
//
//    GL11.glBegin(GL11.GL_QUADS)
//    for (ry <- 0 to HEIGHT_TILES) {
//      val y = oy+int2fixed(ry)
//      val sy = iy+ry
//      for (rx <- 0 to WIDTH_TILES) {
//        val x = ox+int2fixed(rx)
//        val sx = ix+rx
//        val c = if ((sx<0)||(sy<0)||(sx>=mazeWidth)||(sy>=mazeHeight)) 'empty
//                else data(sy*mazeWidth+sx)
//        val tt = tc(c)
//        GL11.glTexCoord2f(tt._1,tt._2); GL11.glVertex2i(x,    y)
//        GL11.glTexCoord2f(tt._3,tt._2); GL11.glVertex2i(x+FX1,y)
//        GL11.glTexCoord2f(tt._3,tt._4); GL11.glVertex2i(x+FX1,y+FX1)
//        GL11.glTexCoord2f(tt._1,tt._4); GL11.glVertex2i(x,    y+FX1)
//      }
//    }
//    GL11.glEnd
  }
}