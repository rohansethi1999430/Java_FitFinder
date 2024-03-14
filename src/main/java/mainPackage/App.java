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

public class App {
	// Initialize necessary objects
	private static HtmlParser parser = new HtmlParser();
	private static InvertedIndexing invertedIndexing = new InvertedIndexing();
	private static FrequencyCount frequencyCount = new FrequencyCount();
	private static Utilities utl = new Utilities();
	private static DealFinder dealFinder = new DealFinder();

	// Define constants for file paths
	public static final String outputFolderName = "ParsedFiles";
	public static final String inputFolderName = "Webpages";
	public static final String citySearchFile = "/Users/rohansethi/Downloads/FitFinder (2) 2/FitFinder/FitFinder/src/main/java/Files/CitySearchHistory.txt";
	public static final String keywordSearchFile = "/Users/rohansethi/Downloads/FitFinder (2) 2/FitFinder/FitFinder/src/main/java/Files/KeySearchHistory.txt";
	private static final String URL_REGEX = "^(https?|ftp)://([A-Za-z0-9.-]+)(:[0-9]+)?(/[A-Za-z0-9/]+)?$";

	// Initialize data structures for search history and inverted index
	public static Hashtable<String, Integer> citySrhCount;
	public static Stack<String> keySrchCount;
	public static Map<String, Set<String>> i_index;

	// Initialize logger
	private static Logger logger = Logger.getLogger(App.class.getName());

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// Print welcome message
		System.out.println("\t ------------------------");
		System.out.println("\t| Welcome to FitTrack Pro! |");
		System.out.println("\t ------------------------");

		// Print menu options
		utl.printOnConsole(utl.msg, "What would you like to do?");
		System.out.println("1. Find the best membership deal");
		System.out.println("2. Search for something about gyms");
		System.out.println("3. Exit");

		// Main program loop
		while (true) {
			byte in;
			try {
				utl.printOnConsole(utl.cmd, "Enter your choice");
				in = scanner.nextByte();

				if (in == 1) {   // Finding a membership deal
					// Process city search history
					citySrhCount = utl.fileToHashmap(citySearchFile);

					// Get manual input from the user
					String loc_City = utl.getLocationInput();

					// Prepare results for the given city
					if (loc_City != null && !loc_City.equals("")) {
						loc_City = loc_City.toLowerCase();

						scanner.nextLine();
						while (true) {
							utl.printOnConsole(utl.cmd, "Do you want to parse the website again? (y/n)");
							String confirmation = scanner.nextLine();
							if (confirmation.equals("y") || confirmation.equals("Y")) {
								dealFinder.webScrapper(loc_City);
								break;
							} else if (confirmation.equals("n") || confirmation.equals("N")) {
								break;
							} else {
								utl.printOnConsole(utl.msg, "Invalid input!");
							}
						}

						try {
							// Read CSV files
							GoodLifeReader.main(args);
							FitnessWorldReader.main(args);
							PlanetFitnessReader.main(args);
						} catch (Exception e) {
							utl.printOnConsole(utl.err, "File not found");
						}

						// Record the searched location in the history
						if (citySrhCount != null && citySrhCount.contains(loc_City)) {
							citySrhCount.put(loc_City, citySrhCount.get(loc_City) + 1);
						} else {
							citySrhCount.put(loc_City, 1);
						}

						// Add searched city into history file
						try (BufferedWriter BufferWriter = new BufferedWriter(new FileWriter(citySearchFile, true))) {
							BufferWriter.write(loc_City);
							BufferWriter.newLine();
							BufferWriter.close();
						} catch (IOException e) {
							utl.printOnConsole(utl.err, "Something went wrong while interacting with the file");
						}
					}
					break;
				} else if (in == 2) {  // Getting results for keyword search
					File PrcdFilesFldr = new File(outputFolderName);

					if (PrcdFilesFldr.exists() == false) {
						PrcdFilesFldr.mkdirs();
						parser.parseWebsites();
					} else {
						if (PrcdFilesFldr.listFiles().length == 0) {
							parser.parseWebsites();
						}
					}

					i_index = invertedIndexing.buildInvertedIndex();

					if (i_index != null) {
						String input = "";
						scanner.nextLine();
						while (true) {
							utl.printOnConsole(utl.cmd, "Search something related to gyms or press '*' to exit the application");
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

							try (BufferedWriter BufferWriter = new BufferedWriter(new FileWriter(keywordSearchFile, true))) {
								BufferWriter.write(input);
								BufferWriter.newLine();
								BufferWriter.close();
							} catch (IOException e) {
								utl.printOnConsole(utl.err, "Something went wrong while interacting with the file");
							}

							String[] in_arr = input.split(" ");
							Set<String> matchedDocs = new HashSet<>();
							for (String keyword : in_arr) {
								if (i_index.get(keyword) != null)
									matchedDocs.addAll(i_index.get(keyword));
							}

							List<String> matchedDocsList = new ArrayList<>();
							matchedDocsList.addAll(matchedDocs);
							List<String> keywordsList = Arrays.asList(in_arr);
							List<Map.Entry<String, Integer>> keywordFrequencyMap = frequencyCount.getFrequencyCount(matchedDocsList, keywordsList);

							if (keywordFrequencyMap.size() > 0) {
								utl.printOnConsole(utl.msg, "Here are the list of most relevant sites for your search");
								int count = 1;
								for (Map.Entry<String, Integer> entry : keywordFrequencyMap) {
									System.out.println(count++ + ". " + entry.getKey() + "\t(total " + entry.getValue() + " Occurrence)");
								}
							} else {
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
									} else if (choice == 2) {
										utl.fetchSearchHistory(keywordSearchFile);
									} else if (choice == 3) {
										utl.printOnConsole(utl.msg, "Thanks for using the app! Bye");
										return;
									}
								} catch (InputMismatchException e) {
									scanner.nextLine();
									utl.printOnConsole(utl.msg, "Invalid input! Please try again");

								} catch (Exception e) {
									utl.printOnConsole(utl.err, "Something went wrong while interacting with the file");
									break;
								}
							}
						}
					}
					break;
				} else if (in == -1 || in == 3) {
					utl.printOnConsole(utl.msg, "Thanks for using the app! Bye");
					return;
				} else {
					utl.printOnConsole(utl.msg, "Invalid input! Please try again (-1 to exit)");
				}
			} catch (InputMismatchException ex) {
				utl.printOnConsole(utl.msg, "Invalid input! Please try again (-1 to exit)");
				scanner.nextLine();
			}
		}
	}

	// Validate URL using regex
	public static File[] getFileList(File folderPath) {

		// Check if the folder already exists
		try {
			if (folderPath.exists() == false)
				folderPath.mkdirs();

			if (folderPath.isDirectory()) {
				// Create an array of files in the input folder
				File[] fileList = folderPath.listFiles();
				return fileList;
			} else {
				return null;
			}
		} catch (SecurityException e) {
			// If given path is violating the security
			utl.printOnConsole(utl.err, "Something went wrong while interacting with the file");
			return null;
		}

	}
}
