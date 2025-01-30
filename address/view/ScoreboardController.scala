package SnakeGame.address.view

import SnakeGame.address.MainApp
import SnakeGame.address.model.{GameModel, PlayerData, Scoreboard}
import scalafx.scene.control.{Button, Label}

import scala.collection.JavaConverters._
import scalafxml.core.macros.sfxml
import scalafx.application.Platform
import scalafx.event.ActionEvent

@sfxml
class ScoreboardController(private val returnToMenuButton: Button,
                           private val playerHeader: Label,
                           private val scoreHeader: Label,
                           private val player1: Label,
                           private val player2: Label,
                           private val player3: Label,
                           private val player4: Label,
                           private val player5: Label,
                           private val score1: Label,
                           private val score2: Label,
                           private val score3: Label,
                           private val score4: Label,
                           private val score5: Label) {

  // Helper method to convert case class data to a sortable format
  private def convertToSortableFormat(data: List[PlayerData]): List[(String, Int)] = {
    data.map { playerData =>
      (playerData.name, playerData.score)
    }
  }

  // Method to update the scoreboard UI
  // gets called by initialize
  def updateScoreboardUI(updatedScoreboard: List[PlayerData]): Unit = {
    val sortableData = convertToSortableFormat(updatedScoreboard)
    // Sort the scoreboard data in descending order to ensure high score is shown
    val sortedScoreboard = sortableData.sortBy(-_._2)

    // Update the labels with the sorted data
    updateLabelData(sortedScoreboard)
  }


  // Method to update each and every label with new data
  // Idea is to only have top 5 scores, which is why there are so many replacing
  // I take the sorted data and replace each and every label in live time
  private def updateLabelData(sortedScoreboard: List[(String, Int)]): Unit = {
    // Fills each label with the data obtained from the sortedScoreboard list
    val entries = sortedScoreboard.take(5)
    val names = entries.map(_._1)
    val scores = entries.map(_._2)
    //replacing each label with the correct details
    player1.text = names.lift(0).getOrElse("")
    player2.text = names.lift(1).getOrElse("")
    player3.text = names.lift(2).getOrElse("")
    player4.text = names.lift(3).getOrElse("")
    player5.text = names.lift(4).getOrElse("")

    score1.text = scores.lift(0).map(_.toString).getOrElse("")
    score2.text = scores.lift(1).map(_.toString).getOrElse("")
    score3.text = scores.lift(2).map(_.toString).getOrElse("")
    score4.text = scores.lift(3).map(_.toString).getOrElse("")
    score5.text = scores.lift(4).map(_.toString).getOrElse("")
  }


  //return to main app
  def handleReturnToMenuButton(event: ActionEvent): Unit = {
    MainApp.showMainMenuOverview()
  }


  //calls the updating scoreboard method
  private def initialize(): Unit = {
    // Initial update with existing scoreboard data
    //calls the updateScoreboardUI method in Scoreboard.scala
    updateScoreboardUI(Scoreboard.getAllScoreboardEntries)
  }

  // Call the initialize method when the ScoreboardController is created
  initialize()
}
