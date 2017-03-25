package main.scala.Implementations

import java.util.NoSuchElementException

import Factories._
import Implementations.AuxiliaryDataRetrievers.FileBasedAuxiliaryDataRetriever
import Interfaces.IExperiment
import Utilities.{CleanTweet, MetricsCalculator, TweetsFileProcessor}
import main.DataTypes.Tweet
import main.Factories.ClassifierFactory
import main.Interfaces.{DataType, IAuxiliaryDataRetriever}
import main.SparkContextManager
import main.scala.Factories.{FeatureGeneratorFactory, FeatureGeneratorType}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD


/**
 * Created by saur6410 on 3/8/17.
 */
class AuxiliaryDataBasedExperiment extends IExperiment {


	override def performExperiment(train: RDD[Tweet], validation: RDD[Tweet]): Unit = {


		//********** FACTORY BASED INITIALIZATIONS ************
		//Get the classifier from the factory
		val classifierFactory = new ClassifierFactory()

		val classifier = classifierFactory.getClassifier(AuxiliaryDataBasedExperiment.classifierType)
		val featureGenerator = FeatureGeneratorFactory.getFeatureGenerator(FeatureGeneratorType.WebServiceWord2Vec)
		val auxiliaryDataRetriever: IAuxiliaryDataRetriever = new AuxiliaryDataRetrieverFactory().getAuxiliaryDataRetriever(AuxiliaryDataBasedExperiment.auxiliaryDataFile)

		val trainingFeatures: RDD[LabeledPoint] = featureGenerator.generateFeatures(train, DataType.TRAINING)

		//***** Do this only for the CNN classifier
		if(AuxiliaryDataBasedExperiment.classifierType == ClassifierType.Cnn){
			//Generate the tweets at the folder using a UUID
			val folderId = java.util.UUID.randomUUID.toString
			writeData(train,folderId)
			//Set the UUID as the REST parameter
			AuxiliaryDataBasedExperiment.folderNameForCnnClassifier = folderId
		}


		//Train the classifier
		var model = classifier.train(trainingFeatures)

		val validationFeatures: RDD[LabeledPoint] = featureGenerator.generateFeatures(validation, DataType.TEST)
		//Perform Validation and get score
		val predictions = model.predict(validationFeatures);
		val metricsCalculator = MetricsCalculator.GenerateClassifierMetrics(predictions)
		var f1 = metricsCalculator.macroF1
		val thresholdF1 = AuxiliaryDataBasedExperiment.thresholdF1
		val auxiliaryThresholdExpectation = AuxiliaryDataBasedExperiment.auxiliaryThresholdExpectation
		println(s"\nInitial F1=${BigDecimal(f1).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble}")
		println(s"Precision=${BigDecimal(metricsCalculator.multiClassMetrics.precision(1.0)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble}")
		println(s"Recall=${BigDecimal(metricsCalculator.multiClassMetrics.recall(1.0)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble}")
		println("Initial - ConfusionMatrix:")
		println(s"${metricsCalculator.confusionMatrix}\n")



		var dataToTrainOn = train
		var numberOfIterations = 0
		while (f1 < thresholdF1 && numberOfIterations < AuxiliaryDataBasedExperiment.maxExperimentIterations) {
			//Get tweets based on most distinguishing words with FPM
			val filterFactory = new AuxiliaryDataFilterFactory(dataToTrainOn, featureGenerator)
			val fpmFilter = filterFactory.getAuxiliaryDataFilter(FilterType.FpmFilter)

			val sourceAuxiliaryData = FileBasedAuxiliaryDataRetriever.readTweetsFromAuxiliaryFile()
			//Retrieve auxiliary data by using most distinguishing words
			val auxiliaryData = CleanTweet.clean(fpmFilter.filter(sourceAuxiliaryData), SparkContextManager.getContext)

			//Filter based on cosine similarity
			val positiveLabelTrainingData = dataToTrainOn.filter(trainingTweet => trainingTweet.label == 1.0)
			val filter = filterFactory.getAuxiliaryDataFilter(AuxiliaryDataBasedExperiment.filterToUse)
			val filteredAuxiliaryData = filter.filter(auxiliaryData)

			println(s"Retrieved ${filteredAuxiliaryData.count()} new auxiliary tweets.")
			print(filteredAuxiliaryData.foreach(tweet => println(s"${tweet.label}|${tweet.tweetText}")))

			//train using training + auxiliary data
			val fullData = dataToTrainOn.union(filteredAuxiliaryData)

			//***** Do this only for the CNN classifier
			if(AuxiliaryDataBasedExperiment.classifierType == ClassifierType.Cnn){
				//Generate the tweets at the folder using a UUID
				val folderId = java.util.UUID.randomUUID.toString
				writeData(fullData,folderId)
				//Set the UUID as the REST parameter
				AuxiliaryDataBasedExperiment.folderNameForCnnClassifier = folderId
			}

			model = classifier.train(featureGenerator.generateFeatures(fullData, DataType.TRAINING))

			//perform prediction on validation data
			val validationDataPredictions = model.predict(validationFeatures)
			val metrics = MetricsCalculator.GenerateClassifierMetrics(validationDataPredictions)
			val auxF1 = metrics.macroF1


			//if f1 is greater than aux threshold, add aux to the training data.
			if ((auxF1 - f1) > auxiliaryThresholdExpectation)
			{
				println(s"Adding ${filteredAuxiliaryData.count()} auxiliary tweets to the training data.")
				f1 = auxF1
				dataToTrainOn = fullData
			}
			numberOfIterations += 1
			println(s"\nAux F1 - Iteration-$numberOfIterations=${BigDecimal(auxF1).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble}")
			println(s"Precision=${BigDecimal(metrics.multiClassMetrics.precision(1.0)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble}")
			println(s"Recall=${BigDecimal(metrics.multiClassMetrics.recall(1.0)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble}")

			println("Aux F1 - ConfusionMatrix:")
			println(s"${metrics.confusionMatrix}\n")
		}
	}

	def writeData(tweets: RDD[Tweet],filename:String) = {
		tweets.map(tweet => tweet.label.toInt + "," + tweet.tweetText) .coalesce(1).saveAsTextFile("data/python/"+filename)

	}

	override def SetupAndRunExperiment(): Unit = {
		println(AuxiliaryDataBasedExperiment.toString)
		val trainingTweets = TweetsFileProcessor.LoadTweetsFromFile(AuxiliaryDataBasedExperiment.trainingDataFile, AuxiliaryDataBasedExperiment.fileDelimiter)
		val validationTweets = TweetsFileProcessor.LoadTweetsFromFile(AuxiliaryDataBasedExperiment.validationDataFile, AuxiliaryDataBasedExperiment.fileDelimiter)
		val cleanTrainingTweets = CleanTweet.clean(trainingTweets, SparkContextManager.getContext)
		val cleanValidationTweets = CleanTweet.clean(validationTweets, SparkContextManager.getContext)

//		writeData(cleanTrainingTweets,java.util.UUID.randomUUID.toString)
//		writeData(cleanValidationTweets,"egypt_validation_data.txt")
//		return

		try {
			this.performExperiment(cleanTrainingTweets, cleanValidationTweets)
		}
		catch {
			case  nes : NoSuchElementException => println(nes.getMessage)
			case uk : UnknownError => println(uk.getMessage)

		}

	}
}

object AuxiliaryDataBasedExperiment {
	val filterToUse = FilterType.CosineSim
	val minSimilarityThreshold = 0.30
	val cosineSimilarityWindowSize= 0.10
	val minWmDistanceThreshold = 0.0199

	val maxFpmWordsToPick = 35
	val minFpmWordsDetected = 0
	val refreshLocalWordVectors = false

	val maxExperimentIterations = 20
	val tweetsToAddEachIteration = 10

	val thresholdF1 = 0.98
	val auxiliaryThresholdExpectation = 0.01

	val classifierType = ClassifierType.Cnn
	var folderNameForCnnClassifier = ""

	val fileDelimiter = ","
	val experimentSet = "egypt"
	val trainingDataFile = s"data/final/${experimentSet}_training_data.txt"
	val validationDataFile = s"data/final/${experimentSet}_validation_data.txt"
	val auxiliaryDataFile = s"data/final/${experimentSet}_auxiliary_data.txt"
	val supplementedCleanAuxiliaryFile = s"data/final/${experimentSet}_auxiliary_data_clean.txt"


override def toString() = {

	"\n***************** Parameters used for EXPERIMENT *****************\n"+
	s"\tfilterToUse = $filterToUse\n" +
	s"\tminSimilarityThreshold = $minSimilarityThreshold\n" +
	s"\tcosineSimilarityWindowSize= $cosineSimilarityWindowSize\n" +
	s"\tminWmDistanceThreshold = $minWmDistanceThreshold\n" +
	s"\tmaxFpmWordsToPick = $maxFpmWordsToPick\n" +
	s"\tminFpmWordsDetected = $minFpmWordsDetected\n" +
	s"\trefreshLocalWordVectors = $refreshLocalWordVectors\n" +
	s"\tmaxExperimentIterations = $maxExperimentIterations\n" +
	s"\tmaxAuxTweetsToAddEachIteration = $tweetsToAddEachIteration\n" +
	s"\tthresholdF1 = $thresholdF1\n" +
	s"\tclassifierType = $classifierType\n" +
	s"\tfolderNameForCnnClassifier = $folderNameForCnnClassifier\n" +
	s"\tauxiliaryThresholdExpectation = $auxiliaryThresholdExpectation\n" +
	s"\tfileDelimiter = $fileDelimiter\n" +
	s"\ttrainingDataFile = $trainingDataFile\n" +
	s"\tvalidationDataFile = $validationDataFile\n" +
	s"\tauxiliaryDataFile = $auxiliaryDataFile\n" +
	s"\tsupplementedCleanAuxiliaryFile = $supplementedCleanAuxiliaryFile\n"+
	"\n***************** Parameters used for EXPERIMENT *****************\n"
}

}
