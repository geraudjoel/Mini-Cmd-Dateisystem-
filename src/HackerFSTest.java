import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HackerFSTest {
    private HackerFS fs;

    @BeforeEach
    public void setUp() {
        fs = new HackerFS();
    }

    @Test
    public void testDirs() throws AlreadyExists, NoSuchFileOrDirectory {
        assertEquals("/", fs.getWorkingDirectory().trim());
        fs.leaveDirectory();
        assertEquals("/", fs.getWorkingDirectory().trim());
        // add directories d1, d2
        fs.createDirectory("d1");
        fs.createDirectory("d2");
        fs.enterDirectory("d1");
        assertEquals("/d1/", fs.getWorkingDirectory().trim());
        fs.leaveDirectory();
        assertEquals("/", fs.getWorkingDirectory().trim());
        fs.enterDirectory("d1");
        fs.enterDirectory();
        assertEquals("/", fs.getWorkingDirectory().trim(), "enterDirectory() did not change back to file system root");
    }

    @Test
    public void testDirsError() throws AlreadyExists, NoSuchFileOrDirectory {
        fs.createDirectory("d2");
        fs.createDirectory("d1");
        assertThrows(AlreadyExists.class, () -> fs.createDirectory("d1"));
        fs.enterDirectory("d1");
        assertThrows(NoSuchFileOrDirectory.class, () -> fs.enterDirectory("d3"));
        assertEquals("/d1/", fs.getWorkingDirectory().trim());
    }

    @Test
    public void testFiles() throws AlreadyExists, NoSuchFileOrDirectory {
        String msg = "Hello World!";
        fs.createEmptyFile("f1.txt");
        fs.writeFile("f1.txt", msg);
        assertEquals(msg, fs.readFile("f1.txt"));
        fs.createEmptyFile("f2.txt");
        assertNull(fs.readFile("f2.txt"));
    }

    @Test
    public void testFilesError() throws AlreadyExists {
        fs.createEmptyFile("f1.txt");
        assertThrows(AlreadyExists.class, () -> fs.createEmptyFile("f1.txt"));
        assertThrows(NoSuchFileOrDirectory.class, () -> fs.writeFile("f2.txt", "Hello"));
        assertThrows(NoSuchFileOrDirectory.class, () -> fs.readFile("f2.txt"));
    }

    @Test
    public void testMixed() throws AlreadyExists, NoSuchFileOrDirectory, NotEmpty {
        // remove, list, listLong, find
        fs.createDirectory("d1");
        fs.createDirectory("d2");
        fs.enterDirectory("d1");
        fs.createEmptyFile("f1.txt");
        String msg = "Hello";
        fs.writeFile("f1.txt", msg);
        assertEquals("f1.txt", fs.list().trim());
        assertEquals("f f1.txt (size " + msg.length() + ")", fs.listLong().trim());
        fs.leaveDirectory();
        assertEquals("/d1/f1.txt", fs.find("f1").trim());
        assertTrue(fs.find().contains("/d1/f1.txt"));
        assertTrue(fs.find().contains("/d1/"));
        assertTrue(fs.find().contains("/d2/"));
        assertTrue(fs.listLong().contains("d d1 (not empty)"));
        fs.enterDirectory("d1");
        fs.remove("f1.txt");
        fs.leaveDirectory();
        assertTrue(fs.list().contains("d1"));
        assertTrue(fs.list().contains("d2"));
        assertFalse(fs.find().contains("f1.txt"));
        assertTrue(fs.listLong().contains("d d2 (empty)"));
    }

    @Test
    public void testMixedError() throws AlreadyExists, NoSuchFileOrDirectory, NotEmpty {
        // check that if file f1 exists, no directory f1 can be created and vice versa
        fs.createEmptyFile("f1");
        assertThrows(AlreadyExists.class, () -> fs.createDirectory("f1"));
        fs.remove("f1");
        fs.createDirectory("d1");
        assertThrows(AlreadyExists.class, () -> fs.createEmptyFile("d1"));
        // check that removing a directory which contains a file throws NotEmpty
        fs.enterDirectory("d1");
        fs.createEmptyFile("f1.txt");
        fs.leaveDirectory();
        assertThrows(NotEmpty.class, () -> fs.remove("d1"));
    }
}
