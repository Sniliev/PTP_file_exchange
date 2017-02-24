package course_project;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MainServer implements AutoCloseable
{
    private ServerSocket firstServerSocket = null;
    private ServerSocket secondServerSocket = null;
    private ServerSocket thirdServerSocket = null;
    private ServerSocket fourthServerSocket = null;
    private ServerSocket fifthServerSocket = null;
    private ServerSocket[] sockets = new ServerSocket[5];
    private Map<String, String> map;

    private class ClientConnectionThread extends Thread
    {
        private ServerSocket serverSocket;

        public ClientConnectionThread(ServerSocket sSocket) {
            this.serverSocket = sSocket;
        }

        @Override
        public void run() {
            Socket socket = null;
            while (true) {
                try {
                    socket = serverSocket.accept();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
                    while (true) {
                        System.out.println("Client " + socket + " connected");

                        String request = reader.readLine();
                        if (request.equals("list-files"))
                        {
                            listFiles(socket);
                            break;
                        }
                        if(request == null) break;
                        String command = request.split(" ")[0];
                        if (command.equals("register")) register(request);
                        else if (command.equals("unregister")) unregister(request);
                        else if (command.equals("list-files")) listFiles(socket);
                        else if (command.equals("exit")) exitUser(request);

                        writer.println(request);
                        writer.flush();
                    }
                    } catch(Exception e){
                        e.printStackTrace();
                    }

            }
        }


    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public Map<String, String> getMap() {
        return map;
    }


    public MainServer(int firstPort,int secondPort, int thirdPort, int fourthPort, int fifthPort,
                      Map<String, String> map) throws IOException {
        System.out.println("Server listening on ports " + firstPort +" "  +secondPort + " " + thirdPort + " " + fourthPort + " " + fifthPort);
        System.out.println("At " + InetAddress.getLocalHost().getHostAddress());
        this.firstServerSocket = new ServerSocket(firstPort);
        this.secondServerSocket = new ServerSocket(secondPort);
        this.thirdServerSocket = new ServerSocket(thirdPort);
        this.fourthServerSocket = new ServerSocket(fourthPort);
        this.fifthServerSocket = new ServerSocket(fifthPort);
        this.sockets[0] = firstServerSocket;
        this.sockets[1] = secondServerSocket;
        this.sockets[2] = thirdServerSocket;
        this.sockets[3] = fourthServerSocket;
        this.sockets[4] = fifthServerSocket;

        setMap(map);
    }
        @Override
        public void close() throws IOException
        {
           // for(ServerSocket serverSocket: sockets)
           // {
           //     if(serverSocket != null)
           //     {
           //         try {
           //             serverSocket.close();
           //         } catch (Exception e) {
           //             e.printStackTrace();
           //         }
           //     }
           // }

      }

    public void register(String request) throws FileNotFoundException {
        List<String> input = new ArrayList<>(Arrays.asList(request.split(" ")));
        input.remove(0);
        String user = input.remove(0);
        for(String add: input)
        {
            addToMap(user, add);
        }
    }
    public void unregister(String request)
    {
        List<String> input = new ArrayList<>(Arrays.asList(request.split(" ")));
        input.remove(0);
        String user = input.remove(0);

        for (String remove: input)
        {
            removeFromMap(user, remove);
        }

    }
    public void exitUser(String request)
    {
        List<String> input = new ArrayList<>(Arrays.asList(request.split(" ")));
        input.remove(0);
        String user = input.remove(0);
        removeUser(user);
    }

    private void removeUser(String user)
    {
        this.map.remove(user);
    }

    private void addToMap(String user, String add)
    {
        if(this.map.containsKey(user))
        {
            String previous = this.map.get(user);
            this.map.remove(user);
            this.map.put(user, add + " " + previous);
        }
        else
        {
            this.map.put(user,add);
        }
    }

    private void removeFromMap(String user, String remove)
    {
        if(this.map.containsKey(user))
        {
            if(this.map.get(user).contains(remove))
            {
                String toPut = this.map.get(user).replace(remove, "");
                this.map.remove(user);
                this.map.put(user, toPut);
                if(this.map.get(user).equals("")) this.map.remove(user);
            }
        }
    }

    public void listFiles(Socket socket) throws IOException
    {
        try(OutputStream out = socket.getOutputStream();
            ObjectOutputStream mapOutputStream = new ObjectOutputStream(out))
        {
            mapOutputStream.writeObject(this.map);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void start() throws IOException
    {
            ClientConnectionThread firstClientConnectionThread = new ClientConnectionThread(firstServerSocket);
            firstClientConnectionThread.start();

            ClientConnectionThread secondClientConnectionThread = new ClientConnectionThread(secondServerSocket);
            secondClientConnectionThread.start();

            ClientConnectionThread thirdClientConnectionThread = new ClientConnectionThread(thirdServerSocket);
            thirdClientConnectionThread.start();

            ClientConnectionThread fourthClientConnectionThread = new ClientConnectionThread(fourthServerSocket);
            fourthClientConnectionThread.start();

            ClientConnectionThread fifthClientConnectionThread = new ClientConnectionThread(fifthServerSocket);
            fifthClientConnectionThread.start();
    }


    public static void main(String[] args)
    {
        Map<String, String> map = new ConcurrentHashMap<>();
        try(MainServer ms = new MainServer( 4444, 4000, 4001,4002,1234, map))
        {
            ms.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}



