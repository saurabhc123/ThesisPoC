package Implementations.DistinguishingWordsGenerator

import Interfaces.IDistinguishingWordsGenerator
import main.DataTypes.Tweet
import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/9/17.
 */
class FpmBasedDistinguishingWordsGenerator extends IDistinguishingWordsGenerator {
	override def generateMostDistinguishingWords(tweets: RDD[Tweet]): Array[String] = {
		val transactions: RDD[Array[String]] = tweets.map(s => s.text.trim.split(' ').distinct)

		val fpg = new FPGrowth()
			.setMinSupport(0.1)
			.setNumPartitions(10)
		val model = fpg.run(transactions)

		model.freqItemsets.collect().foreach { itemset =>
			println(itemset.items.mkString("[", ",", "]") + ", " + itemset.freq)
		}

		val minConfidence = 0.8
		model.generateAssociationRules(minConfidence).collect().foreach { rule =>
			println(
				rule.antecedent.mkString("[", ",", "]")
					+ " => " + rule.consequent.mkString("[", ",", "]")
					+ ", " + rule.confidence)


		}
		//ToDo: Get the top-k array of words
		null
	}
}
