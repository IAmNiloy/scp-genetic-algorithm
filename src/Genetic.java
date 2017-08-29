import java.util.*;

/**
 * Created by jamescobonkerr on 12/11/16.
 */
public class Genetic {
    private int t;
    private Problem problem;
    private int populationSize;
    private BitSet[] population;
    private int[] populationFitness;
    private int runningTime;
    private int tournamentSize;

    private Random rnd = new Random();

    private List<Map.Entry<Long,Integer>> log;

    public Genetic(Problem problem, int populationSize, int runningTime, int tournamentSize) {
        this.problem = problem;
        this.populationSize = populationSize;
        this.runningTime = runningTime;
        this.tournamentSize = tournamentSize;

        this.log = new ArrayList<>();
    }

    public BitSet tournamentSelection(){
        BitSet best = null;
        int bestFitness = Integer.MAX_VALUE;
        for (int i = 0;i < this.tournamentSize;i++){
            int random = rnd.nextInt(population.length);
            BitSet individual = population[random];
            if ((best == null) || (this.populationFitness[random] < bestFitness)) {
                best = individual;
                bestFitness = this.populationFitness[random];
            }
        }
        return best;
    }
//
    public BitSet crossover(BitSet parentOne, BitSet parentTwo){
        BitSet newSolution = new BitSet();
        for (int i = 0;i < parentOne.length();i++){
            if (parentOne.get(i) == parentTwo.get(i))
                newSolution.set(i, parentOne.get(i));
            else{
                double prob = fitness(parentTwo) / (fitness(parentOne) + fitness(parentTwo));
                if (rnd.nextDouble() >= prob) //not sure about this
                    newSolution.set(i, parentOne.get(i));
                else
                    newSolution.set(i, parentTwo.get(i));
            }
        }
        return newSolution;
    }

    public boolean isUnique(BitSet solution){
        for (int i = 0;i < this.populationSize;i++){
            BitSet xor = (BitSet)solution.clone();
            xor.xor(this.population[i]);
            if (xor.length() == 0)
                return false;
        }
        return true;
    }

    public int fitness(BitSet solution){
        int fitness = 0;
        for (int i = 0;i < solution.length();i++){ //for each column
            if (solution.get(i)){
                fitness += problem.getCosts().get(i);
            }
        }
        return fitness;
    }

    public BitSet mutation(BitSet solution){
        solution.flip(rnd.nextInt(solution.length()));
        return solution;
    }

    public BitSet makeFeasible(BitSet solution){
        return Solution.makeFeasible(solution, problem);
    }

    public void replace(BitSet solution){
        if (solution != null){
            int averageFitness = 0;
            for (int i = 0;i < this.populationFitness.length;i++)
                averageFitness += this.populationFitness[i];
            averageFitness = (int) averageFitness / this.populationFitness.length;
            boolean isReplaced = false;
            while (!isReplaced){
                int random = rnd.nextInt(population.length);
                if (this.populationFitness[random] > averageFitness){
                    this.population[random] = solution;
                    this.populationFitness[random] = fitness(solution);
                    isReplaced = true;
                }
            }
        }
    }

    public void evolve(){
        //System.out.println("evolve");
        boolean unique = false;
        BitSet newSolution = null;
        while (!unique) {
            //System.out.println("tournament 1");
            BitSet parentOne = tournamentSelection();
            //System.out.println("tournament 2");
            BitSet parentTwo = tournamentSelection();
            //System.out.println("crossover");
            newSolution = crossover(parentOne, parentTwo);
            //newSolution = child;
            //System.out.println("mutation");
            newSolution = mutation(newSolution);
            //System.out.println("is feasible?" + Solution.isFeasible(newSolution, problem));
            //System.out.println("make feasible");
            newSolution = makeFeasible(newSolution);
            //System.out.println("after make feasible. is feasible?" + Solution.isFeasible(newSolution, problem));
            //System.out.println("is unique");
            unique = isUnique(newSolution);
            //newSolution = child;
        }
        //System.out.println("replace");
        replace(newSolution);
        t++;
    }

    public void generateInitialPopulation(){
        population = new BitSet[this.populationSize];
        for (int i = 0;i < this.populationSize;i++){
            this.population[i] = Solution.createSolution(this.problem);
        }
    }

    public void calculateAllFitness(){
        populationFitness = new int[this.populationSize];
        for (int i = 0;i < this.populationSize;i++){
            this.populationFitness[i] = fitness(this.population[i]);
        }
    }

    public void initialise(){
        t = 0;
        generateInitialPopulation();
        calculateAllFitness();
    }

    public int getBestFitness(){
        int bestFitness = Integer.MAX_VALUE;
        for (int i = 0;i < this.populationSize;i++){
            if (this.populationFitness[i] < bestFitness)
                bestFitness = this.populationFitness[i];
        }
        return bestFitness;
    }

    public boolean isConverged(){
        if (log.isEmpty() || log.size() < 60)
            return false;
        int bestFitness = log.get(log.size() - 1).getValue();
        for (int i = 1;i < 60;i++){
            int temp = log.get(log.size() - i).getValue();
            if (temp != bestFitness) {
                //System.out.println("Converged");
                return false;
            }
        }
        //System.out.println("Converged");
        return true;
    }

    public void train(){
        long startTime = System.currentTimeMillis();
        while (!isConverged() && (((System.currentTimeMillis() - startTime) / 1000) / 60) < this.runningTime){
            if ((System.currentTimeMillis() - startTime) % 1000 == 0)
                log.add(new AbstractMap.SimpleEntry<>((System.currentTimeMillis() - startTime), getBestFitness()));
                //System.out.println("Best fitness\t" + getBestFitness());
            evolve();
        }
        //long elapsedTime = ((System.currentTimeMillis() - startTime) / 1000);
        //System.out.println(elapsedTime);
    }

    public List<Map.Entry<Long, Integer>> getLog() {
        return log;
    }
}
