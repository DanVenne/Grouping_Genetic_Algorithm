# Grouping_Genetic_Algorithm
Genetic Algorithm Implementation for Heterogeneous Grouping

The general Grouping Genetic Algorithm proposed by Falkenauer [1] is comprised of the following steps:
  1.	Form the initial population of chromosomes. 
    a.	The encoding chromosomes must be valid for clustering.
  2.	Calculate the fitness of each chromosome.
  3.	Select the parent chromosomes by the operator based on rank.
  4.	Perform crossover operation to drive children chromosomes by combining parent chromosomes.
  5.	Perform mutation to avoid uniformity population, with dynamic probability.
  6.	Replace the new population that has been created by three former steps. 

Selection

A tournament selection is used to pick suitable candidate solutions.  A tournament size is chosen, and candidate solutions are chosen at random from the list of all populations in the epoch.  From the chosen candidate solutions, the solution with the highest fitness (total GH) is selected as the winner of the tournament and is added as a parent for the particular breeding iteration.  Tournament selection is used twice to select two parents.

Crossover

The crossover algorithm proposed by Falkenauer [1] is:
  1.	Select parents and random crossover points
  2.	Genes belonging to the selected group are copied from the first parent to child.
  3.	Genes belonging to the second person are copied if it is not specified in the first one.
  4.	A group is determined randomly for the genes that do not belong to a group.
  5.	The cluster is deleted if it is empty.
  6.	Members of the chromosomes of child are renamed.

The algorithm is modified such that constraints on group formation can be added.

Mutation

The mutation algorithm is a blend of split and swap mutation algorithms.
The implemented algorithm is:
  1.	If population is chosen for mutation
  2.	Iterate through each of the groups in the population until at least 2 groups are selected
  3.	If the group max Euclidean distance is less than 2 add to selection
  4.	Else if the group is randomly selected (probability 1.5%) add to selection
  5.	Sort students from highest to lowest score
  6.	Add students from highest score to lowest score one at a time iterating through each student through k / 4 groups where k is number of students in all the selected groups

This algorithm does a better approximation of maximizing heterogeneity of the newly formed groups than random shuffling does.

Inversion

The inversion algorithm simply randomly selects two groups in a chromosome and swaps them.

Evaluation

Upon creation of a new group, the minimum Euclidean distance between members, AD, and GH are measured.  Upon each addition of a population to an epoch, total GH is calculated as the sum of all group GH values.  Each epoch the candidate solution with the highest total GH is added with the epoch number to a list for future retrieval.

Termination Condition

The algorithm uses a multi-stage termination condition:
  1.	First, it ensures at least 2 epochs have passed.
  2.	Next, regardless of how many iterations have passed, the algorithm is not satisfied until the problem constraints are met
  3.	Finally, the algorithm will terminate if it either reaches the MAX_ITERATIONS limit, or if the total GH does not improve over MAX_CONSECUTIVE iterations.


[1]	Falkenauer, E. 1996. A hybrid grouping genetic algorithm for bin packing. Journal of Heuristics, 2, 1, pp. 5–30, 1996. DOI= 10.1007/BF00226291
