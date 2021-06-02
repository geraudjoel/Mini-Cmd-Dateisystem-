public class File implements FSObject {
    private String name;
    private FSObject parent;
    private String content;

    /**
     * The constructor.
     *
     * @param name   Name of the file.
     * @param parent Parent FSObject of the file.
     */
    public File(String name, FSObject parent) {
        this.name = name;
        this.parent = parent;
    }

    // name getter
    @Override
    public String getName() {
        return this.name;
    }

    // name setter
    @Override
    public void setName(String name) {
        this.name = name;
    }

    // parent getter
    @Override
    public FSObject getParent() {
        return this.parent;
    }

    // parent setter
    @Override
    public void setParent(FSObject parent) {
        this.parent = parent;
    }

    /**
     * Read contents of the file.
     *
     * @return content of the file
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Write content to file.
     *
     * @param content gets written to the file. Any existing content is overwritten.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Calculate size of the file contents.
     *
     * @return length of the content String
     */
    public int getSize() {
        if (this.content == null) return 0;
        return this.getContent().length();
    }

    /**
     * Determine the full path of the file.
     * E.g. for a file "f1.txt" inside a directory "d1" which in turn is contained in the root folder,
     * one would get "/d1/f1.txt"
     *
     * @return full path of the file.
     */
    @Override
    public String getPath() {
        return this.parent.getPath() + this.name;
    }

    /**
     * Removes the file.
     * If it has a parent directory, make sure to remove it from the directory's content list.
     */
    @Override
    public void remove() {
        if (this.parent instanceof Directory) {
            ((Directory) this.parent).removeEntry(this);
        }
        this.parent = null;
    }
}
