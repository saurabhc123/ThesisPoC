package main.scala

import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.mllib.linalg.Tweet
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object FPMDistinguishingWords {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("ThesisPoC")
    val sc = new SparkContext(conf)

    val data = sc.textFile("data/training/fpm.txt")

    val trainingFileContent = sc.textFile("data/training/multi_class_lem").map(l => l.split(';'))

    // To sample
    def toSample(segments: Array[String]) = segments match {
      case Array(label, tweetText) => Tweet(java.util.UUID.randomUUID.toString , tweetText, Some(label.toDouble))
    }

    val trainSamples = trainingFileContent map toSample

    val classLabel = 4.0
    val filteredTweets = trainSamples.filter(x => x.label == Some(classLabel))

    val transactions: RDD[Array[String]] = filteredTweets.map(s => s.tweetText.trim.split(' ').distinct)

    val fpg = new FPGrowth()
      .setMinSupport(0.05)
      .setNumPartitions(10)
    val model = fpg.run(transactions)

    model.freqItemsets.collect().foreach { itemset =>
      println(itemset.items.mkString("[", ",", "]") + ", " + itemset.freq)
    }

    val minConfidence = 0.8
    model.generateAssociationRules(minConfidence).collect().foreach { rule =>
      println(
        rule.antecedent.mkString("[", ",", "]")
          + " => " + rule.consequent .mkString("[", ",", "]")
          + ", " + rule.confidence)
  }


  }

  def getUniqueWords(tweet: String):Iterable[String] = {

    val distinctWords = tweet.split(' ').distinct
    distinctWords
  }

}
