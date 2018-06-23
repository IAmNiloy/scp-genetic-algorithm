import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args){
        int populationSize = 100;

        int maxRunningTime = 30; // Max running time in minutes

        int tournamentSize = 2;

        String fileList = "data/Instances/filelist.txt";
        File fileListFile = new File(fileList);
        BufferedReader reader;
        List<String> files = new ArrayList<>();
        try {
            reader = new BufferedReader(new java.io.FileReader(fileListFile));
            String line;
            while ((line = reader.readLine()) != null)
            files.add(line.trim());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        String fileDirectory = "data/Instances/";

        long startTime = System.currentTimeMillis();

        for (String file : files) {
            System.out.println("Running file " + file);
            Problem problem = new Problem();

            problem.readFile(fileDirectory + file);

            GeneticAlgorithm ga = new GeneticAlgorithm(problem, populationSize, maxRunningTime, tournamentSize);

            System.out.println("Initialising initial population");
            ga.initialise();
            System.out.println("Completed initialising initial population");

            ga.train();

            List<Map.Entry<Long, Integer>> log = ga.getLog();

            try{
                PrintWriter writer = new PrintWriter("data/Output/" + file, "UTF-8");
                for (Map.Entry<Long, Integer> entry : log) {
                    writer.println(entry.getKey() + "," + entry.getValue());
                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long elapsedTime = (((System.currentTimeMillis() - startTime) / 1000) / 60);
        System.out.println("Total time: " + elapsedTime);
    }
}
