import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BFS {

    //A queue including all the links in the seed set
    Queue<String> linkQueue;

    //A set for storing the UUID of already visited URLs to avoid visiting them again
    Set<String> visitedLinks;

    //Used to create a ThreadPool with a predefined number of threads
    ExecutorService executor;

    //Specifies a path for downloading the files
    String downloadPath;

    //Maximum number of webpages to be downloaded from the web
    int maxWebPages;

    int cnt=0;

    Object lock1=new Object();

    public BFS(int threadNum,int maxWebPages,String downloadPath,String seedSetPath)
    {
        linkQueue=new LinkedList<>();
        visitedLinks=new TreeSet<>();
        executor= Executors.newFixedThreadPool(threadNum);
        this.downloadPath=downloadPath;
        this.maxWebPages=maxWebPages;

//        FileUtility.readFiletoQueue(seedSetPath,linkQueue);
        ArrayList<String> fileContent=FileUtility.readFileToStringArray(seedSetPath);
        for(int i=0;i<fileContent.size();i++)
        {
            String linkUUID=UUID.nameUUIDFromBytes(fileContent.get(i).getBytes()).toString();
            visitedLinks.add(linkUUID);
            linkQueue.add(fileContent.get(i));
        }



    }

    public void start()
    {

        while(!linkQueue.isEmpty()&&cnt<maxWebPages)
        {
            BFSWorker worker=new BFSWorker();
            executor.execute(worker);
           // System.out.println(cnt);
            System.out.print("size:"+linkQueue.size());
        }

    }



    private class BFSWorker implements Runnable
    {


        @Override
        public void run() {
            String front=null;

            synchronized (lock1)
            {
                if(!linkQueue.isEmpty())
                {
                    front=linkQueue.poll();
                    linkQueue.remove();
                    cnt++;
                    //visitedLinks.add(UUID.nameUUIDFromBytes(front.getBytes()).toString());
                    System.out.println(cnt);
                }
            }

            ArrayList<String> links= FileUtility.downloadFileAndExtractLinks(front,downloadPath);


              synchronized (lock1)
            {
                for(int i=0;i<links.size();i++ )
                {
                    String linkUUID=UUID.nameUUIDFromBytes(links.get(i).getBytes()).toString();
                    if(!visitedLinks.contains(linkUUID))
                    {
                        visitedLinks.add(linkUUID);
                        linkQueue.add(linkUUID);
                    }
                }
            }
        }
    }

@Override
    public void finalize() throws Throwable {
        super.finalize();
        executor.shutdown();
    }


}
