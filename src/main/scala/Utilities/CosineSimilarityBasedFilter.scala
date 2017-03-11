package Utilities


import main.DataTypes.Tweet
import main.Interfaces.IFeatureGenerator
import org.apache.spark.rdd.RDD

class CosineSimilarityBasedFilter {

	val minSimilarityThreshold = 0.5

	def filter(train:RDD[Tweet], auxiliary:RDD[Tweet], featureGenerator: IFeatureGenerator):RDD[Tweet] = {

		val trainTweetsVectors = featureGenerator.generateFeatures(train)
		val auxiliaryTweetsSimilarity = auxiliary.map(aux => (aux, featureGenerator.generateFeature(aux)))
			.map(auxTweetVectorTuple => (auxTweetVectorTuple._1 ,trainTweetsVectors.map(t =>  CosineSimilarity.cosineSimilarity(auxTweetVectorTuple._2.features.toArray, t.features.toArray))) )
		val filteredTweets = auxiliaryTweetsSimilarity.map(aux => (aux._1, aux._2.max())).filter(auxTweet => auxTweet._2 > minSimilarityThreshold)
			.map(_._1)

		filteredTweets
	}
}