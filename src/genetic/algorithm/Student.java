import java.util.ArrayList;
import java.util.List;

public final class Student {

    private final int studentID;
    private final int groupWorkAttitude;
    private final int mathInterest;
    private final int motivation;
    private final int selfConfidence;
    private final int shyness;
    private final int mathPerformance;
    private final int englishPerformance;
    private final int score;

    private final List<Integer> attributes;

    public Student (List<Integer> attributes) {
        this.attributes = attributes;

        this.studentID = attributes.get(0);
        this.groupWorkAttitude = attributes.get(1);
        this.mathInterest = attributes.get(2);
        this.motivation = attributes.get(3);
        this.selfConfidence = attributes.get(4);
        this.shyness = attributes.get(5);
        this.mathPerformance = attributes.get(6);
        this.englishPerformance = attributes.get(7);

        //calculate student score
        this.score = calculateScore();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + studentID;

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Student other = (Student)obj;
        return studentID == other.studentID;

    }

    public List<Integer> getAttributes() {
        return attributes;
    }

    public int getStudentID() {
        return studentID;
    }

    public double getScore() {
        return score;
    }


    private int calculateScore() {
        return groupWorkAttitude + mathInterest + motivation + selfConfidence +
                shyness + mathPerformance + englishPerformance;
    }




}
