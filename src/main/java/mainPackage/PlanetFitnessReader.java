package mainPackage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PlanetFitnessReader {
    public static void main(String[] args) {
        String csvFile = "output.csv"; 

        try (BufferedReader buffRead = new BufferedReader(new FileReader(csvFile))) {
            String individualLine;
            
            System.out.println("Planet Fitness");
            individualLine = buffRead.readLine().replaceAll(",", "  ");
            System.out.println(individualLine);
            individualLine = buffRead.readLine();
            
            //First individualLine is being split by ,
            String[] foo = individualLine.split(",");
            System.out.println("+----------------------------------------------------+-----------------+-----------------+");
            System.out.format("| %-50s | %-15s | %-15s |%n", foo[0], foo[1], foo[2]);
            System.out.println("+----------------------------------------------------+-----------------+-----------------+");
            
            individualLine = buffRead.readLine();
            individualLine = buffRead.readLine();
            individualLine = buffRead.readLine();
            individualLine = buffRead.readLine();
            
            String[] foo1 = individualLine.split(",");
            System.out.format("| %-50s | %-15s | %-15s |%n", "", foo1[1], foo1[2]);
            System.out.println("+----------------------------------------------------+-----------------+-----------------+");

            // Print table rows
            //In This individualLine is being read until null appears 
            while ((individualLine = buffRead.readLine()) != null) {
                String[] data = individualLine.split(",");
                
                if(data.length == 0) break;
                System.out.format("| %-50s | %-15s | %-15s |%n", data[0], data[1], data[2]);
            }
            System.out.println("+----------------------------------------------------+-----------------+-----------------+\n");
            
            individualLine = buffRead.readLine();
            if (individualLine != null)
            	individualLine.replaceAll(",", "");
            //System.out.println(individualLine);
            
            System.out.println("+--------------------------------+-------------+-------------+---------------------------+");
            while ((individualLine = buffRead.readLine()) != null) {
                String[] data = individualLine.split(",");
                
                if(data.length == 0) {
                	System.out.println("No Club Found in your desired location of this chain.");
                }
                else if(data.length>=5) {
                System.out.format("| %-30s | %-11s | %-11s | %-25s |%n", data[0].replaceAll("\"", ""), data[1].replaceAll("\"", ""), data[2].replaceAll("\\s+", ""), data[3], data[4]);
                }
            }
            System.out.println("+--------------------------------+-------------+-------------+---------------------------+\n");

        } catch (IOException e) {
        	System.out.println("No gym found at this location");
            //e.printStackTrace();
        }
    }
}
