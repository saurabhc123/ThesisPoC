package Implementations.AuxiliaryDataRetrievers

import main.DataTypes.Tweet
import main.Interfaces.IAuxiliaryDataRetriever
import main.SparkContextManager
import org.apache.spark.rdd.RDD


/**
 * Created by saur6410 on 3/9/17.
 */
class FileBasedAuxiliaryDataRetriever extends IAuxiliaryDataRetriever {


	val numberOfTweetsToRetrieve = 10
	//var lastLineRead = 0

	override def retrieveAuxiliaryData(distinguishingWords: Array[String]): RDD[Tweet] = {

		val sc = SparkContextManager.getContext
		//Read the tweets from the file one by one.
		val auxiliaryTweets = FileBasedAuxiliaryDataRetriever.readTweetsFromAuxiliaryFile()

		//Update the cursor for each step.
		val tweetsContainingRelevantWords = auxiliaryTweets.filter(x => doesTweetContainsDistinguishingWords(x.tweetText, distinguishingWords)
		&& x.identifier.toInt > FileBasedAuxiliaryDataRetriever.lastLineRead)

		val tweetsCount = tweetsContainingRelevantWords.count()

		if(tweetsCount == 0)
		{
			throw new Exception("No more auxiliary tweets to retrieve.")
		}

		var auxiliaryMatches:Array[Tweet] = null
		//Once the required number of tweets are retrieved, get the line number of the last tweet. Save it
		if(tweetsCount < numberOfTweetsToRetrieve)
		{
			auxiliaryMatches = tweetsContainingRelevantWords.take(tweetsCount.toInt)
		}
		else
		{
			auxiliaryMatches = tweetsContainingRelevantWords.take(numberOfTweetsToRetrieve)
		}



		FileBasedAuxiliaryDataRetriever.lastLineRead = auxiliaryMatches.last.identifier.toInt

		sc.parallelize(auxiliaryMatches)
	}

	def doesTweetContainsDistinguishingWords(tweetText:String, distinguishingWords: Array[String]) : Boolean = {

		val intersectionResult = tweetText.split(" ").intersect(distinguishingWords)
		if(intersectionResult.length > 1)
			return true
		return false

	}
}


object FileBasedAuxiliaryDataRetriever
{
	var lastLineRead = 0
	val _auxiliaryFileName = "data/training/multi_class_lem"

	var _auxiliaryTweets:RDD[Tweet] = null

	def readTweetsFromAuxiliaryFile() : RDD[Tweet] = {

		if(_auxiliaryTweets != null)
			return _auxiliaryTweets

		val sc = SparkContextManager.getContext
		val delimiter = ";"
		val auxiliaryFileContent = sc.textFile(_auxiliaryFileName).map(l => l.split(delimiter))

		var counter = 0

		def toTweet(segments: Array[String]) = segments match {
			case Array(label, tweetText) =>
				counter += 1
				Tweet(counter.toString, tweetText, Some(label.toDouble))
		}

		def cleanHtml(str: String) = str.replaceAll( """<(?!\/?a(?=>|\s.*>))\/?.*?>""", "")
		def cleanSampleHtml(sample: Tweet) = sample copy (tweetText = cleanHtml(sample.tweetText))
		// Words only
		def cleanWord(str: String) = str.split(" ").map(_.trim.toLowerCase).filter(_.size > 0).map(_.replaceAll("\\W", "")).reduce((x, y) => s"$x $y")
		def wordOnlySample(sample: Tweet) = sample copy (tweetText = cleanWord(sample.tweetText))

		val trainSamples = auxiliaryFileContent map toTweet
		val cleanTrainSamples = trainSamples map cleanSampleHtml
		//FileBasedAuxiliaryDataRetriever.lastLineRead += counter
		_auxiliaryTweets = cleanTrainSamples

		_auxiliaryTweets
	}

}