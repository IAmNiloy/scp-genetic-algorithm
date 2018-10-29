import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.PrintWriter;
import java.util.Properties;

public class Main {

    private static final String CONFIG_FILE = "config/config.properties";

    private static final String POPULATION_SIZE = "population_size";
    private static final String MAX_RUNNING_TIME = "max_running_time";
    private static final String TOURNAMENT_SIZE = "tournament_size";
    private static final String FILE_LIST = "file_list";
    private static final String FILE_DIRECTORY = "file_directory";
    private static final String OUTPUT_DIRECTORY = "output_directory";

    private int populationSize;
    private int maxRunningTime; // Max running time in minutes
    private int tournamentSize;
    private String fileList;
    private String fileDirectory;
    private String outputDirectory;

    private List<String> files = new ArrayList<String>();

    private void loadProperties() {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(CONFIG_FILE));
        } catch (IOException e) {
            System.out.println("Config file was either not found, or found with error");
            System.out.println(e);
            System.exit(0);
        }

        this.populationSize = Integer.parseInt(properties.getProperty(POPULATION_SIZE));
        this.maxRunningTime = Integer.parseInt(properties.getProperty(MAX_RUNNING_TIME));
        this.tournamentSize = Integer.parseInt(properties.getProperty(TOURNAMENT_SIZE));
        this.fileList = properties.getProperty(FILE_LIST);
        this.fileDirectory = properties.getProperty(FILE_DIRECTORY);
        this.outputDirectory = properties.getProperty(OUTPUT_DIRECTORY);
    }

    private void loadFileList() {
        BufferedReader reader;
        
        try {
            reader = new BufferedReader(new java.io.FileReader(new File(this.fileList)));
            String line;
            while ((line = reader.readLine()) != null)
                this.files.add(line.trim());
        } catch (IOException e) {
            System.out.println("File list file was either not found, or found with error");
            System.out.println(e);
            System.exit(0);
        }
    }

    private void logResults(List<Map.Entry<Long, Integer>> log, String fileName) {
        try{
            PrintWriter writer = new PrintWriter(this.outputDirectory + fileName, "UTF-8");
            for (Map.Entry<Long, Integer> entry : log) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error when logging results for file " + fileName);
            System.out.println(e);
        }
    }

    private void runFile(String fileName) {
        System.out.println("Running file " + fileName);
        Problem problem = new Problem();
        problem.readFile(this.fileDirectory + fileName);
        GeneticAlgorithm ga = new GeneticAlgorithm(problem, this.populationSize, this.maxRunningTime, this.tournamentSize);
        System.out.println("Initialising initial population");
        ga.initialise();
        System.out.println("Completed initialising initial population");
        ga.train();
        this.logResults(ga.getLog(), fileName);
    }

    private void run() {
        long startTime = System.currentTimeMillis();

        for (String file : files) {
            this.runFile(file);
        }

        long elapsedTime = (((System.currentTimeMillis() - startTime) / 1000) / 60);
        System.out.println("Total time: " + elapsedTime);
    }

    public static void main(String[] args){
        Main main = new Main();
        main.loadProperties();
        main.loadFileList();
        main.run();
    }
}
