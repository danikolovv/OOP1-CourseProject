package commandOutputs.loggers;

public class Logger {
    // technical logger


    /**
     *
     */
    public static void save() {
        System.out.println("Successfully saved!");
    }



    public static void unionOperationFailed() {
        System.out.println("Info: Grammar unions operation has not been successful!\n New grammar has not been created!");
    }



    public static void operationFailed(OperationFailedVars operationId) {
        switch (operationId) {
            case CYK:
                System.out.println("CYK operation failed! The grammar is not in CNF format.");
                break;
        }
    }



    public static void emptyFormalLanguageSet() {
        System.out.println("Result: Language is EMPTY (No rules defined).");
    }

}
