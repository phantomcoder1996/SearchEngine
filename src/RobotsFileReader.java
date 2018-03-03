import java.io.*;
import java.net.URL;
import java.util.ArrayList;
//import java.util.Scanner;

public class RobotsFileReader {

     BufferedReader fileScanner;
     ArrayList<String> disallowedDirectories;
     String siteMapURL;
    RobotsFileReader(URL fileName)
    {
        disallowedDirectories=new ArrayList<>();
        try  {

//            File file = new File(fileName);
//            fileScanner=new Scanner(file);
            fileScanner =new BufferedReader(new InputStreamReader(fileName.openStream()));
            parseFile();

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }



    }


    public void parseFile()
    {

        try {
            String line;
          while((line=fileScanner.readLine())!=null)
          {
              line=line.toLowerCase().toString();
              if(line.contains("user-agent"))
              {
                  if(line.contains("*")) //All crawlers are not allowed
                  {
                      boolean disallowed=true;
                      while(((line=fileScanner.readLine())!=null)&&disallowed)
                      {
                          line=line.toLowerCase().toString();
                          if(line.contains("disallow"))
                          {
                             int i=line.indexOf(":");
                             line=line.substring(i+1).trim();
                             disallowedDirectories.add(line);
                             System.out.println(line);
                          }
                          else
                          {
                              disallowed=false;
                          }
                      }
                  }
              }
              else
              {
                  if(line.contains("sitemap"))
                  {
                      int i=line.indexOf(":");
                      siteMapURL=line.substring(i+1).trim();
                  }
              }


          }

            fileScanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public ArrayList<String> getDisallowedDirectories()
    {
        return disallowedDirectories;
    }


    public String getSiteMapURL()
    {
        return siteMapURL;
    }



}
