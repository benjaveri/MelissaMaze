/**
 * main program
 *
 * Copyright (c) 2009-2011 by the de Waal family. All Rights reserved
 */
import org.lwjgl.LWJGLException
import org.lwjgl.opengl.{GL11, Display, DisplayMode}
import org.newdawn.slick.opengl.{TextureLoader, Texture}

object Main
{
  /**
   * game variables
   */
  var fullScreen   = false
  var screenWidth  = 1280
  var screenHeight = 1080

  /*
    main entry
     */
  def main(args: Array[String]) {
    // set display
    Display.setDisplayMode (new DisplayMode(screenWidth,screenHeight))
    Display.create()
    Display.setVSyncEnabled (true)

    // test
    // we just draw a maze for now - will do proper maze screen later
    val w = 50
    val h = 30
    val maze = new Maze(w,h)
    maze.initialize()
    maze.draw(-1)

    //run (new Prepare())
    run (new Quest(maze))

    Display.destroy()
  }

  /**
   * main loop
   */
  def run(controller: Controller) {
    // initialize
    controller.initialize()

    // go
    var count = 0
    var last  = System.nanoTime
    while (!Display.isCloseRequested) {
      // update and render
      controller.update()
      controller.render()
      Display.sync(60)
      Display.update()

      // update and display stats
      val now = System.nanoTime
      count += 1
      if ((now - last) > 1e9) {
        val fps: Double = (1.0e9*count) / (now-last)
        println (fps+" fps")
        count = 0
        last = now
      }
    }

    controller.destroy()
  }
}