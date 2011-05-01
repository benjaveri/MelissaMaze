/**
 * controller interface
 *
 * Copyright (c) 2009-2011 by the de Waal family. All Rights reserved
 */
abstract class Controller {
  def initialize()
  def destroy()
  def update()
  def render()
}
