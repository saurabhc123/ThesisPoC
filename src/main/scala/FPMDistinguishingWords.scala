package main.scala

import Utilities.CleanTweet
import main.DataTypes.Tweet
import main.SparkContextManager
import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.rdd.RDD

import scala.collection.immutable.HashSet

object FPMDistinguishingWords {

	var _frequentItemSets: Array[(Array[String], Long)] = null

	def findNearestWord(word: String): Unit = {

	}

	def main(args: Array[String]): Unit = {


		val sc = SparkContextManager.getContext

		val trainingFileContent = sc.textFile("data/final/egypt_auxiliary_data_clean.txt").map(l => l.split(','))

		// To sample
		def toSample(segments: Array[String]) = segments match {
			case Array(label, tweetText) => Tweet(java.util.UUID.randomUUID.toString , tweetText, label.toDouble)
			case _ => Tweet("0",segments(0), 0.0)
		}

		val trainSamples = trainingFileContent map toSample

		val classLabel = 1.0
		val filteredTweets =  CleanTweet.clean(trainSamples, sc)//.filter(x => x.label == Some(classLabel))

		val transactions: RDD[Array[String]] = filteredTweets.map(s => s.tweetText.trim.split(' ').distinct)

		val fpg = new FPGrowth()
			.setMinSupport(0.01)
			.setNumPartitions(10)
		val model = fpg.run(transactions)

		var frequentWords =  new HashSet[String]

		model.freqItemsets.collect().foreach { itemset =>

			itemset.items.foreach(word => {
				if(!frequentWords.contains(word))
					{
					frequentWords = frequentWords.+(word)
					//println(word)
					}
			}
						)

			println(itemset.items.mkString("[", ",", "]") + ", " + itemset.freq)


		}
		val sortedValues = model.freqItemsets.collect().filter(f => f.items.length > 1).map(fq => (fq.items,fq.freq)).sortBy(p => -p._2)
		_frequentItemSets = sortedValues




		println(getReplacementWord("fukushima"))

//		val minConfidence = 0.8
//		model.generateAssociationRules(minConfidence).collect().foreach { rule =>
//			println(
//				rule.antecedent.mkString("[", ",", "]")
//					+ " => " + rule.consequent .mkString("[", ",", "]")
//					+ ", " + rule.confidence)
//		}
//
//		frequentWords.foreach(println)


	}

	def getReplacementWord(word: String):String = {

		val set = _frequentItemSets.filter(fq => fq._1.contains(word))
		if(set.length < 1)
			return ""
		else {
			val result = set.take(1)
			val returnValue = result(0)._1.filter(w => w != word)
			println(s"Replacing $word with ${returnValue(returnValue.length - 1)}. Choices ${result.map(s => s._1.deep.mkString("[", ",", "]")).deep.mkString}")
			return returnValue(returnValue.length - 1)
		}
	}

}
