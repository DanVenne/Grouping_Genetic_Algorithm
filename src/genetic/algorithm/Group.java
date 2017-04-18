import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class Group {

    private final List<Student> students = new ArrayList<>();   // List of students in group
    private final double AD;                                    // Group average distance
    private final double GH;                                    // Group heterogeneity factor
    private final double maxDistance;                           // Max euclidean distance between group members

    public Group(List<Student> students) {

        if (students.size() != GeneticAlgorithm.getStudentsPerGroup()) {
            throw new IllegalArgumentException ("A complete group must have: " +
                    GeneticAlgorithm.getStudentsPerGroup() + " students!");
        }

        // Add student list
        for (Student s : students) {
            this.students.add(s);
        }

        //Calculate average distance
        this.AD = calculateAD();

        // Calculate group heterogeneity factor
        this.GH = calculateGH();

        // Calculate max euclidean distance
        this.maxDistance = calculateMaxDistance();

    }

    /***
     * Override equals so that Population can remove group by object reference
     * @param arg0 Group being compared
     * @return
     */
    @Override
    public boolean equals(Object arg0) {
        if (this == arg0) {
            return true;
        }
        if (arg0 == null) {
            return false;
        }
        if (getClass() != arg0.getClass()) {
            return false;
        }

        if (!(students.size() == ((Group)arg0).getStudents().size())) {
            return false;
        }

        for (int i = 0; i < students.size(); i++) {
            if (!(students.get(i).getStudentID() == ((Group)arg0).getStudents().get(i).getStudentID())) {
                return false;
            }
        }

        return true;
    }


    // Return students list
    public List<Student> getStudents() {
        return students;
    }

    // Return Heterogeneity of group
    public double getGH() {
        return GH;
    }

    // Return average distance of group
    public double getAD() {
        return AD;
    }

    // Return max euclidean distance of group
    public double getMaxDistance() {
        return maxDistance;
    }

    private List<Student> getOrderedStudents() {
        List<Student> orderedStudents = new ArrayList<>();

        for (Student s : students) {
            orderedStudents.add(s);
        }

        // Sort list by score
        orderedStudents.sort(Comparator.comparing(Student::getScore));

        return orderedStudents;
    }

    // Calculate Group average distance
    private double calculateAD() {

        // Get ordered student list
        List<Student> orderedStudents = getOrderedStudents();

        return (orderedStudents.get(3).getScore() + orderedStudents.get(0).getScore()) / 2;
    }

    // Calculate Group Heterogeneity Factor
    private double calculateGH() {

        // Get ordered student list
        List<Student> orderedStudents = getOrderedStudents();

        return (orderedStudents.get(3).getScore() - orderedStudents.get(0).getScore()) /
                (1 + (Math.abs(getAD() - orderedStudents.get(1).getScore())) + (Math.abs((getAD() - orderedStudents.get(2).getScore()))));
    }

    // Calculate group max euclidean distance
    private double calculateMaxDistance() {

        List<Double> euclideanDistances = new ArrayList<>();
        // There are six combinations try them all

        euclideanDistances.add(euclideanDistance(students.get(0), students.get(1)));
        euclideanDistances.add(euclideanDistance(students.get(0), students.get(2)));
        euclideanDistances.add(euclideanDistance(students.get(0), students.get(3)));
        euclideanDistances.add(euclideanDistance(students.get(1), students.get(2)));
        euclideanDistances.add(euclideanDistance(students.get(1), students.get(3)));
        euclideanDistances.add(euclideanDistance(students.get(2), students.get(3)));

        // Sort in descending order
        Collections.sort(euclideanDistances, (a, b) -> b.compareTo(a));

        return euclideanDistances.get(0);
    }

    // Calculate euclidean distance between two students
    private double euclideanDistance(Student s1, Student s2) {

        List<Integer> s1Attributes = s1.getAttributes();
        List<Integer> s2Attributes = s2.getAttributes();

        double a1 = (s1Attributes.get(1) - s2Attributes.get(1));
        double a2 = (s1Attributes.get(2) - s2Attributes.get(2));
        double a3 = (s1Attributes.get(3) - s2Attributes.get(3));
        double a4 = (s1Attributes.get(4) - s2Attributes.get(4));
        double a5 = (s1Attributes.get(5) - s2Attributes.get(5));
        double a6 = (s1Attributes.get(6) - s2Attributes.get(6));
        double a7 = (s1Attributes.get(7) - s2Attributes.get(7));

        return Math.sqrt((a1 * a1) + (a2 * a2) + (a3 * a3) + (a4 * a4) + (a5 * a5) + (a6 * a6) + (a7 * a7));
    }

}
