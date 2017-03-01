package org.apache.spark.mllib.linalg

import org.apache.spark.mllib.feature.Word2Vec
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.Try

/**
 * Created by saur6410 on 2/26/17.
 */

case class Tweet(id: String, tweetText: String, label: Option[Double] = None)

object Word2VecGenerator {

  def main(args: Array[String]): Unit = {

    val trainingFilename = "data/training/multi_class_lem"
    val exportedVectors = "data/training/fire.vec"
    val testFilename = "data/training/generated.vec"

    val conf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("ThesisPoC")
    val sc = new SparkContext(conf)


    // To sample
    def toSample(segments: Array[String]) = segments match {
      case Array(label, tweetText) => Tweet(java.util.UUID.randomUUID.toString , tweetText, Some(label.toDouble))
    }

    val delimiter = ";"
    val trainingFileContent = sc.textFile(trainingFilename).map(l => l.split(delimiter))

    val trainSamples = trainingFileContent map toSample


    def cleanHtml(str: String) = str.replaceAll( """<(?!\/?a(?=>|\s.*>))\/?.*?>""", "")

    def cleanSampleHtml(sample: Tweet) = sample copy (tweetText = cleanHtml(sample.tweetText))

    val cleanTrainSamples = trainSamples map cleanSampleHtml

    // Words only
    def cleanWord(str: String) = str.split(" ").map(_.trim.toLowerCase).filter(_.size > 0).map(_.replaceAll("\\W", "")).reduce((x, y) => s"$x $y")

    def wordOnlySample(sample: Tweet) = sample copy (tweetText = cleanWord(sample.tweetText))

    val wordOnlyTrainSample = cleanTrainSamples map wordOnlySample

    // Word2Vec
    val samplePairs = wordOnlyTrainSample.map(s => s.id -> s).cache()
    val reviewWordsPairs: RDD[(String, Iterable[String])] = samplePairs.mapValues(_.tweetText.split(" ").toIterable)
    println("Start Training Word2Vec --->")
    val word2vecModel = new Word2Vec().fit(reviewWordsPairs.values)


    println("Finished Training")
    println(word2vecModel.transform("hurricane"))
    println(word2vecModel.findSynonyms("shooting", 4))


    val classLabel = 1.0
    var filteredTweets = samplePairs.filter(x => x._2.label == Some(classLabel))
    val reviewWordsPairs1: RDD[(String, Iterable[String])] = filteredTweets.mapValues(_.tweetText.split(" ").toIterable)


    def wordFeatures(words: Iterable[String]): Iterable[Vector] = words.map(w => Try(word2vecModel.transform(w))).filter(_.isSuccess).map(x => x.get)

    def avgWordFeatures(wordFeatures: Iterable[Vector]): Vector = Vectors.fromBreeze(wordFeatures.map(_.toBreeze).reduceLeft((x,y) => x + y) / wordFeatures.size.toDouble)

    // Create feature vectors
    val wordFeaturePair = reviewWordsPairs1 mapValues wordFeatures
    val intermediateVectors = wordFeaturePair.mapValues(x => x.map(_.toBreeze))
    //val nonNullValues = intermediateVectors.map(x => (!x._2.isEmpty, x) ).filter(_._1).map(v => v)
    val inter2 = wordFeaturePair.filter(!_._2.isEmpty)
    val avgWordFeaturesPair = inter2 mapValues avgWordFeatures
    //inter 2 has 30 tweets. Each tweet has words, and each word is a [1 x 100] dimension vector.
    val avgWordFeaturesPair1 = inter2.map(x => (x._2.map(_.toBreeze).reduceLeft((a,b) => (a+b)/x._2.size.toDouble)))
    val avgWordFeaturesPair2 = avgWordFeaturesPair1.map(x => Vectors.fromBreeze(x))
    //avgWordFeaturesPair1 contains 30 tweets, with each tweet is a [1x100] vector
    val featuresPair = avgWordFeaturesPair join samplePairs mapValues {
      case (features, Tweet(id, review, label)) => LabeledPoint(label.get, features)
    }
    val trainingSet = featuresPair.values

    trainingSet.collect()
    trainingSet.coalesce(1).saveAsTextFile(exportedVectors)



  }

}
