import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Problem {
    private int rows;
    private int columns;
    private List<Integer> costs;
    private List<List<Integer>> rowCoverings;
    private List<List<Integer>> columnCoverings;

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public List<Integer> getCosts() {
        return costs;
    }

    public void setCosts(List<Integer> costs) {
        this.costs = costs;
    }

    public List<List<Integer>> getRowCoverings() {
        return rowCoverings;
    }

    public void setRowCoverings(List<List<Integer>> rowCoverings) {
        this.rowCoverings = rowCoverings;
    }

    public List<List<Integer>> getColumnCoverings() {
        return columnCoverings;
    }

    public void setColumnCoverings(List<List<Integer>> columnCoverings) {
        this.columnCoverings = columnCoverings;
    }

    public void readFile(String inputFile){
        File file = new File(inputFile);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new java.io.FileReader(file));
            String size = reader.readLine();
            String[] sizes = size.trim().split("\\s+");
            rows = Integer.parseInt(sizes[0]);
            columns = Integer.parseInt(sizes[1]);

            costs = new ArrayList<Integer>();

            int addedCosts = 0;

            while(addedCosts < columns){
                String line = reader.readLine();
                String[] temp_costs = line.trim().split("\\s+");
                for (int j = 0;j < temp_costs.length;j++){
                    costs.add(Integer.parseInt(temp_costs[j]));
                    addedCosts++;
                }
            }

            rowCoverings = new ArrayList<List<Integer>>();

            for (int i = 0;i < rows;i++){ // Collect columns for each row
                int noColumns = Integer.parseInt(reader.readLine().trim().split("\\s+")[0]);
                int addedColumns = 0;
                List<Integer> singleRowCoverings = new ArrayList<Integer>();
                while(addedColumns < noColumns){
                    String line = reader.readLine();
                    String[] temp_columns = line.trim().split("\\s+");
                    for (int k = 0;k < temp_columns.length;k++){
                        singleRowCoverings.add(Integer.parseInt(temp_columns[k]));
                        addedColumns++;
                    }
                }
                rowCoverings.add(singleRowCoverings);

            }

            columnCoverings = new ArrayList<List<Integer>>();

            for (int i = 1;i <= columns;i++){
                List<Integer> singleColumnCoverings = new ArrayList<Integer>(); // Each column covers this set of rows
                for (int j = 1;j <= rows;j++){
                    List<Integer> singleRowCoverings = rowCoverings.get(j - 1);
                    if (singleRowCoverings.contains(i)){
                        singleColumnCoverings.add(j);
                    }
                }
                columnCoverings.add(singleColumnCoverings);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
