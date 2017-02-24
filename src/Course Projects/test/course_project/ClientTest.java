package course_project;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class ClientTest {
    private static Client cl;

    @BeforeAll
    public static void setUp() throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("User1", "/home/user1/file1");
        map.put("User2", "/home/user2/file1");
        map.put("User3", "/home/user3/file1");
        map.put("User4", "/home/user4/file1");
        String host = "127.0.1.1";
        int rPort = 4444;
        int sPort = 4000;
        String username = "User5";
        cl = new Client(map, host, rPort, sPort, username);
    }
    @AfterAll
    public void close()
    {
    }
    @Test
    public void seed() throws IOException
    {
        String expected = "register User5-127.0.1.1:4000 /home/svetoslav/Test/2.txt " +
                "/home/svetoslav/Test/1.txt ";
        assertEquals(expected, cl.seed("/home/svetoslav/Test"));
    }

    @Test
    public void search() throws IOException
    {
        String res = cl.search("search (.*)user2(.*)");
        String expected = " /home/user2/file1 ";
        assertEquals(expected, res);
    }

    @Test
    public void usersFolder() throws IOException
    {
        cl.usersFolder();
        File users = new File("/home/svetoslav/usersMap");
        assertTrue(users.isFile());
    }

    @Test
    public void exit() throws IOException
    {
        cl.exit();
        assertEquals(new ArrayList<>(), cl.getCs().getMyUploads());
    }

    @Test
    public void unregister() throws IOException
    {
        String expected = "unregister User5-127.0.1.1:4000 /home/User5/file1";

        assertEquals(expected, cl.unregister("/home/User5/file1"));
    }
}