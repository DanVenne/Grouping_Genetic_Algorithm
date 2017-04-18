/**
 * The GeneticAlgorithm Class
 * This class contains the Group Genetic Algorithm methods
 * */

import java.util.*;
import java.util.List;

class GeneticAlgorithm {

    // Parameters
    private static final int MAX_ITERATIONS         = 300;      // maximum iterations
    private static final double MUTATION_RATE       = 0.15;     // initial likeliness of mutation
    private static final double INVERSION_RATE      = 0.05;     // likeliness of inversion
    private static final int NUMBER_OF_POPULATIONS  = 300;      // number of candidate solutions
    private static final int ELITISM                = 2;       // number of candidates carried into next epoch
    private static final int TOURNAMENT_SIZE        = 2;       // size of tournament selection
    private static final int MAX_CONSECUTIVE        = 500;      // max consecutive results without increase in GH

    // Constants
    private static final int STUDENTS_PER_GROUP     = 4;        // students per group
    private static final int MIN_MEMBER_DIST        = 2;        // minimum euclidean distance between group members

    private List<List<Integer>> studentData = new ArrayList<>();
    private List<Population> populations = new ArrayList<>();
    private Map<Integer, Double> epochFitness = new HashMap<>();

    private Population bestPopulation;
    private int epoch;

    public GeneticAlgorithm(InputData inputData) {
        epochFitness.put(0, 0.0);

        studentData = inputData.getStudentData();

        //ensure num students / group size makes sense
        if (!(studentData.size() % getStudentsPerGroup() == 0)) {
            System.out.println("Error: number of student records not divisible by number of groups");
            return;
        }

        List<Student> students = createStudents();
        List<Integer> studentIndex = new ArrayList<>();
        for (int j = 0; j < students.size(); j++) {
            studentIndex.add(j);
        }

        // create NUMBER_OF_POPULATIONS populations
        for (int i = 0; i < NUMBER_OF_POPULATIONS; i++) {

            // shuffle student data
            Collections.shuffle(studentIndex);

            // create list of population groups
            List<Group> chromosome = new ArrayList<>();

            for (int k = 3; k < studentIndex.size(); k += STUDENTS_PER_GROUP) {

                // Create list of students to add to group
                List<Student> studentGroup = new ArrayList<>();
                studentGroup.add(students.get(studentIndex.get(k - 3)));
                studentGroup.add(students.get(studentIndex.get(k - 2)));
                studentGroup.add(students.get(studentIndex.get(k - 1)));
                studentGroup.add(students.get(studentIndex.get(k)));

                // create group
                Group group = new Group(studentGroup);
                chromosome.add(group);
            }

            // create population with shuffled data
            Population population = new Population(chromosome);

            populations.add(population);
        }


    }

    /**
     * Evolution Events
     */
    public void evolve() {

        /** Evolve until termination criteria is met
         * Hybrid Criteria from Michael Mutingi, Charles Mbohwa, Grouping Genetic Algorithms: Advances and Applications
         *  Input: itCount, itWithoutImp, maxItCount, ?, ?
         *  Terminate = false //initialize terminate;
         *  Compute ? = f (current conditions) //adjust parameters ?, ? ;
         *  Compute ? = f (current conditions);
         *  If (itCount ? ?·maxItCount & itWithoutImp ??·maxItWithoutImp) Then
         *   terminate = true;
         *  return terminate
         * */


        boolean terminate =false;
        while (!terminate) {

            //advance epoch
            epoch++;

            // Create new empty group container to populate with next generation
            List<Population> newPopulations = new ArrayList<>();

            // Elitism
            newPopulations.addAll(getElitePopulations());

            // To keep population full, loop through selection and crossover procedures
            for (int k = 0; k < (NUMBER_OF_POPULATIONS - evenElitism()) / 2; k++) {

                // Selection - select two parent solutions
                Population firstParent = populations.get(tournamentSelect());
                Population secondParent = populations.get(tournamentSelect());

                // Crossover - create child candidate solutions from two parent solutions

                //Population child = crossoverChild(firstParent, secondParent);
                List<Population> children = crossover(firstParent, secondParent);

                // Generate random numbers
                Random random = new Random();
                List<Population> mutatedChildren = new ArrayList<>();

                // Randomly mutate and invert certain children
                for (Population p : children) {
                    if (random.nextDouble() < MUTATION_RATE) {
                        mutatedChildren.add(mutate(p));
                    } else {
                        if (random.nextDouble() < INVERSION_RATE) {
                            mutatedChildren.add(invert(p));
                        } else {
                            mutatedChildren.add(p);
                        }
                    }
                }

                newPopulations.addAll(mutatedChildren);
            }

            // Replace population with new population
            populations = newPopulations;


            // Record data for export
            updateBestPopulation();
            updateEpochFitness();

            terminate = terminate();
        }
    }

    public Population getBestPopulation() {
        return bestPopulation;
    }

    public int getNumberOfPopulations() {
        return NUMBER_OF_POPULATIONS;
    }

    public int getMaxIterations() {
        return MAX_ITERATIONS;
    }

    public double getMutationRate() {
        return MUTATION_RATE;
    }

    public double getInversionRate() {
        return INVERSION_RATE;
    }

    public int getElitism() {
        return ELITISM;
    }

    public int getTournamentSize() {
        return TOURNAMENT_SIZE;
    }

    /***
     * Inversion method randomly swaps two groups in population
     * @param initialPopulation
     * @return
     */
    private Population invert(Population initialPopulation) {

        // Get mutable list of groups
        List<Group> groups = initialPopulation.getGroups();

        // Select 2 groups at random
        List<Integer> invertGroupID = getCuts();

        Collections.swap(groups, invertGroupID.get(0), invertGroupID.get(1));

        if (!(groups.size() == 128)) {
            System.out.println("Wrong size population created in mutate method. Population size: " + groups.size());
        }

        // return mutated population
        return new Population(groups);
    }

    /***
     * Randomly modifies a population by modifying groups
     * Attempts to maximize GH of newly created groups
     * @param initialPopulation population selected for mutation
     * @return
     */
    private Population mutate(Population initialPopulation) {

        // Get mutable list of groups
        List<Group> groups = initialPopulation.getGroups();

        // Select between 2 and 4 groups at random
        Random random = new Random();
        List<Group> randomGroups = new ArrayList<>();

        int selectedCount = 0;


        // Make sure at least 2 groups are selected
        while (selectedCount < 2) {

            // Loop through all groups and randomly select
            for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
                Group group = iterator.next();
                if (group.getMaxDistance() < MIN_MEMBER_DIST) {
                    randomGroups.add(group);
                    iterator.remove();
                    selectedCount++;
                } else if (random.nextDouble() < 0.015) {
                    randomGroups.add(group);
                    iterator.remove();
                    selectedCount++;
                }
            }
        }

        // Get ranked students from groups
        groups.addAll(groupSelectedStudents(randomGroups));

        if (!(groups.size() == 128)) {
            System.out.println("Wrong size population created in mutate method. Population size: " + groups.size());
        }

        // return mutated population
        return new Population(groups);
    }

    /***
     * Conditions for terminating Genetic Algorithm search
     * @return true to terminate loop
     */
    private boolean terminate() {

        // epoch at least 2
        if (epoch < 2) {
            return false;
        }

        // reaches max iterations
        if (epoch >= MAX_ITERATIONS) {
            // cannot terminate if min Euclidean distance < MIN_MEMBER_DIST
            if (bestPopulation.getMinEuclideanDistance() < (MIN_MEMBER_DIST - 0.01)) {
                return false;
            }
            return true;
        }


        // terminate if fitness doesn't improve in MAX_CONSECUTIVE epoch
        if (epoch > MAX_CONSECUTIVE && Double.compare(epochFitness.get(epoch - 1), epochFitness.get(epoch - MAX_CONSECUTIVE - 1)) == 0) {
            return true;
        }

        return false;
    }

    /***
     * Returns an ArrayList of all Students in a List of Groups
     * @param selectedGroups list of Groups
     * @return
     */
    private List<Group> groupSelectedStudents(List<Group> selectedGroups) {

        List<Student> selectedStudents = studentsFromGroup(selectedGroups);

        // Collections.shuffle(selectedStudents, new Random());
        Collections.sort(selectedStudents, (s1, s2) -> {
            if (s1.getScore() > s2.getScore()) {
                return 1;
            }
            if (s1.getScore() == s2.getScore()) {
                return 0;
            }
            return -1;
        });//*/

        /** Modify order from 1, 2, 3, 4, ..., n to 1, 1, 1, 1, 2, 2, ..., n
            This gives a better GH when reassembled */

        List<Group> groups = new ArrayList<>();

        int numberOfGroups = (selectedStudents.size() / GeneticAlgorithm.getStudentsPerGroup());
        // Organize students
        for (int i = 0; i < numberOfGroups; i++) {
            List<Student> groupStudent = new ArrayList<>();
            for (int j = i; j < 4 * numberOfGroups; j += numberOfGroups ) {
                groupStudent.add(selectedStudents.get(j));
            }
            groups.add(new Group(groupStudent));
        }

        return groups;
    }

    /***
     * getElitePopulations method
     * @return returns a collections of n populations where n is the
     * number of groups to be retained from one generation to the next
     */
    private List<Population> getElitePopulations() {
        List<Population> elitePopulations = new ArrayList<>();

        // Elitism
        int evenElitism = evenElitism();

        // Get the evenElitism populations with highest GH
        Queue<Population> rankedPopulations = new PriorityQueue<>(populations.size(), (a,b) -> (int)(b.getPopulationFitness() - a.getPopulationFitness()));
        rankedPopulations.addAll(populations);
        for (int i = 0; i < evenElitism; i++) {
            elitePopulations.add(rankedPopulations.poll());
        }

        return elitePopulations;
    }

    private int evenElitism() {
        return (ELITISM % 2 == 0) ? ELITISM : ELITISM + 1;
    }

    /***
     * Crossover Method
     * @param parent1, parent2 are the two parent populations
     *
     */
    private List<Population> crossover(Population parent1, Population parent2) {

        List<Population> children = new ArrayList<>();

        // list of groups for new populations
        List<Group> child1Groups = new ArrayList<>();
        List<Group> child2Groups = new ArrayList<>();

        // create child by creating creating a new list of students that can be adding to a Population object
        List<Integer> cuts = getCuts(); // two positions for cutting chromosome

        int cut1 = cuts.get(0);
        int cut2 = cuts.get(1);

        List<Group> groupsFromP1 = parent1.getGroups().subList(cut1, cut2);
        List<Group> groupsFromP2 = parent2.getGroups().subList(cut1, cut2);

        List<Student> studentsRemovedFromParent1 = studentsFromGroup(groupsFromP1);
        List<Student> studentsRemovedFromParent2 = studentsFromGroup(groupsFromP2);

        /**
         * Students removed from groups take from parent that were not in
         * groups received from other parent
         * Use a queue for FIFO student retrieval
          */
        Queue<Student> studentsToAddToChild1 = new LinkedList<>(); // removed in studentsRemovedFromParent1 and not replaced
        Queue<Student> studentsToAddToChild2 = new LinkedList<>(); // removed in studentsRemovedFromParent2 and not replaced
        for (int i = 0; i < studentsRemovedFromParent1.size(); i++) {

            // check both lists
            if (!(studentsRemovedFromParent2.contains(studentsRemovedFromParent1.get(i)))) {
                studentsToAddToChild1.add(studentsRemovedFromParent1.get(i));
            }
            if (!(studentsRemovedFromParent1.contains(studentsRemovedFromParent2.get(i)))) {
                studentsToAddToChild2.add(studentsRemovedFromParent2.get(i));
            }
        }


        List<Student> parent1Students = studentsFromGroup(parent1.getGroups());
        List<Student> parent2Students = studentsFromGroup(parent2.getGroups());
        for (int i = 0; i < (studentData.size() / STUDENTS_PER_GROUP); i++) {  // code below skips i between cut1 and cut2

            // when i reaches cut1, add crossover groups and continue at cut2
            if (i == cut1) {
                // between cuts
                child1Groups.addAll(groupsFromP2);
                child2Groups.addAll(groupsFromP1);
                i = cut2;
            }

            // for each group in parent1 add each student if they aren't in student to remove list
            List<Student> students1 = new ArrayList<>();
            List<Student> students2 = new ArrayList<>();
            for (int j = 0; j < STUDENTS_PER_GROUP; j++) {
                Student student1 = parent1Students.get((i * STUDENTS_PER_GROUP) + j);
                if (!(studentsRemovedFromParent2.contains(student1))) {
                    students1.add(student1);
                } else {
                    students1.add(studentsToAddToChild1.poll());
                }

                Student student2 = parent2Students.get((i * STUDENTS_PER_GROUP) + j);
                if (!(studentsRemovedFromParent1.contains(student2))) {
                    students2.add(student2);
                } else {
                    students2.add(studentsToAddToChild2.poll());
                }

            }
            child1Groups.add(new Group(students1));
            child2Groups.add(new Group(students2));
        }

        Population firstChild = new Population(child1Groups);
        Population secondChild = new Population(child2Groups);

        children.add(firstChild);
        children.add(secondChild);

        return children;

    }

    private void updateEpochFitness() {
        // updates epoch fitness list
        epochFitness.put(epoch, getEpochBestFitness());
        System.out.println("Epoch:  " + epoch + "    |    Best GH: " + epochFitness.get(epochFitness.size() - 1));
    }

    private double getEpochBestFitness() {
        double bestFitness = 0.0;
        for (Population p : populations) {
            if (p.getPopulationFitness() > bestFitness) {
                bestFitness = p.getPopulationFitness();
            }
        }

        return bestFitness;
    }

    private void updateBestPopulation() {

        Population epochBestPopulation = getEpochBestPopulation();

        if (epoch == 1) {
            this.bestPopulation = epochBestPopulation;
            return;
        }

        // updates best population each epoch
        // check if solution is better than current
        Double currentBestFitness = epochFitness.get(epoch - 1);
        Double previousBestFitness = epochFitness.get(epoch - 2);

        if (currentBestFitness > previousBestFitness) {
            this.bestPopulation = epochBestPopulation;
        }
    }

    public void displayBestPopulation() {
        System.out.println("The Best Population");
        int index = 1;
        for (Group g : bestPopulation.getGroups()) {
            System.out.println("Group: " + index);
            for (Student s : g.getStudents()) {
                System.out.println("Student: " + s.getStudentID() + "  |  Score: " + s.getScore());
            }
            System.out.println("Group GH: " + g.getGH());
            System.out.println("Euclidean distance: " + g.getMaxDistance());
        index++;
        }
        System.out.println("Final GH: " + epochFitness.get(epochFitness.size() - 1));
    }

    private Population getEpochBestPopulation() {

        Stack<Population> populationStack = new Stack<>();
        populationStack.push(populations.get(0));

        //Iterate through populations and
        for (Population population : populations) {
            if (population.getPopulationFitness() >= populationStack.peek().getPopulationFitness()) {
                populationStack.push(population);
            }
        }

        return populationStack.pop();
    }


    public static int getStudentsPerGroup() {
        return STUDENTS_PER_GROUP;
    }


    /***
     * studentsFromGroup method
     * returns list of students from list of groups
     *
     * */
    private List<Student> studentsFromGroup(List<Group> childGroups) {
        List<Student> childStudents = new ArrayList<>();

        for (Group childGroup : childGroups) {
            for (Student s : childGroup.getStudents()) {
                childStudents.add(s);
            }
        }

        return childStudents;
    }


    private List<Integer> getCuts() {
        // Randomly select two points to cut the chromosome
        Random random = new Random();
        List<Integer> cuts = new ArrayList<>();
        int r1 = random.nextInt((studentData.size() / STUDENTS_PER_GROUP) - 1) + 1;
        int r2 = random.nextInt((studentData.size() / STUDENTS_PER_GROUP) - 1) + 1;

        while(r2 == r1) {
            r2 = random.nextInt(studentData.size() / STUDENTS_PER_GROUP);
        }

        cuts.add(r1);
        cuts.add(r2);
        Collections.sort(cuts);

        return cuts;
    }

    // Select candidate solution to be parent
    private int tournamentSelect() {

        List<Population> selection = new ArrayList<>();

        // Randomly choose selection of groups based on tournament size
        Random random = new Random();

        List<Integer> index = new ArrayList<>();

        for (int i = 0; i < GeneticAlgorithm.TOURNAMENT_SIZE; i++) {

            index.add(random.nextInt(populations.size()));
            Population selectedPopulation = populations.get(index.get(i));

            // Add random groups to the tournament
            selection.add(selectedPopulation);
        }

        int winner = 0;
        int winningIndex = 0;
        // Select winner
        for (int j = 0; j < selection.size(); j++) {
            if (selection.get(j).getPopulationFitness() > selection.get(winningIndex).getPopulationFitness()) {
                winner = index.get(j);
                winningIndex = j;
            }
        }

        // At random, with low probability, select a different winner
        if (random.nextDouble() < 0.1) {
            winner = index.get((int)(random.nextDouble() * selection.size()));
        }

        return winner;
    }

    private List<Student> createStudents() {
        List<Student> students = new ArrayList<>();

        for (List<Integer> A : studentData) {
            Student student = new Student(A);

            // add student to collection
            students.add(student);
        }

        return students;
    }
}