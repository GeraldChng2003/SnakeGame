package SnakeGame.address.view

import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label}
import scalafxml.core.macros.sfxml
import SnakeGame.address.MainApp
import SnakeGame.address.model.GameModel
import scalafx.scene.control.TextField

@sfxml
class DifficultySelectController(private val easyButton: Button,
                                 private val mediumButton: Button,
                                 private val hardButton: Button,
                                 private val proceedButton: Button,
                                 private val cancelButton: Button,
                                 private val enterNameLabel: Label,
                                 private val selectedDifficultyLabel: Label,
                                 private val nameTextField: TextField
                                ) {
  //Auxilary Functions to ensure buttons are clicked
  private var isEasy = false
  private var isMedium  = false
  private var isHard = false

  //tell system of setting "Easy"
  def handleEasyButton(event: ActionEvent): Unit = {
    //informing game model case class
    GameModel.setSelectedDifficulty("Easy")
    updateSelectedDifficultyLabel()
    isEasy = true

  }
  //tell system of setting "Medium"
  def handleMediumButton(event: ActionEvent): Unit = {
    //informing game model case class
    GameModel.setSelectedDifficulty("Medium")
    updateSelectedDifficultyLabel()
    isMedium = true
  }

  //tell system of setting "Hard"
  def handleHardButton(event: ActionEvent): Unit = {
    //informing game model case class
    GameModel.setSelectedDifficulty("Hard")
    updateSelectedDifficultyLabel()
    isHard = true
  }

  def handleProceedButton(event: ActionEvent): Unit = {
    //informing game model case class
    //Gets the current name entered by the user in the text field
    val playerName = nameTextField.getText.trim

    //Notifier, notifies the user what difficulty they picked
    val selectedDifficulty = GameModel.getSelectedDifficulty

    //user picked easy, medium or hard and inserted name
    if ( (isEasy||isMedium||isHard) && playerName.nonEmpty) {
      GameModel.setPlayerName(playerName) // Set the player name in GameModel
      MainApp.showGamePlayOverview()
    }
    //user did not enter name and select a difficulty
    else if ( !(isEasy||isMedium||isHard) && playerName.isEmpty) {
      enterNameLabel.text = "Please enter your name" // Update the enterNameLabel
      selectedDifficultyLabel.text = "Please select a difficulty" // Update the selectedDifficultyLabel
    }
    //user did not enter name
    else if (playerName.isEmpty ) {
      enterNameLabel.text = "Please enter your name" // Update the enterNameLabel
    }
    //user did not selected difficulty
    else {
      selectedDifficultyLabel.text = "Please select a difficulty" // Update the selectedDifficultyLabel
    }
  }

  //button for canceling and returning back to main menu
  def handleCancelButton(event: ActionEvent): Unit = {
    MainApp.showMainMenuOverview()
  }

  //updates the label in live time to notify user of selected difficulty and name
  private def updateSelectedDifficultyLabel(): Unit = {
    val selectedDifficulty = GameModel.getSelectedDifficulty
    if (selectedDifficulty != null) {
      selectedDifficultyLabel.text = s"Selected Difficulty: $selectedDifficulty"
    } else {
      selectedDifficultyLabel.text = "Please select a difficulty"
    }
  }

}
