  package main.scala.Implementations

  import Interfaces.{IDistinguishingWordsGenerator, IClassifierModel, IExperiment, IClassifier}
  import main.DataTypes.Tweet
  import main.Interfaces.IAuxiliaryDataRetriever
  import org.apache.spark.rdd.RDD

  /**
   * Created by saur6410 on 3/8/17.
   */
  class AuxiliaryDataBasedExperiment  extends IExperiment {
    override def performExperiment(train: RDD[Tweet], validation: RDD[Tweet]): Unit = {


      //Get the classifier from the factory
      //ToDo: Fix the below
      val classifier : IClassifier = null
      val classiferFactory : ClassifierFactory = new ClassifierFactory()

      //Train the classifier
      //ToDo: Fix the below
      var model = classifier.train(train.map())

      //Perform Validation and get score

      //ToDo: Fix the below
      var predictions = model.predict(validation);
      var f1 = 0.0
      val thresholdF1 = 0.9
      val auxiliaryThresholdExpectation = 0.05

      val distinguishingWordGenerator:IDistinguishingWordsGenerator = null
      val auxiliaryDataRetriever:IAuxiliaryDataRetriever = null;

      var distinguishingWordTweetCorpus = train

      while(f1 < thresholdF1) {
        //Get the most distinguishing words
        val distinguishingWords = distinguishingWordGenerator.generateMostDistinguishingWords(distinguishingWordTweetCorpus)

        //Retrieve auxiliary data by using most distinguishing words
        val auxiliaryData = auxiliaryDataRetriever.retrieveAuxiliaryData(distinguishingWords)

        //train using training + auxiliary data
        val fullData = train.union(auxiliaryData)
        //ToDo: Fix the below
        model = classifier.train(fullData)
        //perform prediction on validation data
        //ToDo: Fix the below
        val auxiliaryPredictions = model.predict(validation)
        val auxF1 = 0.1
        //if f1 is greater than aux threshold, add aux to the training data.
        if((auxF1 - f1) > auxiliaryThresholdExpectation)
          {
            f1 = auxF1
          }

      }
    }
  }
