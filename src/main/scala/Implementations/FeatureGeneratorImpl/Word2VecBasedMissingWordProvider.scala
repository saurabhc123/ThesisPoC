package Implementations.FeatureGeneratorImpl

import Interfaces.IReplacementWordProvider
import org.apache.spark.mllib.linalg.Word2VecGenerator

/**
 * Created by saur6410 on 3/19/17.
 */
class Word2VecBasedMissingWordProvider extends IReplacementWordProvider{

	override def replaceWord(missingWord: String): String = {
		Word2VecBasedMissingWordProvider.getReplacementWord(missingWord)
	}
}


object Word2VecBasedMissingWordProvider
{
	def getReplacementWord(missingWord: String): String = {

		Word2VecGenerator.getTopSynonym(missingWord)
	}

}
