package commandOutputs.loggers;

import commandOutputs.loggers.loggerFlags.OperationFailedVars;

public class Logger {
    // technical logger


    /**
     * <span>
     * Prints a generic confirmation notification indicating that file context serialization was finalized successfully.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    public static void save() {
        System.out.println("Successfully saved!");
    }

    /**
     * <span>
     * Displays an informational failure message explaining that the structural union could not be completed and no target instance was initialized.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    public static void unionOperationFailed() {
        System.out.println("Info: Grammar unions operation has not been successful!\n New grammar has not been created!");
    }

    /**
     * <span>
     * Evaluates execution flags via a conditional branch and routes diagnostic alerts regarding core runtime failures to the console screen.
     * </span>
     * <br><br>
     * Params: OperationFailedVars operationId
     * @return (void)
     */
    public static void operationFailed(OperationFailedVars operationId) {
        switch (operationId) {
            case CYK:
                System.out.println("CYK operation failed! The grammar is not in CNF format.");
                break;
        }
    }

    /**
     * <span>
     * Outputs a terminal result evaluation message confirming that the designated linguistic rule configuration resolves to an empty formal language set.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    public static void emptyFormalLanguageSet() {
        System.out.println("Result: Language is EMPTY (No rules defined).");
    }

}
