import ann.NetworkTrainerOLD;
import ann.NeuralNetworkOLD;

/**
 * Created by Lorenz on 13/04/2016.
 */
public class ANNOLD {

    private NetworkTrainerOLD networkTrainer = new NetworkTrainerOLD("src/main/resources/TrainingsSet.tsv");
    private NeuralNetworkOLD ann;

    public static void main(String[] args) {
        new ANNOLD().run();
    }

    public void run() {
        networkTrainer.train();
        networkTrainer.exportModel();

        ann = new NeuralNetworkOLD();
        ann.loadModel("src/main/resources/network/encogNormalizationHelper.eg", "src/main/resources/network/encogBestMethod.eg");
        ann.predictFromCSV("src/main/resources/TrainingsSet.tsv");
    }

    public void draw() {

    }
}
