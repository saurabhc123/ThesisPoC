package Implementations.AuxiliaryDataRetrievers

import Interfaces.IAuxiliaryDataFilter
import Utilities.CosineSimilarity
import main.DataTypes.Tweet
import main.Interfaces.IFeatureGenerator
import main.SparkContextManager
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/17/17.
 */
class CosineSimAuxiliaryFilter(trainingTweets: RDD[Tweet], featureGenerator:IFeatureGenerator) extends IAuxiliaryDataFilter {
	override def filter(auxiliaryTweets: RDD[Tweet]): RDD[Tweet] = {
		filterData(trainingTweets, auxiliaryTweets,featureGenerator)
	}

	def filterData(train:RDD[Tweet], auxiliary:RDD[Tweet], featureGenerator: IFeatureGenerator):RDD[Tweet] = {

		val sc = SparkContextManager.getContext

		val minSimilarityThreshold = AuxiliaryDataBasedExperiment.minSimilarityThreshold
		val trainTweetsFeatures = featureGenerator.generateFeatures(train)
		val auxiliaryTweetsFeatures = auxiliary.map(aux => (aux, featureGenerator.generateFeature(aux)))

		val auxArray = auxiliaryTweetsFeatures.collect()

		val auxTweetsSimilarities = auxArray.map(auxTweet => {
			val auxTweetSimilarity = trainTweetsFeatures.map(tr =>
			{
				val cos_sim = CosineSimilarity.cosineSimilarity(tr.features.toArray, auxTweet._2.features.toArray)
				cos_sim
			})
			val maxSimilarity = auxTweetSimilarity.max()
			//println(s"$maxSimilarity|${auxTweet._1.tweetText}")
			(auxTweet,maxSimilarity)
		})

		auxTweetsSimilarities.foreach(auxTweet => {
			if(auxTweet._2 > minSimilarityThreshold)
			{
				auxTweet._1.x._1.label = 1.0
			}
			else{
				auxTweet._1.x._1.label = 0.0
			}

		}
		)

		sc.parallelize(auxTweetsSimilarities.map(t => t._1.x._1).toSeq)
	}
}
