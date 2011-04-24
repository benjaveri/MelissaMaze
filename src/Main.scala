/**
 * main program
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
  var screenWidth  = 800
  var screenHeight = 600
  var tileWidth    = 64
  var tileHeight   = 64

  /*
    main entry
     */
  def main(args: Array[String]) {
    // set display
    Display.setDisplayMode (new DisplayMode(screenWidth,screenHeight))
    Display.create
    Display.setVSyncEnabled (true)

    run (new Maze())

    Display.destroy
  }

  /**
   * main loop
   */
  def run(controller: Controller) {
    // initialize
    controller.initialize

    // go
    var count = 0
    var last  = System.nanoTime
    while (!Display.isCloseRequested) {
      // update and render
      controller.update
      controller.render
      Display.update

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

    controller.destroy





    /*
    // setup transforms
    val rx = screenWidth.toDouble / tileWidth
    val ry = screenHeight.toDouble / tileHeight
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity
    GL11.glOrtho(0,rx,ry,0,1,-1);
    GL11.glMatrixMode(GL11.GL_MODELVIEW);

    // toy texture
    val texture = TextureLoader.getTexture("PNG",new FileInputStream("art/tile00.png"))
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_S,GL11.GL_CLAMP)
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_T,GL11.GL_CLAMP)

    // go

      //x += dx
      if ((x < 0) || (x>9)) dx = -dx
      //y += dy
      if ((y < 0) || (y>9)) dy = -dy


      // Clear the screen and depth buffer
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

      GL11.glColor3f(1,1,1)
      texture.bind

      // draw quad
      GL11.glColor3f(0.5f,0.5f,1.0f);
      GL11.glBegin(GL11.GL_QUADS);

      for (x <- 0 to 5) for (y <- 0 to 5)
      {
        GL11.glTexCoord2f(0,0); GL11.glVertex2d(x,  y);
        GL11.glTexCoord2f(1,0); GL11.glVertex2d(x+1,y);
        GL11.glTexCoord2f(1,1); GL11.glVertex2d(x+1,y+1);
        GL11.glTexCoord2f(0,1); GL11.glVertex2d(x,  y+1);
      }
      GL11.glEnd

      //Display.sync(60)
      Display.update
    }
    */


  }
}