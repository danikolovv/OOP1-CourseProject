package cfgService.operations;

import fileServices.FileService;

public class FileOperations {

    public static void open(String filename) {
        FileService.open(filename);    // execute open file
    }

    public static void close() {
        FileService.close();
    }

    public static void save() {
        FileService.save();
    }

    public static void saveAs(String directory) {
        if (directory != null)
            FileService.saveAs(directory);
    }

    public static void help() {
        FileService.help();
    }

    public static void exit() {
        FileService.exit();
    }
}
