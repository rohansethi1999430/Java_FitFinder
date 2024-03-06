package mainPackage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FitnessWorldReader {
	public static void main(String[] args) {
        String csvFile = "/Users/rohansethi/Downloads/FitFinder (2) 2/FitFinder/FitFinder/ftoutput.csv"; // Specify the path to your CSV file

        try (BufferedReader buffRead = new BufferedReader(new FileReader(csvFile))) {
            String individualLine;
            int iterator=0;
            
            // each individualLine are read through the excel file
            while ((individualLine = buffRead.readLine()) != null) {
                String[] data = individualLine.split(",");
                
                System.out.println(data[0]);
                if(!(iterator++ < 26)) break;
            }
            
            System.out.println("+--------------------------------+--------------------------------+-----------------------+-----------------+");
            buffRead.readLine();
            while ((individualLine = buffRead.readLine()) != null) {
                String[] data = individualLine.split(",");
                
              
                if(data.length == 0) {
                	System.out.format("No Club Found in your desired location of this chain.");
                }
                else if(data.length==4) {
                	System.out.format("| %-30s | %-30s | %-11s | %-15s |%n", data[0], data[1], data[2], data[3]);
                }
                
            }
            System.out.println("+--------------------------------+--------------------------------+-----------------------+-----------------+");       
            
            

        } catch (IOException e) {
        	System.out.println("No gym found at this location!");
            //e.printStackTrace();
        }
    }
}
