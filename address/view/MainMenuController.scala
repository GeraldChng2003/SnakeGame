package SnakeGame.address.view

import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafxml.core.macros.sfxml
import SnakeGame.address.MainApp

//Main Navigation
@sfxml
class MainMenuController(private val howToPlayButton: Button,
                         private val playGameButton: Button,
                         private val scoreboardButton: Button
                        ) {
  //load the how to play page
  //scene already created in MainApp, so we can just load
  def handleHowToPlayButton(event: ActionEvent): Unit = {
    MainApp.showHowToPlayOverview()
  }
  //load the difficulty select page
  def handlePlayGameButton(event: ActionEvent): Unit = {
    MainApp.showDifficultySelectOverview()
  }
  //load the scoreboard page
  def handleScoreboardButton(event: ActionEvent): Unit = {
    MainApp.showScoreBoardOverview()
  }
}


