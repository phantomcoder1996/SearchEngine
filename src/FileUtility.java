import org.jsoup.Jsoup;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;
import java.util.UUID;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FileUtility {


    public static ArrayList<String> readFileToStringArray(String fileName)
    {
        ArrayList<String>lines=new ArrayList<>();

        File file=new File(fileName);
        String line;
        Scanner fileScanner= null;
        try {
            fileScanner = new Scanner(file);
            while(fileScanner.hasNext())
            {
              line=fileScanner.nextLine();
              lines.add(line);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


       return lines;
    }

    public static void readFiletoQueue(String fileName,Queue<String> q)
    {

        File file=new File(fileName);
        String line;
        Scanner fileScanner= null;
        try {
            fileScanner = new Scanner(file);
            while(fileScanner.hasNext())
            {
                line=fileScanner.nextLine();
                q.add(line);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    public static ArrayList<String> downloadFileAndExtractLinks(String url,String downloadPath)
    {
        StringBuilder fileContent=new StringBuilder();
        ArrayList<String> links=new ArrayList<>();
        URL myUrl=null;
        try {
            myUrl=new URL(url);
            InputStreamReader myStreamReader=new InputStreamReader(myUrl.openStream());
            BufferedReader reader=new BufferedReader(myStreamReader );

            String fileUUID= UUID.nameUUIDFromBytes(url.getBytes()).toString();
            BufferedWriter writer=new BufferedWriter(new FileWriter(downloadPath + fileUUID + ".txt"));
            String line;
            while((line=reader.readLine())!=null)
            {
                fileContent.append(line);
                writer.append(line);
                writer.newLine();

            }
            writer.close();
            reader.close();



            //Now Extract all the links from the page
            Document doc=Jsoup.parse(fileContent.toString());
            Elements anchors=doc.getElementsByTag("a");
            for(Element anchor:anchors)
            {
                String link=anchor.attr("href");
                String fullUrl=LinkParser.parseLink(myUrl,link);

                if(fullUrl!="") {
                    links.add(fullUrl);
                    System.out.println(fullUrl);
                }
            }

        } catch (MalformedURLException e) {
            //e.printStackTrace();
            if(myUrl!=null)
            System.out.println(myUrl.toString()+ "is invalid");

        } catch (IOException e) {
            e.printStackTrace();
        }


        return links;
    }


}
