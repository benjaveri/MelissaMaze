/**
 * controller interface
 *
 * Copyright (c) 2009-2014 by the de Waal family. All Rights reserved
 */
package com.bronzecastle.MelissaMaze

abstract class Controller {
  def initialize()
  def destroy()
  def update()
  def render()
}
