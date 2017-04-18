import java.io.File;
import java.nio.file.Paths;

public class GGA {

    public GGA() {

    }

    public static void main(String[] args) {
        File file = new File (Paths.get(".").toAbsolutePath().normalize().toString() + "/src/data/input.txt");

        //create inputData structure
        InputData inputData = new InputData(file);

        //Create a Genetic Algorithm object
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(inputData);

        geneticAlgorithm.evolve();

        geneticAlgorithm.displayBestPopulation();

        Solution solution = new Solution(geneticAlgorithm);
        solution.printSolutionToFile();



    }

}
