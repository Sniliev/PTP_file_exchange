package course_project;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by svetoslav on 19.02.17.
 */
public class MainServerTest {

    public MainServer setUp() throws IOException
    {
        HashMap<String, String> map = new HashMap<>();
        map.put("User1", "/home/user1/file1");
        map.put("User2", "/home/user2/file1");
        map.put("User3", "/home/user3/file1");
        map.put("User4", "/home/user4/file1");
        MainServer mainServer = new MainServer(
                4000, 4001, 4002,
                4003, 4004, map);

        return mainServer;
    }
    @Test
    public void register() throws Exception
    {
        MainServer mainServer = setUp();
        mainServer.register("register User5 /home/user5/myFile");;
        HashMap<String, String> expected = new HashMap<>();
        expected.put("User1", "/home/user1/file1");
        expected.put("User2", "/home/user2/file1");
        expected.put("User3", "/home/user3/file1");
        expected.put("User4", "/home/user4/file1");
        expected.put("User5", "/home/user5/myFile");

        assertEquals(expected, mainServer.getMap());
    }

    @Test
    public void unregister() throws IOException
    {
        MainServer mainServer = setUp();
        mainServer.unregister("unregister User4 /home/user4/file1");;
        HashMap<String, String> expected = new HashMap<>();
        expected.put("User1", "/home/user1/file1");
        expected.put("User2", "/home/user2/file1");
        expected.put("User3", "/home/user3/file1");

        assertEquals(expected, mainServer.getMap());
    }

    @Test
    public void exitUser() throws IOException
    {
        MainServer mainServer = setUp();
        mainServer.exitUser("exit User3");

        HashMap<String, String> expected = new HashMap<>();
        expected.put("User1", "/home/user1/file1");
        expected.put("User2", "/home/user2/file1");
        expected.put("User4", "/home/user4/file1");

        assertEquals(expected, mainServer.getMap());
    }

}