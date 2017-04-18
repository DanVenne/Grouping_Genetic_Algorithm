import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Solution to export
 */
public final class Solution {

    private final GeneticAlgorithm geneticAlgorithm;
    private final File file;

    public Solution (GeneticAlgorithm geneticAlgorithm) {

        this.geneticAlgorithm = geneticAlgorithm;
        this.file = createCSVFile();
    }


    public void printSolutionToFile() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {

            // write parameters
            bufferedWriter.write("Populations,Iterations,Mutation Rate,Inversion Rate,Elitism,Tournament Size,Sum of GH");
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(geneticAlgorithm.getNumberOfPopulations()) + ","
                    + String.valueOf(geneticAlgorithm.getMaxIterations()) + ","
                    + String.valueOf(geneticAlgorithm.getMutationRate()) + ","
                    + String.valueOf(geneticAlgorithm.getInversionRate()) + ","
                    + String.valueOf(geneticAlgorithm.getElitism()) + ","
                    + String.valueOf(geneticAlgorithm.getTournamentSize()) + ","
                    + String.valueOf(geneticAlgorithm.getBestPopulation().getPopulationFitness()));
            bufferedWriter.newLine();

            // write headings
            bufferedWriter.write("GroupID,Student1,Student2,Student3,Student4,GroupGH,MaxEucDist");
            bufferedWriter.newLine();

            int groupID = 1;
            // write all groups to file
            for (Group g : geneticAlgorithm.getBestPopulation().getGroups()) {
                bufferedWriter.write(getLine(groupID, g));
                bufferedWriter.newLine();
                groupID++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close output
        }
    }

    private String getLine(int groupID, Group group) {

        return String.valueOf(groupID) + ","
                + String.valueOf(group.getStudents().get(0).getStudentID()) + ","
                + String.valueOf(group.getStudents().get(1).getStudentID())  + ","
                + String.valueOf(group.getStudents().get(2).getStudentID())  + ","
                + String.valueOf(group.getStudents().get(3).getStudentID())  + ","
                + String.valueOf(group.getGH())  + ","
                + String.valueOf(group.getMaxDistance());
    }

    private File createCSVFile() {
        int i = 0;
        String filename = "OutputGASolution" + Integer.toString(i) + ".csv";
        File file = new File(filename);

        while (file.exists()) {
            i++;
            filename = "OutputGASolution" + Integer.toString(i) + ".csv";
            file = new File(filename);
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

}


