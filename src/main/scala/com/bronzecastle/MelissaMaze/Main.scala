/**
 * main program
 *
 * Copyright (c) 2009-2014 by the de Waal family. All Rights reserved
 */
package com.bronzecastle.MelissaMaze

import org.newdawn.slick.AppGameContainer
import org.newdawn.slick.BasicGame
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.SlickException

object Main extends BasicGame("Melissa's Maze")
{
  /**
   * game variables
   */
  var fullScreen   = false
  var screenWidth  = 1280
  var screenHeight = 1080
  val app = new AppGameContainer(this)

  def main(args: Array[String]) {
    app.setDisplayMode(screenWidth,screenHeight,fullScreen)
    app.setForceExit(false)
    app.start()
  }

  def init(container: GameContainer) {}

  def update(container: GameContainer, delta: Int) {}

  def render(container: GameContainer, g: Graphics) {
    g.drawString("Hello!", 50, 50)
  }

  //  /*
//    main entry
//     */
//  def main(args: Array[String]) {
//    // set display
//    Display.setDisplayMode (new DisplayMode(screenWidth,screenHeight))
//    Display.create()
//    Display.setVSyncEnabled (true)
//
//    // test
//    // we just draw a maze for now - will do proper maze screen later
//    val w = 50
//    val h = 30
//    val maze = new Maze(w,h)
//    maze.initialize()
//    maze.draw()
//
//    //run (new Prepare())
//    run (new Quest(maze))
//
//    Display.destroy()
//  }
//
//  /**
//   * main loop
//   */
//  def run(controller: Controller) {
//    // initialize
//    controller.initialize()
//
//    // go
//    var count = 0
//    var last  = System.nanoTime
//    while (!Display.isCloseRequested) {
//      // update and render
//      controller.update()
//      controller.render()
//      Display.sync(60)
//      Display.update()
//
//      // update and display stats
//      val now = System.nanoTime
//      count += 1
//      if ((now - last) > 1e9) {
//        val fps: Double = (1.0e9*count) / (now-last)
//        println (fps+" fps")
//        count = 0
//        last = now
//      }
//    }
//
//    controller.destroy()
//  }
}