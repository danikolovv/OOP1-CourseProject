package fileService;

public class FileManager {
    private static String currentPath = null;
    private static boolean isOpened = false;

    public static void setFile(String path) {
        currentPath = path;
        isOpened = true;
    }

    // getters
    public static boolean isFileLoaded() {
        return isOpened;
    }

    public static String getCurrentPath() {
        return currentPath;
    }

    // setters
    public static void setCurrentPath(String currentPathReceive) {
        currentPath = currentPathReceive;
    }

}
