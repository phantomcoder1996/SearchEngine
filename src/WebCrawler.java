import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.Scanner;


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

      Runtime.getRuntime().addShutdownHook(new CheckPointSaver());
      //First: Connect to the dataBase

      DBManager.connect("localhost",27017,"SearchEngineDB");

     // loadResources(); //To start from checkpoint

      CommanderThread commanderThread=new CommanderThread(1000,30,"/Users/macbookpro/IdeaProjects/SearchEngine/.idea/WebPages/","/Users/macbookpro/IdeaProjects/SearchEngine/.idea/SeedSet/seedSet.txt");
      commanderThread.start();

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

}


