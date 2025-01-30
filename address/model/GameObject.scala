
package SnakeGame.address.model

import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

//parent class of the game objects such as snake, rock and apple

abstract class GameObject {
  val objectBody: Rectangle
  var color: Color

}
