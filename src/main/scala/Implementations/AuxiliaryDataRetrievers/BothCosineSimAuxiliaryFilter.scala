package Implementations.AuxiliaryDataRetrievers

import Interfaces.IAuxiliaryDataFilter
import Utilities.CosineSimilarity
import main.DataTypes.Tweet
import main.Interfaces.IFeatureGenerator
import main.SparkContextManager
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.spark.rdd.RDD

/**
  * Created by ericrw96 on 3/24/17.
  */
class BothCosineSimAuxiliaryFilter(trainingTweets: RDD[Tweet], featureGenerator:IFeatureGenerator) extends IAuxiliaryDataFilter {
  override def filter(auxiliaryTweets: RDD[Tweet]): RDD[Tweet] = {
    filterData(trainingTweets, auxiliaryTweets,featureGenerator)
  }

  def filterData(train:RDD[Tweet], auxiliary:RDD[Tweet], featureGenerator: IFeatureGenerator):RDD[Tweet] = {

    val sc = SparkContextManager.getContext

    val minSimilarityThreshold = AuxiliaryDataBasedExperiment.minSimilarityThreshold
    //This is all the positve and negative training data.
    val trainTweetsFeatures = featureGenerator.generateFeatures(train)//.filter(trainingTweet => trainingTweet.label == 1.0)
    val positiveFeatures = trainTweetsFeatures.filter(lp => lp.label == 1.0)
    val negativeFeatures = trainTweetsFeatures.filter(lp => lp.label == 0.0)
    val auxiliaryTweetsFeatures = auxiliary.map(aux => (aux, featureGenerator.generateFeature(aux)))

    val auxArray = auxiliaryTweetsFeatures.collect()

    val auxTweetsSimilarities = auxArray.map(auxTweet => {
      val auxTweetSimilarityPos = positiveFeatures.map(tr =>
      {
        val cos_sim = CosineSimilarity.cosineSimilarity(tr.features.toArray, auxTweet._2.features.toArray)
        cos_sim
      })
      val auxTweetSimilarityNeg = negativeFeatures.map(tr =>
      {
        val cos_sim = CosineSimilarity.cosineSimilarity(tr.features.toArray, auxTweet._2.features.toArray)
        cos_sim
      })
      val maxSimilarityPos = auxTweetSimilarityPos.max()//mean()
      val maxSimilarityNeg = auxTweetSimilarityNeg.max()//mean()
      println(s"$maxSimilarityPos|$maxSimilarityNeg|${auxTweet._1.tweetText}")
      (auxTweet,maxSimilarityPos, maxSimilarityNeg)
    })
    auxTweetsSimilarities.foreach(auxTweet => {
      if(auxTweet._2 > minSimilarityThreshold && auxTweet._3 > minSimilarityThreshold)
      {
          auxTweet._1._1.label = -1.0
       }
      else if (auxTweet._2 > minSimilarityThreshold)
      {
        auxTweet._1.x._1.label = 1.0
      }
      else if(auxTweet._2 > minSimilarityThreshold - AuxiliaryDataBasedExperiment.cosineSimilarityWindowSize)
      {
        auxTweet._1.x._1.label = -1.0
      }
      else{
        auxTweet._1.x._1.label = 0.0
      }

    }
    )



    sc.parallelize(auxTweetsSimilarities.map(t => t._1.x._1).filter(t => t.label != -1.0).toSeq)
  }

}
