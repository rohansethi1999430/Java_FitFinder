package mainPackage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GoodLifeReader {
	public static void main(String[] args) {
        String csvFile = "/Users/rohansethi/Downloads/FitFinder (2) 2/FitFinder/FitFinder/GLoutput.csv"; // Specify the path to your CSV file

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            System.out.println("GoodLife Fitness");
            System.out.println("+--------------------------------+--------------------------------+--------------------------------+");
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
                if(data.length == 0) break;
                System.out.format("| %-30s | %-30s | %-30s |%n", data[0], data[1], data[2]);
            }
            System.out.println("+--------------------------------+--------------------------------+--------------------------------+\n");
            
            System.out.println("\n+--------------------------------+--------------------------------+-----------------+-----------------+");
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
                if(data.length == 0) {
                System.out.format("No Club Found in your desired location of this chain.");	
                }
                else if(data.length==4)
                	System.out.format("| %-30s | %-30s | %-15s | %-15s |%n", data[0], data[1], data[2], data[3]);
            }
            System.out.println("+--------------------------------+--------------------------------+-----------------+-----------------+");
            
        } catch (IOException e) {
            System.out.println("No gym found at this location!");
        	//e.printStackTrace();
        }
    }
}

