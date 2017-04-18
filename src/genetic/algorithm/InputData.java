import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final class InputData {

    private final List<List<Integer>> studentData = new ArrayList<>();  // accepts comma-delimited file of type int

    public InputData(File file) {

        // Read file into studentData list
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){

            String line;

            // iterate through lines
            while ((line = bufferedReader.readLine()) != null) {

                // parse line
                List<Integer> studentValues = parseStudentValues(line);
                studentData.add(studentValues);
            }

        } catch (IOException e){

            // handle exception
            e.printStackTrace();
        }
    }

    //return contents of studentData as ArrayList of ArrayList<Integer>
    public List<List<Integer>> getStudentData() {
        return studentData;
    }

    private List<Integer> parseStudentValues(String line) {

        List<Integer> studentValues = new ArrayList<>();

        // parse line of values into array
        String[] valueArray = line.split(",");

        for (String aValueArray : valueArray) {
            studentValues.add(Integer.parseInt(aValueArray));
        }

        return studentValues;
    }

}
