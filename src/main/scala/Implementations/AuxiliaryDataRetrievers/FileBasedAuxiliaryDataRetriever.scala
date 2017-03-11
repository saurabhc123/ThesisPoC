package Implementations.AuxiliaryDataRetrievers

import main.DataTypes.Tweet
import main.Interfaces.IAuxiliaryDataRetriever
import org.apache.spark.rdd.RDD


/**
 * Created by saur6410 on 3/9/17.
 */
class FileBasedAuxiliaryDataRetriever extends IAuxiliaryDataRetriever {

	val _auxiliaryFileName = "data/all_data.txt"
	val numberOfTweetsToRetrieve = 10

	override def retrieveAuxiliaryData(distinguishingWords: Array[String]): RDD[Tweet] = {

		//Read the tweets from the file one by one.

		//Update the cursor for each step.

		//Once the required number of tweets are retrieved, return those.

		//Throw exception if no more tweets available.

		//ToDo: Complete the above
		null
	}
}


object FileCursor
{
	var lastLineRead = 0


}