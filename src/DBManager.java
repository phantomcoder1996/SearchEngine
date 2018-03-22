import com.mongodb.*;
import javafx.util.Pair;


import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Set;

import org.bson.*;


public class DBManager
{

    static MongoClient mongoClient;
    static String host;
    static int port;
    static DB db;
    static BulkWriteOperation writer;

    public static void connect(String host,int port,String dbName )
    {
        host=host;
        port=port;
        try {
            mongoClient=new MongoClient(host,port);
            db=mongoClient.getDB(dbName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }

    public static boolean insert(MyDBObject obj,String collectionName)
    {
        return obj.insert(db,collectionName,writer);
    }


    public static void delete(MyDBObject obj,String collectionName)
    {
        obj.delete(db,collectionName, writer);
    }

    public static void update(MyDBObject obj,String collectionName,String key,Object val,String op)
    {
        obj.update(db,collectionName,key,val,op, writer);
    }


    public static void retreiveURLs() {

        String prevCrawlerRun="";
        BasicDBObject where = new BasicDBObject("rank",new BasicDBObject("$gt", 10));
       // where.put("rank", new BasicDBObject("$gt", 10));

        //GET state variables
        DBCursor crawlerState=db.getCollection("State").find();
        if(crawlerState.hasNext()) {
             prevCrawlerRun = crawlerState.next().get("lastRunTime").toString();
           where=new BasicDBObject("rank",new BasicDBObject("$gt", 10));
           where.append("lastModified",new BasicDBObject("$lt",new Date(prevCrawlerRun)));
        }
        BasicDBObject field = new BasicDBObject();
        field.put("url", 1);

        DBCursor cursor = db.getCollection("urls").find(where, field);
        while (cursor.hasNext()) {
            String url=cursor.next().get("url").toString();
            System.out.println("url retreived"+url);
            Resources.noRevisit.add(url);
        }

        //We have to retreive previos simhashes as well
        field.put("myHash",1);
        DBCursor cursor2 = db.getCollection("urls").find(new BasicDBObject(),field);
        while (cursor2.hasNext()) {
            String url=cursor2.next().get("url").toString();

            String simHash=cursor2.curr().get("myHash").toString(); //TODO:May change that
            System.out.println("simhash retreived"+simHash);


               Resources.simHash.put(url,simHash);
        }

    }

    public static void AddFilesToDB()
    {
        System.out.println("Adding Files to db");
        BulkWriteOperation bulk = db.getCollection("urls").initializeUnorderedBulkOperation();
        for(int i=0;i<Resources.scheduledDownloads.size();i++)
        {

            FileInfo currentFile=Resources.scheduledDownloads.get(i);



            BasicDBObject url=new BasicDBObject("url",currentFile.url);
            //BasicDBObject fileName=new BasicDBObject("fileName",currentFile.fileName);
            BasicDBObject outLinks=new BasicDBObject("outLinks",currentFile.outlinks);
            BasicDBObject simHash=new BasicDBObject("simHash",currentFile.simHash);
            BasicDBObject rank=new BasicDBObject("rank", Math.random()*20);
            BasicDBObject updated=new BasicDBObject("updated",1);
             //BasicDBObject title=new BasicDBObject("title",currentFile.title);
         //    BasicDBObject headers=new BasicDBObject("headers",currentFile.headers);
           //  BasicDBObject body=new BasicDBObject("body",currentFile.body);
           // bulk.insert(url.append("outLinks",currentFile.outlinks).append("simHash",currentFile.simHash).append("rank", Math.random()*20).append("updated",1));
            ArrayList<String>temp=new ArrayList<>();
              try{
                  bulk.find(url).upsert().update(new BasicDBObject("$set",
                          url.append("outLinks", currentFile.outlinks).append("myHash", currentFile.myHash).append("rank", Math.random() * 20).append("updated", 1).append("title", currentFile.title).append("headers", currentFile.headers).append("body", currentFile.body).append("inLinks", temp.toArray()))
                          .append("$currentDate", new BasicDBObject("lastModified", true)));


                  bulk.execute();
              }catch (BulkWriteException e)
              {
                  System.out.println("url must be unique");
              }



        }

    }

    public static void updateInlinks()
    {
        System.out.println("updating links");

        BulkWriteOperation bulk = db.getCollection("urls").initializeUnorderedBulkOperation();
        for(String page:Resources.downloaded)
        {
            BasicDBObject q = new BasicDBObject("url", page);
            System.out.println("Checking downloads");
            Set<String> inLinks=Resources.inLinks.get(page);
            if(inLinks!=null) {
                System.out.println("inlinks: "+inLinks.size());

               // BasicDBObject arrayUpdate = new BasicDBObject("inLinks", new BasicDBObject("$addToSet", new BasicDBObject("$each", inLinks.toArray())));
                BasicDBObject arrayUpdate=new BasicDBObject("$addToSet",new BasicDBObject("inLinks",new BasicDBObject("$each", inLinks.toArray())));
                bulk.find(q).upsert().update(new BasicDBObject(arrayUpdate));
            }
//            else
//            {
//                BasicDBObject arrayUpdate=new BasicDBObject("$set",new BasicDBObject("$currentDate",new BasicDBObject("lastModified",true)));
//                bulk.find(q).upsert().update(new BasicDBObject(arrayUpdate));
//            }
        }
        bulk.execute();
    }

}
