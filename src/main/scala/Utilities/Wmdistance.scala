package Utilities

import java.net.URLEncoder

import com.netaporter.uri.dsl._
import main.DataTypes.Tweet
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._

/**
 * Created by ericrw96 on 3/17/17.
 */
class Wmdistance{
	val url = "http://localhost:5000/distance"

	def getSimilarities(first: Tweet, second: Tweet): Double = {
		implicit val formats = DefaultFormats
		var uri = url ? ("document1" -> first.tweetText) & ("document2" -> second.tweetText)
		uri = URLEncoder.encode(uri, "utf-8").replaceAll("\\+", "%20");
		val result = scala.io.Source.fromURL(uri).mkString
		val distance = parse(result).extract[Double]
		distance
	}
}
