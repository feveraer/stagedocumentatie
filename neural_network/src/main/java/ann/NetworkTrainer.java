package ann;

import org.encog.ConsoleStatusReportable;
import org.encog.Encog;
import org.encog.ml.MLRegression;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.model.EncogModel;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by Lorenz on 13/04/2016.
 */
public class NetworkTrainer {
    private VersatileDataSource dataSource;

    private NormalizationHelper helper;
    private MLRegression bestMethod;

    public NetworkTrainer(String pathToTrainingsData) {
        File trainingsData = new File(pathToTrainingsData);

        // create data source (file, header presence, format of csv)
        dataSource = new CSVDataSource(trainingsData, true, EncogConstants.FORMAT);
    }

    public void train() {
        if (dataSource == null) {
            throw new NullPointerException("No data source.");
        }

        // The versatile dataset supports several advanced features:
        // 1. it can directly read and normalize from a CSV file.
        // 2. It supports virtual time-boxing for time series data (the data is NOT expanded in memory).
        // 3. It can easily be segmented into smaller datasets.
        VersatileMLDataSet data = new VersatileMLDataSet(dataSource);

        // Set NormHelper to correct format
        data.getNormHelper().setFormat(EncogConstants.FORMAT);

        // Define columns
        ColumnDefinition columnSetTemp = data.defineSourceColumn("SetTemp", ColumnType.continuous);
        ColumnDefinition columnMeasuredTemp = data.defineSourceColumn("MeasuredTemp", ColumnType.continuous);
        ColumnDefinition columnDayDiff = data.defineSourceColumn("DayDiff", ColumnType.continuous);
        ColumnDefinition columnHourDiff = data.defineSourceColumn("HourDiff", ColumnType.continuous);
        ColumnDefinition columnMinuteDiff = data.defineSourceColumn("MinuteDiff", ColumnType.continuous);
        ColumnDefinition columnSecondDiff = data.defineSourceColumn("SecondDiff", ColumnType.continuous);

        ColumnDefinition columnNextMeasured = data.defineSourceColumn("NextMeasured", ColumnType.continuous);

        // Analyze data: max/min/stddev of each column
        data.analyze();

        // Map the NextMeasured column to the output of the model, and all others to the input.
        data.defineSingleOutputOthersInput(columnNextMeasured);
        EncogModel model = new EncogModel(data);
        // Select method to use:
        // dataset - feed forward - layout network, trainingtype, trainingArgs
        // make trainingsArgs an empty string
        model.selectMethod(data,
                MLMethodFactory.TYPE_FEEDFORWARD,
                "?:B->SIGMOID->42:B->SIGMOID->?",
                MLTrainFactory.TYPE_BACKPROP,
                "");

        // Send output to console
        model.setReport(new ConsoleStatusReportable());

        // Normalize data. Encog will choose the right normalization type based on the model.
        data.normalize();

        // === FITTING THE MODEL ===

        // Set time series. Lead window of 1 and lag window of <WINDOW_SIZE>.
        // This means we will use the last <WINDOW_SIZE> SetTemp, MeasuredTemp,
        // HourDiff, MinuteDiff and NextMeasured values to predict the next NextMeasured?
        // Why do we need NextMeasured column???
        data.setLeadWindowSize(1);
        data.setLagWindowSize(EncogConstants.WINDOW_SIZE);

        // Hold back 20% of the data for validation.
        // Don't shuffle data for time series.
        // Use a seed of 1001 so that we always use the same holdback and will get more consistent results.
        model.holdBackValidation(0.2, false, 1001);

        // Select default training type for this model.
        model.selectTrainingType(data);

        // Use a 10−fold cross−validated train, return the best method found.
        // Never shuffle time series.
        // Cross-validation breaks the training dataset into 10 different combinations of
        // training and validation data. Do not confuse the cross-validation validation
        // data with the ultimate validation data that we set aside previously. The cross-validation
        // process does not use the validation data that we previously set aside.
        // Those data are for a final validation, after training has occurred.
        bestMethod = (MLRegression) model.crossvalidate(10, false);

        // Display the training and validation errors
        System.out.println("Training error: " + model.calculateError(bestMethod, model.getTrainingDataset()));
        System.out.println("Validation error: " + model.calculateError(bestMethod, model.getValidationDataset()));
        // Display our normalization parameters
        helper = data.getNormHelper();
        System.out.println(helper.toString());
        // Display the final model
        System.out.println("Final model: " + bestMethod);

        Encog.getInstance().shutdown();
    }

    public void exportModel() {
        if (helper == null) {
            throw new NullPointerException("Normalization helper is null");
        }
        if (bestMethod == null) {
            throw new NullPointerException("Best method is null");
        }

//        File normalizationFile = new File("src/main/resources/network/encogNormalizationHelper.eg");
        File bestMethodFile = new File("src/main/resources/network/encogBestMethod.eg");

        try {
//            normalizationFile.getParentFile().mkdirs();
//            normalizationFile.createNewFile();
            bestMethodFile.getParentFile().mkdirs();
            bestMethodFile.createNewFile();
        }catch(IOException e){
            System.out.println("Files already exist");
        }

        try {
            FileOutputStream fos = new FileOutputStream("src/main/resources/network/encogNormalizationHelper.eg");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(helper);
            oos.close();
        } catch (IOException e){
            e.printStackTrace();
        }

//        EncogDirectoryPersistence.saveObject(normalizationFile, helper);
        EncogDirectoryPersistence.saveObject(bestMethodFile, bestMethod);

    }
}
