import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lorenz on 7/04/2016.
 */
public class NeurophJavaTest {
    public static void main(String[] args) {
        System.out.println("Creating network");
        List<Integer> neuronsPerLayer = new ArrayList<>();
        neuronsPerLayer.add(3);
        neuronsPerLayer.add(2);
        neuronsPerLayer.add(1);

        MultiLayerPerceptron mlpOR = new MultiLayerPerceptron(neuronsPerLayer);

        System.out.println("Create training set");
        DataSet trainingSet = new DataSet(3, 1);

        trainingSet.addRow (new DataSetRow(new double[]{0, 0, 0},
                new double[]{0}));
        trainingSet.addRow (new DataSetRow (new double[]{0, 1, 0},
                new double[]{1}));
        trainingSet.addRow (new DataSetRow (new double[]{1, 0, 0},
                new double[]{1}));
        trainingSet.addRow (new DataSetRow (new double[]{1, 1, 0},
                new double[]{1}));
        trainingSet.addRow (new DataSetRow(new double[]{0, 0, 1},
                new double[]{1}));
        trainingSet.addRow (new DataSetRow (new double[]{0, 1, 1},
                new double[]{1}));
        trainingSet.addRow (new DataSetRow (new double[]{1, 0, 1},
                new double[]{1}));
        trainingSet.addRow (new DataSetRow (new double[]{1, 1, 1},
                new double[]{1}));

        System.out.println("Start learning");
        mlpOR.learn(trainingSet);

        System.out.println("Feed input");
//        mlpOR.setInput(0,0,0);
//        mlpOR.setInput(1,0,0);
//        mlpOR.setInput(0,1,0);
//        mlpOR.setInput(0,0,1);
//        mlpOR.setInput(1,1,0);
//        mlpOR.setInput(1,0,1);
        mlpOR.setInput(1,1,1);

        mlpOR.calculate();

        double[] outputs = mlpOR.getOutput();

        for(double output : outputs){
            System.out.println(output);
        }

    }
}
