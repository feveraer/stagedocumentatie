import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.ResilientPropagation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lorenz on 7/04/2016.
 */
public class NeurophJavaTest {
//    private static final double rate = 0.5;
//    private static final double maxError = 0.5;

    public static void main(String[] args) {
        System.out.println("Creating network");
        List<Integer> neuronsPerLayer = new ArrayList<>();
        neuronsPerLayer.add(48);
        neuronsPerLayer.add(32);
//        neuronsPerLayer.add(12);
//        neuronsPerLayer.add(6);
//        neuronsPerLayer.add(48);
//        neuronsPerLayer.add(48);
//        neuronsPerLayer.add(48);
        neuronsPerLayer.add(1);

        MultiLayerPerceptron mlp = new MultiLayerPerceptron(neuronsPerLayer);
//        BackPropagation bp = new BackPropagation();
//        bp.setLearningRate(rate);
//        bp.setMaxError(maxError);

        System.out.println("Create training set");
        TrainingSetCreator creator = new TrainingSetCreator();
        DataSet trainingSet = creator.createTrainingSet();

        // Uses back propagation as learning algorithm
        System.out.println("Start learning");
        mlp.learn(trainingSet);

        mlp.save("mlp_temp.nnet");

        System.out.println("Feed input");

        List<double[]> toCalc = new ArrayList<>();
        toCalc.add(new double[]{19.5,20.2667,20,19.8083,20,19.9917,19.5,20,19.5917,20.0333,19.5,20,19.7,19.1,19,19.3417,19.5,19.8083,19.6417,19,19.1417,20,19.6833,19.4417,19.6,19.6417,20.0083,19.5167,19.7333,19.5,19.5,19.5833,20,19.8,19.3167,19.5,20,19.5,20,19.85,19,19,18.2583,19.525,19.6417,19.5,20,19.5});
        // 20
        toCalc.add(new double[]{21,21.5,21.5,21,21,20.5,20.5,20.5,20.5,20.5,21,21.35,21,20.5,21,21,21,21,21.5,21.5,21.5,21,21.5,21.5,21.5,21,21,21,20.5,21,21,20.5,21,21.5,21,21.5,22,22,22,22,21.5,22,22,21.5,21,21,20.5,20.5});
        // 21
        toCalc.add(new double[]{24.5,24,23.5,23,23.3833,23.5,23.5,24,24.5,24.5,24.5,24,23.5,23.5,23,23,23,23,23,23.5,24,24,24,24.5,24.5,24,23.5,23,23,23,23.5,24,24.5,25,25,25,24.5,24,23.5,23,23,23,23.5,24,24,24.5,24.5,24.5});
        // 24.5
        toCalc.add(new double[]{4,4.5,4.125,4.48333,4.34167,4.5,4,4,4.43333,4.00833,4,4,3.575,3.5,4.44167,6.54167,7.14167,8.4,10.4167,10.1,8.25833,7.21667,6.4,5.03333,4.325,3.23333,2.3,1.375,0.166667,0,0.5,1.89167,3.99167,5.81667,8.125,10.3667,9.33333,7.61667,6.23333,5.15,4.23333,2.16667,1.425,0,0,0.466667,2.44167,4.675});
        // 6.875

        toCalc.forEach(x -> {
            mlp.setInput(x);
            mlp.calculate();

            double[] outputs = mlp.getOutput();

            double denormalizedOutput = creator.denormalize(outputs[0], 0, 40);

            System.out.println(denormalizedOutput);
        });

    }
}
