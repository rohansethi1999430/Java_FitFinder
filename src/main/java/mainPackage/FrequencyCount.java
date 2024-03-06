package mainPackage;

import java.io.*;
import java.util.*;

public class FrequencyCount {

	public final String outputFolderName = "ParsedFiles";
	private Utilities utl = new Utilities();
	private String[] keywords;
	private List<String> filesList = new ArrayList<>();

    public List<Map.Entry<String, Integer>> getFrequencyCount(List<String> files, List<String> keywords) {
      
    	Map<String, Map<String, Integer>> keywordFrequencyMap = countKeywordFrequency(files, keywords);
        
    	List<Map.Entry<String, Integer>> sortedList = null;
        // Sort the result by frequency in descending order
        if (keywordFrequencyMap != null) {
        	sortedList = sortByFrequency(keywordFrequencyMap);
        }
		return sortedList;
    }

    private Map<String, Map<String, Integer>> countKeywordFrequency(List<String> files, List<String> keywords) {
        Map<String, Map<String, Integer>> keywordFrequencyMap = new HashMap<>();

        for (String fileCNC : files) {
            Map<String, Integer> frequencyMap = new HashMap<>();

            try (BufferedReader redrCNC = new BufferedReader(new FileReader(new File("ParsedFiles\\" + fileCNC)))) {
                String lineCNC;

                while ((lineCNC = redrCNC.readLine()) != null) {
                    String[] wordsCNC = lineCNC.split("\\s+");

                    for (String wordCNC : wordsCNC) {
                        wordCNC = wordCNC.toLowerCase(); // Case-insensitive comparison
                        if (keywords.contains(wordCNC)) {
                            frequencyMap.put(wordCNC, frequencyMap.getOrDefault(wordCNC, 0) + 1);
                        }
                    }
                }
            } catch (Exception excptCNC) {
				utl.printOnConsole(utl.err, "Something went wrong while interacting with file");
            }
            
            // convert file name to url
            fileCNC = fileCNC.replace('@', ':');
            fileCNC = fileCNC.replace('_', '/');
            fileCNC = fileCNC.replace('~', '.');
            fileCNC = fileCNC.replace(".txt", "");
            keywordFrequencyMap.put(fileCNC, frequencyMap);
        }

        return keywordFrequencyMap;
    }

    private static List<Map.Entry<String, Integer>> sortByFrequency(Map<String, Map<String, Integer>> keywordFrequencyMap) {
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>();

        for (Map.Entry<String, Map<String, Integer>> entry : keywordFrequencyMap.entrySet()) {
            int totalFrequency = 0;
            for (int frequency : entry.getValue().values()) {
                totalFrequency += frequency;
            }
            sortedList.add(new AbstractMap.SimpleEntry<>(entry.getKey(), totalFrequency));
        }

        // Sort the list by frequency in decreasing order
        Collections.sort(sortedList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        return sortedList;
    }
}
