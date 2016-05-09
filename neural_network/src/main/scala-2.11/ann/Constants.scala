package ann

import org.encog.util.csv.CSVFormat
import time.DateTimeDifference

/**
  * Created by Lorenz on 19/04/2016.
  */
object Constants {
  val WINDOW_SIZE = 2
  val FORMAT = new CSVFormat('.', '\t')
  val RESOURCES_PATH = "src/main/resources/"
  val DEFAULT_TRAINING_SET_PATH = "training_set.tsv"
  val DEFAULT_TRAINING_SET_PATH_V2 = "training_set.tsv"
  val ENCOG_BEST_METHOD_PATH = "network/best_method.eg"
  val ENCOG_NORMALIZATION_HELPER_PATH = "network/normalization_helper.eg"
  val DEFAULT_USER_ID = 0

  // time difference to prediction in days, hours, minutes, seconds
  val DIFF_TO_PREDICTION: DateTimeDifference = new DateTimeDifference(0, 0, 6, 0)
  val DIFF_TO_PREDICTION_MINUTES: Long = 6
}
