import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.ResilientPropagation;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Lorenz on 7/04/2016.
 */
public class NeurophJavaTest {
    public static void main(String[] args) {
        System.out.println("Creating network");
        List<Integer> neuronsPerLayer = new ArrayList<>();
        neuronsPerLayer.add(48);
        neuronsPerLayer.add(32);
//        neuronsPerLayer.add(22);
//        neuronsPerLayer.add(15);
//        neuronsPerLayer.add(10);
//        neuronsPerLayer.add(6);
//        neuronsPerLayer.add(3);
        neuronsPerLayer.add(1);

        MultiLayerPerceptron mlp = new MultiLayerPerceptron(neuronsPerLayer);

        System.out.println("Create training set");
        TrainingSetCreator creator = new TrainingSetCreator();
        DataSet trainingSet = creator.createTrainingSet();

        System.out.println("Start learning");
        mlp.learn(trainingSet, new ResilientPropagation());

        System.out.println("Feed input");

        List<double[]> toCalc = new ArrayList<>();
        toCalc.add(new double[]{19.5, 19.0083, 20.0417, 20.5, 20, 19.5833, 20, 20.0917, 21, 20.5, 20, 20.5, 21, 21.5, 22, 21.5667, 22, 21.5, 21, 20.5, 20, 19.5167, 20.7417, 20.7833, 20, 20.5, 20.5, 21, 21.5, 21.5, 21.375, 21, 20.5, 20, 19.5, 19.8083, 20.5, 20, 20, 20.5, 20, 20.1167, 21, 21.35, 21, 20.5, 20, 20.1583});
        toCalc.add(new double[]{19.8583, 21.5, 21.5, 21, 20.5, 20, 19.5, 19.5, 20.5667, 20.9833, 20, 20, 19.5, 19, 19.2667, 20.5, 21, 21.5, 21.5, 21, 20.5, 20, 19.5, 19.5, 20.3333, 20.5, 20, 19.5, 19.5, 20.3417, 20, 20, 20.325, 20.4, 21.5, 21.975, 21, 20.5, 20, 19.5, 19.5, 20.5167, 20.9583, 20, 20, 20.5, 21, 21});
        toCalc.add(new double[]{17, 16.4167, 15.525, 15, 15.5, 16, 16.5, 16.5, 17, 16.5, 16, 16, 16.5, 16.5, 16.5, 17, 17.5, 17.5, 18, 18, 18.5, 18.5, 18.5, 18.5, 18.5, 18.5, 18.5, 18.5, 19.3167, 19, 18.5, 18, 18, 18.5, 19, 19, 18.5, 18, 17.5, 17, 17, 16.5, 17, 17, 17.5, 16.8667, 15.7083, 15.3167});
        toCalc.add(new double[]{16.8417, 17.6167, 18.5, 18.925, 20, 19.5, 19, 18.5, 18, 17.5, 17, 16.5, 16, 16.8083, 17, 16.5, 17, 16.8083, 16.5, 17, 16.5, 17.2167, 18, 17.5, 17.5, 17.4583, 17.5917, 17.5, 18, 18.5, 18, 18, 17.5, 17, 16.5, 17.2417, 18, 17.5, 18, 17.5, 17, 17.5, 18, 17.5, 18, 17.5, 18, 17.5});

        toCalc.forEach(x -> {
            mlp.setInput(x);
            mlp.calculate();

            double[] outputs = mlp.getOutput();

            double denormalizedOutput = creator.denormalize(outputs[0], -20, 50);

            System.out.println(denormalizedOutput);
        });

    }
}
