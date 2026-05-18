package commandOutputs.loggers;

public class Logger {
    // technical logger

    /**
     *
     */
    public static void save() {
        System.out.println("Successfully saved!");
    }

    public static void operationFailed() {
        System.out.println("Info: Grammar unions operation has not been successful!\n New grammar has not been created!");
    }

}
