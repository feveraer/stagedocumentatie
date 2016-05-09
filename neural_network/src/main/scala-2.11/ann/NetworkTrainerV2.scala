package ann

import java.io.{File, FileOutputStream, IOException, ObjectOutputStream}

import org.encog.{ConsoleStatusReportable, Encog}
import org.encog.ml.MLRegression
import org.encog.ml.data.versatile.columns.{ColumnDefinition, ColumnType}
import org.encog.ml.data.versatile.{NormalizationHelper, VersatileMLDataSet}
import org.encog.ml.data.versatile.sources.CSVDataSource
import org.encog.ml.factory.{MLMethodFactory, MLTrainFactory}
import org.encog.ml.model.EncogModel
import org.encog.persist.EncogDirectoryPersistence

/**
  * Created by Lorenz on 9/05/2016.
  */
class NetworkTrainerV2 (val pathToTrainingsData: String) {
  // private attributes
  private var helper: NormalizationHelper = null
  private var bestMethod: MLRegression = null

  private val trainingsData: File = new File(pathToTrainingsData)

  // Create data source
  // file, header presence, format of csv
  private val dataSource = new CSVDataSource(trainingsData, true, Constants.FORMAT)

  def train {

    if (dataSource == null) {
      throw new NullPointerException("No data source.")
    }

    // The versatile dataset supports several advanced features:
    // 1. it can directly read and normalize from a CSV file.
    // 2. It supports virtual time-boxing for time series data (the data is NOT expanded in memory).
    // 3. It can easily be segmented into smaller datasets.
    val data: VersatileMLDataSet = new VersatileMLDataSet(dataSource)

    // Set NormHelper to correct format
    data.getNormHelper.setFormat(Constants.FORMAT)

    // Define columns
    val columnSetTemp: ColumnDefinition = data.defineSourceColumn("SetTemp", ColumnType.continuous)
    val columnMeasuredTemp: ColumnDefinition = data.defineSourceColumn("MeasuredTemp", ColumnType.continuous)
    val columnDiff: ColumnDefinition = data.defineSourceColumn("TimeDiff", ColumnType.continuous)

    val columnNextMeasured: ColumnDefinition = data.defineSourceColumn("NextMeasured", ColumnType.continuous)

    // Analyze data: max/min/stddev of each column
    data.analyze

    // Map the NextMeasured column to the output of the model, and all others to the input.
    data.defineSingleOutputOthersInput(columnNextMeasured)

    // Degine the model
    val model: EncogModel = new EncogModel(data)

    // Select method to use:
    // dataset - feed forward - layout network, trainingtype, trainingArgs
    // Make trainingsArgs an empty string
    model.selectMethod(data,
      MLMethodFactory.TYPE_FEEDFORWARD,
      "?:B->SIGMOID->7:B->SIGMOID->?",
      MLTrainFactory.TYPE_BACKPROP,
      "")

    // Send output to console
    model.setReport(new ConsoleStatusReportable)

    // Normalize data. Encog will choose the right normalization type based on the model.
    data.normalize

    // === FITTING THE MODEL ===

    // Set time series. Lead window of 1 and lag window of <WINDOW_SIZE>.
    // This means we will use the last <WINDOW_SIZE> SetTemp, MeasuredTemp,
    data.setLeadWindowSize(1)
    data.setLagWindowSize(Constants.WINDOW_SIZE)

    // Hold back 20% of the data for validation.
    // Don't shuffle data for time series.
    // Use a seed of 1001 so that we always use the same holdback and will get more consistent results.
    model.holdBackValidation(0.2, false, 1001)

    // Select default training type for this model.
    model.selectTrainingType(data)

    // Use a 10−fold cross−validated train, return the best method found.
    // Never shuffle time series.
    // Cross-validation breaks the training dataset into 10 different combinations of
    // training and validation data. Do not confuse the cross-validation validation
    // data with the ultimate validation data that we set aside previously. The cross-validation
    // process does not use the validation data that we previously set aside.
    // Those data are for a final validation, after training has occurred.
    bestMethod = model.crossvalidate(10, false).asInstanceOf[MLRegression]

    // Display the training and validation errors
    System.out.println("Training error: " + model.calculateError(bestMethod, model.getTrainingDataset))
    System.out.println("Validation error: " + model.calculateError(bestMethod, model.getValidationDataset))

    // Display our normalization parameters
    helper = data.getNormHelper
    System.out.println(helper.toString)

    // Display the final model
    System.out.println("Final model: " + bestMethod)

    // Close the Encog instance
    Encog.getInstance.shutdown
  }

  def exportModel {
    if (helper == null) {
      throw new NullPointerException("Normalization helper is null")
    }
    if (bestMethod == null) {
      throw new NullPointerException("Best method is null")
    }

    val bestMethodFile: File = new File(Constants.RESOURCES_PATH + Constants.ENCOG_BEST_METHOD_PATH)

    try {
      bestMethodFile.getParentFile.mkdirs
      bestMethodFile.createNewFile
    }
    catch {
      case e: IOException => {
        System.out.println("Files already exist")
      }
    }

    // Export normalizationHelper
    try {
      val fos: FileOutputStream = new FileOutputStream(Constants.RESOURCES_PATH + Constants.ENCOG_NORMALIZATION_HELPER_PATH)
      val oos: ObjectOutputStream = new ObjectOutputStream(fos)
      oos.writeObject(helper)
      oos.close
    }
    catch {
      case e: IOException => {
        e.printStackTrace
      }
    }

    // Export best method
    EncogDirectoryPersistence.saveObject(bestMethodFile, bestMethod)
  }
}
