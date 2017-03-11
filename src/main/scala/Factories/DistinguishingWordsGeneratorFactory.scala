package Factories

import Implementations.DistinguishingWordsGenerator.FpmBasedDistinguishingWordsGenerator
import Interfaces.IDistinguishingWordsGenerator

/**
 * Created by saur6410 on 3/9/17.
 */
class DistinguishingWordsGeneratorFactory {

	def getDistinguishingWordsGenerator() : IDistinguishingWordsGenerator =
	{
		new FpmBasedDistinguishingWordsGenerator()
	}

}
