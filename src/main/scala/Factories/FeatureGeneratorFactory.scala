package main.scala.Factories

import Implementations.FeatureGeneratorImpl.WebServiceBasedWordVectorGenerator
import main.Implementations.FeatureGeneratorImpl.WordVectorGenerator
import main.Interfaces.IFeatureGenerator
import main.scala.Factories.FeatureGeneratorType.FeatureGeneratorType

/**
 * Created by Eric on 2/1/2017.
 */
object FeatureGeneratorFactory {
	def getFeatureGenerator(featureGeneratorType: FeatureGeneratorType): IFeatureGenerator = {
		featureGeneratorType match {
			case FeatureGeneratorType.Word2Vec => new WordVectorGenerator
			case FeatureGeneratorType.WebServiceWord2Vec => new WebServiceBasedWordVectorGenerator
		}
	}

}

object FeatureGeneratorType extends Enumeration {
	type FeatureGeneratorType = Value
	val Word2Vec = Value
	val WebServiceWord2Vec = Value
}
