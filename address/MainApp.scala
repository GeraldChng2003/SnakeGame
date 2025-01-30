package SnakeGame.address

import SnakeGame.address.util.Database
import scalafx.application.JFXApp
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.scene.Scene
import scalafx.stage.Stage
import SnakeGame.address.view.{DifficultySelectController, GameEnvironmentController, HowToPlayController, MainMenuController, ScoreboardController}

//SNAKE GAME Application
//Main and Only Entry Point
object MainApp extends JFXApp {

  Database.setupDB()

  // Set the primary stage
  stage = new JFXApp.PrimaryStage {
    title = "Snake Game"
  }
  // Method to handle scene navigation
  def changeScene(scene: Scene): Unit = {
    stage.scene = scene
  }


  def showMainMenuOverview(): Unit = {
    val mainMenuResource = getClass.getResource("/SnakeGame/address/view/MainMenu.fxml")
    val mainMenuLoader = new FXMLLoader(mainMenuResource, NoDependencyResolver)
    val mainMenuRoot: jfxs.layout.AnchorPane = mainMenuLoader.load()
    val mainMenuScene = new Scene(mainMenuRoot)
    changeScene(mainMenuScene) // Change the scene to the main menu

  }

  def showHowToPlayOverview(): Unit = {
    val howToPlayResource = getClass.getResource("/SnakeGame/address/view/HowToPlay.fxml")
    val howToPlayLoader = new FXMLLoader(howToPlayResource, NoDependencyResolver)
    val howToPlayRoot: jfxs.layout.AnchorPane = howToPlayLoader.load()
    val howToPlayScene = new Scene(howToPlayRoot)
    changeScene(howToPlayScene) // Change the scene to the how-to-play screen
  }

  // Method to show the DifficultySelect view
  def showDifficultySelectOverview(): Unit = {
    val difficultySelectResource =
      getClass.getResource("/SnakeGame/address/view/DifficultySelect.fxml")
    val difficultySelectLoader = new FXMLLoader(difficultySelectResource, NoDependencyResolver)
    val difficultySelectRoot: jfxs.layout.AnchorPane = difficultySelectLoader.load()
    val difficultySelectScene = new Scene(difficultySelectRoot)
    changeScene(difficultySelectScene) // Change the scene to the difficulty select screen
  }

  // Method to show the GamePlay view
  def showGamePlayOverview(): Unit = {
    // Load the game play view and set the scene
    val gamePlayResource = getClass.getResource("/SnakeGame/address/view/GameEnvironment.fxml")
    val gamePlayLoader = new FXMLLoader(gamePlayResource, NoDependencyResolver)
    val gamePlayRoot: jfxs.layout.AnchorPane = gamePlayLoader.load()
    val gamePlayScene = new Scene(gamePlayRoot)

    changeScene(gamePlayScene) // Change the scene to the Game Environment screen

  }

  // Scoreboard Scene
  def showScoreBoardOverview(): Unit = {
    // Load the scoreboard view and set the scene
    val scoreBoardResource = getClass.getResource("/SnakeGame/address/view/Scoreboard.fxml")
    val scoreBoardLoader = new FXMLLoader(scoreBoardResource, NoDependencyResolver)
    val scoreBoardRoot: jfxs.layout.AnchorPane = scoreBoardLoader.load()
    val scoreBoardScene = new Scene(scoreBoardRoot)
    changeScene(scoreBoardScene)  // Change the scene to the Scoreboard screen
  }


  //Return to Main Menu Method
  def returnToMenuAction(): Unit = {
    showMainMenuOverview()
  }

  // Show the main menu scene by default
  showMainMenuOverview()

}