package main.Factories

import Factories.ClassifierType
import ClassifierType.ClassifierType
import main.Interfaces.IClassifier
import main.scala.Implementations.ClassifierImpl.LogisticRegressionClassifier

/**
  * Created by ericrw96 on 2/2/17.
  */
object ClassifierFactory {
  def getClassifier(classifierType: ClassifierType): IClassifier = {
     classifierType match {
       case ClassifierType.LogisticRegression => new LogisticRegressionClassifier
       case ClassifierType.SVM => throw new NotImplementedError("The is no SVM as of now")
     }
  }

}


