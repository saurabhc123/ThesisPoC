package Implementations.FeatureGeneratorImpl

import Interfaces.IMissingWordFeatureProcessor
import main.DataTypes.Tweet
import main.scala.Factories.{FeatureGeneratorFactory, FeatureGeneratorType}
import org.apache.spark.rdd.RDD

import scala.collection.immutable.{HashMap, HashSet}

/**
 * Created by saur6410 on 3/19/17.
 */
class MissingWordFeatureProcessor extends IMissingWordFeatureProcessor{
	override def replaceMissingWords(tweets: RDD[Tweet]): RDD[Tweet] = ???

	override def replaceMissingWords(tweet: Tweet): Tweet = {
		val replacedText = tweet.tweetText.split(" ").map(word => MissingWordFeatureProcessor.getWord(word)).mkString(" ")

		Tweet(tweet.identifier,replacedText,tweet.label)
		
	}
}

object MissingWordFeatureProcessor
{
	var orphanedWords = new HashMap[String, String]
	var presentWords = new HashSet[String]
	val replacementWordProvider = new FpmBasedMissingWordProvider //new Word2VecBasedMissingWordProvider

	def getMissingWord(missingWord: String): String = {

		//Check whether the word exists in the orphaned words dictionary
		if(orphanedWords.contains(missingWord)) {
				//If it exists, return it.
				return orphanedWords(missingWord)
			}

		//Else find the nearest word that matches that.
		val replacementWord = replacementWordProvider.replaceWord(missingWord)
		orphanedWords += (missingWord -> replacementWord)
		replacementWord

	}

	def getWord(word : String): String = {
		if (presentWords.contains(word)){
			return word
		}
		val featureGenerator = FeatureGeneratorFactory.getFeatureGenerator(FeatureGeneratorType.WebServiceWord2Vec)
		val point = featureGenerator.generateFeature(Tweet("temp",word,0))
		if(point.features.numNonzeros == 0){
			return getMissingWord(word)
		}
		presentWords += word
		word


	}

}
