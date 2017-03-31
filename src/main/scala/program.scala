package main.scala

import java.time.{ZonedDateTime, ZoneOffset}

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

		val istOffset = ZoneOffset.ofHoursMinutesSeconds(-4, 0, 0)

		// time representation in EST
		val zonedDateTimeIst = ZonedDateTime.now(istOffset)

		println(s"Starting experiment at $zonedDateTimeIst")
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

		new AuxiliaryDataBasedExperiment(args).SetupAndRunExperiment()
		//GenerateCleanTweetStrings.GenerateCleanStrings("data/final/ebola_auxiliary_data.txt")
		println(s"Ending experiment at $zonedDateTimeIst")

	}
}


