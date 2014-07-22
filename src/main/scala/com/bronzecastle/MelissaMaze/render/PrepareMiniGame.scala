/*
 * Copyright (c) 2009-2014 by the De Waal family. All Rights reserved.
 */
package com.bronzecastle.MelissaMaze.render

import com.bronzecastle.MelissaMaze.menu.{Menu, MenuItem}
import com.bronzecastle.MelissaMaze.{MainGame, Maze}
import org.newdawn.slick.{Color, Graphics}

class PrepareMiniGame extends MiniGame {
  //
  // constants
  //

  //
  // state
  //
  override def initialize() {
    MainGame.pushState(ChooseGame)
  }

  override def destroy() {
    val x = 0
  }

  def terminate(next: Option[MiniGame]) {
    MainGame.popState() // inner state
    MainGame.popState() // this
    next.foreach(MainGame.pushState) // next
  }

  /**
   * choose maze
   */
  object ChooseGame extends MiniGame {
    val menu = new Menu(
      MainGame.screenWidth / 5,
      MainGame.screenHeight / 5,
      3, // escape maps on to go-back
      List(
        new MenuItem("Try  50 x 37",()=>{ MainGame.maze = new Maze(50,37); MainGame.changeState(DrawMaze) }),
        new MenuItem("Try 100 x 75",()=>{ MainGame.maze = new Maze(100,75); MainGame.changeState(DrawMaze) }),
        new MenuItem("Try 200 x 150",()=>{ MainGame.maze = new Maze(200,150); MainGame.changeState(DrawMaze) }),
        new MenuItem("Go Back",()=>{ terminate(None) })
      )
    )

    override def render(g: Graphics) {
      g.clear()
      menu.draw(g)
    }

    override def keyPressed(key: Int, c: Char) {
      menu.keyPressed(key,c)
    }

    override def keyReleased(key: Int, c: Char) {
      menu.keyReleased(key,c)
    }

  }

  /**
   * draw maze
   */
  object DrawMaze extends MiniGame {
    val portalW = 16
    val portalH = 16
    var done = false
    var top = 0
    var left = 0
    var steps = 0

    //
    // controller
    //
    override def initialize() {
      val maze = MainGame.maze
      done = false
      left = (maze.width - portalW)/2
      top = (maze.height - portalH)/2
      steps = math.max(1,(MainGame.maze.width * MainGame.maze.height) / 100)
      MainGame.maze.initialize()
    }

    override def update(delta: Int) {
      if (done) MainGame.changeState(GameReady)
      val maze = MainGame.maze
      if (!maze.done) {
        maze.draw(steps)
        if (top > maze.curY) top = maze.curY
        if ((top+portalH) <= maze.curY) top = maze.curY-portalH
        if (left > maze.curX) left = maze.curX
        if ((left+portalW) <= maze.curX) left = maze.curX-portalW
      } else {
        // make sure we have a good quality maze
        val blocks = maze.data.map(t => if (t == Maze.TILE_BLOCK) 1 else 0).sum
        val perc = (blocks * 100) / (maze.width * maze.height)
        if (perc < 39) {
          maze.initialize()
          return
        }
        // place treasure
        maze.place(maze.deepestX,maze.deepestY,Maze.TILE_TREASURE)
        // place prisoners
        maze.placePrisoners(8)
        // place swords
        maze.placeSwords(20)
        // place monsters
        maze.placeMonsters(15)
        // done
        done = true
      }
    }

    override def render(g: Graphics) {
      g.clear()
      MainGame.font.drawString(MainGame.screenWidth / 5,MainGame.screenHeight / 5,"Drawing maze....",Color.gray)
      drawMaze(g)
    }

    def drawMaze(g: Graphics) {
      val maze = MainGame.maze
      var idx0 = top * maze.width + left
      var dy = MainGame.screenHeight/4
      for (y <- 0 until portalH) {
        var idx = idx0
        var dx = MainGame.screenWidth/2 - portalW/2*MainGame.smallTileSize
        for (x <- 0 until portalW) {
          val sx = maze.data(idx) * MainGame.smallTileSize
          g.drawImage(
            MainGame.smallTileImage,
            dx,dy,dx+MainGame.smallTileSize,dy+MainGame.smallTileSize,
            sx,0,sx+MainGame.smallTileSize,MainGame.smallTileSize
          )
          idx += 1
          dx += MainGame.smallTileSize
        }
        idx0 += maze.width
        dy += MainGame.smallTileSize
      }
    }
  }

  /**
   * game ready
   */
  object GameReady extends MiniGame {
    var togo = 5000

    override def initialize() {
      val maze = MainGame.maze
      DrawMaze.left = math.max(0,maze.deepestX-DrawMaze.portalW/2)
      DrawMaze.top = math.max(0,maze.deepestY-DrawMaze.portalH/2)
    }

    override def update(delta: Int) {
      togo -= delta
      if (togo < 0) terminate(Some(new QuestMiniGame))
    }

    override def render(g: Graphics) {
      g.clear()
      MainGame.font.drawString(MainGame.screenWidth / 5, MainGame.screenHeight / 5, "Ready...", Color.gray)
      DrawMaze.drawMaze(g)
    }
  }
}
