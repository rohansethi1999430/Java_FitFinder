package mainPackage;

import java.io.*;
import java.util.*;

public class InvertedIndexing {

    private Map<String, Set<String>> invertedIndex;
	public final String outputFolderName = "ParsedFiles";
	public Utilities utl = new Utilities();
    public InvertedIndexing() {
        invertedIndex = new HashMap<>();
    }

    public Map<String, Set<String>> buildInvertedIndex() {
        File directoryCNC = new File(outputFolderName);

        if (!directoryCNC.exists() || !directoryCNC.isDirectory()) {
            System.err.println("Directory path not valid");
            return null;
        }

        File[] filesCNC = directoryCNC.listFiles();

        if (filesCNC == null || filesCNC.length == 0) {
            System.err.println("Files could not be found in the directory");
            return null;
        }

        for (File fileCNC : filesCNC) {
            if (fileCNC.isFile() && fileCNC.getName().endsWith(".txt")) {
                indexDocument(fileCNC);
            }
        }
        return invertedIndex;
    }

    private void indexDocument(File fileCNC) {
        try (BufferedReader redrCNC = new BufferedReader(new FileReader(fileCNC))) {
            String lineCNC;
            String documentId = fileCNC.getName();

            while ((lineCNC = redrCNC.readLine()) != null) {
                String[] terms = lineCNC.split("\\s+");

                for (String term : terms) {
                	Set<String> documents = invertedIndex.get(term);
                    if (documents == null) {
                        documents = new HashSet<>();
                        invertedIndex.put(term, documents);
                    }
                    documents.add(documentId);
                }
            }
        } catch (IOException e) {
			utl.printOnConsole(utl.err, "Something went wrong while interacting with file");
        }
    }

    public void printIndex() {
        for (Map.Entry<String, Set<String>> entry : invertedIndex.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
