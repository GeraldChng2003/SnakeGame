package SnakeGame.address.model

import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scala.collection.mutable.ListBuffer

//from scala 2.12
//https://www.scala-lang.org/api/2.12.x/scala/Enumeration.html
//Mainly for directions
object SnakeDirection extends Enumeration {
  val Up, Down, Left, Right = Value
}

// Change and overwrite the components of snake
class Snake extends GameObject {
  override val objectBody: Rectangle = new Rectangle {
    width = 10
    height = 10
    fill = Color.Green
  }
  var color: Color = Color.Green

  val body: ListBuffer[Rectangle] = ListBuffer()
  var direction: SnakeDirection.Value = SnakeDirection.Right
  val initialSize: Int = 5 // Set the initial size of the snake (you can adjust this as needed)

}
