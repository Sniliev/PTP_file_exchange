package course_project;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientServer extends Thread implements AutoCloseable
{
    private ServerSocket serverSocket = null;
    private List<String> myUploads;

    public List<String> getMyUploads() {
        return myUploads;
    }

    public void setMyUploads(List<String> myUploads) {
        this.myUploads = myUploads;
    }

    public ClientServer(int port) throws IOException
    {
        this.serverSocket = new ServerSocket(port);
        this.myUploads = new ArrayList<>();
    }

    public void addUpload(String directory)
    {
        this.myUploads.add(directory);
    }

    @Override
    public void run()
    {
        while(true)
        {
            Socket socket = null;
            InputStream fis = null;
            OutputStream out = null;
            String path = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException ex) {
                System.out.println("Can't accept client connection. ");
            }
            System.out.println("Peer " + socket + " connected");

            try
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                path = reader.readLine();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if(!this.myUploads.contains(path))
            {
                System.out.println("Attempt at accesing not a listed file!");
                continue;
            }

            try {
                out = socket.getOutputStream();
            } catch (IOException ex) {
                System.out.println("Can't get socket output stream. ");
            }

            try {
                fis = new FileInputStream(path);
            } catch (FileNotFoundException ex) {
                System.out.println("File not found. ");
            }

            BufferedInputStream in = new BufferedInputStream(fis);

            byte[] bytes = new byte[8192];

            int count;
            try {
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
                out.flush();
                socket.close();
                fis.close();
                in.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close()
    {
        if(serverSocket != null)
        {
            try
            {
                serverSocket.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}

