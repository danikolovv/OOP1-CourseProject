package fileServices;

import cfgService.objects.*;
import cfgService.objects.collections.GrammarManager;
import commandOutputs.loggers.FileOperationsLogger;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileService {
    // Functions operating on files

    // open
    public static void open(String directory) {
        File file = new File(directory);
        GrammarManager grammarManager = GrammarManager.getInstance();

        try {
            // handle folder creation first
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();    // create folders if they don't exist
            }

            if (!file.exists()) {
                file.createNewFile();
                System.out.println("Created new file: " + directory);
            } else {
                List<String> lines = Files.readAllLines(file.toPath());
                Grammar currentGrammar = null;

                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    if (line.startsWith("Grammar with id")) {
                        int id = Integer.parseInt(line.substring(line.indexOf("(") + 1, line.indexOf(")")));
                        currentGrammar = new Grammar(id, grammarManager);
                    }
                    else if (line.contains("->") && currentGrammar != null) {
                        parseRuleLine(line, currentGrammar);
                    }
                }
            }

            // set state so other commands can run
            FileManager.setFile(directory);
            System.out.println("Successfully opened " + directory);

        } catch (Exception e) {
            System.err.println("Error: Could not open file.");
            System.err.println("Details: " + e.getMessage());
            System.exit(1);     // requirements: Terminate on error
        }
    }

    private static void parseRuleLine(String line, Grammar grammar) {
        // input: "№1 A -> sk"
        String content = line.substring(line.indexOf(" ") + 1); // Remove "№1 "
        String[] parts = content.split("->");

        char lhsChar = parts[0].trim().charAt(0);
        String rightSideString = parts[1].trim();

        NonTerminal leftSide = new NonTerminal(lhsChar, grammar);
        List<RightHandSide> rightSide = new ArrayList<>();

        for (char character : rightSideString.toCharArray()) {
            if (Character.isUpperCase(character)) {
                rightSide.add(new NonTerminal(character, grammar));
            } else {
                rightSide.add(new Terminal(character, grammar));
            }
        }

        grammar.getRules().add(new Rule(leftSide, rightSide, grammar));
        grammar.updateRules();
    }

    // save

    // save as

    // close
    public static void close() {


        //FileOperationsLogger.closeLog(/*file name*/);
    }


    public static void save() {
        /*if (!isFileOpened) {
            System.out.println("Error: Please open a file first using the 'open' command.");
            return;
        }*/


    }


    public static void saveAs(String directory) {

    }


    /**
     * <span>
     * Assembles string data that gets passed to corresponding log method for the operation.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    public static void help() {
        StringBuilder sb = new StringBuilder();

        sb.append("The following commands are supported:").append("\n")
                .append("open <file>").append("\t\t").append("opens <file>").append("\n")
                .append("close").append("\t\t\t").append("closes currently opened file").append("\n")
                .append("save").append("\t\t\t").append("saves the currently open file").append("\n")
                .append("save as <file>").append("\t").append("saves the currently open file in <file>").append("\n")
                .append("help").append("\t\t\t").append("prints this information").append("\n")
                .append("exit").append("\t\t\t").append("exits the program").append("\n");

        FileOperationsLogger.helpLog(sb.toString());
    }

    /**
     * <span>
     * Calls System.exit(exitStatus) method for JVM termination i.e. program execution termination
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    public static void exit() {
        FileOperationsLogger.exitLog();
        System.exit(0);     // not abnormal termination sequence initiation of JVM
    }

}
