package main.scala

import _root_.Factories.AuxiliaryDataRetrieverFactory
import org.apache.log4j.{Level, Logger}

/**
 * Created by saur6410 on 2/25/17.
 */
object program extends App {

	override def main(args: Array[String]) {


		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)
		println("Hello World!" + 11)
		//LrClassifier.main(args)
		//LrOnlineClassifier.main(args)
		//Word2VecGenerator.main(args)
		//FPMDistinguishingWords.main(args)
		//WebServiceClient.main(args)

		val distinguishingWords = Array("storm", "isaac", "hurricane")
		val auxDataRetriever = new AuxiliaryDataRetrieverFactory().getAuxiliaryDataRetriever()
		val firstSet = auxDataRetriever.retrieveAuxiliaryData(distinguishingWords)
		val secondSet = auxDataRetriever.retrieveAuxiliaryData(distinguishingWords)
	}
}


