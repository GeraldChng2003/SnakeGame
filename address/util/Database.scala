package SnakeGame.address.util

import SnakeGame.address.model.Scoreboard
import scalikejdbc._

/*
Referenced the practical class codes
 */

trait Database {
  // the imports needed to have a proper and apache database
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:myDB;create=true;" // Local file-based database
  Class.forName(derbyDriverClassname) // Load the Derby driver

  // Initialize the database connection
  ConnectionPool.singleton(dbURL, "me", "mine", settings = ConnectionPoolSettings(connectionPoolFactoryName = "commons-dbcp2"))

  implicit val session: DBSession = AutoSession
}

object Database extends Database {
  //called by MyApp to initialize
  def setupDB(): Unit = {
    if (!hasDBInitialized)
      Scoreboard.initializeTable()

    //loads the database if  database has been initialized once
    def hasDBInitialized: Boolean = {
      DB.getTable("Scoreboard") match {
        case Some(x) => true
        case None => false
      }
    }
  }
}
