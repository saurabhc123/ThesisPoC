package main.scala

import main.SparkContextManager
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.log4j.{Level, Logger}

/**
 * Created by saur6410 on 2/25/17.
 */
object program extends App {

	override def main(args: Array[String]) {


		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)
		Logger.getLogger("logreg").setLevel(Level.OFF)
		val sc = SparkContextManager.getContext
		sc.setLogLevel("ERROR")

		println("Hello World!" + 11)
		//LrClassifier.main(args)
		//LrOnlineClassifier.main(args)
		//Word2VecGenerator.main(args)
		//FPMDistinguishingWords.main(args)
		//WebServiceClient.main(args)

		//Code to test distinguishing words
		/*val distinguishingWords = Array("storm", "isaac", "hurricane")
		val auxDataRetriever = new AuxiliaryDataRetrieverFactory().getAuxiliaryDataRetriever("The auxiliaryfilename here")
		val firstSet = auxDataRetriever.retrieveAuxiliaryData(distinguishingWords)
		val secondSet = auxDataRetriever.retrieveAuxiliaryData(distinguishingWords)*/

		//ScratchPad.Scratch()
//		val classifier = new CnnClassifier()
//		val model = classifier.train(null)
//		val predictions = model.predict(null)

		new AuxiliaryDataBasedExperiment().SetupAndRunExperiment()
		//GenerateCleanTweetStrings.GenerateCleanStrings("data/final/ebola_auxiliary_data.txt")

	}
}


