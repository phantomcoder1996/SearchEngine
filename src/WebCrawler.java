import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;


public class WebCrawler {



  public static void main(String[] args)
  {


//      String html = "<html><head><title>First parse</title></head>"
//              + "<body><p>Parsed HTML into a doc.</p></body></html>";
//
//      Document doc = Jsoup.parse(html);
//
//      try {
//          Document doc2=Jsoup.connect("https://www.searchmetrics.com/").get();
//
//          System.out.println(doc2);
//          System.out.println(UUID.nameUUIDFromBytes(new String("https://www.searchmetrics.com/robots.txt").getBytes()));
//          System.out.println(UUID.nameUUIDFromBytes(new String("https://www.searchmetrics.com/robots.txt").getBytes()));
//          Elements link=doc2.getElementsByTag("a");
//          for(Element e:link)
//          { String fullUrl=e.baseUri();
//            String link2=  e.attr("href").toString();
//            System.out.println(link2);
//            System.out.println(e.text());
//            System.out.println(fullUrl);
//            System.out.println();
//          }
//      } catch (IOException e) {
//          e.printStackTrace();
//      }
//
//      try {
//          URL myUrl;
//          RobotsFileReader rf;
//          myUrl = new URL("https://www.searchmetrics.com/robots.txt");
//          HttpURLConnection urlc= (HttpURLConnection)myUrl.openConnection();
//          urlc.setRequestMethod("HEAD");
//          urlc.connect();
//          if(urlc.getResponseCode()==HttpURLConnection.HTTP_OK) {
//              rf = new RobotsFileReader(myUrl);
//
//
//          }
//          myUrl = new URL("https://www.searchmetrics.com");
//          HttpURLConnection urlc2= (HttpURLConnection)myUrl.openConnection();
//         String d= LinkParser.getDocumentType(urlc);
//
//         System.out.println(d);
//
//
//
////          String linktext=myUrl.toString();
////          Document doc3=Jsoup.connect(linktext).get();
////          Elements link3=doc3.getElementsByTag("a");
////          for(Element e:link3)
////          {
////              String link4=  e.attr("href").toString();
////              if(link4.startsWith("/"))
////              {
////                  System.out.println(myUrl.getProtocol()+"://"+myUrl.getHost()+link4);
////              }
////          }
//
//
//      } catch (Exception e) {
//          e.printStackTrace();
//      }
//
     // BFS myBFS=new BFS(5,20,"/Users/macbookpro/IdeaProjects/SearchEngine/.idea/WebPages/","/Users/macbookpro/IdeaProjects/SearchEngine/.idea/SeedSet/seedSet.txt");
     // myBFS.start();

    //  Runtime.getRuntime().addShutdownHook(new CheckPointSaver());

      long prevTime=System.currentTimeMillis();
      //First: Connect to the dataBase

      DBManager.connect("localhost",27017,"SearchEngineDB");

      //Crawler shall record its runTime





      CommanderThread commanderThread=null;
      commanderThread = new CommanderThread(20, 30, "/Users/macbookpro/IdeaProjects/SearchEngine/.idea/WebPages/", "/Users/macbookpro/IdeaProjects/SearchEngine/.idea/SeedSet/seedSet.txt");

      //start Timer and backup every two minutes
//      Timer timer=new Timer();
//      timer.scheduleAtFixedRate(new TimedSerialization(commanderThread),1*60*1000,1*60*1000);

      // loadResources(); //To start from checkpoint
      if(checkExists(System.getProperty("user.dir"),"backup")) {
          deserializeCrawler(commanderThread);
          System.out.println("Backing up after previous shutdown");

      }
      else
      {
          try {
              Files.createFile(Paths.get(System.getProperty("user.dir")+"/backup.txt"));
          } catch (IOException e) {
              e.printStackTrace();
          }
          System.out.println("Crawling started");

      }
      commanderThread.start();

      while(Resources.getCount()<20)
      {
          long curTime=System.currentTimeMillis();
          if(curTime-prevTime>=20*1000) {
              prevTime=curTime;
              serializeCrawler(commanderThread);
          }
      }

     // DBManager.AddFilesToDB();
      DBManager.updateInlinks();
      try {
          // commanderThread.join();
          //Wait until commander thread terminates and then delete backup.txt
          Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/backup.txt"));
      }
//       catch (InterruptedException e) {
//          e.printStackTrace();
//      }
      catch (IOException e) {
          e.printStackTrace();
      }




  }



  private static void loadResources()
  {
      try {
          Scanner reader=new Scanner(new File("../backup.txt"));

          int pageCnt;
          int j=0;
          int setSize=1;

          while(reader.hasNext()&&j<setSize) {
              pageCnt = reader.nextInt();

              Resources.setCurrentCnt(pageCnt);

              int qSize = reader.nextInt();
              for (int i = 0; i < 2*qSize; i++) {
                  String link=reader.nextLine();
                  String parent=reader.nextLine();
                  Resources.addLinkToQueue(link,parent);
              }

                setSize = reader.nextInt();
              for ( j = 0; j < setSize; j++) {
                  Resources.updateVisited(reader.nextLine());
              }
          }
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

    public static void serializeCrawler(CommanderThread commanderThread)
    {
        try {
            System.out.println("timer task entered");
            if(commanderThread!=null)
            {
                System.out.println("Starting serialization");
                ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream("backup.txt"));
                Resources.serializeResources(os);
                commanderThread.serializeCommanderThread(os);
              //  os.writeObject(commanderThread);
                os.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


  public static CommanderThread deserializeCrawler(CommanderThread commanderThread)
  {

      try {

          ObjectInputStream is=new ObjectInputStream(new FileInputStream(System.getProperty("user.dir")+"/backup.txt"));
          Resources.deSerializeResources(is);
          //commanderThread=(CommanderThread)is.readObject();
          commanderThread.deSerializeCommanderThread(is);

      } catch (IOException e) {
          e.printStackTrace();
          System.out.println("File backup does not exist");
      }
//      } catch (ClassNotFoundException e) {
//          e.printStackTrace();
//      }

      return commanderThread;

  }


    public static boolean checkExists(String directory, String file) {
        File dir = new File(directory);
        File[] dir_contents = dir.listFiles();
        String temp = file + ".txt";
        boolean check = new File(directory,temp).exists();
        System.out.println("Check"+check);  // -->always says false



        return check;
    }

}


