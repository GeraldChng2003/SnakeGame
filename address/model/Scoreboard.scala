package SnakeGame.address.model

import SnakeGame.address.util.Database
import scalikejdbc._

case class Scoreboard(id: Int, name: String, score: Int)
/*
Referenced the practical class codes
 */
object Scoreboard extends Database {
  //creating of a  new sql data
  def apply(id: Int, name: String, score: Int): Scoreboard = {
    new Scoreboard(id, name, score)
  }


  // saving the new data to sql
  def save(playerData: PlayerData): Unit = {
    val existingEntry = sql"select * from Scoreboard where name = ${playerData.name}"
      .map(rs => PlayerData(rs.string("name"), rs.int("score")))
      .single
      .apply()

    //check if data matches database
    // case some = update data because it already exists
    // case non = add new entry
    existingEntry match {
      case Some(existingPlayerData) =>
        if (playerData.score > existingPlayerData.score) {
          updateScoreboardEntry(playerData)
        }
      case None =>
        addScoreboardEntry(playerData)
    }
  }


  //collecting all data from sql
  def getAllScoreboardEntries: List[PlayerData] = {
    sql"select * from Scoreboard".map { rs =>
      PlayerData(rs.string("name"), rs.int("score"))
    }.list.apply()
  }

  // Method to update existing entry
  private def updateScoreboardEntry(playerData: PlayerData): Unit = {
    sql"update Scoreboard set score = ${playerData.score} where name = ${playerData.name}".update.apply()
  }

  // Method to add a new entry
  private def addScoreboardEntry(playerData: PlayerData): Unit = {
    sql"insert into Scoreboard (name, score) values (${playerData.name}, ${playerData.score})".update.apply()
  }

  //method to initalize the database
  def initializeTable(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
        create table Scoreboard (
          id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
          name varchar(64),
          score int,
          primary key (id)
        )
      """.execute().apply()
    }
  }

}
