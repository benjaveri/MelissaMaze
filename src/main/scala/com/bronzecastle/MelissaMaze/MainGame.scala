/**
 * main program
 *
 * Copyright (c) 2009-2014 by the de Waal family. All Rights reserved
 */
package com.bronzecastle.MelissaMaze

import java.awt.Font

import com.bronzecastle.MelissaMaze.render.{MiniGame, PrepareMiniGame}
import org.newdawn.slick._

import scala.collection.mutable

object MainGame extends MainGame {
  def main(args: Array[String]) {
    start(args)
  }
}

class MainGame extends BasicGame("Melissa's Maze")
{
  /**
   * game constants
   */
  val smallTileSize = 16
  val largeTileSize = 128

  /**
   * game variables
   */
  var mini = new mutable.Stack[MiniGame]
  var app: AppGameContainer = null
  var container: GameContainer = null
  var fullScreen   = false
  var screenWidth  = 1024
  var screenHeight = 768
  var largeTileImage: Image = null
  var smallTileImage: Image = null
  var maze: Maze = null
  var font: TrueTypeFont = null
  var fontHeight = 0

  var totalTime = 0
  def period(p: Int) = (totalTime % p) < (p/2)

  def start(args: Array[String]) {
    app = new AppGameContainer(this)
    app.setDisplayMode(screenWidth,screenHeight,fullScreen)
    app.setForceExit(false)
    app.start()
  }

  def pushState(newState: MiniGame) {
    mini.push(newState)
    newState.initialize()
  }

  def popState() {
    val s = mini.pop()
    s.destroy()
  }

  def changeState(newState: MiniGame) {
    popState()
    pushState(newState)
  }

  def init(_container: GameContainer) {
    container = _container
    container.setClearEachFrame(false)
    container.setShowFPS(false)
    fontHeight = screenHeight / 20
    font = new TrueTypeFont(Font.createFont(Font.TRUETYPE_FONT,getClass.getResourceAsStream("/art/mainfont.ttf")).deriveFont(fontHeight.toFloat),true)
    container.setDefaultFont(font)
    largeTileImage = new Image(getClass.getResourceAsStream("/art/000large.jpg"),"large",false)
    smallTileImage = new Image(getClass.getResourceAsStream("/art/000small.jpg"),"small",false)
    pushState(new PrepareMiniGame)
  }

  /**
   * game loop & rendering
   */
  def update(_container: GameContainer, delta: Int) {
    container = _container
    totalTime += delta
    if (mini.isEmpty) { container.exit(); return }
    mini.headOption.foreach(_.update(delta))
  }

  def render(_container: GameContainer, g: Graphics) {
    container = _container
    mini.headOption.foreach(_.render(g))
  }

  /**
   * input
   */
  override def keyPressed (key: Int, c: Char) {
    mini.headOption.foreach(_.keyPressed(key,c))
  }

  override def keyReleased(key: Int, c: Char) {
    mini.headOption.foreach(_.keyReleased(key,c))
  }
}