package gsp;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * Example of how to use the algorithm GSP, saving the results in a given
 * file
 * @author agomariz
 */
public class MainTestGSP_saveToFile {


    public static void main(String[] args) throws IOException {
    	String output = "/Users/bvbang/workspace/smun/src/main/resources/output_gsp.txt";
        // Load a sequence database
        double support = 0.3, mingap = 0, maxgap = Integer.MAX_VALUE, windowSize = 0;

        boolean keepPatterns = true;
        boolean verbose=false;
        
        // if you set the following parameter to true, the sequence ids of the sequences where
        // each pattern appears will be shown in the result
        boolean outputSequenceIdentifiers = false;

        AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative.getInstance();
        SequenceDatabase sequenceDatabase = new SequenceDatabase(abstractionCreator);

        //String filename = "/input.txt";
        //String filename = "/BIBLE.txt";
        //String filename = "/BMS1_spmf.txt";
        String filename = "/FIFA.txt";
        
        sequenceDatabase.loadFile(fileToPath(filename), support);

        AlgoGSP algorithm = new AlgoGSP(support, mingap, maxgap, windowSize,abstractionCreator);


        //System.out.println(sequenceDatabase.toString());

        //Change the file path in order to change the destination file
        algorithm.runAlgorithm(sequenceDatabase,keepPatterns,verbose, output, outputSequenceIdentifiers);
        //System.out.println(algorithm.getNumberOfFrequentPatterns()+ " frequent pattern found.");

        //System.out.println(algorithm.printedOutputToSaveInFile());
        
        System.out.println(algorithm.printStatistics());
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestGSP_saveToFile.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
    }
}
