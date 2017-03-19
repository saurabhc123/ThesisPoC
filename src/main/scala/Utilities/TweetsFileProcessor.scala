package Utilities

import java.util.UUID

import main.DataTypes.Tweet
import main.SparkContextManager
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/11/17.
 */
object TweetsFileProcessor {

	def LoadTweetsFromFile(filename:String, delimiter:String = ";", useUUID:Boolean = true):RDD[Tweet] =
	{
		val sc = SparkContextManager.getContext
		var fileContent = sc.textFile(filename).map(l => l.split(delimiter))
		fileContent = fileContent.map(stringArrays => {
			if(stringArrays.length > 2){
				val resultArray = new Array[String](2)
				resultArray(0) = stringArrays(0)
				resultArray(1) = stringArrays(1) + stringArrays(2)
				resultArray
			}
			else
				stringArrays
		})
		var counter = 0

		def toTweet(segments: Array[String]) = segments match {

			case Array(label, tweetText) =>
				try {
					counter += 1
					if(!useUUID)
						Tweet(counter.toString, tweetText, label.toDouble)
					else
						Tweet(UUID.randomUUID().toString, tweetText, label.toDouble)
				}
				catch
					{
						case unknown => println(s"Issue with Tweet:${segments}, $counter")
							Tweet(counter.toString, "", 0.0)
					}
			case _ =>
				{
					println(s"Issue with Tweet:${segments.array.deep.mkString(" ")}, $counter")
					counter += 1
					Tweet(counter.toString, "hello", 0.0)
				}
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
