package Utilities

import Implementations.AuxiliaryDataRetrievers.FileBasedAuxiliaryDataRetriever
import main.DataTypes.Tweet
import main.Interfaces.IFeatureGenerator
import main.SparkContextManager
import main.scala.Factories.{FeatureGeneratorFactory, FeatureGeneratorType}
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/29/17.
 */
object GenerateCosineSimStatistics extends App {

	override def main(args: Array[String]): Unit = {

		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)
		val sc = SparkContextManager.getContext
		sc.setLogLevel("ERROR")
		AuxiliaryDataBasedExperiment.experimentSet = "greece"
		AuxiliaryDataBasedExperiment.vectorType = s"local"
		GetData(0)
//		GetData(1)
//		GetData(2)
//		GetData(3)
//		Reset()
//		val (threshold1, wallSize1) = getCosineSimParametersForTweets(tweets)
//		print(threshold1,wallSize1)


	}

	def GetData(experimentSetNumber:Int): Unit = {

		if(experimentSetNumber == 0)
			{
				AuxiliaryDataBasedExperiment.experimentSetNumber == ""
			}
		else
			{
				AuxiliaryDataBasedExperiment.experimentSetNumber = experimentSetNumber.toString
			}
		val filename = s"data/final/${AuxiliaryDataBasedExperiment.experimentSet}_training_data${AuxiliaryDataBasedExperiment.experimentSetNumber}.txt"
		FileBasedAuxiliaryDataRetriever._auxiliaryFileName = filename
		Reset()
		AuxiliaryDataBasedExperiment.setVectorType(AuxiliaryDataBasedExperiment.vectorType)
		val tweets = FileBasedAuxiliaryDataRetriever.readTweetsFromFile(filename)
		var (threshold, wallSize) = getCosineSimParametersForTweets(tweets)
		print(threshold, wallSize)
	}

	def Reset() = {
		val uri = s"http://localhost:5000/cnn_reset/${AuxiliaryDataBasedExperiment.experimentSet}"
		val result = scala.io.Source.fromURL(uri).mkString
		print(result)
	}

	def getCosineSimParametersForTweets(tweets:RDD[Tweet]): (Double, Double) = {
		val featureGenerator =  FeatureGeneratorFactory.getFeatureGenerator(FeatureGeneratorType.WebServiceWord2Vec)
		val (threshold, wallSize) = getCosineSimParameters(tweets.map(tweet => (tweet,featureGenerator.generateFeature(tweet))))
		(threshold, wallSize)
	}

	def getCosineSimParametersForTweets(tweets:RDD[Tweet], featureGenerator:IFeatureGenerator): (Double, Double) = {
		val (threshold, wallSize) = getCosineSimParameters(tweets.map(tweet => (tweet,featureGenerator.generateFeature(tweet))))
		(threshold, wallSize)
	}


	def getCosineSimParameters(tweetFeatureVectorTuple: RDD[(Tweet,LabeledPoint)]) : (Double, Double) = {
		
		var threshold = 0.0
		var wallSize = 0.0
		
		//Get the positive tweets
		val positiveTweets = tweetFeatureVectorTuple.filter(tweetFeatureVectorElement => tweetFeatureVectorElement._1.label == 1.0)
		val positiveTweetsArray = positiveTweets.collect()
		
		//Get the negative tweets
		val negativeTweets = tweetFeatureVectorTuple.filter(tweetFeatureVectorElement => tweetFeatureVectorElement._1.label == 0.0)
		
		//Calculate the average cosine similarities of the positive tweets with the positive ones
		val posposCosineSimilarities = positiveTweetsArray.map(auxTweet => {
			val auxTweetSimilarity = positiveTweets.map(tr =>
			{
				val cos_sim = CosineSimilarity.cosineSimilarity(tr._2.features.toArray, auxTweet._2.features.toArray)
				cos_sim
			})
			var sim = auxTweetSimilarity.mean()
			if(sim.equals(Double.NaN))
				sim = 0.0
			val meanSimilarity =  BigDecimal(sim).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
			println(s"*** Positive Tweet *** $meanSimilarity|${auxTweet._1.tweetText}")
			(auxTweet._1.tweetText, meanSimilarity)
		})

		//Calculate the average cosine similarities of the negative tweets with the positive ones
		val posnegCosineSimilarities = positiveTweetsArray.map(auxTweet => {
			var negativeTweetText = ""
			val auxTweetSimilarity = negativeTweets.map(tr =>
			{
				val cos_sim = CosineSimilarity.cosineSimilarity(tr._2.features.toArray, auxTweet._2.features.toArray)
				negativeTweetText = tr._1.tweetText
				//println(s"*** Negative Tweet *** $negativeTweetText")
				cos_sim
			})
			var sim = auxTweetSimilarity.mean()
			if(sim.equals(Double.NaN))
				sim = 0.0
			val meanSimilarity =  BigDecimal(sim).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
			println(s"*** Negative Tweet Similarity*** $meanSimilarity")
			(auxTweet._1.tweetText, meanSimilarity)
		})

		threshold = posposCosineSimilarities.map(_._2).sum/posposCosineSimilarities.length
		val negCosineSimilarityMean = posnegCosineSimilarities.map(_._2).sum/posnegCosineSimilarities.length
		wallSize = threshold - negCosineSimilarityMean

		(threshold,wallSize)
		
	}



}
