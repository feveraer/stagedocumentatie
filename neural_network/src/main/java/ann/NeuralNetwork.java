package ann;

import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.VectorWindow;
import org.encog.util.csv.ReadCSV;

import java.io.*;

/**
 * Created by Lorenz on 13/04/2016.
 */
public class NeuralNetwork {
    private static final int numberOfColumns = 6;

    private NormalizationHelper helper;
    private MLRegression bestMethod;

    public void loadModel(String pathToNormalizationHelper, String pathToBestModel) {
        try {
            FileInputStream fin = new FileInputStream(pathToNormalizationHelper);
            ObjectInputStream ois = new ObjectInputStream(fin);
            helper = (NormalizationHelper) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
//        helper = new NormalizationHelper();
//        helper.setNormStrategy(EncogConstants.NORMALIZATIION_STRATEGY);
        bestMethod = (MLRegression) EncogDirectoryPersistence.loadObject(new File(pathToBestModel));
    }

    public void predictFromCSV(String filename) {
        // Iterator for csv data
        // filname - headers - format
        ReadCSV csv = new ReadCSV(filename, true, EncogConstants.FORMAT);

        // create empty arrays for later usage
        // this will be needed to store the columns of a row in the csv
        String[] line = new String[numberOfColumns];
        double[] slice = new double[numberOfColumns];

        // Create a vector to hold each time−slice , as we build them.
        // These will be grouped together into windows
        VectorWindow window = new VectorWindow(EncogConstants.WINDOW_SIZE + 1);
        MLData input = helper.allocateInputVector(EncogConstants.WINDOW_SIZE + 1);

        // Only take the first 100 to predict
        int stopAfter = 100;

        while (csv.next() && stopAfter > 0) {
            // parse the csv row to an array
            for (int i = 0; i < numberOfColumns; i++) {
                line[i] = csv.get(i);
            }

            // normalize the input
            // input array - output array - shuffle
            // Never shuffle timeseries
            helper.normalizeInputVector(line, slice, false);

            // check if window is ready
            // enough data to build a full window
            if (window.isReady()) {
                StringBuilder result = new StringBuilder();

                // Copy the window
                // data - start position
                window.copyWindow(input.getData(), 0);
                String correct = csv.get(numberOfColumns);

                // predict output
                MLData output = bestMethod.compute(input);
                // denormalize prediction
                String predicted = helper.denormalizeOutputVectorToString(output)[0];

                result.append("Predicted:\t" + predicted + "\n");
                result.append("Correct:\t" + correct + "\n");
                result.append("\n");
                System.out.println(result.toString());
            }

            // add data to windows
            window.add(slice);
            stopAfter--;
        }
    }

}
