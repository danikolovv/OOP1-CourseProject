package cfgService.operations;

import cfgService.objects.Grammar;
import cfgService.objects.RightHandSide;
import cfgService.objects.Rule;
import cfgService.objects.collections.GrammarManager;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CFGFileOperations {

    /**
     * <span>
     * Serializes a single, specific isolated grammar identity out to an external text file without modifying or wiping the active primary application session path context.
     * </span>
     * <br><br>
     * Params: int id, String filename
     * @return (void)
     */
    public static void save(int id, String filename) {
        // implement file saving of the Grammar
        GrammarManager grammarManager = GrammarManager.getInstance();
        Grammar grammar = grammarManager.getGrammar(id);

        // requirement verification: Check if target grammar exists in application memory
        if (grammar == null) {
            System.out.println("Error: Grammar with ID " + id + " does not exist.");
            return;
        }

        File file = new File(filename);

        // handle subdirectory structural creation safely if needed
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        // isolated file context writing via auto-closing try-with-resources statement
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {

            // write the header matching your parsing layout structure
            writer.println("Grammar with id (" + grammar.getId() + ")");

            // serialize rules matching production layout format: "№1 A -> sk"
            if (grammar.getRules() != null && !grammar.getRules().isEmpty()) {
                int ruleCounter = 1;
                for (Rule rule : grammar.getRules()) {
                    String lhs = String.valueOf(rule.getNonTerminal().getSymbol());

                    StringBuilder rhs = new StringBuilder();
                    for (RightHandSide component : rule.getRightSide()) {
                        rhs.append(component.getSymbol());
                    }

                    writer.println("№" + ruleCounter + " " + lhs + " -> " + rhs.toString());
                    ruleCounter++;
                }
            }

            // exactly one trailing newline at the bottom of the structure
            writer.println();

            System.out.println("Successfully saved grammar " + id + " to isolated file: " + filename);

        } catch (Exception e) {
            System.err.println("Error: Failed to execute isolated grammar save operation.");
            System.err.println("Details: " + e.getMessage());
        }
    }
}
