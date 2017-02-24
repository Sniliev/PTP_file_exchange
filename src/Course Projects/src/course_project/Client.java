package course_project;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class Client {
    private Map<String, String> userNameAndIP;
    private String remoteHost;
    private int remotePort;
    private String thisUser;
    private ClientServer cs;

    private Map<String, String> getUserNameAndIP()
    {
        return this.userNameAndIP;
    }
    private String getRemoteHost() {
        return remoteHost;
    }

    public ClientServer getCs() {
        return cs;
    }
    private int getRemotePort() {
        return remotePort;
    }
    private void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
    private void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }
    private void setUserNameAndIP(HashMap<String, String> userNameAndIP)
    {
        this.userNameAndIP = userNameAndIP;
    }
    public Client(HashMap<String, String> userNameAndIP,
                 String remoteHost, int remotePort, int serverPort, String userName) throws IOException
    {
        setUserNameAndIP(userNameAndIP);
        this.thisUser = userName + "-" + InetAddress.getLocalHost().getHostAddress() + ":" + serverPort;
        setRemoteHost(remoteHost);
        setRemotePort(remotePort);
        this.cs = new ClientServer(serverPort);
        cs.start();
    }
    public String seed(String directory) throws IOException
    {
        String s = getSeedString(directory);
        return "register " + this.thisUser + " " + s;
    }
    private String getSeedString(String directory) throws IOException
    {
        String res = "";
        File[] files = new File(directory).listFiles();

        for(File f: files)
        {
            if(f.isFile())
            {
                res = res.concat(f.getAbsolutePath());
                res = res.concat(" ");
                this.cs.addUpload(f.getAbsolutePath());
            }
            if(f.isDirectory())
            {
                res = res.concat(getSeedString(f.getAbsolutePath()));
            }
        }
        return res;
    }

    public String unregister(String file)
    {
        return "unregister " + this.thisUser + " " + file;
    }

    public void listFiles()
    {
        for(String key: this.userNameAndIP.keySet())
        {
            System.out.println(key);
            String[] paths = this.userNameAndIP.get(key).split(" ");
            for (String path: paths)
            {
                System.out.println("\t" + path);
            }
        }
    }

    public String exit()
    {
        this.cs.setMyUploads(new ArrayList<>());
        return "exit " + this.thisUser;
    }

    public String search(String request)
    {
        List<String> input = new ArrayList<>(Arrays.asList(request.split(" ")));
        String regEx = input.get(1);
        String res = " ";

        for(String value: this.userNameAndIP.values())
        {
            String[] valuesForKey = value.split(" ");
            for(String path: valuesForKey)
            {
                if(path.matches(regEx)) res += path + " ";
            }
        }
        return res;

    }

    public void download(String ipAndPath)
    {
        String[] splited = ipAndPath.split(" ");
        String ip = splited[0].split(":")[0];
        int port = Integer.parseInt(splited[0].split(":")[1]);
        String pathOnUser = splited[1];
        String pathToSave = splited[2];

       try(Socket socket = new Socket(ip, port);
            PrintWriter writer = new PrintWriter(socket.getOutputStream()))
       {
           System.out.println("Connected to peer: " + socket);
           File file = new File(pathToSave);
           file.createNewFile();
           writer.println(pathOnUser);
           writer.flush();

           byte[] bytes = new byte[8192];
           InputStream in = socket.getInputStream();
           FileOutputStream fos = new FileOutputStream(file);
           BufferedOutputStream out = new BufferedOutputStream(fos);

           int count;
           while ((count = in.read(bytes)) > 0) {
               out.write(bytes, 0, count);
           }
           out.flush();

           fos.close();
           in.close();
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
        System.out.println(pathOnUser + " downloaded to " + pathToSave);
    }

    public void start() throws IOException {
        SyncRegistered syncRegistered = new SyncRegistered();
        syncRegistered.start();
        Scanner console = new Scanner(System.in);
        String consoleInput;
        System.out.println("Enter a command:");
        while ((consoleInput = console.nextLine()) != null) {

            String command = consoleInput.split(" ")[0];

            if(command.equals("list-files"))
            {
                listFiles();
            }
            else if(command.equals("search")) System.out.println(search(consoleInput));
            else if(command.equals("download")) connectToPeer(consoleInput);
            else if(command.equals("seed") || command.equals("unregister") || command.equals("exit"))
                connectToServer(consoleInput);
            else
            {
                System.out.println("Invalid command, try again");
                continue;
            }
            System.out.println("Enter a command:");
        }

    }

    private void connectToServer(String consoleInput) throws IOException
    {
        try(Socket socket = new Socket(remoteHost, remotePort);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream())))
        {
            System.out.println("Client" + socket + "conected to Server");
             List<String> input = new ArrayList<>(Arrays.asList(consoleInput.split(" ")));
             String command = input.get(0);
             input.remove(0);
             String res ="";
             if(command.equals("seed")) res= seed(String.join(" ", input));
             else if(command.equals("unregister")) res= unregister(String.join(" ", input));
             else if(command.equals("exit")) res = exit();

             out.println(res);
             out.flush();

             String response = in.readLine();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private void connectToPeer(String consoleInput)
    {
        List<String> input = new ArrayList<>(Arrays.asList(consoleInput.split(" ")));
        input.remove(0);
        download(String.join(" ",input));
    }

    private class SyncRegistered extends Thread
    {
        @Override
        public void run()
        {
            while(true)
            {
                try (Socket socketSync = new Socket(remoteHost, 4000);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(socketSync.getOutputStream())))
                {
                    writer.println("list-files");
                    writer.flush();
                    try(InputStream inputStream = socketSync.getInputStream();
                        ObjectInputStream mapInputStream = new ObjectInputStream(inputStream)) {
                        userNameAndIP = (Map) mapInputStream.readObject();
                        Thread.sleep(30000);
                        usersFolder();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }

    }

    public void usersFolder() throws IOException
    {
        File file = new File("/home/svetoslav/usersMap");
        if(!file.isFile()) file.createNewFile();

        try(PrintWriter writer = new PrintWriter(new FileWriter(file)))
        {
           for(String user: this.userNameAndIP.keySet())
           {
               writer.println(user);
           }
           writer.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException
    {
        System.out.println("Select a port for the Client Server");
        Scanner scanner = new Scanner(System.in);
        int port;
        port = scanner.nextInt();
        System.out.println("Enter the IP and the port of the Server");
        String serverIp = scanner.nextLine();
        scanner.nextLine();
        int serverPort = scanner.nextInt();
        System.out.println("Enter a username");
        scanner.nextLine();
        String userName = scanner.nextLine();
        HashMap<String, String> map = new HashMap<>();
        Client client = new Client(map, serverIp, serverPort, port, userName);
        client.start();
        System.out.println("Client server listening on port " + port);
    }
}
