package course_project;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by svetoslav on 20.02.17.
 */
public class ClientServerTest {
    @Test
    public void addUpload() throws Exception
    {
        ClientServer clientServer = new ClientServer(1444);
        clientServer.addUpload("/home/svetoslav/file1");
        clientServer.addUpload("/home/svetoslav/file2");
        clientServer.addUpload("/home/svetoslav/file3");
        ArrayList<String > expected = new ArrayList<>();
        expected.add("/home/svetoslav/file1");
        expected.add("/home/svetoslav/file2");
        expected.add("/home/svetoslav/file3");

        assertEquals(expected, clientServer.getMyUploads());
    }

}