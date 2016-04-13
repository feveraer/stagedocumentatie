package ann;

import org.encog.ConsoleStatusReportable;
import org.encog.ml.MLRegression;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.File;

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
        ColumnDefinition columnSSN = data.defineSourceColumn("SSN", ColumnType.continuous);
        ColumnDefinition columnDEV = data.defineSourceColumn("DEV", ColumnType.continuous);

        ColumnDefinition columnOUTPUT = data.defineSourceColumn("OUTPUT", ColumnType.continuous);

        // Analyze data: max/min/stddev of each column
        data.analyze();

        // Map all columns to the OUTPUT column
        data.defineSingleOutputOthersInput(columnOUTPUT);

        // Create model
        model = new EncogModel(data);
        model.selectMethod(data, MLMethodFactory.TYPE_FEEDFORWARD);

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
        model.holdBackValidation(0.3, false, 1001);

        // Selecteer het default trainingstype voor dit model
        model.selectTrainingType(data);

        // Use a 5−fold cross−validated train
        bestMethod = (MLRegression) model.crossvalidate(5, false);

        // Display the training and validation errors
        System.out.println("Training error : " + model.calculateError(bestMethod, model.getTrainingDataset()));
        System.out.println(" Valilation error:" + model.calculateError(bestMethod, model.getValidationDataset()));
        // Display our normalization parameters
        helper = data.getNormHelper();
        System.out.println(helper.toString());
        // Display the final model
        System.out.println(" Final model:" + bestMethod);
    }

    public void exportModel() {
        if (model == null) {
            throw new NullPointerException("model is null");
        }
        if (helper == null) {
            throw new NullPointerException("Normalization helper is null");
        }
        if (bestMethod == null) {
            throw new NullPointerException("best method is null");
        }

        EncogDirectoryPersistence.saveObject(new File("encogmodel.eg"), model);
        EncogDirectoryPersistence.saveObject(new File("encogNormalizationHelper.eg"), helper);
        EncogDirectoryPersistence.saveObject(new File("encogBestMethod.eg"), bestMethod);

    }
}
