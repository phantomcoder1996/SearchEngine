import javafx.util.Pair;
import sun.awt.image.ImageWatched;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommanderThread extends Thread implements Serializable{

    int maxWebPages;
    ExecutorService executor;
    int threadNum;
    String downloadPath;
    String seedSetPath;
    ArrayList<SoldierThread> soldiers=new ArrayList<>();
    Integer soldiersSize=0;


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
        DBManager.retreiveURLs();
        if(!Resources.noRevisit.isEmpty())
        {
            for(int i=0;i<Resources.noRevisit.size();i++)
            Resources.addLinkToQueue(Resources.noRevisit.get(i),"");
        }
        else {
            ArrayList<String> fileContent = FileUtility.readFileToStringArray(seedSetPath);
            for (int i = 0; i < fileContent.size(); i++) {

                Resources.addLinkToQueue(fileContent.get(i), "");

            }

        }


        //TODO: Fill queue with retreived urls

    }

    @Override public void run()
    {
        //Commander: Get me that seed set at once :( !!!
        if(soldiersSize==0)
        readSeedSet();
        else
        {
            for(int i=0;i<soldiers.size();i++)
            {
                executor.execute(soldiers.get(i));
            }
        }

        //Commander: I must think of a way to distribute the tasks among those lazy soldiers :( !!
        while((Resources.getCount()<maxWebPages))
        {
         //   System.out.println("Thread cnt from commander is "+Resources.getCount());
           if(!Resources.isQueueEmpty())
           {    Pair<String,String> url=Resources.getLink();
                //String urlHash= LinkParser.hashLink(url.getKey());

                 String key=url.getKey();
                if(!Resources.isVisited(key)) { //TODO: Hash value changed


                    Resources.updateVisited(key); //TODO: Hash value changed
                    SoldierThread newSoldier=new SoldierThread(url.getKey(),url.getValue(), maxWebPages,downloadPath);

                    soldiers.add(newSoldier);

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


    public void serializeCommanderThread(ObjectOutputStream os)
    {
        try {
//            os.writeObject(new Integer(maxWebPages));
//           // os.writeObject(executor);
//            os.writeObject(new Integer(threadNum));
//            os.writeObject(downloadPath);
//            os.writeObject(seedSetPath);
            os.writeObject(soldiers.size());
            os.writeObject(soldiers);
//            for(int i=0;i<soldiers.size();i++)
//            {
//                soldiers.get(i).serializeSoldierThread(os);
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }





    }

    public void deSerializeCommanderThread(ObjectInputStream is)
    {
        try {
//            maxWebPages=((Integer)is.readObject()).intValue();
//           // executor=(ExecutorService)is.readObject();
//            threadNum=((Integer)is.readObject()).intValue();
//            downloadPath=(String)is.readObject();
//            seedSetPath=(String)is.readObject();
            soldiersSize=(Integer)is.readObject();
//            for(int i=0;i<soldiersSize;i++)
//            {
//                soldiers
//            }
           soldiers=(ArrayList<SoldierThread>)is.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void Recrawlprev()
    {

    }


}

