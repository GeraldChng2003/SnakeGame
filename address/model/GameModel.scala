package SnakeGame.address.model

case class PlayerData(name: String, score: Int)

object GameModel {
  private var selectedDifficulty: String = "Easy"
  private var playerName: Option[String] = None
  private var currentScore: Int = 0

  def getSelectedDifficulty: String = selectedDifficulty

  def setSelectedDifficulty(difficulty: String): Unit = {
    selectedDifficulty = difficulty
  }

  def getPlayerName: Option[String] = playerName

  def setPlayerName(name: String): Unit = {
    playerName = Some(name)
  }

  //Called by GameEnvironment
  //updates the case class with the higher score
  def addToScore(newScore: Int): Unit = {
    currentScore = Math.max(currentScore, newScore)
  }

  //Called By GameEnvironment
  //updates the database after checking for conditions
  def saveScore(): Unit = {
    playerName.foreach { name =>
      // Get the current player's score from the model and create a PlayerData instance
      val playerData = PlayerData(name, currentScore)
      // Save the score in the database using the Scoreboard object's save method
      Scoreboard.save(playerData)
    }
  }

  def getScoreboard: List[PlayerData] = Scoreboard.getAllScoreboardEntries.map(entry =>
    PlayerData(entry.name, entry.score)
  )

}
