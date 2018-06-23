import java.util.*;

public class Solution {

    private static Random rnd = new Random();

    public static BitSet createSolution(Problem problem){
        BitSet genome = new BitSet(problem.getColumns());
        for (int i = 0;i < genome.length();i++){
            genome.set(i, 0); // Zero out genome
        }
        for (int i = 0;i < problem.getRows();i++){
            List<Integer> columnsCoveringRow =  problem.getRowCoverings().get(i);

            Integer randomColumn = columnsCoveringRow.get(rnd.nextInt(columnsCoveringRow.size()));

            genome.set(randomColumn - 1);
        }
        return genome;
    }

    public static BitSet makeFeasible(BitSet solution, Problem problem){
        List<Integer> coveredRows = new ArrayList<Integer>();
        for (int i = 0;i < solution.length();i++){ // For each column
            if (solution.get(i)) {
                coveredRows.addAll(problem.getColumnCoverings().get(i));
            }
        }
        int[] noColumnsCoveringRows = new int[problem.getRows()];
        List<Integer> uncoveredRows = new ArrayList<Integer>();
        for (int i = 0;i < noColumnsCoveringRows.length;i++){ // For each row
            int count = 0;
            for (Integer in : coveredRows){
                if (in == (i + 1))
                    count++;
            }
            if (count == 0)
                uncoveredRows.add(i + 1);
            noColumnsCoveringRows[i] = count;
        }
        for (Integer row : uncoveredRows){
            List<Integer> rowCoverings = problem.getRowCoverings().get(row - 1);
            int column = rowCoverings.get(0);
            solution.set(column - 1);
        }
        return solution;
    }
}
