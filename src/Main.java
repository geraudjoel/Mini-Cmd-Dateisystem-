import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static void clone(HackerFS fs, String path) {
        java.io.File f = new java.io.File(path);
        for (java.io.File e : Objects.requireNonNull(f.listFiles())) {
            if (!e.isHidden()) {
                if (e.isDirectory() && !e.getName().contains("lib")) {
                    // add dir and recurse
                    try {
                        fs.createDirectory(e.getName());
                        fs.enterDirectory(e.getName());
                        clone(fs, path + "/" + e.getName());
                        fs.leaveDirectory();
                    } catch (AlreadyExists | NoSuchFileOrDirectory ex) {
                        ex.printStackTrace();
                    }
                } else if (e.isFile()) {
                    try {
                        fs.createEmptyFile(e.getName());
                        BufferedReader br = new BufferedReader(new FileReader(e));
                        fs.writeFile(e.getName(), br.lines().collect(Collectors.joining("\n")));
                    } catch (AlreadyExists | FileNotFoundException | NoSuchFileOrDirectory ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        HackerFS fs = new HackerFS();

        // create HackerFS clone of the project directory
        clone(fs, System.getProperty("user.dir"));

        System.out.println("HackerShell for HackerFS");

        boolean loop = true;
        while (loop) {
            System.out.print("$ ");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            String input = buffer.readLine().trim();
            String[] cmdargs = input.split(" ");
            String cmd = cmdargs[0];
            boolean hasArgs = cmdargs.length > 1;
            List<String> onlyArgs = new ArrayList<>(Arrays.asList(cmdargs));
            if (hasArgs) onlyArgs.remove(0);
            switch (cmd.toLowerCase()) {
                case "exit":
                    loop = false;
                    break;
                case "ls":
                    if (hasArgs) System.out.println("ls does not support operands");
                    else System.out.print(fs.list());
                    break;
                case "ll":
                    if (hasArgs) System.out.println("ll does not support operands");
                    else System.out.print(fs.listLong());
                    break;
                case "pwd":
                    System.out.println(fs.getWorkingDirectory());
                    break;
                case "mkdir":
                    if (!hasArgs) System.out.println("missing operand");
                    else {
                        for (String d : onlyArgs) {
                            try {
                                fs.createDirectory(d);
                            } catch (AlreadyExists alreadyExists) {
                                System.out.println("AlreadyExists: " + alreadyExists.getMessage());
                            }
                        }
                    }
                    break;
                case "rm":
                    if (!hasArgs) System.out.println("missing operand");
                    else {
                        for (String d : onlyArgs) {
                            try {
                                fs.remove(d);
                            } catch (NoSuchFileOrDirectory | NotEmpty ex) {
                                System.out.println(ex.getClass().getName() + ": " + ex.getMessage());
                            }
                        }
                    }
                    break;
                case "cd":
                    if (cmdargs.length == 1) fs.enterDirectory();
                    else if (cmdargs[1].equals(".."))
                        fs.leaveDirectory();
                    else {
                        try {
                            fs.enterDirectory(cmdargs[1]);
                        } catch (NoSuchFileOrDirectory noSuchFileOrDirectory) {
                            System.out.println("NoSuchFileOrDirectory: " + noSuchFileOrDirectory.getMessage());
                        }
                    }
                    break;
                case "cat":
                    if (!hasArgs) System.out.println("missing operand");
                    else {
                        for (String f : onlyArgs) {
                            try {
                                System.out.println(fs.readFile(f));
                            } catch (NoSuchFileOrDirectory ex) {
                                System.out.println("NoSuchFileOrDirectory: " + ex.getMessage());
                            }
                        }
                    }
                    break;
                case "find":
                    if (!hasArgs) System.out.print(fs.find());
                    else System.out.print(fs.find(cmdargs[1]));
                    break;
                default:
                    System.out.println("Unknown command: " + cmd);
            }
        }

    }
}
