import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BFS {

//    //A queue including all the links in the seed set
//    Queue<String> linkQueue;
//
//    //A set for storing the UUID of already visited URLs to avoid visiting them again
//    Set<String> visitedLinks;


    //Used to create a ThreadPool with a predefined number of threads
    ExecutorService executor;

    //Specifies a path for downloading the files
    String downloadPath;

    //Maximum number of webpages to be downloaded from the web
    int maxWebPages;

    // int cnt=0;

    //Object lock1=new Object();

    public BFS(int threadNum,int maxWebPages,String downloadPath,String seedSetPath)
    {
       // linkQueue=new LinkedList<>();
       // visitedLinks=new TreeSet<>();

        executor= Executors.newFixedThreadPool(threadNum);
        this.downloadPath=downloadPath;
        this.maxWebPages=maxWebPages;

//        FileUtility.readFiletoQueue(seedSetPath,linkQueue);
        ArrayList<String> fileContent=FileUtility.readFileToStringArray(seedSetPath);
        for(int i=0;i<fileContent.size();i++)
        {
            String linkUUID=UUID.nameUUIDFromBytes(fileContent.get(i).getBytes()).toString();
            Resources.updateVisited(linkUUID);
            Resources.addLinkToQueue(fileContent.get(i),"");
//            visitedLinks.add(linkUUID);
//            linkQueue.add(fileContent.get(i));
        }



    }

    public void start()
    {

       // while(!linkQueue.isEmpty()&&cnt<maxWebPages)
        while ((!Resources.isQueueEmpty())&&Resources.getCount()<=maxWebPages)

        {
            //if(!Resources.isQueueEmpty()) {
                BFSWorker worker = new BFSWorker();
                executor.execute(worker);
               System.out.println("ih");
            //}
            //System.out.println(cnt);
          //  System.out.print("size:"+linkQueue.size());
        }

        //executor.shutdown();

    }



    private class BFSWorker implements Runnable
    {

//
////        @Override
////        public void run() {
////            String front=null;
////
//////            synchronized (lock1)
//////            {
////               // if(!linkQueue.isEmpty())
////            if(!Resources.isQueueEmpty())
////                {
////                    //front=linkQueue.poll();
////                    front=Resources.getLink();
////                    Resources.incrementCount();
////                    //cnt++;
////                    //visitedLinks.add(UUID.nameUUIDFromBytes(front.getBytes()).toString());
////                   // System.out.println(cnt);
////                }
//////            }
////
////            ArrayList<String> links=new ArrayList<>();
////            if(front!=null) links=FileUtility.downloadFileAndExtractLinks(front,downloadPath);
////
////
//////              synchronized (lock1)
//////            {
////                for(int i=0;i<links.size();i++ )
////                {
////                    String linkUUID=UUID.nameUUIDFromBytes(links.get(i).getBytes()).toString();
////                    //if(!visitedLinks.contains(linkUUID))
////                    if(!Resources.isVisited(linkUUID))
////                    {
////
////                        Resources.updateVisited(linkUUID);
////                        Resources.addLinkToQueue(linkUUID);
//////                        visitedLinks.add(linkUUID);
//////                        linkQueue.add(linkUUID);
////                    }
////                }
//////            }
////        }
//
//
        @Override
        public void run()
        {
//            String front=null;
//            while(!Resources.isQueueEmpty())
//            {
//
//                front=Resources.getLink();
//
//                //Now download that page and increment the count
//                  ArrayList<String> links;
//                if(Resources.getCount()<maxWebPages) {
//                    Resources.incrementCount();
//                    links = FileUtility.downloadFileAndExtractLinks(front, downloadPath);
//                    System.out.println(Resources.getCount());
//
//                    if(Resources.getCount()<maxWebPages) {
//                        for (int i = 0; i < links.size(); i++) {
//
//                            String linkUUID=UUID.nameUUIDFromBytes(links.get(i).getBytes()).toString();
//
//                            if(!Resources.isVisited(linkUUID))
//                              {
//
//                                  Resources.updateVisited(linkUUID);
//                                  Resources.addLinkToQueue(linkUUID);
//
//                              }
//
//                             // BFSWorker worker=new BFSWorker();
//                            //  executor.execute(worker);
//                        }
//                    }
//                    else
//                        break;
//
//                }
//
//                else
//                    break;
//
//
//
//
//
//
//            }
        }

    }
//
//@Override
//    public void finalize() throws Throwable {
//        super.finalize();
//        executor.shutdown();
//    }


}
