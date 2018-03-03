import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;


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
      BFS myBFS=new BFS(5,10,"/Users/macbookpro/IdeaProjects/SearchEngine/.idea/WebPages/","/Users/macbookpro/IdeaProjects/SearchEngine/.idea/SeedSet/seedSet.txt");
      myBFS.start();

  }

}
