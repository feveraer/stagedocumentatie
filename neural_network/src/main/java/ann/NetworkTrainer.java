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
    private File trainingsData;
    private VersatileDataSource dataSource;

    private EncogModel model;
    private NormalizationHelper helper;
    private MLRegression bestMethod;

    public NetworkTrainer(String pathToTrainingsData) {
        trainingsData = new File(pathToTrainingsData);

        // datasource aanmaken. (file, aanwezigheid headers en formaat meegeven)
        dataSource = new CSVDataSource(trainingsData, true, EncogConstants.FORMAT);
    }

    public void train() {
        if (dataSource == null) {
            throw new NullPointerException("No datasource");
        }

        // Transfrom the dataset
        VersatileMLDataSet data = new VersatileMLDataSet(dataSource);

        // Set NormHelper to correct format
        data.getNormHelper().setFormat(EncogConstants.FORMAT);

        // Define columns
        ColumnDefinition columnSetTemp = data.defineSourceColumn("SetTemp", ColumnType.continuous);
        ColumnDefinition columnMeasuredTemp = data.defineSourceColumn("MeasuredTemp", ColumnType.continuous);
        ColumnDefinition columnHourDiff = data.defineSourceColumn("hourDiff", ColumnType.continuous);
        ColumnDefinition columnMinuteDiff = data.defineSourceColumn("minuteDiff", ColumnType.continuous);

        ColumnDefinition columnOUTPUT = data.defineSourceColumn("Output", ColumnType.continuous);

        // Analyze data: max/min/stddev of each column
        data.analyze();

        // Map all columns to the OUTPUT column
        data.defineSingleOutputOthersInput(columnOUTPUT);

        // Create model
        model = new EncogModel(data);
        model.selectMethod(data,
                MLMethodFactory.TYPE_FEEDFORWARD,
                "?:B->SIGMOID->42:B->?",
                MLTrainFactory.TYPE_BACKPROP,
                "");


        // Send output to console
        model.setReport(new ConsoleStatusReportable());

        // Normalize data
        // Encog will choose the right normalization type based on the model
        data.normalize();

        // Set timeseries
        data.setLeadWindowSize(1);
        data.setLagWindowSize(EncogConstants.WINDOW_SIZE);

        // Hold back 30% of the data for validation
        // Don't shuffle data for time series
        // Use a seed of 1001 so that we always use the same holdback and will get more consistent results .
        model.holdBackValidation(0.2, false, 1001);

        // Selecteer het default trainingstype voor dit model
        model.selectTrainingType(data);

        // Use a 5−fold cross−validated train
        bestMethod = (MLRegression) model.crossvalidate(10, false);

        // Display the training and validation errors
        System.out.println("Training error : " + model.calculateError(bestMethod, model.getTrainingDataset()));
        System.out.println(" Valilation error:" + model.calculateError(bestMethod, model.getValidationDataset()));
        // Display our normalization parameters
        helper = data.getNormHelper();
        System.out.println(helper.toString());
        // Display the final model
        System.out.println(" Final model:" + bestMethod);

        Encog.getInstance().shutdown();
    }

    public void exportModel() {
        if (helper == null) {
            throw new NullPointerException("Normalization helper is null");
        }
        if (bestMethod == null) {
            throw new NullPointerException("best method is null");
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
            FileOutputStream fout = new FileOutputStream("src/main/resources/network/encogNormalizationHelper.eg");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(helper);
            oos.close();
        } catch (IOException e){
            e.printStackTrace();
        }

//        EncogDirectoryPersistence.saveObject(normalizationFile, helper);
        EncogDirectoryPersistence.saveObject(bestMethodFile, bestMethod);

    }
}
