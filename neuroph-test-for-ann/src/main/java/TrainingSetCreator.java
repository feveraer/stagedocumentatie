import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lorenz on 7/04/2016.
 */

public class TrainingSetCreator {
    private final static int MIN_TEMP = 0;
    private final static int MAX_TEMP = 40;
    private final static int MOD = 2;

    public DataSet createTrainingSet() {
        List<ArrayList<Double>> inputs = createInputs();
        List<ArrayList<Double>> outputs = createDesiredOutputs();

        DataSet trainingSet = new DataSet(48, 1);

        if (inputs.size() != outputs.size()) {
            throw new RuntimeException("input and output length are not the same");
        }

        for (int i = 0; i < inputs.size(); i++) {
            DataSetRow row = new DataSetRow(inputs.get(i), outputs.get(i));
            trainingSet.addRow(row);
        }

        return trainingSet;
    }

    private List<ArrayList<Double>> createInputs() {
        List<ArrayList<Double>> inputs = new ArrayList<>();
        try {
            int counter = 0;
            for (String line : Files.readAllLines(Paths.get("src/main/resources/valuesSet.txt"))) {
                if (counter % MOD == 0) {
                    String[] parts = line.split(",");
                    ArrayList<Double> values = new ArrayList<>();
                    if (parts.length == 48) {
                        for (String part : parts) {
                            double value = Double.parseDouble(part);
                            value = normalize(value, MIN_TEMP, MAX_TEMP);
                            values.add(value);
                        }
                    }
                    inputs.add(values);
                    counter++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputs;
    }

    private List<ArrayList<Double>> createDesiredOutputs() {
        List<ArrayList<Double>> outputs = new ArrayList<>();
        try {
            int counter = 0;
            for (String line : Files.readAllLines(Paths.get("src/main/resources/ExpectedSet.txt"))) {
                if (counter % MOD == 0) {
                    ArrayList<Double> values = new ArrayList<>();
                    double value = Double.parseDouble(line);
                    value = normalize(value, MIN_TEMP, MAX_TEMP);
                    values.add(value);
                    outputs.add(values);
                    counter++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputs;
    }

    private double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    public double denormalize(double value, double min, double max) {
        return value * (max - min) + min;
    }
}
