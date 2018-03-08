import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ExecutorService;

public class Resources {
    static Queue<Pair<String,String>> linkQueue=new LinkedList<>();

    //A set for storing the UUID of already visited URLs to avoid visiting them again
    static Set<String> visitedLinks=new TreeSet<>();



    static int pageCount=0;

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



}
