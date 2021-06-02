import java.util.*;

public class HackerFS {
    private final Directory root;
    private Directory wd; // working directory

    /**
     * The constructor.
     * <p>
     * Create a root folder with an empty String as name "" and null as parent.
     * Set the working directory to the root folder.
     */
    public HackerFS() {
        this.root = new Directory("", null);
        this.wd = this.root;
    }

    // ----------------------------------------------------
    // Directory Functions

    public void enterDirectory() {
        this.wd = this.root;
    }

    /**
     * Changes the current working directory to a subdirectory.
     *
     * @param name of the directory that we want to enter
     * @throws NoSuchFileOrDirectory if the directory name does not exist in the current working directory
     */
    public void enterDirectory(String name) throws NoSuchFileOrDirectory {
        Optional<FSObject> existingFSObject = this.wd.contains(name);
        if (existingFSObject.isEmpty()) throw new NoSuchFileOrDirectory("No such File or Directory");
        this.wd = (Directory) existingFSObject.get();
    }

    /**
     * Leave directory, i.e. change working directory to parent directory.
     * If the current working directory is root, do nothing.
     */
    public void leaveDirectory() {
        if (this.wd.getParent() != null) {
            this.wd = (Directory) this.wd.getParent();
        }
    }

    /**
     * Return the name of the current working directory.
     *
     * @return name of the working directory.
     */
    public String getWorkingDirectory() {
        return this.wd.getPath();
    }

    /**
     * Creates a new directory inside the current working directory.
     *
     * @param name of the new directory
     * @throws AlreadyExists if a file or directory with the same name already exists in the current working directory.
     */
    public void createDirectory(String name) throws AlreadyExists {
        Optional<FSObject> existingFSObject = this.wd.contains(name);
        if (existingFSObject.isPresent()) throw new AlreadyExists("Directory already exists!");
        Directory directory = new Directory(name, this.wd);
        this.wd.getContents().add(directory);
    }

    // ----------------------------------------------------
    // File Functions

    /**
     * Create a new empty File inside the current working directory.
     *
     * @param name of the new file
     * @throws AlreadyExists if a file or directory with the same name already exists in the current working directory.
     */
    public void createEmptyFile(String name) throws AlreadyExists {
        Optional<FSObject> existingFSObject = this.wd.contains(name);
        if (existingFSObject.isPresent())
            throw new AlreadyExists("File or Directory already exists in the current working directory!");
        File file = new File(name, this.wd);
        this.wd.getContents().add(file);
    }

    /**
     * Writes to a file inside the current working directory.
     *
     * @param name    of the file data should be written to.
     * @param content that should be written to the file. Existing content is overwritten.
     * @throws NoSuchFileOrDirectory if no file with name exists in the current working directory.
     */
    public void writeFile(String name, String content) throws NoSuchFileOrDirectory {
        Optional<FSObject> existingFSObject = this.wd.contains(name);
        if (existingFSObject.isEmpty()) throw new NoSuchFileOrDirectory("No such File or Directory");
        ((File) existingFSObject.get()).setContent(content);
    }

    /**
     * Read content from a file inside the current working directory.
     *
     * @param name of the file which should be read.
     * @return content of the file
     * @throws NoSuchFileOrDirectory if no file with name exists in the current working directory.
     */
    public String readFile(String name) throws NoSuchFileOrDirectory {
        Optional<FSObject> existingFSObject = this.wd.contains(name);
        if (existingFSObject.isEmpty()) throw new NoSuchFileOrDirectory("No such File or Directory");
        return ((File) existingFSObject.get()).getContent();
    }

    // ----------------------------------------------------
    // Functions involving both Files and Directories

    /**
     * Remove a file or an empty directory inside the current working directory.
     *
     * @param name of the file or directory
     * @throws NoSuchFileOrDirectory if no file or directory exists
     * @throws NotEmpty              in an attempt to remove a non-empty directory
     */
    public void remove(String name) throws NoSuchFileOrDirectory, NotEmpty {
        Optional<FSObject> existingFSObject = this.wd.contains(name);
        if (existingFSObject.isEmpty()) throw new NoSuchFileOrDirectory("No such File or Directory");
        existingFSObject.get().remove();
    }

    // calls corresponding function of Directory class
    public String list() {
        return this.wd.list();
    }

    // calls corresponding function of Directory class
    public String listLong() {
        return this.wd.listLong();
    }

    // calls corresponding function of Directory class
    public String find() {
        return this.wd.find();
    }

    // calls corresponding function of Directory class
    public String find(String name) {
        return this.wd.find(name);
    }
}
