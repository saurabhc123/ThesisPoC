package main.scala.Implementations

import Factories.{AuxiliaryDataRetrieverFactory, ClassifierType, DistinguishingWordsGeneratorFactory}
import Interfaces.{IDistinguishingWordsGenerator, IExperiment}
import Utilities.MetricsCalculator
import main.DataTypes.Tweet
import main.Factories.ClassifierFactory
import main.Interfaces.IAuxiliaryDataRetriever
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/8/17.
 */
class AuxiliaryDataBasedExperiment extends IExperiment {


	override def performExperiment(train: RDD[Tweet], validation: RDD[Tweet]): Unit = {


		//Get the classifier from the factory
		val classifierFactory = new ClassifierFactory()
		val classifier = classifierFactory.getClassifier(ClassifierType.LogisticRegression)

		//Train the classifier
		var model = classifier.train(train.map(tweet => tweet.transformToLabeledPoint()))

		//Perform Validation and get score
		val predictions = model.predict(validation.map(tweet => tweet.transformToLabeledPoint()));
		val metricsCalculator = MetricsCalculator.GenerateClassifierMetrics(predictions)
		var f1 = metricsCalculator.macroF1
		val thresholdF1 = 0.9
		val auxiliaryThresholdExpectation = 0.05

		val distinguishingWordGenerator: IDistinguishingWordsGenerator = new DistinguishingWordsGeneratorFactory().getDistinguishingWordsGenerator()
		val auxiliaryDataRetriever: IAuxiliaryDataRetriever = new AuxiliaryDataRetrieverFactory().getAuxiliaryDataRetriever()

		var distinguishingWordTweetCorpus = train
		var numberOfIterations = 0
		while (f1 < thresholdF1 && numberOfIterations < 5) {
			//Get the most distinguishing words
			val distinguishingWords = distinguishingWordGenerator.generateMostDistinguishingWords(distinguishingWordTweetCorpus)

			//Retrieve auxiliary data by using most distinguishing words
			val auxiliaryData = auxiliaryDataRetriever.retrieveAuxiliaryData(distinguishingWords)

			//train using training + auxiliary data
			val fullData = train.union(auxiliaryData)
			model = classifier.train(fullData.map(tweet => tweet.transformToLabeledPoint()))

			//perform prediction on validation data
			val auxiliaryPredictions = model.predict(validation.map(tweet => tweet.transformToLabeledPoint()))
			val metrics = MetricsCalculator.GenerateClassifierMetrics(auxiliaryPredictions)
			val auxF1 = metrics.macroF1

			//if f1 is greater than aux threshold, add aux to the training data.
			if ((auxF1 - f1) > auxiliaryThresholdExpectation) {
				f1 = auxF1
				distinguishingWordTweetCorpus = fullData
			}
			numberOfIterations += 1

		}
	}
}
