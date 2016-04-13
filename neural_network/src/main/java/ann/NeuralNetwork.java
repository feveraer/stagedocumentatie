package ann;

import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.model.EncogModel;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.VectorWindow;
import org.encog.util.csv.ReadCSV;

import java.io.File;

/**
 * Created by Lorenz on 13/04/2016.
 */
public class NeuralNetwork {
    private static final int numberOfColumns = 4;

    private EncogModel model;
    private NormalizationHelper helper;
    private MLRegression bestMethod;

    public void loadModel(String pathToModel, String pathToNormalizationHelper, String pathToBestModel) {
        model = (EncogModel) EncogDirectoryPersistence.loadObject(new File(pathToModel));
        helper = (NormalizationHelper) EncogDirectoryPersistence.loadObject(new File(pathToNormalizationHelper));
        bestMethod = (MLRegression) EncogDirectoryPersistence.loadObject(new File(pathToBestModel));
    }

    public void predictFromCSV(String filename) {
        ReadCSV csv = new ReadCSV(filename, true, EncogConstants.FORMAT);

        String[] line = new String[numberOfColumns];
        double[] slice = new double[numberOfColumns];

        VectorWindow window = new VectorWindow(EncogConstants.WINDOW_SIZE + 1);
        MLData input = helper.allocateInputVector(EncogConstants.WINDOW_SIZE + 1);

        int stopAfter = 100;

        while (csv.next() && stopAfter > 0) {
            for (int i = 0; i < numberOfColumns; i++) {
                line[i] = csv.get(i);
            }

            helper.normalizeInputVector(line, slice, false);

            if (window.isReady()) {
                StringBuilder result = new StringBuilder();

                window.copyWindow(input.getData(), 0);
                String correct = csv.get(numberOfColumns - 1);

                MLData output = bestMethod.compute(input);

                String predicted = helper.denormalizeOutputVectorToString(output)[0];

                result.append("Predicted:\t" + predicted + "\n");
                result.append("Correct:\t" + correct + "\n");
                result.append("\n");
                System.out.println(result.toString());
            }

            window.add(slice);
            stopAfter--;
        }
    }

}
