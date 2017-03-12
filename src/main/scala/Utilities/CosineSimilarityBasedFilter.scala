package Utilities


import main.DataTypes.Tweet
import main.Interfaces.IFeatureGenerator
import main.SparkContextManager
import org.apache.spark.rdd.RDD

class CosineSimilarityBasedFilter {



	def filter(train:RDD[Tweet], auxiliary:RDD[Tweet], featureGenerator: IFeatureGenerator):RDD[Tweet] = {

		val sc = SparkContextManager.getContext

		val minSimilarityThreshold = 0.5
		val trainTweetsFeatures = featureGenerator.generateFeatures(train)
		val auxiliaryTweetsFeatures = auxiliary.map(aux => (aux, featureGenerator.generateFeature(aux)))
		trainTweetsFeatures.cache()
		auxiliaryTweetsFeatures.cache()

		val bcTrain = sc.broadcast(trainTweetsFeatures)

		val auxSimilarityArray = new Array[Double](auxiliaryTweetsFeatures.count().toInt)
		auxiliaryTweetsFeatures.foreach(auxTweet => {
			val auxTweetSimilarity = bcTrain.value.map(tr =>
				{
					CosineSimilarity.cosineSimilarity(tr.features.toArray, auxTweet._2.features.toArray)
				})
			val maxSimilarity = auxTweetSimilarity.max()
			auxSimilarityArray(auxTweet._1.identifier.toInt) = maxSimilarity
			//return maxSimilarity
		})

		val auxTweetsSimilarities = auxiliaryTweetsFeatures.map(auxTweet => (auxTweet, auxSimilarityArray(auxTweet._1.identifier.toInt)))

		//val auxiliaryTweetsSimilarity =	auxiliaryTweetsFeatures.map(auxTweetVectorTuple => (auxTweetVectorTuple._1 ,trainTweetsFeatures.map(t =>  CosineSimilarity.cosineSimilarity(auxTweetVectorTuple._2.features.toArray, t.features.toArray))) )
		val filteredTweets = auxTweetsSimilarities.filter(auxTweet => auxTweet._2 > minSimilarityThreshold)
			.map(_._1)

		filteredTweets.map(t => t._1)
	}
}