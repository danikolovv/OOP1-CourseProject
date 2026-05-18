package commandOutputs.loggers;

public class FileOperationsLogger {
    // technical file operations logger

    /**
     * <span>
     * Executes System.out.println() method -> File operation help() logging
     * </span>
     * <br><br>
     * @param stringData data that help() should inform the user with
     * @return (void)
     */
    public static void helpLog(String stringData) {
        System.out.println(stringData);
    }

    /**
     * <span>
     * Executes System.out.println() method -> File operation close() logging
     * </span>
     * <br><br>
     * @param fileName name of the file to be contained in the output
     * @return (void)
     */
    public static void closeLog(String fileName) {
        System.out.println(fileName);
    }

    /**
     * <span>
     * Executes System.out.println() method -> File operation exit() logging
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    public static void exitLog() {
        System.out.println("Exiting the program...");
    }

}
