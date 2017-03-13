package Utilities

import main.DataTypes.Tweet
import main.SparkContextManager
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/11/17.
 */
object TweetsFileProcessor {

	def LoadTweetsFromFile(filename:String, delimiter:String = ";"):RDD[Tweet] =
	{
		val sc = SparkContextManager.getContext
		val fileContent = sc.textFile(filename).map(l => l.split(delimiter))
		var counter = 0

		def toTweet(segments: Array[String]) = segments match {
			case Array(label, tweetText) =>
				counter += 1
				Tweet(counter.toString, tweetText, label.toDouble)
		}

		def cleanHtml(str: String) = str.replaceAll( """<(?!\/?a(?=>|\s.*>))\/?.*?>""", "")
		def cleanTweetHtml(sample: Tweet) = sample copy (tweetText = cleanHtml(sample.tweetText))
		// Words only
		def cleanWord(str: String) = str.split(" ").map(_.trim.toLowerCase).filter(_.size > 0).map(_.replaceAll("\\W", "")).reduce((x, y) => s"$x $y")
		def wordOnlyTweet(sample: Tweet) = sample copy (tweetText = cleanWord(sample.tweetText))

		val fileTweets = fileContent map toTweet
		val cleanTweets = fileTweets map cleanTweetHtml

		val wordOnlyTweets = cleanTweets map wordOnlyTweet
		wordOnlyTweets
	}

}
