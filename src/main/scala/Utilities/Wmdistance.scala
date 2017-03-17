package Utilities

import com.netaporter.uri.dsl._
import main.DataTypes.Tweet
import org.json4s.jackson.JsonMethods._

/**
  * Created by ericrw96 on 3/17/17.
  */
class Wmdistance{
  val url = "http://localhost:5000/distance"

  def getSimilarities(first: Tweet, second: Tweet): Double = {
    val uri = url ? ("document1" -> first.tweetText) & ("document2" -> second.tweetText)
    val result = scala.io.Source.fromURL(uri.toString).mkString
    val distance = parse(result).extract[Double]
    distance
  }
}
