/*
 * Copyright (c) 2009-2014 by the De Waal family. All Rights reserved.
 */

package com.bronzecastle.MelissaMaze

import org.junit.Test
import org.junit.Assert._

import scala.util.Random

@Test
class MazeTest {
  val rng = new Random

  @Test
  def testGenAndSearch() {
    for (i <- 0 until 100) {
      // draw a maze
      val w = 10 + rng.nextInt(30)
      val h = 10 + rng.nextInt(30)
      val maze = new Maze(w, h)
      maze.initialize()
      maze.draw()

      // place treasure
      maze.place(maze.deepestX, maze.deepestY, Maze.TILE_TREASURE)

      // now look for for it
      val (found, steps, tx, ty) = maze.find(maze.curX, maze.curY, Maze.TILE_TREASURE)

      // validate things check out
      assertTrue("Found", found)
      assertTrue("Depth mismatch", steps == maze.deepest)
    }
  }

  @Test
  def testGenStats() {
    println("test skipped")
//    println("area\tempty\tblock\n")
//    for (dside <- 10 until 40) {
//      val area  = dside*dside
//      var total = 0
//      var empty = 0
//      var block = 0
//      for (i <- 0 until 100) {
//        // draw a maze
//        val w = dside
//        val h = dside
//        val maze = new Maze(w,h)
//        maze.initialize()
//        maze.draw()
//
//        // gen some stats
//        def count(tile: Byte) = maze.data.map(t => if (tile == t) 1 else 0).sum
//        block += count(Maze.TILE_BLOCK)
//        empty += count(Maze.TILE_EMPTY)
//        total += maze.data.size
//      }
//      println(s"$area\t${(empty * 100) / total}%\tblock:${(block * 100) / total}%")
//    }
  }
}
