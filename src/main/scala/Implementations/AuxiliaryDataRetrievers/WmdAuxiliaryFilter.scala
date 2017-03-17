package Implementations.AuxiliaryDataRetrievers

import Interfaces.IAuxiliaryDataFilter
import main.DataTypes.Tweet
import main.Interfaces.IFeatureGenerator
import main.SparkContextManager
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/17/17.
 */
class WmdAuxiliaryFilter(trainingTweets: RDD[Tweet], featureGenerator:IFeatureGenerator) extends IAuxiliaryDataFilter {
	override def filter(auxiliaryTweets: RDD[Tweet]): RDD[Tweet] = {


		val sc = SparkContextManager.getContext

		val minDistanceThreshold = AuxiliaryDataBasedExperiment.minWmDistanceThreshold
		val auxiliaryTweetsFeatures = auxiliaryTweets.map(aux => (aux, featureGenerator.generateFeature(aux)))

		val auxArray = auxiliaryTweetsFeatures.collect()

		val auxTweetsWMDistances = auxArray.map(auxTweet => {
			val auxTweetTrainTweetsWMDistances = trainingTweets.map(tr =>
			{
				val wmDistance = new Wmdistance().getSimilarities(tr.tweetText, auxTweet._1.tweetText)
				wmDistance
			})
			val minDistance = auxTweetTrainTweetsWMDistances.min()
			println(auxTweet._1.tweetText,minDistance)
			(auxTweet,minDistance)
		})

		auxTweetsWMDistances.foreach(auxTweet => {
			if(auxTweet._2 < minDistanceThreshold)
			{
				auxTweet._1.x._1.label = 1.0
			}
			else{
				auxTweet._1.x._1.label = 0.0
			}

		}
		)

		sc.parallelize(auxTweetsWMDistances.map(t => t._1.x._1).toSeq)
	}
}
