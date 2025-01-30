package SnakeGame.address.model

import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

class Rock extends GameObject {
  // Change and overwrite the components of rock
  val objectBody: Rectangle = new Rectangle {
    width = 20
    height = 20
    fill = Color.Gray
  }
  var color: Color = Color.Gray

}
