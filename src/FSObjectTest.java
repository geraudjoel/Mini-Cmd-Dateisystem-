import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FSObjectTest {
    private Directory root;
    private Directory d1;
    private Directory d2;
    private File f1;

    @BeforeEach
    public void setUp() throws AlreadyExists {
        root = new Directory("", null);
        d1 = new Directory("d1", root);
        d2 = new Directory("d2", root);
        root.addEntry(d1);
        root.addEntry(d2);
        f1 = new File("f1.txt", d1);
        d1.addEntry(f1);
        // root
        // +- d1
        // |  +- f1.txt
        // +- d2
    }

    @Test
    void testGetName() {
        assertEquals("", root.getName());
        assertEquals("d1", d1.getName());
        assertEquals("d2", d2.getName());
        assertEquals("f1.txt", f1.getName());
    }

    @Test
    void testSetName() {
        d1.setName("d1new");
        d2.setName("d2new");
        f1.setName("f1new.txt");
        assertEquals("", root.getName());
        assertEquals("d1new", d1.getName());
        assertEquals("d2new", d2.getName());
        assertEquals("f1new.txt", f1.getName());
    }

    @Test
    void testGetParent() {
        assertNull(root.getParent());
        assertEquals(root, d1.getParent());
        assertEquals(root, d2.getParent());
        assertEquals(d1, f1.getParent());
    }

    @Test
    void testFileOps() {
        assertNull(f1.getContent());
        assertEquals(0, f1.getSize());

        String msg = "Hallo";
        f1.setContent(msg);
        assertEquals(msg, f1.getContent());
        assertEquals(msg.length(), f1.getSize());
    }

    @Test
    void testGetPath() {
        assertEquals("/", root.getPath());
        assertEquals("/d1/", d1.getPath());
        assertEquals("/d2/", d2.getPath());
        assertEquals("/d1/f1.txt", f1.getPath());
    }

    @Test
    void testContainsFile() {
        Optional<File> of1 = d1.containsFile(f1.getName());
        assertTrue(of1.isPresent());
        assertEquals(f1, of1.get());
    }

    @Test
    void testNotContainsFile() {
        assertFalse(d1.containsFile("foo.java").isPresent());
        assertFalse(root.containsFile(d1.getName()).isPresent());
    }

    @Test
    void testContainsDirectory() {
        assertTrue(root.containsDirectory(d1.getName()).isPresent());
        assertEquals(d1, root.containsDirectory(d1.getName()).get());
        assertTrue(root.containsDirectory(d2.getName()).isPresent());
    }

    @Test
    void testNotContainsDirectory() {
        assertFalse(d1.containsDirectory(f1.getName()).isPresent());
        assertFalse(root.containsDirectory("foo").isPresent());
    }

    @Test
    void testContains() {
        assertTrue(root.contains(d1.getName()).isPresent());
        assertTrue(d1.contains(f1.getName()).isPresent());
    }

    @Test
    void testNotContains() {
        assertFalse(root.contains("foo").isPresent());
    }

    @Test
    void testRemoveEntry() {
        d1.removeEntry(f1);
        assertFalse(d1.contains(f1.getName()).isPresent());
        assertEquals(d1, f1.getParent());
    }

    @Test
    void testRemoveNonEmptyDir() {
        assertThrows(NotEmpty.class, () -> d1.remove());
        assertEquals(root, d1.getParent());
        assertThrows(NotEmpty.class, () -> root.remove());
    }

    @Test
    void testRemoveFile() {
        f1.remove();
        assertFalse(d1.contains(f1.getName()).isPresent());
        assertFalse(d1.containsFile(f1.getName()).isPresent());
        assertNull(f1.getParent());
    }

    @Test
    void testRemoveEmptyDir() throws NotEmpty {
        d2.remove();
        assertFalse(root.contains(d2.getName()).isPresent());
        assertFalse(root.containsDirectory(d2.getName()).isPresent());
        assertNull(d2.getParent());
    }

    @Test
    void testList() {
        //assertEquals(2, root.list().split("\n").length);
        assertTrue(root.list().contains("d1"));
        assertTrue(root.list().contains("d2"));
        //assertFalse(root.list().contains("f1.txt"));
        assertTrue(root.list().contains("f1.txt"));
        assertEquals("", d2.list().trim());
        assertEquals("f1.txt", d1.list().trim());
    }

    @Test
    void testListLong() {
        //assertEquals(2, root.listLong().split("\n").length);
        assertTrue(root.listLong().contains("d d1 (not empty)"));
        assertTrue(root.listLong().contains("d d2 (empty)"));
        assertTrue(d1.listLong().contains("f f1.txt (size 0)"));
        f1.setContent("Hallo");
        assertTrue(d1.listLong().contains("f f1.txt (size 5)"));
        assertEquals("", d2.list().trim());
    }

    @Test
    void testFindName() {
        assertEquals("/d1/f1.txt", root.find("f1").trim());
        assertEquals("/d1/f1.txt", root.find("txt").trim());
        assertEquals("/d1/", root.find("d1").trim());
        assertTrue(root.find("d").contains("/d1/"));
        assertTrue(root.find("d").contains("/d2/"));
        assertFalse(root.find("d").contains("f1"));
        assertTrue(root.find("1").contains("/d1/"));
        assertTrue(root.find("1").contains("/d1/f1.txt"));
        assertFalse(root.find("1").contains("d2"));
    }

    @Test
    void testDontFind() {
        assertEquals("", d1.find("2").trim());
        assertEquals("", root.find("abc").trim());
    }

    @Test
    void testFindAll() {
        assertTrue(root.find().contains("/d1/"));
        assertTrue(root.find().contains("/d2/"));
        assertTrue(root.find().contains("/d1/f1.txt"));
    }
}
