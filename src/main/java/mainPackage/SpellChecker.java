package mainPackage;

import java.io.*;
import java.util.*;

// Defining the trie node.
class TrieNode {
    TrieNode[] children;
    boolean isEOWCNC;

    TrieNode() {
        this.children = new TrieNode[40]; // Considering alphanumeric characters (26 lowercase + 10 digits + '.', ' ', ''', '-')
        isEOWCNC = false;
    }
}

class TrieCNC {
    public TrieNode root;

    public TrieCNC() {
        this.root = new TrieNode();
    }
}

public class SpellChecker {

    private String filePath;
    private ArrayList<String> cityList;
    //protected File cities;
    private final int maxEditDistance = 2;
    public static TrieNode root = new TrieNode();
    public Utilities utl = new Utilities();
    
    public SpellChecker(String filePath) {
    	this.filePath = filePath;
		//cities = new File(filePath);
		cityList = new ArrayList<String>();
		
		// reading values from the cities.txt file
		readCityFile();
	}
    
    public boolean searchInTrie(String wordCNC) {
        TrieNode currentCNC = root;
        for (char chCNC : wordCNC.toLowerCase().toCharArray()) {
            int indexCNC = getIndex(chCNC);
            if (currentCNC.children[indexCNC] == null) {
                return false; // no word found in the trie
            }
            currentCNC = currentCNC.children[indexCNC];
        }
        return currentCNC.isEOWCNC; // Returns the value of last node after checking the last node is the end of a correct word
    }
    
    public void readCityFile() {
    	 try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
             String lineCNC;
             while ((lineCNC = br.readLine()) != null) {
                 String[] wordsCNC = lineCNC.split("\\r?\\n");
                 for (String wordCNC : wordsCNC) {
                     wordCNC = wordCNC.replaceAll("[^a-zA-Z0-9.\\-' ]", "").toLowerCase().trim();
                     if (!wordCNC.isEmpty()) {
                        insert(wordCNC);
                     }
                 }
             }
         } catch (Exception e) {
 			utl.printOnConsole(utl.err, "Something went wrong while interacting with file");
         }	
    }
    
    public void insert(String wordCNC) {
        TrieNode currentCNC = root;
        for (char chCNC : wordCNC.toCharArray()) {
            int indexCNC = getIndex(chCNC);
            if (currentCNC.children[indexCNC] == null) {
                currentCNC.children[indexCNC] = new TrieNode();
            }
            currentCNC = currentCNC.children[indexCNC];
        }
        currentCNC.isEOWCNC = true;
    }
    
    public List<String> suggestCorrectionsCNC(String wordCNC) {
    	final String temp = wordCNC;
        List<String> suggestionsCNC = new ArrayList<>();
        suggestCorrectionsUtilCNC(root, wordCNC, new StringBuilder(), suggestionsCNC, maxEditDistance); 
        
    	suggestionsCNC.sort(new Comparator<String>() {
             @Override
             public int compare(String axc, String bxc) {
                 return Integer.compare(calculateEditDistance(temp, axc), calculateEditDistance(temp, bxc));
             }
         });
        return suggestionsCNC;
    }
    
    private void suggestCorrectionsUtilCNC(TrieNode nodeCNC, String wordCNC, StringBuilder currentCNCWord,
            List<String> suggestionsCNC, int maxDistanceCNC) {
    	wordCNC = wordCNC.toLowerCase();
		if (currentCNCWord.length() > wordCNC.length() + maxDistanceCNC) {
			return;
		}
	
		if (nodeCNC.isEOWCNC && currentCNCWord.toString().length() >= wordCNC.length() - maxDistanceCNC
		&& currentCNCWord.toString().length() <= wordCNC.length() + maxDistanceCNC) {
			int dist = calculateEditDistance(currentCNCWord.toString(), wordCNC);
			if (dist <= maxDistanceCNC) {
				suggestionsCNC.add(currentCNCWord.toString());
			}
		}
		
		for (int i = 0; i < nodeCNC.children.length; i++) {
			if (nodeCNC.children[i] != null) {
				char ch = getCharFromIndex(i);
				currentCNCWord.append(ch);
				suggestCorrectionsUtilCNC(nodeCNC.children[i], wordCNC, currentCNCWord, suggestionsCNC, maxDistanceCNC);
				currentCNCWord.setLength(currentCNCWord.length() - 1);
			}
		}
    }
    
    private int getIndex(char ch) {
        if (Character.isLetter(ch)) {
            return Character.toLowerCase(ch) - 'a';
        } else if (Character.isDigit(ch)) {
            return ch - '0' + 26;
        } else if (ch == ' ') {
            return 36;
        } else if (ch == '.') {
            return 37;
        } else if (ch == '-') {
        	return 38;	
        }  else if (ch == '\'') {
        	return 39; 
        }
    	else {
	        return -1;
        }
    }

    private char getCharFromIndex(int index) {
        if (index < 26) {
            return (char) ('a' + index);
        } else if (index < 36) {
            return (char) ('0' + index - 26);
        } else if (index == 36) {
            return ' ';
        } else if (index == 37) {
            return '.';
        }
        else if (index == 38) {
            return '-';
        }
        else if (index == 39) {
            return '\'';
        }
        else {
            // Handle other indices as needed
            return '\0';
        }
    }
    private int calculateEditDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i][j - 1], Math.min(dp[i - 1][j], dp[i - 1][j - 1]));
                }
            }
        }
        return dp[m][n];
    }
}
