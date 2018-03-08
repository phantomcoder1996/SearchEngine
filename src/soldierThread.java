import javafx.util.Pair;
import sun.awt.image.ImageWatched;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;

public class soldierThread implements Runnable {

    int maxWebPages;
    String baseURL;
    ArrayList<String> disallowedDirectories;
    Queue<Pair<String,String>>links;

    String downloadPath;

    final int maxSearchLevel=200;




    soldierThread(String url,String urlP,int maxWebPages,String downloadPath)
    {
        this.maxWebPages=maxWebPages;
        links=new LinkedList<>();
        baseURL=url;
        this.downloadPath=downloadPath;

        links.add(new Pair<>(url,urlP));
    }

    private void readRobotFile()
    {
        RobotsFileReader robotReader;
        URL myUrl = null;
        HttpURLConnection urlC=null;
        try {
            myUrl = new URL(baseURL+"/robots.txt");
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


    @Override
    public void run()
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
                            DBManager.update(new MyURL(fetchedLink),"urls","inLinks",link.getKey(),"addToSet");
                        }
                    }



            }

            level++;
        }




    }




}
