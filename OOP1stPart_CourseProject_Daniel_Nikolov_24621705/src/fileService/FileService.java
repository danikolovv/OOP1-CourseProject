package fileService;

import cfgService.objects.*;
import cfgService.objects.collections.GrammarManager;
import commandOutputs.loggers.FileOperationsLogger;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileService {
    // Functions operating on files

    /**
     * <span>
     * Validates file presence, clears current state, and parses input line sequences into structures.
     * </span>
     * <br><br>
     * Params: String directory
     * @return (void)
     */
    // open
    public static void open(String directory) {
        File file = new File(directory);
        GrammarManager grammarManager = GrammarManager.getInstance();

        grammarManager.clearPresentData();

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

                        currentGrammar = grammarManager.getGrammar(id);
                        if (currentGrammar == null) {
                            currentGrammar = new Grammar(id, grammarManager);
                        }
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

    /**
     * <span>
     * Deconstructs raw text production rules into non-terminal variables and right-hand side token paths.
     * </span>
     * <br><br>
     * Params: String line, Grammar grammar
     * @return (void)
     */
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

    /**
     * <span>
     * Wipes loaded context arrays from execution memories and detaches the active tracking path handles.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    // close
    public static void close() {
        if (!FileManager.isFileLoaded()) {
            System.out.println("Error: No open file to close.");
            return;
        }

        String closedFileName = new File(FileManager.getCurrentPath()).getName();

        // 1. Wipe the runtime data from memory
        GrammarManager.getInstance().clearPresentData();

        // 2. Clear state variables so other commands are locked again
        FileManager.setCurrentPath(null);

        System.out.println("Successfully closed " + closedFileName);
        FileOperationsLogger.closeLog(closedFileName);
    }

    /**
     * <span>
     * Serializes memory objects back to raw format characters inside the primary path address context.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    // save
    public static void save() {
        if (!FileManager.isFileLoaded()) {
            System.out.println("Error: Please open a file first using the 'open' command.");
            return;
        }

        // Explicitly re-open the file context strictly for writing, closing it immediately after
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(FileManager.getCurrentPath()))) {
            GrammarManager grammarManager = GrammarManager.getInstance();

            for (Grammar grammar : grammarManager.getGrammarValuesOnly()) { // Assuming getGrammars() returns List<Grammar>
                writer.println("Grammar with id (" + grammar.getId() + ")");

                int ruleCounter = 1;
                for (Rule rule : grammar.getRules()) {
                    // Reconstruct Left Hand Side string
                    String lhs = String.valueOf(rule.getNonTerminal().getSymbol());

                    // Reconstruct Right Hand Side string
                    StringBuilder rhs = new StringBuilder();
                    for (RightHandSide component : rule.getRightSide()) {
                        rhs.append(component.getSymbol()); // Assuming your Terminal/NonTerminal classes have getSymbol()
                    }

                    // Print matching your parsing format: "№1 A -> sk"
                    writer.println("№" + ruleCounter + " " + lhs + " -> " + rhs.toString());
                    ruleCounter++;
                }
                writer.println();
            }
            System.out.println("Successfully saved changes to " + FileManager.getCurrentPath());

        } catch (Exception e) {
            System.err.println("Error: Could not save changes to the file.");
            System.err.println("Details: " + e.getMessage());
        }
    }

    /**
     * <span>
     * Reroutes the session destination pointer before initializing a fresh text stream creation cycle.
     * </span>
     * <br><br>
     * Params: String directory
     * @return (void)
     */
    // save as
    public static void saveAs(String directory) {
        if (!FileManager.isFileLoaded()) {
            System.out.println("Error: Please open a file first using the 'open' command.");
            return;
        }

        try {
            File file = new File(directory);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            // Update the active file path to the new directory target
            FileManager.setCurrentPath(directory);

            // Delegate to the save operation to write data to the new location
            save();

        } catch (Exception e) {
            System.err.println("Error: Failed to process Save As operation.");
            System.err.println("Details: " + e.getMessage());
        }
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
