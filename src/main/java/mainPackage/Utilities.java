package mainPackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
	
	Scanner scn = new Scanner(System.in);
	protected final String msg = "Message";
	protected final String cmd = "Command";
	protected final String err = "Error";

    private static final String URL_REGEX = "(\\b(https?|ftp|file)://)?[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
	
	public String getLocationInput() {
		printOnConsole(msg, "We would need the name of your city."); 
		System.out.println("1. Look up the history and choose from there. \n2. Enter location manually");
		printOnConsole(cmd, "Enter your choice");
		
		int inChoice = 0;
		try { 
			inChoice = scn.nextInt(); 
			scn.nextLine();
		} 
		catch (InputMismatchException ex) { 
			printOnConsole(msg, "Invalid input! Please try again");  
			scn.nextLine();   // to clear the buffer and avoid infinite loop.
			return getLocationInput(); 
		}
				
		if (inChoice == 1) {  // when choose to go with history
			return proceedWithHistory();    
		}
		else if (inChoice ==  2) {  // when choose to go with manual input
			return proceedWithManualInput();
		}
		else {
			printOnConsole(msg, "Invalid input! Please try again.");
			return getLocationInput();
		}
	}		

	public void printOnConsole(String title, String msg) {
		String printStr = title + " | " + msg;
		StringBuilder dasher = new StringBuilder();
		for (int i=0; i<printStr.length()+1; i++) {
			dasher.append("-");
		}
		System.out.println(dasher.toString());
		if (title.equals("Error")) {
			System.out.println(msg);
			System.out.println(dasher.toString());
			return;
		}
		System.out.println(printStr);
		System.out.println(dasher.toString());
		
		if (title.equals("Command")) {
			System.out.print("=> ");
		}
	}
	
	public Hashtable<String, Integer> fileToHashmap(String filePath) {
		Hashtable<String, Integer> srhCount = new Hashtable<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String city = line.trim();
	            
	            if (srhCount.containsKey(city)) {  // if city has been added already.
	            	srhCount.put(city, srhCount.get(city)+1);
	            }
	            else {
	            	srhCount.put(city, 1);
	            }             
	        }
	        return srhCount;
	    } 
		catch (IOException e) {
			printOnConsole(err, "Something went wrong while interacting with file");
	        return null;
	    }
		
	}
	
	public String proceedWithHistory() {
		
		printOnConsole(msg, "Here is the list of cities that you have searched.");
		
		List<CitySearchFrqPair> srchdCityList = new ArrayList<>();
		
		// preparing list of CitySearchFrqPair from the hashtable.
		for (String key : Collections.list(App.citySrhCount.keys())) {
			srchdCityList.add(new CitySearchFrqPair(key, App.citySrhCount.get(key)));
		}
		
		// sorting the list in the descending order.
		Collections.sort(srchdCityList);
		
		// showing list of cities with its search frequency.
		for (int i=0; i<srchdCityList.size(); i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(i+1); sb.append(". "); sb.append(srchdCityList.get(i).cityName);	
			sb.append("\t\t"); sb.append("searched "); sb.append(srchdCityList.get(i).frq); sb.append(" times");
			
			System.out.println(sb.toString());
		}
		printOnConsole(cmd, "Enter the corresponding number to go with or -1 to go back");
		
		byte cityListCmd = 0;
		try { 
			cityListCmd = scn.nextByte();
			scn.nextLine();
		} 
		catch (InputMismatchException ex) { 
			printOnConsole(msg, "Invalid input! Please try again");  
			scn.nextLine();   // to clear the buffer and avoid infinite loop.
			return proceedWithHistory(); 
		}
		
		if (cityListCmd == -1) { // return to previous page.
			return getLocationInput();
		}
		else if (cityListCmd > 0 && cityListCmd <= srchdCityList.size()){
			return srchdCityList.get(cityListCmd-1).cityName;
		}
		else {
			printOnConsole(msg, "Invalid input! Please try again.");
			return proceedWithHistory();
		}
	}

	public String proceedWithManualInput() {
		// getting input form user.
		System.out.println("\n1. Enter city name manually.\n2. Go back.");
		printOnConsole(cmd, "Please choose from above options");
		
		byte inChoice = 0;
		try { 
			inChoice = scn.nextByte();
			scn.nextLine();  // clearing buffer
		} 
		catch (InputMismatchException ex) { 
			printOnConsole(msg, "Invalid input! Please try again");  
			scn.nextLine();   // to clear the buffer and avoid infinite loop.
			return proceedWithManualInput(); 
		}
		
		if (inChoice == 1) {
			printOnConsole(cmd, "Enter your location (Add * at the end to get recommendation)");
			String locationIn = scn.nextLine();
			if (locationIn.length() == 0) {
				printOnConsole(msg, "Input cannot be blank. Please try again.");
				return proceedWithManualInput();
			}
			//String[] temp = locationIn.split("~");
			if (locationIn.charAt(locationIn.length()-1) ==  '*') {
				locationIn = locationIn.substring(0,locationIn.length()-1);
				// check if it is a valid string
				while (!locationIn.matches("[a-zA-Z0-9.\\-'* ]+")) {
					printOnConsole(msg, "Input is not valid!");
					System.out.println();
					printOnConsole(msg, "Enter the location again");
					//locationIn = scn.nextLine();	
					return proceedWithManualInput();
				}
				
				WordCompletion wordCompletion = new WordCompletion("src/main/java/Files/Cities.txt");
				List<String> cmpltdWordList = wordCompletion.findWordsWithPrefix(locationIn);
				
				// if entered prefix is not matching with any city name.
				if (cmpltdWordList.isEmpty()) {
					printOnConsole(msg, "No recommendation found for the word you entered. Please try again.");
	                return proceedWithManualInput();
	            } 
	            else {
	            	printOnConsole(msg, "These are the cities completing the word you entered");

	        		for (int i=0; i<cmpltdWordList.size(); i++) {
	        			System.out.println((i+1) + ". " + cmpltdWordList.get(i));
	        		}
	        		
	        		while (true) {
	        			printOnConsole(cmd, "Does the list contain the city you want to search? (y/n)");
	        			String confirmation = scn.nextLine();
	        			if (confirmation.equals("y") || confirmation.equals("Y")) {
	        				printOnConsole(cmd, "Please enter the corrosponding number to proceed");
	        				byte choice = 0;
	        				try { 
	        					choice = scn.nextByte();
	        				} 
	        				catch (InputMismatchException ex) { 
	        					printOnConsole(msg, "Invalid input! Please try again");  
	        					scn.nextLine();   // to clear the buffer and avoid infinite loop.
	        					return proceedWithManualInput(); 
	        				}
	        				if (choice > 0 && choice <= cmpltdWordList.size()) {
	        					scn.nextLine();   // to clear the buffer and avoid infinite loop.
		                		return cmpltdWordList.get(choice-1);
	        				}
	        				else {
	        					printOnConsole(msg, "Invalid input! Please try again.");
	        					return proceedWithHistory();
	        				}
	            		}
	            		else if (confirmation.equals("n") || confirmation.equals("N")) {
	            			printOnConsole(msg, "Please try again!");
	            			return proceedWithManualInput();
	            		}
	            		else {
	            			printOnConsole(msg, "Invalid input!");
	            		}
	        		}

	            }
			}
			else { // spell checking logic when input is without *
				while (!locationIn.matches("[a-zA-Z0-9.\\-'* ]+")) {
					printOnConsole(msg, "Input is not valid!");
					System.out.println();
					//locationIn = scn.nextLine();	
					scn.nextLine();   // clear the buffer
					return proceedWithManualInput();
				}
				SpellChecker spellChecker = new SpellChecker("src\\main\\java\\Files\\Cities.txt");
				
				if (spellChecker.searchInTrie(locationIn)) {
					printOnConsole(msg, "Correct input. Preparing results...");
					return locationIn;
				}
				else {
					List<String> suggestions = spellChecker.suggestCorrectionsCNC(locationIn);
		            if (suggestions.isEmpty()) {
		            	printOnConsole(msg, "No city found in the record matching to the given input. Please try again");
		                return proceedWithManualInput();
		            } 
		            else {
		            	printOnConsole(msg, "Did you mean: ");

		        		for (int i=0; i<suggestions.size(); i++) {
		        			System.out.println((i+1) + ". " + suggestions.get(i));
		        		}
		        		
		        		while (true) {
		        			printOnConsole(cmd, "Does the list contain the city you want to search? (y/n)");
		        			String confirmation = scn.nextLine();
		        			if (confirmation.equals("y") || confirmation.equals("Y")) {
		        				printOnConsole(cmd, "Please enter the corrosponding number to proceed");
		        				byte choice = 0;
		        				try { 
		        					choice = scn.nextByte();
		        					scn.nextLine();  // clearing buffer
		        				} 
		        				catch (InputMismatchException ex) { 
		        					printOnConsole(msg, "Invalid input! Please try again");  
		        					scn.nextLine();   // to clear the buffer and avoid infinite loop.
		        					return proceedWithManualInput(); 
		        				}
		        				if (choice > 0 && choice <= suggestions.size()) {
			                		return suggestions.get(choice-1);
		        				}
		        				else {
		        					printOnConsole(msg, "Invalid input! Please try again.");
		        					return proceedWithHistory();
		        				}
		            		}
		            		else if (confirmation.equals("n") || confirmation.equals("N")) {
		            			printOnConsole(msg, "Please try again!");
		            			return proceedWithManualInput();
		            		}
		            		else {
		            			printOnConsole(msg, "Invalid input! Please try again.");
		            		}
		        		}
		            }
				}
			}
		}
		else if (inChoice == 2) {  // starting from beginning.
			return getLocationInput();
		}
		else {
			printOnConsole(msg, "Invalid input! Please try again.");
			return proceedWithManualInput();
		}		
	}

	public boolean isUrlValid(String url) {
	     Pattern pattern = Pattern.compile(URL_REGEX);
	     Matcher matcher = pattern.matcher(url);
	     return matcher.matches();
	}
	
	public void fetchSearchHistory(String filePath) {
		Stack<String> srhHistoryStk = new Stack<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            srhHistoryStk.push(line.trim());          
	        }
	        
	        int count = 1;
	        printOnConsole(msg, "Here is the list of keyword you search (most recent one appears first)");
	        while (srhHistoryStk.size() > 0 ) {
	        	System.out.println(count++ + ". " + srhHistoryStk.pop());
	        }
	        
	    } 
		catch (IOException e) {
			printOnConsole(err, "Something went wrong while interacting with file");
	        return;
	    }
		
	}
}

class CitySearchFrqPair implements Comparable<CitySearchFrqPair> {
	String cityName;
	int frq;
	
	public CitySearchFrqPair(String cityName, int frq) {
		this.cityName = cityName;
		this.frq = frq;
	}

	@Override
	public int compareTo(CitySearchFrqPair o) {
        return Integer.compare(o.frq, this.frq);
	}	
}
