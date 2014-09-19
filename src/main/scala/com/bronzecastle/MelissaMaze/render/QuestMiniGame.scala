/*
 * Copyright (c) 2009-2014 by the De Waal family. All Rights reserved.
 */
package com.bronzecastle.MelissaMaze.render

import com.bronzecastle.MelissaMaze.{Maze, MainGame}
import org.newdawn.slick.{Graphics, Input}

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
         |  |   |      |   |  |  |  in pixels
         +--|---+------+---|--+  |
         |  |   |      |   |  |  |
         |  |   |      |   |  |  v
         |  +--------------+  | --
         |      |      |      |
         +------+------+------+
            |              |
            |<------------>|
             WIDTH in pixels


  game coordinates in x.12 fixed coordinates
  world coordinates set up so 1 unit equals 1 tile
  it is scaled so you see the same amount of tiles
    regardless of resolution
  we draw only the tiles that are visible
  (tx,ty) defines the top-left coord

*/

class QuestMiniGame extends MiniGame {
  //
  // constants
  //
  val LOGTIX  = 12 // fixed point precision

  //
  // utilities
  //
  def int2fixed(i: Int) = i << LOGTIX
  def fixed2int(f: Int) = f >> LOGTIX
  def fixedMulDiv(a: Int,b: Int,c: Int) = ((a.toLong*b.toLong)/c).toInt
  def float2fixed(f: Float) = (f*(1<<LOGTIX)).toInt
  def fixed2float(f: Int) = f.toFloat / (1<<LOGTIX)
  def fixedTrunc(f: Int) = f & -(1<<LOGTIX)
  def fixedFrac(f: Int) = f & ((1<<LOGTIX)-1)

  //
  // state
  //
  val LTS = MainGame.largeTileSize
  var STS = 0 // screen tile size, screen resolution dependent
  var tx = 0
  var ty = 0
  var manX = 0
  var manY = 0
  val input = Array(false,false,false,false)

  //
  // mini-game implementation
  //
  override def initialize() {
    // screen tile size
    STS = MainGame.screenWidth / 8
    // start position
    val maze = MainGame.maze
    manX = maze.width / 2
    manY = maze.height / 2
    maze.place(manX,manY,Maze.TILE_MAN)
    tx = float2fixed(manX)-float2fixed(3.5f)
    ty = float2fixed(manY)-(maze.height*float2fixed(3.5f))/maze.width
  }

  override def update(delta: Int) {
    val ds = (delta*int2fixed(1)) / 250
    if (input(0)) ty -= ds
    if (input(1)) tx += ds
    if (input(2)) ty += ds
    if (input(3)) tx -= ds
  }

  override def render(g: Graphics) {
    val maze = MainGame.maze
    val dx0 = fixed2int(-fixedFrac(tx)*STS)
    var dy = fixed2int(-fixedFrac(ty)*STS)
    val ix = fixed2int(tx)
    val iy = fixed2int(ty)
    var idx0 = iy*maze.width+ix
    g.clear()
    while (dy < MainGame.screenHeight) {
      var idx = idx0
      var dx = dx0
      while (dx < MainGame.screenWidth) {
        val sx = maze.data(idx)*LTS
        g.drawImage(
          MainGame.largeTileImage,
          dx,dy,dx+STS,dy+STS,
          sx+1,1,sx+LTS-1,LTS-1 // compensate for bi-linear sampling across borders
        )
        dx += STS
        idx += 1
      }
      dy += STS
      idx0 += maze.width
    }
  }

  override def keyPressed(key: Int, c: Char) {
    key match {
      case Input.KEY_ESCAPE => MainGame.popState()
      case Input.KEY_UP => input(0) = true
      case Input.KEY_RIGHT => input(1) = true
      case Input.KEY_DOWN => input(2) = true
      case Input.KEY_LEFT => input(3) = true
      case _ => {}
    }
  }

  override def keyReleased(key: Int, c: Char) {
    key match {
      case Input.KEY_UP => input(0) = false
      case Input.KEY_RIGHT => input(1) = false
      case Input.KEY_DOWN => input(2) = false
      case Input.KEY_LEFT => input(3) = false
      case _ => {}
    }
  }

  //
  // character (common to monsters and players)
  //
  class Character {
    // position := base + delta * t
    var x = 0
    var y = 0
    var bx = 0
    var by = 0
    var dx = 0
    var dy = 0
    var t = 0

    def update(delta: Int) {
      t += delta
      x = bx + (t * dx) / 1000
      y = by + (t * dy) / 1000
    }
  }
}