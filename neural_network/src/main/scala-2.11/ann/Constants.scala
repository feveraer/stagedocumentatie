package ann

import org.encog.util.csv.CSVFormat

/**
  * Created by Lorenz on 19/04/2016.
  */
object Constants {
  val WINDOW_SIZE = 2
  val FORMAT = new CSVFormat('.', '\t')
  val RESOURCES_PATH = "src/main/resources/"
  val DEFAULT_TRAINING_SET_PATH = "training_set.tsv"
  val ENCOG_BEST_METHOD_PATH = "network/best_method.eg"
  val ENCOG_NORMALIZATION_HELPER_PATH = "network/normalization_helper.eg"

  // time difference to prediction in days, hours, minutes, seconds
  val DIFF_TO_PREDICTION: Vector[Double] = Vector(0, 0, 15, 0)
}
