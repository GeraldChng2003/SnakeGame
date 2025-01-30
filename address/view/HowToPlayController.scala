package SnakeGame.address.view

import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.Includes._
import scalafxml.core.macros.sfxml
import SnakeGame.address.MainApp

@sfxml
class HowToPlayController(private val returnToMenuButton: Button) {


  //button to return to Main Menu
  def handleReturnToMenuButton(event: ActionEvent): Unit = {
    MainApp.showMainMenuOverview()
  }

}
