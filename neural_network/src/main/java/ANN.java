import ann.NetworkTrainer;
import ann.NeuralNetwork;

/**
 * Created by Lorenz on 13/04/2016.
 */
public class ANN {
    public static void main(String[] args) {
        NetworkTrainer networkTrainer = new NetworkTrainer("src/main/resources/TrainingsSet.tsv");
        networkTrainer.train();
        networkTrainer.exportModel();

        NeuralNetwork ann = new NeuralNetwork();
        ann.loadModel("src/main/resources/network/encogNormalizationHelper.eg", "src/main/resources/network/encogBestMethod.eg");
        ann.predictFromCSV("src/main/resources/TrainingsSet.tsv");
    }
}
