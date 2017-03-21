package Implementations.FeatureGeneratorImpl

import Interfaces.IMissingWordFeatureProcessor
import main.DataTypes.Tweet
import main.scala.Factories.{FeatureGeneratorFactory, FeatureGeneratorType}
import org.apache.spark.rdd.RDD

import scala.collection.immutable.HashMap

/**
 * Created by saur6410 on 3/19/17.
 */
class MissingWordFeatureProcessor extends IMissingWordFeatureProcessor{
	override def replaceMissingWords(tweets: RDD[Tweet]): RDD[Tweet] = ???

	override def replaceMissingWords(tweet: Tweet): Tweet = {

		val featureGenerator = FeatureGeneratorFactory.getFeatureGenerator(FeatureGeneratorType.WebServiceWord2Vec)
		var tweetText = tweet.tweetText
		val tweetWordFeatureTuple = tweet.tweetText.split(" ").map(word => (word,featureGenerator.generateFeature( Tweet(tweet.identifier, word,tweet.label))))
		//ToDo: Can be optimized further by checking a word against the dictionary and not getting its vectors
		val missingFeaturesWord = tweetWordFeatureTuple.filter(missingVectorWord => missingVectorWord._2.features.numNonzeros == 0 )

		//Replace the missing words with replacement words
		missingFeaturesWord.foreach(missingWord => tweetText = tweetText.replace(missingWord._1, MissingWordFeatureProcessor.getMissingWord(missingWord._1)))

		Tweet(tweet.identifier,tweetText,tweet.label)
		
	}
}

object MissingWordFeatureProcessor
{
	var orphanedWords = new HashMap[String, String]
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

}
