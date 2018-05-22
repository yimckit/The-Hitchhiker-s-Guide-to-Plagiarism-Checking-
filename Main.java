package com.company;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        sysPrint("Running the program to scan all the files now!\n");

        checkArgs(args);

        // Reads in files and Tokenises the content into array
        String[][][] docArray = genTxtArray(args);
        // arrayPrint(docArray);

        // Remove Punctuation, space, new lines and quote marks
        // Word Frequency
        termFreq(docArray);
        phaseMatching(docArray);
        // Phrase Matching
        // Compare each files
        // Ignore matches in quote marks
        // Rank Result
        // Display Screen

        sysPrint("All the processed are finished GLHF!");
    }

    public static void sysPrint(String msg) {
        System.out.println(msg);
    }

    public static boolean regex(String text, String pattern) {
        // String to be scanned to find the pattern.
        String line = text;
        String regexPattern = pattern;

        // Create a Pattern object
        Pattern r = Pattern.compile(regexPattern);

        // Now create matcher object.
        Matcher m = r.matcher(line);
        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }

    public static void arrayPrint(String[][][] sArray) {
        // backup code for the array testing
        for (int d = 0; d < sArray.length; d++) {
            for (int s = 0; s < sArray[d].length; s++) {
                for (String w : sArray[d][s]) {
                    sysPrint("Doc: " + d + " Line:" + s + " word:" + w + "\n");
                }
            }
        }
    }

    public static void checkArgs(String[] args) {
        // ref: https://stackoverflow.com/questions/890966/what-is-string-args-parameter-in-main-method-java
        // Check # of Args passed
        for (int i = 0; i < args.length; i++) {
            sysPrint(args[i] + "\n");
        }

        // Check if Doc Path is specified
        if (args.length == 0) {
            sysPrint("No doc path was given! Shutting down now .......");
            //System.exit(1);
        }


    }

    public static String[][][] genTxtArray(String[] args) {
        int nDoc = 0;
        int nLine;
        String[][][] docArray;

        // ref: https://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder
        File folder = new File("C://Users/User/Desktop/SCC110/Project3TestFiles/");
        // File folder = new File(args[0]);
        File[] listOfFiles = folder.listFiles();

        docArray = new String[listOfFiles.length][][];

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() & regex(listOfFiles[i].getName(), ".*txt$")) {
                sysPrint("Path: " + listOfFiles[i].getAbsolutePath() + "\n");
                String fileName = listOfFiles[i].getAbsolutePath();

                //read file into stream, try-with-resources
                // ref: https://www.mkyong.com/java8/java-8-stream-read-a-file-line-by-line/
                try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
                    // Convert the stream into array
                    String[] streamArray = stream.toArray(String[]::new);
                    sysPrint(streamArray.length + " of Lines in the " + listOfFiles[i].getName() + "\n");

                    nLine = 0; //Reset after each run
                    docArray[nDoc] = new String[streamArray.length][];

                    // Operation of each array
                    for (int n = 0; n < streamArray.length; n++) {
                        sysPrint("Line" + n + ": " + streamArray[n] + "\n");
                        sysPrint("mCounter" + nDoc + " sCounter" + nLine + "\n");
                        // Tokenize the words from the each sentence
                        // http://javadevnotes.com/java-string-split-tutorial-and-examples
                        docArray[nDoc][nLine] = streamArray[n].split(" ");
                        nLine++;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            nDoc++;
        }
        return docArray;
    }

    public static void termFreq(String[][][] docArray) {

        // 升序比较器
        // ref: https://crane-yuan.github.io/2016/08/15/The-map-of-java-sorted-by-value/
        Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                // TODO Auto-generated method stub
                return o1.getValue() - o2.getValue();
            }
        };

        for (int d = 0; d < docArray.length; d++) {
            // ref: https://stackoverflow.com/questions/21771566/calculating-frequency-of-each-word-in-a-sentence-in-java
            Map<String, Integer> map = new TreeMap<>(Collections.reverseOrder());
            for (int s = 0; s < docArray[d].length; s++) {
                for (String w : docArray[d][s]) {
                    w = w.toLowerCase();
                    // remove punctuation
                    w = w.replaceAll("[\\,\\s\\.\\'\"\\n]|^-$", "");

                    if (w != " " && w != "\n" && w != "" && w.length() > 0) {
                        // change all to lower case for more effective analysis
                        Integer n = map.get(w);
                        n = (n == null) ? 1 : ++n;
                        map.put(w, n);
                    }
                }
            }

            // map转换成list进行排序
            List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());

            // 排序
            // ref: https://stackoverflow.com/questions/5894818/how-to-sort-arraylistlong-in-java-in-decreasing-order
            Collections.sort(list, valueComparator);
            Collections.reverse(list);

            // 默认情况下，TreeMap对key进行升序排序
            System.out.println("------------Doc " + d + "--------------------");
            for (Map.Entry<String, Integer> entry : list) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }
        }
    }

    public static void phaseMatching(String[][][] docArray) {

        // 升序比较器
        // ref: https://crane-yuan.github.io/2016/08/15/The-map-of-java-sorted-by-value/
        Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                // TODO Auto-generated method stub
                return o1.getValue() - o2.getValue();
            }
        };

        for (int d1 = 0; d1 < docArray.length; d1++) {
           for (int d2 = 0; d2< docArray.length; d2++) {
               if (d2 != d1){
                   for (int s1 = 0; s1< docArray[d1].length; s1++) {
                       for (int s2 =0; s2<docArray[d2].length; s2++) {
                           for (int w1 = 0; w1< docArray[d1][s1].length; w1++) {
                               for (int w2 = 0; w2< docArray[d2][s2].length; w2++) {
                                       if (w2 == w1){
                                           w2++; w1++; sysPrint( "doc " + d1 + " " + docArray[d1][s1][w1]  + "\n");
                                       }
                                       if (w2 != w1){
                                       }
                               }
                           }
                       }
                   }
               }
           }
        }
    }
}