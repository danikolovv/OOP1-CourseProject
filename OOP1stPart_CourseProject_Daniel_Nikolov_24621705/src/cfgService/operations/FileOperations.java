package cfgService.operations;

import fileService.FileService;

public class FileOperations {

    /**
     * <span>
     * Dispatches the absolute folder pathway parameter down to the primary system storage component to initialize data parsing.
     * </span>
     * <br><br>
     * Params: String directory
     * @return (void)
     */
    public static void open(String directory) {
        FileService.open(directory);    // execute open file
    }

    /**
     * <span>
     * Triggers the memory cleanup routine to disconnect active file context tracks and unlock access locks.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    public static void close() {
        FileService.close();
    }

    /**
     * <span>
     * Initiates a serialization write action targeting the primary system path pointer context.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    public static void save() {
        FileService.save();
    }

    /**
     * <span>
     * Validates input location parameters before shifting the target session pointer to a fresh disk storage address.
     * </span>
     * <br><br>
     * Params: String directory
     * @return (void)
     */
    public static void saveAs(String directory) {
        if (directory != null)
            FileService.saveAs(directory);
    }

    /**
     * <span>
     * Routes requests to print program command availability syntax strings directly into the terminal stream.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    public static void help() {
        FileService.help();
    }

    /**
     * <span>
     * Commands the virtual machine engine instance to kill execution tracking frameworks and terminate runtime activity.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    public static void exit() {
        FileService.exit();
    }
}
