//package course_project;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.*;
//import java.nio.file.*;
//import java.nio.file.attribute.BasicFileAttributes;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Created by svetoslav on 07.02.17.
// */
//class MainServerTest2
//{
//   // @Test
//   // void register() throws FileNotFoundException
//   // {
//   //     MainServer s1 = new MainServer("/home/svetoslav/Torrents1");
//
//   //     String reg1 = "Pesho123-87.120.123.76:1234 ~/myTorrents/IamAPirate";
//   //     String reg2 = "Pesho123-87.120.123.76:1234 ~/myTorrents/IamNotACrook";
//
//   //     s1.register(reg1);
//   //     s1.register(reg2);
//
//   //     String line;
//   //     boolean flag1 = false;
//   //     boolean flag2 = false;
//   //     try(BufferedReader r = new BufferedReader(new FileReader("/home/svetoslav/Torrents1/Pesho123.csv")))
//   //     {
//   //           while((line = r.readLine()) != null)
//   //           {
//   //               if(line.equals("~/myTorrents/IamAPirate"))
//   //               {
//   //                   flag1 = true;
//   //               }
//   //               if(line.equals("~/myTorrents/IamNotACrook"))
//   //               {
//   //                   flag2 = true;
//   //               }
//   //               if(flag1 && flag2) break;
//
//   //           }
//   //     }
//   //     catch (Exception e)
//   //     {
//   //         e.printStackTrace();
//   //     }
//
//   //     assertTrue(flag1 && flag2);
//   // }
//
//    @Test
//    void unregister() throws IOException
//    {
//        MainServer s1 = new MainServer("/home/svetoslav/Torrents1");
//
//        String reg1 = "Pesho123-87.120.123.76:1234 ~/myTorrents/IamAPirate";
//        String reg2 = "Pesho123-87.120.123.76:1234 ~/myTorrents/IamNotACrook";
//
//        s1.register(reg1);
//        s1.register(reg2);
//
//        s1.unregister(reg1);
//        String line;
//        boolean flag = true;
//        try(BufferedReader r = new BufferedReader(new FileReader("/home/svetoslav/Torrents1/Pesho123.csv")))
//        {
//              while((line = r.readLine()) != null)
//              {
//                  if(line.equals("~/myTorrents/IamAPirate"))
//                  {
//                      flag = false;
//                  }
//              }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        assertTrue(flag);
//
//    }
//
//
//    @Test
//    void listFiles() throws IOException
//    {
//
//        MainServer s1 = new MainServer("~/Torrents1");
//
//        String reg1 = "Pesho123-87.120.123.76:1234 ~/myTorrents/IamAPirate";
//        String reg2 = "Pesho123-87.120.123.76:1234 ~/myTorrents/IamNotACrook";
//        String reg3 = "Pesho123-87.120.123.76:1234 ~/myTorrents/IamRetarded";
//        String reg4 = "Pesho123-87.120.123.76:1234 ~/myTorrents/IamTesting";
//        String reg5 = "Goshko2-87.120.127.76:1234 ~/myTorrents/IamTesting";
//        String reg6 = "Goshko2-87.120.127.76:1234 ~/myTorrents/IamNesting";
//        String reg7 = "Goshko2-87.120.127.76:1234 ~/myTorrents/IamFestin";
//
//        s1.register(reg1);
//        s1.register(reg2);
//        s1.register(reg3);
//        s1.register(reg4);
//        s1.register(reg5);
//        s1.register(reg6);
//        s1.register(reg7);
//        s1.unregister(reg3);
//
//        String expected = reg1 + '\n' + reg2 + '\n' + reg4 + '\n' + reg5 +
//            '\n' + reg6 + '\n' + reg7;
//
//        assertEquals(expected, s1.listFiles());
//    }
//
//    @Test
//    void search() throws FileNotFoundException
//    {
//
//        MainServer s1 = new MainServer("~/Torrents1");
//
//        String reg1 = "Pesho123-87.120.123.76:1234 ~/myTorrents/IamAPirate";
//        String reg2 = "Pesho123-87.120.123.76:1234 ~/myTorrents/IamNotACrook";
//        String reg3 = "Pesho123-87.120.123.76:1234 ~/myTorrents/IamNotACroc";
//
//        s1.register(reg1);
//        s1.register(reg2);
//        s1.register(reg3);
//
//        String expected = reg2 + '\n' + reg3;
//
//        assertEquals(expected, s1.search("*.NotACr.*"));
//    }
//
//    @AfterEach
//    public void removeDirs() throws IOException
//    {
//         Path directory = Paths.get("/home/svetoslav/Torrents1");
//         Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
//            @Override
//            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                Files.delete(file);
//                return FileVisitResult.CONTINUE;
//            }
//
//            @Override
//            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                Files.delete(dir);
//                return FileVisitResult.CONTINUE;
//            }
//        });
//    }
//}