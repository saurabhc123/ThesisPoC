package Factories

import Implementations.AuxiliaryDataRetrievers.FileBasedAuxiliaryDataRetriever
import main.Interfaces.IAuxiliaryDataRetriever

/**
 * Created by saur6410 on 3/9/17.
 */
class AuxiliaryDataRetrieverFactory {
	def getAuxiliaryDataRetriever(): IAuxiliaryDataRetriever = {
		return new FileBasedAuxiliaryDataRetriever()
	}
}
