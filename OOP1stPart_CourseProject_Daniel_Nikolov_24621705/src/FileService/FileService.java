package FileService;

import Loggers.FileOperationsLogger;

public class FileService {
    // Operation functions

    // open

    // save

    // save as

    // close
    public void close() {


        //FileOperationsLogger.closeLog(/*file name*/);
    }

    /**
     * <span>
     * Assembles string data that gets passed to corresponding log method for the operation.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    public void help() {
        StringBuilder sb = new StringBuilder();

        sb.append("The following commands are supported:").append("\n")
                .append("open <file>").append("\t\t").append("opens <file>").append("\n")
                .append("close").append("\t\t").append("closes currently opened file").append("\n")
                .append("save").append("\t\t").append("saves the currently open file").append("\n")
                .append("save as <file>").append("\t\t").append("saves the currently open file in <file>").append("\n")
                .append("help").append("\t\t").append("prints this information").append("\n")
                .append("exit").append("\t\t").append("exits the program").append("\n");

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
    public void exit() {
        FileOperationsLogger.exitLog();
        System.exit(0);     // not abnormal termination sequence initiation of JVM
    }

}
