/**
 * maze implementation
 *
 * Copyright (c) 2009-2011 by the de Waal family. All Rights reserved
 */
//
// maze drawer
//
class Maze(_width: Int,_height: Int) {
  val rg = scala.util.Random
  val dx = +1 ::  0 :: -1 :: 0 :: Nil
  val dy =  0 :: +1 ::  0 :: -1 :: Nil
  val width = _width
  val height = _height
  val data: Array[Symbol] = new Array[Symbol](_width*_height)
  var done = false
  var x = _width / 2
  var y = _height / 2

  def initialize() {
    // set up state
    done = false
    x = _width / 2
    y = _height / 2

    // clear maze
    for (y <- 0 until height) {
      for (x <- 0 until width) {
        data(x+width*y) = if ((x==0) || (y==0) || (x==width/2) || (x==(width-1)) || (y==(height-1))) 'edge else 'block
      }
    }
  }

  def draw (steps: Int) {
    // function to count # of block symbols surrounding a position
    def countBlocks(x: Int,y: Int): Int = {
      def fn(dir: Int) = if (data(x+dx(dir)+width*(y+dy(dir)))=='block) 1 else 0
      var sum = 0
      (0 to 3).foreach(sum += fn(_))
      sum
    }

    // one step - either advance in a valid random direction, or backtrack
    def step() {
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

    // draw either the whole thing, or just a fixed amount of steps
    if (steps > 0) {
      for (i <- 0 until steps) {
        if (done) return
        step()
      }
    } else {
      while (!done) step()
    }
  }
}
