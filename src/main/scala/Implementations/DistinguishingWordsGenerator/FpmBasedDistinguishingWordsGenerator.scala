package Implementations.DistinguishingWordsGenerator

import Interfaces.IDistinguishingWordsGenerator
import main.DataTypes.Tweet
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.rdd.RDD

import scala.collection.immutable.HashSet

/**
 * Created by saur6410 on 3/9/17.
 */
class FpmBasedDistinguishingWordsGenerator extends IDistinguishingWordsGenerator {
	override def generateMostDistinguishingWords(tweets: RDD[Tweet]): Array[String] = {
		val transactions: RDD[Array[String]] = tweets.map(s => s.tweetText.trim.split(' ').distinct)

		val fpg = new FPGrowth()
			.setMinSupport(0.01)
			.setNumPartitions(10)
		val model = fpg.run(transactions)
		val _model = model.freqItemsets
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
			//println(itemset.items.mkString("[", ",", "]") + ", " + itemset.freq)
		}

		frequentWords.take(AuxiliaryDataBasedExperiment.maxFpmWordsToPick).toArray
	}
}

object FpmBasedDistinguishingWordsGenerator
{
	//val _model:FreqItemset[Item]
}
