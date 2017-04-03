package Utilities

import Implementations.AuxiliaryDataRetrievers.FileBasedAuxiliaryDataRetriever
import main.SparkContextManager
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.log4j.{Level, Logger}

/**
 * Created by saur6410 on 3/12/17.
 */
object ScratchPad extends App {

	override def main(args: Array[String]) {
		{

			Logger.getLogger("org").setLevel(Level.OFF)
			Logger.getLogger("akka").setLevel(Level.OFF)
			Logger.getLogger("logreg").setLevel(Level.OFF)
			val sc = SparkContextManager.getContext
			sc.setLogLevel("ERROR")

			AuxiliaryDataBasedExperiment.experimentSet = "winterstorm"
			AuxiliaryDataBasedExperiment.tweetsToAddEachIteration = 40
			val auxiliaryDataFile = s"data/final/${AuxiliaryDataBasedExperiment.experimentSet}_auxiliary_data.txt"
			FileBasedAuxiliaryDataRetriever._auxiliaryFileName = auxiliaryDataFile
			print(auxiliaryDataFile)
			var totalCount:Long = 0
			print(s"Initial Tweet Count:${FileBasedAuxiliaryDataRetriever.readTweetsFromAuxiliaryFile().count()}")
			while(true) {
				val sourceAuxiliaryData = new FileBasedAuxiliaryDataRetriever(auxiliaryDataFile).retrieveAuxiliaryData(new Array[String](0))
				println(s"Retrieved ${sourceAuxiliaryData.count()} new auxiliary tweets.")
				totalCount = totalCount + sourceAuxiliaryData.count()
				print(s"TotalCount:$totalCount")
				//print(sourceAuxiliaryData.foreach(tweet => println(s"${tweet.label}|${tweet.tweetText}")))
			}

			//		val sc = SparkContextManager.getContext
			//		val bookpair = Array(1,2,3,4,5)
			//		val bookpairRdd = sc.parallelize(bookpair)
			//		val readerbook = Array(6,7,8,9)
			//		val readerRdd = sc.parallelize(readerbook).map(x => x)
			//		val readerRddBc = sc.broadcast(readerRdd)
			//		val bookpairBc = sc.broadcast(bookpairRdd)
			//		val joinedRdd = readerRdd.map(r => bookpairBc.value.map(b => println(b*r)) )
			//		joinedRdd.collect()
			//		joinedRdd.foreach(x => println(x.collect()))

			//val wmDistanceInstance = new Wmdistance()
			//wmDistanceInstance.getSimilarities("Hello World there", "Hello World")
		}

	}
}
