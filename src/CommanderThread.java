import javafx.util.Pair;
import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommanderThread extends Thread{

    int maxWebPages;
    ExecutorService executor;
    int threadNum;
    String downloadPath;
    String seedSetPath;

    CommanderThread(int maxWebPages,int threadNum,String downloadPath,String seedSetPath)
    {
        this.maxWebPages=maxWebPages;
        this.threadNum=threadNum;
        executor= Executors.newFixedThreadPool(threadNum);
        this.downloadPath=downloadPath;
        this.seedSetPath=seedSetPath;
    }


    private void readSeedSet()
    {
        ArrayList<String> fileContent=FileUtility.readFileToStringArray(seedSetPath);
        for(int i=0;i<fileContent.size();i++)
        {

            Resources.addLinkToQueue(fileContent.get(i),"");

        }
    }

    @Override public void run()
    {
        //Commander: Get me that seed set at once :( !!!
        readSeedSet();

        //Commander: I must think of a way to distribute the tasks among those lazy soldiers :( !!
        while((Resources.getCount()<maxWebPages))
        {
         //   System.out.println("Thread cnt from commander is "+Resources.getCount());
           if(!Resources.isQueueEmpty())
           {    Pair<String,String> url=Resources.getLink();
                String urlHash= LinkParser.hashLink(url.getKey());


                if(!Resources.isVisited(urlHash)) {


                    Resources.updateVisited(urlHash);
                    soldierThread newSoldier=new soldierThread(url.getKey(),url.getValue(), maxWebPages,downloadPath);

                    executor.execute(newSoldier);
                    //Thread myThread=new Thread(newSoldier);
                   // myThread.start();

                }
            }
        //    System.out.println("q empty: "+Resources.isQueueEmpty());
        }
    //When maximum number of pages has been downloaded
             executor.shutdown();

    }

}
