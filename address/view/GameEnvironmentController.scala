package SnakeGame.address.view

import SnakeGame.address.MainApp

//Implicit scalafx converter all javafx becomes scalafx
import scalafx.Includes._
//The frame of the game, will be converted into scalafx
import javafx.animation.AnimationTimer
import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml
import scalafx.scene.layout.Pane
import scalafx.scene.control.Button
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.scene.input.KeyEvent
//snake direction also known as the"Direction"
import SnakeGame.address.model.{Snake, SnakeDirection}
import scala.util.Random
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import javafx.{scene => jfxs}
import SnakeGame.address.model.Rock
import SnakeGame.address.model.GameModel

/*
Referred to this youtube series
For Animation Timer
https://www.youtube.com/watch?v=zojzE67cjj8

and this API
https://www.scalafx.org/api/8.0/#scalafx.animation.AnimationTimer

Referred to this document
for snake game movement logic
https://blog.rockthejvm.com/snake/

 */
@sfxml
class GameEnvironmentController(private val startGameButton: Button, private val gamePane: Pane,
                                private val scoreLabel: Label, private val gameOverScreen: VBox, private val retryButton: Button,
                                private val mainMenuButton: Button) {
  //game environment variables
  private val gridSize = 600 //(600 by 600 grid)
  private val elementDistanceThreshold = 5
  private var moveInterval = 300

  // the "state" of the game
  //checking for occupied positions
  private val occupiedPositions: scala.collection.mutable.Set[(Int, Int)] =
  scala.collection.mutable.Set.empty

  //game-live time variables
  private var lastUpdateTime: Long = 0
  private var isGameLoopRunning = false
  private var isGameOver = false

  //variable to store difficulty selected by user
  // difficulty remains the same for every retry untl user returns to main menu, then they can pick new difficulty
  private val selectedDifficulty = GameModel.getSelectedDifficulty

  //game objects
  private var snake: Snake = _
  private var apple: SnakeGame.address.model.Apple = new SnakeGame.address.model.Apple()
  private val numRocksToSpawn = calculateNumRocks(selectedDifficulty)

  //creating a sequences of rocks to store rock positions
  private var rocks: Seq[Rock] = Seq.empty


  // GameModel Data
  private val playerName: String = GameModel.getPlayerName.getOrElse("")
  //Score variable
  private var score: Int = 0

  // A reference to the GameModel to ensure data is not lost
  private val gameModel = GameModel

  //methods to initialize the game
  // the "frame" of the game
  //ensures game is always updated
  //ensures game is rendered properly with any collision as well
  private val gameLoopTimer: AnimationTimer = new AnimationTimer {
    //method needs to be overriden
    override def handle(now: Long): Unit = {
      val elapsedTime = now - lastUpdateTime
      if (elapsedTime >= moveInterval) {
        updateSnakePosition()
        lastUpdateTime = now
      }
    }
  }

  //Companion Object - CollisionDetector
  //done for better code organisation
  //functionalities remain

  object GameEnvironmentController {
    def hasSnakeCollidedWithWall(snake: Snake, gridSize: Int): Boolean = {
      val headCollidedWithWall =
        snake.objectBody.layoutX.value < 0 || snake.objectBody.layoutX.value + snake.objectBody.width.value > gridSize ||
          snake.objectBody.layoutY.value < 0 || snake.objectBody.layoutY.value + snake.objectBody.height.value > gridSize

      val bodyCollidedWithWall = snake.body.exists { bodyPart =>
        bodyPart.layoutX.value < 0 || bodyPart.layoutX.value + bodyPart.width.value > gridSize ||
          bodyPart.layoutY.value < 0 || bodyPart.layoutY.value + bodyPart.height.value > gridSize
      }

      headCollidedWithWall || bodyCollidedWithWall
    }

    def hasSnakeCollidedWithItself(snake: Snake): Boolean = {
      val headPosition = (snake.objectBody.layoutX.value.toInt, snake.objectBody.layoutY.value.toInt)
      snake.body.drop(1).exists { bodyPart =>
        val bodyPartPosition = (bodyPart.layoutX.value.toInt, bodyPart.layoutY.value.toInt)
        bodyPartPosition == headPosition
      }
    }

    def hasSnakeCollidedWithRock(snake: Snake, rocks: Seq[Rock]): Boolean = {
      val snakeHeadBounds = snake.objectBody.localToScene(snake.objectBody.getBoundsInLocal)

      // Check for collision between the snake and each rock in the sequence
      rocks.exists { rock =>
        val rockBounds = rock.objectBody.localToScene(rock.objectBody.getBoundsInLocal)
        snakeHeadBounds.intersects(rockBounds)
      }
    }
  }


  //method to initialize the scene, ensuring the game starts properly
  def initialize(): Unit = {
    startGameButton.layoutX = (gridSize - startGameButton.width.value) / 2
    startGameButton.layoutY = (gridSize - startGameButton.height.value) / 2

    //get the pane
    val scene = gamePane.getScene
    //initialize Allowing "WASD" keys to move snake
    scene.setOnKeyPressed(handleKeyPressed)
    // Display the score in the label
    updateScoreLabel()
  }

  //controls the start button on the center of the screen
  def handleStartGame(event: ActionEvent): Unit = {

    //makes the button invisible, and score visible
    //calls initializeGameEnvironment() which spawns the game objects like apple, snake, rock
    startGameButton.visible = false
    gamePane.children.clear()
    initializeGameEnvironment()
    gamePane.requestFocus()

    scoreLabel.visible = true
    //Start Game Loop
    lastUpdateTime = System.currentTimeMillis()
    isGameLoopRunning = true
    gameLoopTimer.start()


  }

  //method that spawns the game objects like apple, snake, rock
  def initializeGameEnvironment(): Unit = {
    occupiedPositions.clear()

    // Add a rectangle to represent the boundary
    val boundary = new Rectangle {
      width = gridSize.toDouble
      height = gridSize.toDouble
      stroke = Color.Black
      fill = Color.White

    }
    //add boundary to empty pane
    gamePane.children.add(boundary)

    //spawn gameobjects
    spawnSnake()
    spawnApple()
    spawnRocks()


  }

  //spawning of GameObjects

  // Use the selectedDifficulty from the GameModel to determine the number of rocks to spawn
  def calculateNumRocks(selectedDifficulty: String): Int = selectedDifficulty match {
    case "Easy" => 2
    case "Medium" => 5
    case "Hard" => 10
    case _ => 2 //default easy
  }
  //method to clear sequence of rocks, ensuring that old rocks from previous session gets removed properly
  private def clearRocks(): Unit = {
    rocks.foreach(rock => gamePane.children.remove(rock.objectBody))
    rocks = Seq.empty
  }

  //method for spawning rock logic
  private def spawnRocks(): Unit = {
    //spawning of rocks
    for (i <- 0 until numRocksToSpawn) {
      //rock creation
      val rock = new Rock()

      // Randomly generate a suitable unoccupied position for rocks
      var rockX = Random.nextInt(gridSize / elementDistanceThreshold) * elementDistanceThreshold
      var rockY = Random.nextInt(gridSize / elementDistanceThreshold) * elementDistanceThreshold

      //ensures rock does not spawn in position that are occupied
      while (occupiedPositions.contains((rockX, rockY))) {
        rockX = Random.nextInt(gridSize / elementDistanceThreshold) * elementDistanceThreshold
        rockY = Random.nextInt(gridSize / elementDistanceThreshold) * elementDistanceThreshold
      }

      // Set the found suitable position
      rock.objectBody.layoutX = rockX
      rock.objectBody.layoutY = rockY

      // Addition of the rock to the empty pane
      gamePane.children.add(rock.objectBody)

      // add all created rocks to sequence of rocks created earlier
      rocks = rocks :+ rock

      // tell the system that each position the rock has is occupied
      occupiedPositions.add((rockX, rockY))
    }
  }

  //spawning of snake
  private def spawnSnake(): Unit = {
    snake = new Snake()
    val snakeX = gridSize / 2 - snake.objectBody.width.value / 2
    val snakeY = gridSize / 2 - snake.objectBody.height.value / 2

    snake.objectBody.layoutX = snakeX
    snake.objectBody.layoutY = snakeY
    gamePane.children.add(snake.objectBody)

    snake.body.clear()
    snake.body += snake.objectBody

    for (i <- 1 until snake.initialSize) {
      val newBodyPart = new Rectangle {
        width = 10
        height = 10
        fill = Color.Green
      }
      newBodyPart.layoutX = snakeX - i * elementDistanceThreshold
      newBodyPart.layoutY = snakeY
      gamePane.children.add(newBodyPart)
      snake.body += newBodyPart
    }
  }
  //creation of apple
  private def spawnApple(): Unit = {
    var appleX = Random.nextInt(gridSize / elementDistanceThreshold) * elementDistanceThreshold
    var appleY = Random.nextInt(gridSize / elementDistanceThreshold) * elementDistanceThreshold

    while (occupiedPositions.contains((appleX, appleY))) {
      appleX = Random.nextInt(gridSize / elementDistanceThreshold) * elementDistanceThreshold
      appleY = Random.nextInt(gridSize / elementDistanceThreshold) * elementDistanceThreshold
    }
    apple.objectBody.layoutX = appleX
    apple.objectBody.layoutY = appleY

    //add the new apple to the game
    gamePane.children.add(apple.objectBody)
    occupiedPositions.add((appleX, appleY))
  }


  // handling of keys
  //uses Enumeration, which allows keys to be represented easily
  def handleKeyPressed(event: KeyEvent): Unit = {
    if (isGameOver) return
    val keyCode = event.getCode
    keyCode.getName match {
      case "W"  =>
        snake.direction = SnakeDirection.Up
      case "A"  =>
        snake.direction = SnakeDirection.Left
      case "S"  =>
        snake.direction = SnakeDirection.Down
      case "D"  =>
        snake.direction = SnakeDirection.Right
      case _ => // Ignore other keys
    }
  }
  //updating snake position in live time
  private def updateSnakePosition(): Unit = {
    if (!isGameOver) {
      moveRemainingBodyParts()

      var prevX = snake.objectBody.layoutX.value
      var prevY = snake.objectBody.layoutY.value
      //making sure each keys change the snake in the correct direction
      snake.direction match {
        case SnakeDirection.Up => moveSnakeUp()
        case SnakeDirection.Left => moveSnakeLeft()
        case SnakeDirection.Down => moveSnakeDown()
        case SnakeDirection.Right => moveSnakeRight()
      }

      //collision detector for eating apple
      if (isCollisionWithApple() && !isGameOver) {
        increaseScore()
        growSnake()
        gamePane.children.remove(apple.objectBody)
        spawnApple()
      }

      occupiedPositions.clear()
      snake.body.foreach { bodyPart =>
        occupiedPositions.add((bodyPart.layoutX.value.toInt, bodyPart.layoutY.value.toInt))
      }

      if (GameEnvironmentController.hasSnakeCollidedWithWall(snake, gridSize) || GameEnvironmentController.hasSnakeCollidedWithItself(snake) ||
        GameEnvironmentController.hasSnakeCollidedWithRock(snake, rocks)) {
        isGameOver = true
        showGameOverScreen()
      }
    }
  }

  //Collision with snake, placed here because it interacts with other methods very often
  //grows snake and loads the new snake in real time
  private def isCollisionWithApple(): Boolean = {
    val snakeHeadBounds = snake.objectBody.localToScene(snake.objectBody.getBoundsInLocal)
    val appleBounds = apple.objectBody.localToScene(apple.objectBody.getBoundsInLocal)
    snakeHeadBounds.getMaxX > appleBounds.getMinX &&
      snakeHeadBounds.getMinX < appleBounds.getMaxX &&
      snakeHeadBounds.getMaxY > appleBounds.getMinY &&
      snakeHeadBounds.getMinY < appleBounds.getMaxY
  }

  //increase the score after each apple ate
  private def increaseScore(): Unit = {
    // increment value to check for difficulty, higher difficlty rewards player
    val scoreIncrement = selectedDifficulty match {
      case "Easy" => 10
      case "Medium" => 15
      case "Hard" => 20
      case _ => 10 // default value, score for easy
    }

    score += scoreIncrement
    // Update the GameModel with the new player's name and score
    GameModel.setPlayerName(playerName)
    GameModel.addToScore(score)

    updateScoreLabel()
  }

  //update the score label in live time
  private def updateScoreLabel(): Unit = {
    // Updating the scoreLabelText in the bottom of the screen with the current score
    scoreLabel.text = s"Score: $score"
  }

  //growth of snake
  private def growSnake(): Unit = {
    val newSnakePart = new Rectangle {
      width = 10
      height = 10
      fill = Color.Green
    }

    val lastPart = snake.body.lastOption.getOrElse(snake.objectBody)
    newSnakePart.layoutX = lastPart.layoutX.value
    newSnakePart.layoutY = lastPart.layoutY.value

    gamePane.children.add(newSnakePart)
    occupiedPositions.add((newSnakePart.layoutX.value.toInt, newSnakePart.layoutY.value.toInt))

    snake.body += newSnakePart
  }

  //method to allow movement of snake
  private def moveSnakeUp(): Unit = {
    if (snake.objectBody.layoutY.value - elementDistanceThreshold >= 0) {
      snake.objectBody.layoutY = snake.objectBody.layoutY.value - elementDistanceThreshold
    } else {
      isGameOver = true
      showGameOverScreen()
    }
  }

  //method to allow movement of snake
  private def moveSnakeLeft(): Unit = {
    if (snake.objectBody.layoutX.value - elementDistanceThreshold >= 0) {
      snake.objectBody.layoutX = snake.objectBody.layoutX.value - elementDistanceThreshold
    } else {
      isGameOver = true
      showGameOverScreen()
    }
  }
  //to ensure that all parts move together
  private def moveRemainingBodyParts(): Unit = {
    var prevX = snake.objectBody.layoutX.value
    var prevY = snake.objectBody.layoutY.value
    snake.body.tail.foreach { bodyPart =>
      val nextX = bodyPart.layoutX.value
      val nextY = bodyPart.layoutY.value
      bodyPart.layoutX = prevX
      bodyPart.layoutY = prevY
      prevX = nextX
      prevY = nextY
    }
  }
  //method to allow movement of snake
  private def moveSnakeDown(): Unit = {
    if (snake.objectBody.layoutY.value + elementDistanceThreshold < gridSize) {
      snake.objectBody.layoutY = snake.objectBody.layoutY.value + elementDistanceThreshold
    }
  }
  //method to allow movement of snake
  private def moveSnakeRight(): Unit = {
    if (snake.objectBody.layoutX.value + elementDistanceThreshold < gridSize) {
      snake.objectBody.layoutX = snake.objectBody.layoutX.value + elementDistanceThreshold
    }
  }

  //for adjusting the snake speed
  def adjustSnakeSpeed(newInterval: Long): Unit = {
    moveInterval = newInterval.toInt
    if (isGameLoopRunning) {
      gameLoopTimer.stop()
      gameLoopTimer.start()
    }
  }

  // Method to handle showing the Game Over screen
  private def showGameOverScreen(): Unit = {
    // Stop the game loop
    gameLoopTimer.stop()

    // Add the player's data to the scoreboard in DataModel
    GameModel.saveScore()

    // Show the Game Over screen and buttons
    gameOverScreen.setVisible(true)
    retryButton.visible = true
    mainMenuButton.visible = true


  }

  // Method to handle the "Retry" button click
  def handleRetry(): Unit = {
    // Set the player's name and score in GameModel
    GameModel.setPlayerName(playerName)
    GameModel.addToScore(score)

    // Reset the game and start again
    resetGame()

  }

  // Method to handle the "Main Menu" button click
  def handleMainMenu(): Unit = {
    // Go back to the main menu
    MainApp.showMainMenuOverview()

  }

  // Method to reset the game and all environment variables and start again
  def resetGame(): Unit = {
    gamePane.children.clear()
    gameLoopTimer.stop()
    isGameOver = false
    clearRocks()
    initializeGameEnvironment()
    lastUpdateTime = System.currentTimeMillis()
    isGameLoopRunning = true
    gameLoopTimer.start()
    gamePane.visible = true
    gameOverScreen.setVisible(false)
    score = 0
    initialize()

  }
}
