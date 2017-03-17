package Implementations.AuxiliaryDataRetrievers

import Factories.DistinguishingWordsGeneratorFactory
import Interfaces.{IDistinguishingWordsGenerator, IAuxiliaryDataFilter}
import main.DataTypes.Tweet
import main.SparkContextManager
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/17/17.
 */
class FpmAuxiliaryFilter(trainingTweets: RDD[Tweet]) extends IAuxiliaryDataFilter {


	override def filter(auxiliaryData: RDD[Tweet]): RDD[Tweet] = {

		val distinguishingWordGenerator: IDistinguishingWordsGenerator =
			new DistinguishingWordsGeneratorFactory().getDistinguishingWordsGenerator()
		val distinguishingWords = distinguishingWordGenerator.generateMostDistinguishingWords(trainingTweets.filter(t => t.label == 1.0))
		retrieveAuxiliaryData(distinguishingWords, auxiliaryData)

	}

	def retrieveAuxiliaryData(distinguishingWords: Array[String], auxiliaryData: RDD[Tweet]): RDD[Tweet] = {

		val sc = SparkContextManager.getContext
		//Read the tweets from the file one by one.
		val auxiliaryTweets = auxiliaryData

		//Update the cursor for each step.
		val tweetsContainingRelevantWords = auxiliaryTweets.filter(x => doesTweetContainsDistinguishingWords(x.tweetText, distinguishingWords)
			&& x.identifier.toInt > FpmAuxiliaryFilter.lastLineRead)

		val tweetsCount = tweetsContainingRelevantWords.count()

		if(tweetsCount == 0)
		{
			throw new Exception("No more auxiliary tweets to retrieve.")
		}

		var auxiliaryMatches:Array[Tweet] = null
		//Once the required number of tweets are retrieved, get the line number of the last tweet. Save it
		if(tweetsCount < FpmAuxiliaryFilter.numberOfTweetsToRetrieve)
		{
			auxiliaryMatches = tweetsContainingRelevantWords.take(tweetsCount.toInt)
		}
		else
		{
			auxiliaryMatches = tweetsContainingRelevantWords.take(FpmAuxiliaryFilter.numberOfTweetsToRetrieve)
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

object FpmAuxiliaryFilter
{
	var lastLineRead = 0
	val numberOfTweetsToRetrieve = 10

}
