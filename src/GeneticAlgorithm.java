import java.util.*;

public class GeneticAlgorithm {
    private BitSet[] population;
    private int populationSize;
    private int runningTime;
    private int t;
    private int tournamentSize;
    private int[] populationFitness;
    private Problem problem;

    private List<Map.Entry<Long,Integer>> log = new ArrayList<>();
    private Random rnd = new Random();

    public GeneticAlgorithm(Problem problem, int populationSize, int runningTime, int tournamentSize) {
        this.problem = problem;
        this.populationSize = populationSize;
        this.runningTime = runningTime;
        this.tournamentSize = tournamentSize;

        this.initialise();
    }

    private BitSet tournamentSelection(){
        BitSet best = null;
        int bestFitness = Integer.MAX_VALUE;
        for (int i = 0;i < this.tournamentSize;i++){
            int random = this.rnd.nextInt(this.population.length);
            BitSet individual = this.population[random];
            if ((best == null) || (this.populationFitness[random] < bestFitness)) {
                best = individual;
                bestFitness = this.populationFitness[random];
            }
        }
        return best;
    }

    private BitSet crossover(BitSet parentOne, BitSet parentTwo){
        BitSet newSolution = new BitSet();
        for (int i = 0;i < parentOne.length();i++){
            if (parentOne.get(i) == parentTwo.get(i))
                newSolution.set(i, parentOne.get(i));
            else{
                double prob = this.fitness(parentTwo) / (this.fitness(parentOne) + this.fitness(parentTwo));
                if (this.rnd.nextDouble() >= prob)
                    newSolution.set(i, parentOne.get(i));
                else
                    newSolution.set(i, parentTwo.get(i));
            }
        }
        return newSolution;
    }

    private boolean isUnique(BitSet solution){
        for (int i = 0;i < this.populationSize;i++){
            BitSet xor = (BitSet)solution.clone();
            xor.xor(this.population[i]);
            if (xor.length() == 0)
                return false;
        }
        return true;
    }

    private int fitness(BitSet solution){
        int fitness = 0;
        for (int i = 0;i < solution.length();i++){
            if (solution.get(i)){
                fitness += this.problem.getCosts().get(i);
            }
        }
        return fitness;
    }

    private BitSet mutation(BitSet solution){
        solution.flip(this.rnd.nextInt(solution.length()));
        return solution;
    }

    private BitSet makeFeasible(BitSet solution){
        return Solution.makeFeasible(solution, this.problem);
    }

    private void replace(BitSet solution){
        if (solution != null){
            int averageFitness = 0;
            for (int i = 0;i < this.populationFitness.length;i++)
                averageFitness += this.populationFitness[i];
            averageFitness = (int)averageFitness / this.populationFitness.length;
            boolean isReplaced = false;
            while (!isReplaced){
                int random = this.rnd.nextInt(this.population.length);
                if (this.populationFitness[random] > averageFitness){
                    this.population[random] = solution;
                    this.populationFitness[random] = this.fitness(solution);
                    isReplaced = true;
                }
            }
        }
    }

    private void evolve(){
        boolean unique = false;
        BitSet newSolution = null;
        while (!unique) {
            BitSet parentOne = this.tournamentSelection();
            BitSet parentTwo = this.tournamentSelection();
            newSolution = this.crossover(parentOne, parentTwo);
            newSolution = this.mutation(newSolution);
            newSolution = this.makeFeasible(newSolution);
            unique = this.isUnique(newSolution);
        }
        this.replace(newSolution);
        t++;
    }

    private void generateInitialPopulation(){
        population = new BitSet[this.populationSize];
        for (int i = 0;i < this.populationSize;i++){
            this.population[i] = Solution.createSolution(this.problem);
        }
    }

    private void calculateAllFitness(){
        populationFitness = new int[this.populationSize];
        for (int i = 0;i < this.populationSize;i++){
            this.populationFitness[i] = this.fitness(this.population[i]);
        }
    }

    public void initialise(){
        this.t = 0;
        this.generateInitialPopulation();
        this.calculateAllFitness();
    }

    private int getBestFitness(){
        int bestFitness = Integer.MAX_VALUE;
        for (int i = 0;i < this.populationSize;i++){
            if (this.populationFitness[i] < bestFitness)
                bestFitness = this.populationFitness[i];
        }
        return bestFitness;
    }

    private boolean isConverged(){
        if (this.log.isEmpty() || this.log.size() < 60)
            return false;
        int bestFitness = this.log.get(this.log.size() - 1).getValue();
        for (int i = 1;i < 60;i++){
            int temp = this.log.get(this.log.size() - i).getValue();
            if (temp != bestFitness) {
                return false;
            }
        }
        return true;
    }

    public void train(){
        long startTime = System.currentTimeMillis();
        while (!this.isConverged() && (((System.currentTimeMillis() - startTime) / 1000) / 60) < this.runningTime){
            if ((System.currentTimeMillis() - startTime) % 1000 == 0)
                this.log.add(new AbstractMap.SimpleEntry<>((System.currentTimeMillis() - startTime), this.getBestFitness()));
            this.evolve();
        }
    }

    public List<Map.Entry<Long, Integer>> getLog() {
        return this.log;
    }
}
