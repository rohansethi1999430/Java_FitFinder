package mainPackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.logging.*;

public class App 
{
	private static HtmlParser parser = new HtmlParser();
	private static InvertedIndexing invertedIndexing = new InvertedIndexing();
	private static FrequencyCount frequencyCount = new FrequencyCount();
	private static Utilities utl = new Utilities();
	private static DealFinder dealFinder = new DealFinder();
	public static final String outputFolderName = "ParsedFiles";
	public static final String inputFolderName = "Webpages";
	public static final String citySearchFile = "/Users/rohansethi/Downloads/FitFinder (2) 2/FitFinder/FitFinder/src/main/java/Files/CitySearchHistory.txt";
	public static final String keywordSearchFile = "/Users/rohansethi/Downloads/FitFinder (2) 2/FitFinder/FitFinder/src/main/java/Files/KeySearchHistory.txt";
	private static final String URL_REGEX = "^(https?|ftp)://([A-Za-z0-9.-]+)(:[0-9]+)?(/[A-Za-z0-9/]+)?$";
	public static Hashtable<String, Integer> citySrhCount;
	public static Stack<String> keySrchCount;
	public static Map<String, Set<String>> i_index;
	private static Logger logger = Logger.getLogger(App.class.getName());

	
    public static void main( String[] args )
    {
    	Scanner scanner = new Scanner(System.in);

    	System.out.println("\t ------------------------");
        System.out.println("\t| Welcome to Fit Finder! |");
    	System.out.println("\t ------------------------");

		utl.printOnConsole(utl.msg, "What would you like to do?" );
		System.out.println("1. Find a best membership deal");
		System.out.println("2. Search something about gym");
		System.out.println("3. Exit");
		
		while (true) {
			byte in;
			try { 
				utl.printOnConsole(utl.cmd, "Enter your choice" );
				in = scanner.nextByte();				
				if (in == 1) {   // finding membership deal.
					// process city search history.
			    	citySrhCount = utl.fileToHashmap(citySearchFile);

			        // Get manual input from the user
			        String loc_City = utl.getLocationInput();
			        
			        // prepare results for given city.
			        if (loc_City != null && !loc_City.equals("")) {
			        	loc_City = loc_City.toLowerCase();
				
			        	//utl.printOnConsole(utl.msg, "Proceeding with location: " + loc_City);
						scanner.nextLine();
						while (true) {
							utl.printOnConsole(utl.cmd, "Do you want to parse the website again? (y/n)");
							String confirmation = scanner.nextLine();
							if (confirmation.equals("y") || confirmation.equals("Y")) {
								dealFinder.webScrapper(loc_City);
								break;
							}
							else if (confirmation.equals("n") || confirmation.equals("N")) {
								break;
							}
							else {
								utl.printOnConsole(utl.msg, "Invalid input!");
							}
	        			}
			        	// read csv files.
			        	try {
			        		
			        		GoodLifeReader.main(args);
			        		FitnessWorldReader.main(args);
			        		PlanetFitnessReader.main(args);
			        		
			        	}
			        	catch (Exception e) {
			        		utl.printOnConsole(utl.err, "File not found");
			        	}
			        	
						// recording the searched location in the history.
						if (citySrhCount != null && citySrhCount.contains(loc_City)) {  // if city has been added already.
			            	citySrhCount.put(loc_City, citySrhCount.get(loc_City)+1);
			            }
			            else {
			            	citySrhCount.put(loc_City, 1);
			            }    
						
						// add searched city into history file
						try (BufferedWriter BufferWriter = new BufferedWriter(new FileWriter(citySearchFile, true))) {
							BufferWriter.write(loc_City);
							BufferWriter.newLine(); // new line 
							BufferWriter.close();
						} 
						catch (IOException e) {
							utl.printOnConsole(utl.err, "Something went wrong while interacting with file");
						}
					}        
			        break; 
				}
				else if (in == 2) {  // getting results for keyword search.
					File PrcdFilesFldr = new File(outputFolderName);
			        //check_if_the_folder_already_exists    	
			    	
			        if (PrcdFilesFldr.exists() == false) {
			        	PrcdFilesFldr.mkdirs();
			        	parser.parseWebsites();
			        }
			        else {
			        	if (PrcdFilesFldr.listFiles().length == 0) {   // if there are files present in the folder
			        		parser.parseWebsites();
			        	}
			        }
	        		i_index = invertedIndexing.buildInvertedIndex();

			        if (i_index != null) {
			        	String input = "";
			        	scanner.nextLine(); // clear buffer
			        	while(true) {
				        	utl.printOnConsole(utl.cmd, "Search something related to gym or press '*' to exit the application");	
				        	input = scanner.nextLine();
				        	if (input.equals("*")) {
				        		utl.printOnConsole(utl.msg, "Thanks for using the app! Bye");
				        		return;
				        	}
				        	while (!input.matches("[a-zA-Z0-9 ]+")) {
								utl.printOnConsole(utl.msg, "Only alphabets are allowed. Please try again");
								utl.printOnConsole(utl.cmd, "Please enter the search query again");
								input = scanner.nextLine();	
							}
				        	
				        	// add searched city into history file
							try (BufferedWriter BufferWriter = new BufferedWriter(new FileWriter(keywordSearchFile, true))) {
								BufferWriter.write(input);
								BufferWriter.newLine(); // new line 
								BufferWriter.close();
							} 
							catch (IOException e) {
								utl.printOnConsole(utl.err, "Something went wrong while interacting with file");
							}
				        	
							// processing valid input
			        		String[] in_arr = input.split(" ");
			        		// storing files that contain given keyword
			        		Set<String> matchedDocs = new HashSet<>();
			        		for (String keyword : in_arr) {
			        			if (i_index.get(keyword) != null)
			        				matchedDocs.addAll(i_index.get(keyword));
			        		}
			        		
			        		// processing inverted index to get frequency count and page ranking
			        		List<String> matchedDocsList = new ArrayList<>(); matchedDocsList.addAll(matchedDocs);
			        		List<String> keywordsList = Arrays.asList(in_arr);
			        		List<Map.Entry<String, Integer>> keywordFrequencyMap = frequencyCount.getFrequencyCount(matchedDocsList, keywordsList);
			        		
			        		if (keywordFrequencyMap.size() > 0) {
			        			utl.printOnConsole(utl.msg, "Here are the list of most relavent sites for your search");
			        			int count=1;
				        		for (Map.Entry<String, Integer> entry : keywordFrequencyMap) {
				                    System.out.println(count++ +". "+ entry.getKey() + "\t(total " + entry.getValue() + " Occurrence)");
				                }
			        		}
			        		else {
			        			utl.printOnConsole(utl.msg, "Nothing found related to your search! Try something else related to a gym and fitness");
			        		}
			        		
			        		byte choice = 0;
			        		while (true) {
			        			try {
					        		utl.printOnConsole(utl.msg, "Next step..");
			        				System.out.println("1. Continue Searching");
					        		System.out.println("2. Look up the history");
					        		System.out.println("3. Exit the application");
					        		utl.printOnConsole(utl.cmd, "Enter your choice");
			        				choice = scanner.nextByte();
				        			if (choice == 1) {
				        				scanner.nextLine();
				        				break; 
				        			}
				        			else if (choice == 2) {
				        				utl.fetchSearchHistory(keywordSearchFile);
				        			}
				        			else if (choice == 3) {
						        		utl.printOnConsole(utl.msg, "Thanks for using the app! Bye");
				        				return;
				        			}
			        			}
			        			catch (InputMismatchException e) {
			        				scanner.nextLine();
			        				utl.printOnConsole(utl.msg, "Invalid input! Please try again");		
									        				
			        			}
			        			catch (Exception e) {
									utl.printOnConsole(utl.err, "Something went wrong while interacting with file");
									break;
			        			}			        			
			        		}     		
			        	}		
			        }
			        break;
				}
				else if (in == -1 || in == 3) {
					utl.printOnConsole(utl.msg, "Thanks for using the app! Bye");
					return;
				}
				else {
					utl.printOnConsole(utl.msg, "Invalid input! Please try again (-1 to exit)");
				}			
			} 
			catch (InputMismatchException ex) { 
				utl.printOnConsole(utl.msg, "Invalid input! Please try again (-1 to exit)");  
				scanner.nextLine();   // to clear the buffer and avoid infinite loop.
			}
		}
    }
    
    // validate url using regex. 
    public static File[] getFileList(File folderPath) {
    	
    	// check_if_the_folder_already_exists
    	try {
    		if (folderPath.exists() == false)
            	folderPath.mkdirs();

            if (folderPath.isDirectory()) {
            	// create_an_array_of_files_in_the_input_folder
                File[] fileList = folderPath.listFiles();
                return fileList;
            }
            else {
            	return null;
            }
    	}
    	catch (SecurityException e) {
    		// if given path is violates the security
			utl.printOnConsole(utl.err, "Something went wrong while interacting with file");
    		return null;
    	}
        
    }
    
}
