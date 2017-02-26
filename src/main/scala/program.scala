package main.scala

import org.apache.log4j.{Level, Logger}

/**
 * Created by saur6410 on 2/25/17.
 */
object program extends App{

  override def main(args: Array[String]){


    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    println("Hello World!" + 11)
    LrClassifier.main(args)
    //LrOnlineClassifier.main(args)
  }

}
