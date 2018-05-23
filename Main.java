package com.company;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.text.NumberFormat;

public class Main {

    public static void main(String[] args) {
        sysPrint("Running the program to scan all the files now!\n");

        // Check the links
        checkArgs(args);

        // Reads in files and Tokenises the content into array
        String[][][] docArray = genTxtArray(args);
        // Count the number of words in each file
        termFreq(docArray);
        //Phase Matching of the file
        phaseMatching(docArray);



        // Compare each files
        // Ignore matches in quote marks
        // Rank Result
        // Display Screen

        sysPrint("All the processed are finished.");
    }

    public static void sysPrint(String msg) {
        //Print out all the information in words
        System.out.println(msg);
    }

    public static boolean regex(String text, String pattern){
        // String to be scanned to find the pattern.
        String line = text;
        String regexPattern = pattern;

        // Create a Pattern object
        Pattern r = Pattern.compile(regexPattern);

        // Create matcher object.
        Matcher m = r.matcher(line);
        if (m.find( )) {
            return true;
        }else {
            return false;
        }
    }

    public static void arrayPrint(String[][][] sArray){
        // Backup code for the array testing
        for (int d = 0; d < sArray.length; d++){
            for (int s = 0; s < sArray[d].length; s++) {
                for (String w: sArray[d][s]){
                    sysPrint( "Doc: " + d + " Line:" + s + " word:" + w + "\n");
                }
            }
        }
    }

    public static void checkArgs(String[] args){
        // ref: https://stackoverflow.com/questions/890966/what-is-string-args-parameter-in-main-method-java
        // Check # of Args passed
        for(int i = 0; i < args.length; i++) {
            sysPrint(args[i] + "\n");
        }

        // Check if Doc Path is specified
        if (args.length == 0){
            sysPrint("No doc path was given! Shutting down now .......");
            //System.exit(1);
        }
    }

    public static String[][][] genTxtArray( String[] args){
        int nDoc=0;
        int nLine;
        String[][][] docArray;

        // ref: https://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder
        // Link for development use
//        File folder = new File("C://Users/User/Desktop/SCC110/Project3TestFiles/");
        File folder = new File(args[0]);

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
                    for(int n=0; n < streamArray.length; n++){
                        sysPrint("Line" + (n + 1) + ": " + streamArray[n] + "\n");
//                        sysPrint("mCounter" + nDoc + " sCounter" + nLine + "\n");
                        // Tokenize the words from the each sentence
                        // http://javadevnotes.com/java-string-split-tutorial-and-examples
                        streamArray[n] = streamArray[n].toLowerCase();
                        streamArray[n] = streamArray[n].replaceAll("[\\,\\.\\'\"\\n]|^-$","");
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

    public static void termFreq(String[][][] docArray){

        // ref: https://crane-yuan.github.io/2016/08/15/The-map-of-java-sorted-by-value/
        Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String,Integer>>() {
            // @Override
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                // TODO Auto-generated method stub
                return o1.getValue()-o2.getValue();
            }
        };

        for (int d=0;d<docArray.length;d++){
            // ref: https://stackoverflow.com/questions/21771566/calculating-frequency-of-each-word-in-a-sentence-in-java
            Map<String, Integer> map = new TreeMap<>(Collections.reverseOrder());
            for (int s=0;s<docArray[d].length;s++){
                for (String w : docArray[d][s]) {

                    if (w != " " && w != "\n"  && w != "" && w.length() > 0){
                        // remove all the punctuation
                        // change all to lower case for more effective analysis
                        Integer n = map.get(w);
                        n = (n == null) ? 1 : ++n;
                        map.put(w, n);
                    }
                }
            }

            List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());

            // ref: https://stackoverflow.com/questions/5894818/how-to-sort-arraylistlong-in-java-in-decreasing-order
            Collections.sort(list,valueComparator);
            Collections.reverse(list);

            System.out.println("------------Doc " + (d + 1)+" Frequency Count --------------------");
            for (Map.Entry<String, Integer> entry : list) {
                System.out.println( entry.getKey() + ":" + entry.getValue());
            }
        }
    }

    public static int phaseCompare(String[] phase1, String[] phase2){
        // ref: http://www.avajava.com/tutorials/lessons/how-do-i-use-numberformat-to-format-a-percent.html
        // % format
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
        defaultFormat.setMinimumFractionDigits(1);

        int matchWords = 0;
        int matchCounter = 0;

//        sysPrint("doc1 word count: " + phase1.length + " doc2 word count: " + phase2.length);

        for (int w1=0; w1 < phase1.length; w1++){
            for(int w2=0; w2 < phase2.length; w2++){
                //First word matched
                if( Objects.equals(phase1[w1],phase2[w2])){
                    // Check next word
                    if(((w1 + 2) < phase1.length) && ((w2 + 2) < phase2.length)){
                        if(Objects.equals(phase1[(w1+2)],phase2[(w2+2)])){
                            matchCounter++;
//                            sysPrint("word count" + (w1) + " Matched phase1: " + phase1[(w1)] + " phase2: " + phase2[(w2)] + " Matched " + matchWords + " match Counter " + matchCounter);
                            for(int wAdj=1; (wAdj + w1) < phase1.length && (wAdj + w2) < phase2.length; wAdj++){
                                if( Objects.equals(phase1[(w1+wAdj)],phase2[(w2+wAdj)])){
                                    matchWords++;
                                    matchCounter++;
//                                    sysPrint("word count" + (w1+wAdj) + " Matched phase1: " + phase1[(w1+wAdj)] + " phase2: " + phase2[(w2+wAdj)] + " Matched " + matchWords + " match Counter " + matchCounter);

                                    // End of loop problem
                                    if ((wAdj + w1 + 1 == phase1.length) || (wAdj + w2 + 1 == phase2.length)){
//                                        sysPrint("End of the loop");
                                        matchWords++; // Add back the first match
                                        w1 = w1 + matchCounter - 1; // Reduce 1 due to the loop structure
                                        matchCounter = 0;
                                        break; // Stop the loop
                                    }

                                }
                                else {
//                                    sysPrint("No More Matches");
                                    matchWords++; // Add back the first match
                                    w1 = w1 + matchCounter; // Override the Main loop
                                    matchCounter = 0;
                                    break; // Terminate the loop to save resources
                                }
                            }
                        }
                    }
                }

            }
        }

        return matchWords;

    }

    public static int[] wordCount(String[][][] docArray){
        // String to be scanned to find the pattern.
        int[] totalWords = new int [docArray.length];

        for(int doc = 0; doc < docArray.length; doc++){
            for(int line = 0; line < docArray[doc].length; line++){
                totalWords[doc] += docArray[doc][line].length;
            }
        }

        for(int i = 0; i < totalWords.length; i++){
            sysPrint("Total word count for Doc" + ( i + 1 ) + " is " + totalWords[i]);
        }

        return totalWords;

    }

    public static void phaseMatching(String[][][] docArray){
//        sysPrint("------------ Phase Matching --------------------");

        // ref: http://www.avajava.com/tutorials/lessons/how-do-i-use-numberformat-to-format-a-percent.html
        // % format
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
        defaultFormat.setMinimumFractionDigits(1);
        int[][] totalMatch = new int[docArray.length][docArray.length];
        int[] totalWords = new int[docArray.length];

        for(int doc1=0; doc1<docArray.length; doc1++){
            for(int doc2=0; doc2<docArray.length; doc2++){
                for(int line1=0; line1<docArray[doc1].length; line1++){
                    for(int line2=0; line2<docArray[doc2].length; line2++){
                        int result = phaseCompare(docArray[doc1][line1], docArray[doc2][line2]);
                        totalMatch[doc1][doc2] += result;
                        if(doc1==doc2){
                            totalWords[doc1] += result;
                        } else {
//                            sysPrint("Doc " + doc1 + " compare to Doc " + doc2 + " Matched count " + totalMatch[doc1][doc2] + " Total Words " + totalWords[doc2]);
                        }
                    }
                }
            }
        }

        sysPrint("------------ Printing the phase matching summary --------------------");
        // Show the matching %
        for(int doc1=0; doc1<totalMatch.length;doc1++){
            for (int doc2=0; doc2<totalMatch.length;doc2++){
                if (doc1!=doc2){
                    double matching = ((double)totalMatch[doc1][doc2] / (double)totalWords[doc2]);
                    sysPrint("Doc " + (doc1 + 1) + " compare to Doc " + (doc2 + 1) + " Matched " + totalMatch[doc1][doc2] + " / Total " + totalWords[doc2] + " (" + defaultFormat.format(matching) + ") ");
                }
            }
        }
    }
}

