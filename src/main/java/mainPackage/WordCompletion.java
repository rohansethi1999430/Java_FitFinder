package mainPackage;

import java.io.*;
import java.util.*;

public class WordCompletion {
	
	 private String filePathCNC;
     private ArrayList<String> cityList;
     //protected File cities;
     public TrieNode root = new TrieNode();
     
     public WordCompletion(String filepathCNC) {
    	this.filePathCNC = filepathCNC;
		//cities = new File(filePathCNC);
		cityList = new ArrayList<String>();
		
		// reading values from the cities.txt file
		readCityFile();			
    }   
    
     public void readCityFile() {
	   	 try (BufferedReader brCNC = new BufferedReader(new FileReader(filePathCNC))) {
            String lineCNC;
            while ((lineCNC = brCNC.readLine()) != null) {
                String[] wordsCNC = lineCNC.split("\\r?\\n");
                for (String wordCNC : wordsCNC) {
                    wordCNC = wordCNC.replaceAll("[^a-zA-Z0-9.\\-' ]", "").toLowerCase().trim();
                    if (!wordCNC.isEmpty()) {
                    	insert(wordCNC);
                    }
                }
            }
	     } catch (Exception exct) {
	        exct.printStackTrace();
	     }	
	}
     
    public void insert(String wordCNC) {
        TrieNode currentCNC = root;
        for (char ch : wordCNC.toCharArray()) {
            int index = getIndex(ch);
            if (currentCNC.children[index] == null) {
                currentCNC.children[index] = new TrieNode();
            }
            currentCNC = currentCNC.children[index];
        }
        currentCNC.isEOWCNC = true;
    }

    List<String> findWordsWithPrefix(String prefixCNC) {
        List<String> completionsCNC = new ArrayList<>();
        TrieNode prefixNode = searchNode(prefixCNC);

        if (prefixNode != null) {
            findWordsRecursive(prefixNode, prefixCNC, completionsCNC);
        }

        return completionsCNC;
    }

    private TrieNode searchNode(String word) {
        TrieNode currentCNC = root;
        for (char ch : word.toCharArray()) {
            int indexCNC = getIndex(ch);
            if (currentCNC.children[indexCNC] == null) {
                return null;
            }
            currentCNC = currentCNC.children[indexCNC];
        }
        return currentCNC;
    }

    private void findWordsRecursive(TrieNode nodeCNC, String currentWordCNC, List<String> completions) {
        if (nodeCNC.isEOWCNC) {
            completions.add(currentWordCNC);
        }

        for (int i = 0; i < 40; i++) { // Adjusted for alphanumeric characters
            if (nodeCNC.children[i] != null) {
                char childChar = getCharFromIndex(i);
                findWordsRecursive(nodeCNC.children[i], currentWordCNC + childChar, completions);
            }
        }
    }

    private int getIndex(char chCNC) {
        if (Character.isLetter(chCNC)) {
            return Character.toLowerCase(chCNC) - 'a';
        } else if (Character.isDigit(chCNC)) {
            return chCNC - '0' + 26;
        } else if (chCNC == ' ') {
            return 36;
        } else if (chCNC == '.') {
            return 37;
        } else if (chCNC == '-') {
        	return 38;	
        }  else if (chCNC == '\'') {
        	return 39; 
        }
    	else {
	        return -1;
        }
    }

    private char getCharFromIndex(int indexCNC) {
        if (indexCNC < 26) {
            return (char) ('a' + indexCNC);
        } else if (indexCNC < 36) {
            return (char) ('0' + indexCNC - 26);
        } else if (indexCNC == 36) {
            return ' ';
        } else if (indexCNC == 37) {
            return '.';
        }
        else if (indexCNC == 38) {
            return '-';
        }
        else if (indexCNC == 39) {
            return '\'';
        }
        else {
            // Handle other indices as needed
            return '\0';
        }
    }
}


