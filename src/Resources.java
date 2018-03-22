import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class Resources implements Serializable{
    static Queue<Pair<String,String>> linkQueue=new LinkedList<>();

    //A set for storing the UUID of already visited URLs to avoid visiting them again
    static Set<String> visitedLinks=new TreeSet<>();

    static ArrayList<String>downloaded=new ArrayList<>();

    public static ConcurrentHashMap<String,Set<String>> inLinks=new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String,String> simHash=new ConcurrentHashMap<>();

    public static ArrayList<FileInfo>scheduledDownloads=new ArrayList<>();

    public static ArrayList<String>noRevisit=new ArrayList<>();

  //  public static ConcurrentHashMap<String,ArrayList<String>> robotRead=new ConcurrentHashMap<>();

    static int pageCount=0;

    public static synchronized void addDownloaded(String hashLink)
    {
        downloaded.add(hashLink);
    }

    public static synchronized boolean isDownloaded(String hashLink)
    {
        return downloaded.contains(hashLink);
    }
    public static synchronized boolean isQueueEmpty()
    {

        return linkQueue.isEmpty();

    }

    public static synchronized void addLinkToQueue(String url,String parent)

    {

        linkQueue.add(new Pair<>(url,parent));
    }

    public static synchronized Pair<String,String> getLink()
    {
        Pair<String,String> front=null;
        if(!linkQueue.isEmpty())
        {
            front = linkQueue.poll();



        }
        return front;
    }

    public static synchronized void updateVisited(String uuid)
    {
         visitedLinks.add(uuid);
    }


    public static synchronized boolean isVisited(String uuid)
    {
        return visitedLinks.contains(uuid);
    }


    public static synchronized void incrementCount()
    {

        pageCount++;
        System.out.println("current page count is "+pageCount);

    }


    public static synchronized int  getCount()
    {
        return pageCount;
    }




   public static Set<String> getVisited()
   {
       return visitedLinks;
   }


   public static  Queue<Pair<String,String>> getLinkQueue()
   {
       return linkQueue;
   }

   public static void setCurrentCnt(int cnt)
   {
       pageCount=cnt;
   }

   public static void serializeResources(ObjectOutputStream os)
   {

       try {
           os.writeObject(new Integer(pageCount));
           os.writeObject(linkQueue);
           os.writeObject(visitedLinks);
           os.writeObject(scheduledDownloads);
           os.writeObject(inLinks);
           os.writeObject(simHash);
           os.writeObject(downloaded);
           os.writeObject(noRevisit);
        //   os.writeObject(robotRead);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

    public static void deSerializeResources(ObjectInputStream is)
    {

        try {
            try {
                pageCount=((Integer) is.readObject()).intValue();
                linkQueue=(Queue<Pair<String,String>> )is.readObject();
                visitedLinks=(Set<String>)is.readObject();
                scheduledDownloads=(ArrayList<FileInfo>)is.readObject();
                inLinks=(ConcurrentHashMap<String,Set<String>>)is.readObject();
                simHash=(ConcurrentHashMap<String,String>)is.readObject();
                downloaded=(ArrayList<String>)is.readObject();
                noRevisit=(ArrayList<String>)is.readObject();
           //     robotRead=(ConcurrentHashMap<String,ArrayList<String>>)is.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized static void addFileObject(FileInfo fileInfo) {
        scheduledDownloads.add(fileInfo);
    }

    public synchronized static void decrementCount() {
        pageCount--;
    }

//    public static boolean isLinkAllowed(String url)
//    {
//
//        String urlHost="";
//        ArrayList<String>disallowedDirectories;
//        // **Soldier : Read Robots.txt if exists!
//        try {
//            urlHost=new URL(url).getHost();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        if(!robotRead.containsKey(urlHost)) {
//           // System.out.println(urlHost+"--url host");
//            disallowedDirectories = readRobotFile(url);
//            robotRead.put(urlHost, disallowedDirectories);
//
//        }
//        else {
//            disallowedDirectories = robotRead.get(urlHost);
//        }
//        int i=0;
//        String disallowedDir;
//        boolean stillAllowed=true;
//        while(i<disallowedDirectories.size()&&stillAllowed)
//        {
//            disallowedDir=disallowedDirectories.get(i);
//            if(LinkParser.linksToDirectory(url,disallowedDir)) {
//                stillAllowed = false;
//                System.out.println("Directory "+disallowedDir+ "is disallowed for url:"+url);
//
//            }
//            ++i;
//        }
//        return stillAllowed;
//    }

    private static ArrayList<String> readRobotFile(String URL)//TODO:May remove baseURL
    {
        String baseURL=LinkParser.getBaseUrl(URL);
        ArrayList<String>disallowedDirectories=new ArrayList<String>();
        RobotsFileReader robotReader;
        URL myUrl = null;
        HttpURLConnection urlC=null;
        try {
            myUrl = new URL(baseURL+"/robots.txt");
           // System.out.println(myUrl);
            urlC= (HttpURLConnection)myUrl.openConnection();
            urlC.connect();
            if(urlC.getResponseCode()==HttpURLConnection.HTTP_OK) {
                robotReader= new RobotsFileReader(myUrl);
                disallowedDirectories=robotReader.getDisallowedDirectories();
               // System.out.print(disallowedDirectories.get(0));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(ClassCastException e)
        {
            e.printStackTrace();
        }

        return disallowedDirectories;
    }
}
