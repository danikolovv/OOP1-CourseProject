import cfgService.operations.CFGOperations;
import cfgService.operations.FileOperations;
import fileServices.FileService;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        // Here the logic of the application is established

        Scanner scanner = new Scanner(System.in);

        // main commands loop parser
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] input = line.split(" ");

            switch (input[0]) {
                // File operations
                case "open":
                    FileOperations.open(input[1]);     // opens on application start and asks if the user want to load file's content
                    break;
                case "close":
                    FileOperations.close();
                    break;
                case "help":
                    FileOperations.help();
                    break;
                case "exit":
                    FileOperations.exit();
                    break;

                // Context-free grammar core operations
                case "list":
                    CFGOperations.list();
                    break;
                case "print":
                    CFGOperations.print(Integer.parseInt(input[1]));
                    break;
                case "save":
                    if (Objects.equals(input[1], "as")) {       // File operation
                        FileOperations.saveAs(input[2]);
                    }
                    else if (!Objects.equals(input[1], null)) {
                        CFGOperations.save(Integer.parseInt(input[1]), input[2]);   // save grammar in a file
                    }
                    else {                                         // File operations
                        FileOperations.save();
                    }
                    break;
                case "addRule":
                    CFGOperations.addRule(Integer.parseInt(input[1]), Arrays.copyOfRange(input, 2, input.length));
                    break;
                case "removeRule":
                    CFGOperations.removeRule(Integer.parseInt(input[1]), Integer.parseInt(input[2]));
                    break;
                case "union":
                    CFGOperations.union(Integer.parseInt(input[1]), Integer.parseInt(input[2]));
                    break;
                case "concat":
                    CFGOperations.concat(Integer.parseInt(input[1]), Integer.parseInt(input[2]));
                    break;
                case "chomsky":
                    CFGOperations.chomsky(Integer.parseInt(input[1]));
                    break;
                case "cyk":
                    CFGOperations.cyk(Integer.parseInt(input[1]));
                    break;
                case "iter":
                    CFGOperations.iter(Integer.parseInt(input[1]));
                    break;
                case "empty":
                    CFGOperations.empty(Integer.parseInt(input[1]));
                    break;
                case "chomskify":
                    CFGOperations.chomskify(Integer.parseInt(input[1]));
                    break;
                default:
                    break;
            }

        }

    }
}
