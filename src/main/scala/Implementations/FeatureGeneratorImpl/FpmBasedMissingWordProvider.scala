package Implementations.FeatureGeneratorImpl


import Interfaces.IReplacementWordProvider
import Utilities.CleanTweet
import main.DataTypes.Tweet
import main.SparkContextManager
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.rdd.RDD

import scala.collection.immutable.HashSet

/**
 * Created by saur6410 on 3/19/17.
 */
class FpmBasedMissingWordProvider extends IReplacementWordProvider{

	override def replaceWord(missingWord: String): String = {

		if(FpmBasedMissingWordProvider._frequentItemSets == null) {
			FpmBasedMissingWordProvider.Init()
		}
		FpmBasedMissingWordProvider.getReplacementWord(missingWord)
	}
}


object FpmBasedMissingWordProvider
{

	var _frequentItemSets: Array[(Array[String], Long)] = null

	def getReplacementWord(missingWord: String):String = {

		val set = _frequentItemSets.filter(fq => fq._1.contains(missingWord))
		if(set.length < 1)
			return ""
		else {
			val result = set.take(1)
			val returnValue = result(0)._1.filter(w => w != missingWord)
			val replacedValue = returnValue(returnValue.length - 1)
			println(s"Replacing $missingWord with $replacedValue. Choices ${result.map(s => s._1.deep.mkString("[", ",", "]")).deep.mkString}.")
			return replacedValue
		}
	}

	def Init() = {
		val sc = SparkContextManager.getContext

		val trainingFileContent = sc.textFile(AuxiliaryDataBasedExperiment.supplementedCleanAuxiliaryFile).map(l => l.split(','))

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

			//println(itemset.items.mkString("[", ",", "]") + ", " + itemset.freq)


		}
		val sortedValues = model.freqItemsets.collect().filter(f => f.items.length > 1).map(fq => (fq.items,fq.freq)).sortBy(p => -p._2)
		_frequentItemSets = sortedValues
	}



}


