package SnakeGame.address.model

import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

class Apple extends GameObject {
  // Change and overwrite the components of apple
  override val objectBody: Rectangle = new Rectangle {
    width = 10
    height = 10
    fill = Color.Red
  }
  // Define the color property for Apple
  var color: Color = Color.Red


}
