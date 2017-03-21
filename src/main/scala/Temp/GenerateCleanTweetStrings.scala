package Temp

import Utilities.CleanTweet
import main.DataTypes.Tweet
import main.SparkContextManager
import org.apache.log4j.{Level, Logger}

/**
 * Created by saur6410 on 3/20/17.
 */
object GenerateCleanTweetStrings {

	def GenerateCleanStrings(inputFileName:String)=
	{
		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)
		Logger.getLogger("logreg").setLevel(Level.OFF)
		val sc = SparkContextManager.getContext
		sc.setLogLevel("ERROR")
		def toSample(segments: String) = segments match {
			case tweetText => Tweet(java.util.UUID.randomUUID.toString, tweetText, 0.0)
			case unknown => {
				println(s"Issue with Tweet:${segments}")
				Tweet(java.util.UUID.randomUUID.toString, "hello", 0.0)
			}
		}

		val delimiter = ","
		val trainingFileContent = sc.textFile(inputFileName).map(x => x.split(delimiter)).map(x => x(1)) //.map(l => l.split(" "))

		val trainSamples = trainingFileContent map toSample


		def cleanHtml(str: String) = str.replaceAll( """<(?!\/?a(?=>|\s.*>))\/?.*?>""", "")

		def cleanSampleHtml(sample: Tweet) = sample copy (tweetText = cleanHtml(sample.tweetText))

		val cleanTrainSamples = trainSamples map cleanSampleHtml

		// Words only
		def cleanWord(str: String) = str.split(" ").map(_.trim.toLowerCase).filter(_.size > 0).map(_.replaceAll("\\W", "")).reduce((x, y) => s"$x $y")

		def wordOnlySample(sample: Tweet) = sample copy (tweetText = cleanWord(sample.tweetText))

		//val wordOnlyTrainSample = cleanTrainSamples //map wordOnlySample

		val nlpCleanSamples = CleanTweet.getCleanedTweets (cleanTrainSamples.map(t => t.tweetText),sc)

		nlpCleanSamples.coalesce(1).saveAsTextFile("data/training/cleanOutput")

	}

}
