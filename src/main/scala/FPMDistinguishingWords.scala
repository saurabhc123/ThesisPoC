package main.scala

import Utilities.CleanTweet
import main.DataTypes.Tweet
import main.SparkContextManager
import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.rdd.RDD

import scala.collection.immutable.HashSet

object FPMDistinguishingWords {

	def main(args: Array[String]): Unit = {


		val sc = SparkContextManager.getContext

		//val data = sc.textFile("data/training/fpm.txt")

		val trainingFileContent = sc.textFile("data/final/egypt_training_data.txt").map(l => l.split(','))

		// To sample
		def toSample(segments: Array[String]) = segments match {
			case Array(label, tweetText) => Tweet(java.util.UUID.randomUUID.toString , tweetText, label.toDouble)
			case _ => Tweet("0"," ", 0.0)
		}

		val trainSamples = trainingFileContent map toSample

		val classLabel = 1.0
		val filteredTweets =  CleanTweet.clean(trainSamples, sc)//.filter(x => x.label == Some(classLabel))

		val transactions: RDD[Array[String]] = filteredTweets.map(s => s.tweetText.trim.split(' ').distinct)

		val fpg = new FPGrowth()
			.setMinSupport(0.1)
			.setNumPartitions(10)
		val model = fpg.run(transactions)

		var frequentWords =  new HashSet[String]

		model.freqItemsets.collect().foreach { itemset =>

			itemset.items.foreach(word => {
				if(!frequentWords.contains(word))
					{
					frequentWords = frequentWords.+(word)
					println(word)
					}
			}
						)

			println(itemset.items.mkString("[", ",", "]") + ", " + itemset.freq)
		}

		println()

		val minConfidence = 0.8
		model.generateAssociationRules(minConfidence).collect().foreach { rule =>
			println(
				rule.antecedent.mkString("[", ",", "]")
					+ " => " + rule.consequent .mkString("[", ",", "]")
					+ ", " + rule.confidence)
		}

		frequentWords.foreach(println)


	}

	def getUniqueWords(tweet: String):Iterable[String] = {

		val distinctWords = tweet.split(' ').distinct
		distinctWords
	}

}
