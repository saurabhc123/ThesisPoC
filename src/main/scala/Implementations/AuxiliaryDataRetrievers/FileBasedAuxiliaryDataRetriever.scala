package Implementations.AuxiliaryDataRetrievers

import Utilities.CleanTweet
import main.DataTypes.Tweet
import main.Interfaces.IAuxiliaryDataRetriever
import main.SparkContextManager
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.spark.rdd.RDD


/**
 * Created by saur6410 on 3/9/17.
 */
class FileBasedAuxiliaryDataRetriever(auxiliaryDataFilename:String) extends IAuxiliaryDataRetriever {


	//var lastLineRead = 0
	FileBasedAuxiliaryDataRetriever._auxiliaryFileName = auxiliaryDataFilename

	override def retrieveAuxiliaryData(distinguishingWords: Array[String]): RDD[Tweet] = {

		val sc = SparkContextManager.getContext
		//Read the tweets from the file one by one.
		val auxiliaryTweets = FileBasedAuxiliaryDataRetriever.readTweetsFromAuxiliaryFile()

		//Update the cursor for each step.
		val tweetsContainingRelevantWords = auxiliaryTweets.filter(x => doesTweetContainsDistinguishingWords(x.tweetText, distinguishingWords)
		&& x.identifier.toInt > FpmAuxiliaryFilter.lastLineRead)

		val tweetsCount = tweetsContainingRelevantWords.count()

		if(tweetsCount == 0)
		{
			throw new NoSuchElementException("No more auxiliary tweets to retrieve.")
		}

		var auxiliaryMatches:Array[Tweet] = null
		//Once the required number of tweets are retrieved, get the line number of the last tweet. Save it
		if(tweetsCount < AuxiliaryDataBasedExperiment.tweetsToAddEachIteration)
		{
			auxiliaryMatches = tweetsContainingRelevantWords.take(tweetsCount.toInt)
		}
		else
		{
			auxiliaryMatches = tweetsContainingRelevantWords.take(AuxiliaryDataBasedExperiment.tweetsToAddEachIteration)
		}
		FpmAuxiliaryFilter.lastLineRead = auxiliaryMatches.last.identifier.toInt

		sc.parallelize(auxiliaryMatches)
	}

	def doesTweetContainsDistinguishingWords(tweetText:String, distinguishingWords: Array[String]) : Boolean = {

		val intersectionResult = tweetText.split(" ").intersect(distinguishingWords)
		if(intersectionResult.length >= AuxiliaryDataBasedExperiment.minFpmWordsDetected)
			return true
		return false

	}
}


object FileBasedAuxiliaryDataRetriever
{

	var _auxiliaryFileName = ""

	var _auxiliaryTweets:RDD[Tweet] = null

	def readTweetsFromAuxiliaryFile() : RDD[Tweet] = {

	if(_auxiliaryTweets != null)
		return _auxiliaryTweets

	val sc = SparkContextManager.getContext
	val delimiter = AuxiliaryDataBasedExperiment.fileDelimiter
	var auxiliaryFileContent = sc.textFile(_auxiliaryFileName).map(l => l.split(delimiter))
	auxiliaryFileContent = auxiliaryFileContent.map(stringArrays => {
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
				Tweet(counter.toString, tweetText, label.toDouble)
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
	def cleanSampleHtml(sample: Tweet) = sample copy (tweetText = cleanHtml(sample.tweetText))
	// Words only
	def cleanWord(str: String) = str.split(" ").map(_.trim.toLowerCase).filter(_.size > 0).map(_.replaceAll("\\W", "")).reduce((x, y) => s"$x $y")
	def wordOnlySample(sample: Tweet) = sample copy (tweetText = cleanWord(sample.tweetText))

	val trainSamples = auxiliaryFileContent map toTweet
	val cleanTrainSamples = trainSamples map cleanSampleHtml
	//FileBasedAuxiliaryDataRetriever.lastLineRead += counter
	_auxiliaryTweets = CleanTweet.clean(cleanTrainSamples, SparkContextManager.getContext)

	_auxiliaryTweets
}

	def readTweetsFromFile(filename:String) : RDD[Tweet] = {

		val sc = SparkContextManager.getContext
		val delimiter = AuxiliaryDataBasedExperiment.fileDelimiter
		var auxiliaryFileContent = sc.textFile(filename).map(l => l.split(delimiter))
		auxiliaryFileContent = auxiliaryFileContent.map(stringArrays => {
			if(stringArrays.length > 2){
				val resultArray = new Array[String](2)
				resultArray(0) = stringArrays(0)
				resultArray(1) = stringArrays(1) + stringArrays(2)
				resultArray
			}
			else
				stringArrays
		})

		def toTweet(segments: Array[String]) = segments match {

			case Array(label, tweetText) =>
				try {
					Tweet(java.util.UUID.randomUUID.toString, tweetText, label.toDouble)
				}
				catch
					{
						case unknown => println(s"Issue with Tweet:${segments}")
							Tweet(java.util.UUID.randomUUID.toString, "", 0.0)
					}
			case _ =>
			{
				println(s"Issue with Tweet:${segments.array.deep.mkString(" ")}")
				Tweet(java.util.UUID.randomUUID.toString, "hello", 0.0)
			}
		}

		def cleanHtml(str: String) = str.replaceAll( """<(?!\/?a(?=>|\s.*>))\/?.*?>""", "")
		def cleanSampleHtml(sample: Tweet) = sample copy (tweetText = cleanHtml(sample.tweetText))
		// Words only
		def cleanWord(str: String) = str.split(" ").map(_.trim.toLowerCase).filter(_.size > 0).map(_.replaceAll("\\W", "")).reduce((x, y) => s"$x $y")
		def wordOnlySample(sample: Tweet) = sample copy (tweetText = cleanWord(sample.tweetText))

		val trainSamples = auxiliaryFileContent map toTweet
		val cleanTrainSamples = trainSamples map cleanSampleHtml
		val returnedTweets = CleanTweet.clean(cleanTrainSamples, SparkContextManager.getContext)
		returnedTweets

	}

}