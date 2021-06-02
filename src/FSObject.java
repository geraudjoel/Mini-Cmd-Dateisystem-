public interface FSObject {
    /**
     * @return the name of the file system object
     */
    String getName();

    /**
     * @param name the new name of the file system object
     */
    void setName(String name);

    /**
     * @return reference to the parent file system object
     */
    FSObject getParent();

    /**
     * Updates the parent reference
     *
     * @param parent the new parent
     */
    void setParent(FSObject parent);

    /**
     * @return the full path of the object in the file system
     */
    String getPath();

    /**
     * Remove the file system object
     *
     * @throws NotEmpty If the file system object is a non-empty directory
     */
    void remove() throws NotEmpty;
}
