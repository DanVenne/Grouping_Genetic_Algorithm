import java.util.*;

/**
 * The Population Class
 * This class represents one instance of a candidate solution to the Group Genetic Algorithm problem
 * Can also be considered the Chromosome in the standard Genetic Algorithm
 *
 * Each Population has a fitness function that represents the overall fitness across all the groups
 * of Students contained within the population
 *
 * */

final class Population {

    private final double populationFitness;
    private final List<Group> groups;
    private final double minEuclideanDistance;

    /***
     * Student array is already randomized, so assign in order for random groups
     *
     */
    public Population(List<Group> groups) {

        this.groups = groups;
        this.populationFitness = calculatePopulationFitness();
        this.minEuclideanDistance = calculateMinEuclideanDistance();
    }

    // Return population fitness
    public double getPopulationFitness() {
        return populationFitness;
    }

    // Return min euclidean distance
    public double getMinEuclideanDistance() {
        return minEuclideanDistance;
    }

    public List<Group> getGroups() {
        return groups;
    }

    // calcualte min euclidean distance
    private double calculateMinEuclideanDistance() {
        double minEuclideanDistance = Double.MAX_VALUE;

        for (Group g : groups) {
            if (g.getMaxDistance() < minEuclideanDistance) {
                minEuclideanDistance = g.getMaxDistance();
            }
        }

        return minEuclideanDistance;
    }

    // calculate population fitness
    private double calculatePopulationFitness() {

        double fitness = 0.0;

        for (Group g : groups) {
            fitness += g.getGH();
        }

        return fitness;
    }
}