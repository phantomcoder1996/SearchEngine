import com.apple.eio.FileManager;
import javafx.util.Pair;
import sun.awt.image.ImageWatched;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;

public class SoldierThread implements Runnable,Serializable {

    int maxWebPages;
    String baseURL;
    ArrayList<String> disallowedDirectories;
    Queue<Pair<String,String>>links;

    ArrayList<FileInfo> scheduledDownloads=new ArrayList<>();
    String downloadPath;

    final int maxSearchLevel=400;




    SoldierThread(String url,String urlP,int maxWebPages,String downloadPath)
    {
        this.maxWebPages=maxWebPages;
        links=new LinkedList<>();
        baseURL=url;
        this.downloadPath=downloadPath;

        links.add(new Pair<>(url,urlP));
        Resources.updateVisited(url);//TODO:Revise that line of code
    }

    private void readRobotFile()//TODO:May remove baseURL
    {
        String myBaseURL= LinkParser.getBaseUrl(baseURL);
        RobotsFileReader robotReader;
        URL myUrl = null;
        HttpURLConnection urlC=null;
        try {
            myUrl = new URL(myBaseURL+"/robots.txt");
            urlC= (HttpURLConnection)myUrl.openConnection();
            urlC.connect();
            if(urlC.getResponseCode()==HttpURLConnection.HTTP_OK) {
                robotReader= new RobotsFileReader(myUrl);
                disallowedDirectories=robotReader.getDisallowedDirectories();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isLinkAllowed(String url)
    {
        int i=0;
        String disallowedDir;
        boolean stillAllowed=true;
        while(i<disallowedDirectories.size()&&stillAllowed)
        {
            disallowedDir=disallowedDirectories.get(i);
            if(LinkParser.linksToDirectory(url,disallowedDir)) {
                stillAllowed = false;
                System.out.println("Directory "+disallowedDir+ "is disallowed for url:"+url);

            }
            ++i;
        }
        return stillAllowed;
    }


    //@Override
    public void run2()
    {
        String msg=Thread.currentThread().getName()+ "has started following url";
        System.out.println(msg);

        //**Soldier : Read Robots.txt if exists!
        readRobotFile();

        //**Soldier : Start filling your queue!



        int level=0;
        ArrayList<String> fetchedLinks=null;

        while(level<maxSearchLevel&&Resources.getCount()<maxWebPages)
        {
            System.out.println(Resources.getCount() +"  is from "+Thread.currentThread().getName());
            String msg2=Thread.currentThread().getName()+"is in level "+level;
            System.out.println(msg2);


            if(!links.isEmpty())
            {

                Pair<String,String> link=links.poll();


                if(Resources.getCount()<maxWebPages) {
                    Resources.incrementCount();
                    fetchedLinks = FileUtility.downloadFileAndExtractLinks(link.getKey(), downloadPath);
                    Resources.addDownloaded(link.getKey());
                    MyURL tempUrl=new MyURL(link.getKey(),fetchedLinks.size());

                    if(! DBManager.insert(tempUrl,"urls"))
                        DBManager.update(tempUrl,"urls","outLinks",fetchedLinks.size(),"set");
                    String urlP=link.getValue();
                    if(!urlP.equals(""))
                    {
                        DBManager.update(tempUrl,"urls","inLinks",urlP,"addToSet");
                    }

                }


                    for (String fetchedLink : fetchedLinks) {
                        String hashLink = LinkParser.hashLink(fetchedLink);


                        if (!Resources.isVisited(hashLink)) {
                            Resources.updateVisited(hashLink);
                            if (isLinkAllowed(fetchedLink)) {
                                if (LinkParser.isBaseURL(fetchedLink)) {
                                    //**Soldier : You cannot use that link . Commander shall assign that task to some one!
                                    Resources.addLinkToQueue(fetchedLink,link.getKey());
                                } else {
                                    //**Soldier: I depend on you to go after that link!
                                    links.add(new Pair<>(fetchedLink,link.getKey()));

                                }
                            }
                        }
                        else
                        {
                            if(Resources.isDownloaded(fetchedLink)) {

                                DBManager.update(new MyURL(fetchedLink), "urls", "inLinks", link.getKey(), "addToSet");
                            }
                            else
                            {
                                System.out.println(fetchedLink+"is not  downloaded");
                            }
                        }
                    }



            }

            level++;
        }




    }
    @Override
    public void run()
    {
        String msg=Thread.currentThread().getName()+ "has started following url";
        System.out.println(msg);

        //TODO:Read robot.txt for remainig directories and serialize resources
         //Resources.isLinkAllowed(baseURL);

        readRobotFile();


        int level=0;
        while(level<maxSearchLevel&&Resources.getCount()<maxWebPages&&!links.isEmpty())
        {
            System.out.println(Resources.getCount() +"  is from "+Thread.currentThread().getName());
            String msg2=Thread.currentThread().getName()+"is in level "+level;
            System.out.println(msg2);
            if(!links.isEmpty())
            {
                Pair<String,String>link=links.poll();

               String normalized="";
                normalized=LinkParser.normalize(link.getKey());
                if(!normalized.equals("")) {
                    if (Resources.getCount() < maxWebPages) {

                        Resources.incrementCount();
                        //Now we need to schedule that file for a download
                        MyFileManager fileManager = new MyFileManager(normalized, downloadPath);
                        boolean status;
                        status = fileManager.parseFile();
                        if (status) {
                            ArrayList<String> fetchedLinks = fileManager.extractLinks();
                            System.out.println("flik :"+fetchedLinks.size());
                            // fileManager.createSimHash();
                            fileManager.createHash();

                            FileInfo fileInfo = fileManager.getFileInfo();
                            //If second time this link will have a simHash value,so check for update
                            //If updated the database shall know about it

                            String prevHash = "";
                            if (Resources.simHash.containsKey(normalized)) {
                                prevHash = Resources.simHash.get(normalized);
//                                int dist = compareSimHash(fileInfo.simHash, prevHash);
//                                if (dist > 5) //updated
//                                {
//                                    fileInfo.updated = true;
//                                }
                                if (prevHash.equals(fileInfo.myHash)) continue;


                            }
                                //The page has no prev hash val so we will have to add it to database
                                Resources.addFileObject(fileInfo);


                            //Declare that the file as downloaded so that we can get its in links

                            Resources.addDownloaded(normalized);
                            //Add that files inLink to the hashmap in Resources

                            //Now go through all pages and if not visited add them to queue
                            //If the pages are visited, then they may be visited by me or by someone else
                            //In this case add me as an Inlink to these pages

                            for (String fetchedLink : fetchedLinks) {
//                                if (Resources.getCount() >= maxWebPages) {
//                                    break;
//                                }
                                if (!Resources.inLinks.containsKey(fetchedLink)) {
                                    Resources.inLinks.put(fetchedLink, new TreeSet<>());
                                }
                                Resources.inLinks.get(fetchedLink).add(normalized);

                                if (!Resources.isVisited(fetchedLink)) {


                                    Resources.updateVisited(fetchedLink);
//TODO:change is link allowed arguments
                                    if (isLinkAllowed(fetchedLink)) {
                                        if (LinkParser.isBaseURL(fetchedLink)) {
                                            //Let the commander decide which thread will follow that link
                                            Resources.addLinkToQueue(fetchedLink, normalized);
                                        } else {
                                            links.add(new Pair<>(fetchedLink, normalized));
                                        }
                                    }
                                }


                            }


                                               } else {
                            //Page download was not successful
                            System.out.println("page download was not successful");
                            Resources.decrementCount();

                        }
                    }
                }
                }

            level++;
            System.out.println("level"+level);
        }
    }


    public void  serializeSoldierThread(ObjectOutputStream os)
    {

        try {
            os.writeObject(disallowedDirectories);
            os.writeObject(links);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void  deSerializeSoldierThread(ObjectInputStream is)
    {

        try {
           disallowedDirectories=(ArrayList<String>)is.readObject();
           links=(Queue<Pair<String, String>>)is.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    public  int compareSimHash(String hash1,String hash2)
    {
        long x=Long.parseLong(hash1,2);
        long y=Long.parseLong(hash2,2);
        long z=x^y;
        int dist=0;
        for(int i=0;i<32;i++)
        {
            long bit=( z>>i)&1;
            if(bit==1) dist++;
        }
        return dist;
    }


}


