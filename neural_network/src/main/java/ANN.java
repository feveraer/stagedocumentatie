import ann.NetworkTrainer;
import ann.NeuralNetwork;

/**
 * Created by Lorenz on 13/04/2016.
 */
public class ANN {

    private NetworkTrainer networkTrainer = new NetworkTrainer("src/main/resources/TrainingsSet.tsv");
    private NeuralNetwork ann;

    public static void main(String[] args) {
        new ANN().run();
    }

    public void run() {
        networkTrainer.train();
        networkTrainer.exportModel();

        ann = new NeuralNetwork();
        ann.loadModel("src/main/resources/network/encogNormalizationHelper.eg", "src/main/resources/network/encogBestMethod.eg");
        ann.predictFromCSV("src/main/resources/TrainingsSet.tsv");
    }

    public void draw() {

    }
}
