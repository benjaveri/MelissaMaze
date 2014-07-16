/*
 * Copyright (c) 2009-2014 by the De Waal family. All Rights reserved.
 */
package com.bronzecastle.MelissaMaze

import scala.util.Random

//
// maze representation and operations
//
object Maze {
  val TILE_EMPTY: Byte    = 0
  val TILE_THISWAY: Byte  = 1
  val TILE_CRUMBS: Byte   = 2
  val TILE_EDGE: Byte     = 3
  val TILE_BLOCK: Byte    = 4
  val TILE_SWORD: Byte    = 5
  val TILE_TREASURE: Byte = 6
  val TILE_PRISONER: Byte = 7
  val TILE_MONSTER0: Byte = 8
  val TILE_MONSTER1: Byte = 8
  val TILE_MONSTER2: Byte = 10
  val TILE_MONSTER3: Byte = 11
  val TILE_MAN: Byte      = 12
  val TILE_MAN_S: Byte    = 13
  val TILE_MAN_T: Byte    = 14
}

class Maze(val width: Int,val height: Int) {
  val rng = new Random
  val dx = Array(+1, 0,-1, 0)
  val dy = Array( 0,+1, 0,-1)
  val data = new collection.mutable.ArrayBuffer[Byte]()
  var done = false
  var curX = width / 2
  var curY = height / 2
  var depth = 0
  var deepest = 0
  var deepestX = 0
  var deepestY = 0

  def initialize() {
    // set up state
    rng.setSeed(System.currentTimeMillis())
    done = false
    curX = width / 2
    curY = height / 2

    // clear maze
    for (yy <- 0 until height) {
      for (xx <- 0 until width) {
        data += (if ((xx==0) || (yy==0) || (xx==(width-1)) || (yy==(height-1))) Maze.TILE_EDGE else Maze.TILE_BLOCK)
      }
    }
  }

  override def toString = {
    val sb = new StringBuilder
    sb.append(s"width:$width, height:$height, deepest:$deepest @ ($deepestX,$deepestY)\n")
    for (yy <- 0 until height) {
      for (xx <- 0 until width) {
        sb.append(" :.@#t*p0123xxx".charAt(get(xx,yy)))
      }
      sb.append("\n")
    }
    sb.toString()
  }

  def place(x: Int,y: Int,t : Byte) { data(x+width*y) = t }
  def get(x: Int,y: Int) = data(x+width*y)

  def draw() { draw(Int.MaxValue) }

  /**
   * make sure that you end up with about 41% blocks since algorithm has
   *  some bad corner cases. empty space varies with area, but block
   *  count stays roughly flat
   *
   *  c.f. MazeTest.testGenStats
   */
  def draw (steps: Int) {
    // function to count # of block symbols surrounding a position
    def countBlocks(x: Int,y: Int): Int = {
      def fn(dir: Int) = if (get(x+dx(dir),y+dy(dir)) == Maze.TILE_BLOCK) 1 else 0
      (0 to 3).map(fn).sum
    }

    // one step - either advance in a valid random direction, or backtrack
    def step() {
      if (done) return

      place(curX,curY,Maze.TILE_CRUMBS)
      val dirs = rng.shuffle((0 to 3).toList)
      for (dir <- dirs) {
        val cx = curX + dx(dir)
        val cy = curY + dy(dir)
        if (get(cx,cy) == Maze.TILE_BLOCK) {
          val cb = countBlocks(cx,cy)
          if (cb == 3) {
            // advance
            curX = cx
            curY = cy
            depth += 1
            if (depth > deepest) { deepest = depth; deepestX = curX; deepestY = curY }
            return
          }
        }
      }

      place(curX,curY,Maze.TILE_EMPTY)
      for (dir <- 0 to 3)
      {
        val cx = curX + dx(dir)
        val cy = curY + dy(dir)
        if (get(cx,cy) == Maze.TILE_CRUMBS) {
          // backtrack
          curX = cx
          curY = cy
          depth -= 1
          return
        }
      }

      // cant advance or backtrack, must be done
      done = true
    }

    // draw either the whole thing, or just a fixed amount of steps
    var togo = steps
    while (!done && (togo > 0)) {
      step()
      togo -= 1
    }
  }

  def clearCrumbs() {
    for (yy <- 0 until height) {
      for (xx <- 0 until width) {
        if (get(xx,yy) == Maze.TILE_CRUMBS) place(xx,yy,Maze.TILE_EMPTY)
      }
    }
  }

  def find(startX: Int,startY: Int,targetTile: Byte): (Boolean,Int,Int,Int) = {
    val stack = new scala.collection.mutable.ArrayBuffer[Byte]()
    var x = startX
    var y = startY
    var dir = 0

    while (true) {
      place(x, y, Maze.TILE_CRUMBS)

      val xx = x + dx(dir)
      val yy = y + dy(dir)
      var advanced = false
      get(xx, yy) match {
        case Maze.TILE_EMPTY => {
          // advance
          stack += dir.toByte
          dir = 0
          x = xx
          y = yy
        }
        case tile => {
          if (tile == targetTile) {
            return (true, stack.size+1, xx, yy)
          } else {
            dir = (dir + 1) & 3
            while (dir == 0) {
              // not found?
              if (stack.isEmpty) return (false,0,0,0)

              // backtrack
              place(x, y, Maze.TILE_EMPTY)
              dir = stack.last
              stack.remove(stack.size-1)
              x = x - dx(dir)
              y = y - dy(dir)
              dir = (dir + 1) & 3
            }
          }
        }
      }
    }
    (false,0,0,0)
  }
}
