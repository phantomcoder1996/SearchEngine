import com.mongodb.*;
import javafx.util.Pair;


import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.*;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;


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
        DBCursor crawlerState=db.getCollection("State").find();
        BasicDBObject where = new BasicDBObject("rank",new BasicDBObject("$gt", 10));
        BasicDBObject field = new BasicDBObject();
        field.put("url", 1);
       // where.put("rank", new BasicDBObject("$gt", 10));

        //GET state variables
//
//        if(crawlerState.hasNext()) {
//             prevCrawlerRun = crawlerState.next().get("lastRunTime").toString();
//           where=new BasicDBObject("rank",new BasicDBObject("$gt", 10));
//           where.append("lastModified",new BasicDBObject("$lt",new Date(prevCrawlerRun)));
//        }

     //   db.getCollection("urls").agg

        DBCursor cursor = db.getCollection("urls").find(where, field);
        while (cursor.hasNext()) {
            String url=cursor.next().get("url").toString();
            System.out.println("url retreived"+url);
            Resources.noRevisit.add(url);
        }

        //We have to retreive previos simhashes as well
        field.put("myHash",1);
        DBCursor cursor2 = db.getCollection("urls").find(new BasicDBObject("myHash",new BasicDBObject("$exists",true)),field);
        List<DBObject>list=cursor2.toArray();
        for(int i=0;i<list.size();i++)
        {
            String url=list.get(i).get("url").toString();
            String simHash=list.get(i).get("myHash").toString();
            System.out.println("simhash retreived"+simHash);
            Resources.simHash.put(url,simHash);
        }


    }


    public static void addFilesToDB(ArrayList<FileInfo>files)
    {
        BulkWriteOperation bulk = db.getCollection("urls").initializeUnorderedBulkOperation();

        int size=files.size();
        System.out.println("Adding files "+ size);

            for (int i = 0; i < size; ++i) {
                try {
                    FileInfo currentFile = files.get(i);
                    BasicDBObject url = new BasicDBObject("url", currentFile.url);
                    ArrayList<String> temp = new ArrayList<>();

                    bulk.find(url).upsert().update(new BasicDBObject("$set",
                            new BasicDBObject("outLinks", currentFile.outlinks).append("myHash", currentFile.myHash).append("rank", Math.random() * 20).append("downloaded", 1).append("inLinks", temp.toArray()))
                            .append("$currentDate", new BasicDBObject("lastModified", true)));
                }catch(BulkWriteException e)
                    {
                        System.out.println("url must be unique");
                    }

            }

try {
if(size>0)    bulk.execute();
}
catch (BulkWriteException e) {
e.printStackTrace();

}
catch(IllegalStateException e)
{
    System.out.println("Nothing to write in db");
}
    }
//    public static void AddFilesToDB()
//    {
//        System.out.println("Adding Files to db");
//        try {
//            BulkWriteOperation bulk = db.getCollection("urls").initializeUnorderedBulkOperation();
//            int size= Resources.scheduledDownloads.size();
//            for (int i = 0; i <size; i++) {
//
//                FileInfo currentFile = Resources.scheduledDownloads.get(0);
//                 Resources.scheduledDownloads.remove(0);
//
//                BasicDBObject url = new BasicDBObject("url", currentFile.url);
//                //BasicDBObject fileName=new BasicDBObject("fileName",currentFile.fileName);
//                BasicDBObject outLinks = new BasicDBObject("outLinks", currentFile.outlinks);
//                BasicDBObject simHash = new BasicDBObject("simHash", currentFile.simHash);
//                BasicDBObject rank = new BasicDBObject("rank", Math.random() * 20);
//                BasicDBObject updated = new BasicDBObject("updated", 1);
//                //BasicDBObject title=new BasicDBObject("title",currentFile.title);
//                //    BasicDBObject headers=new BasicDBObject("headers",currentFile.headers);
//                //  BasicDBObject body=new BasicDBObject("body",currentFile.body);
//                // bulk.insert(url.append("outLinks",currentFile.outlinks).append("simHash",currentFile.simHash).append("rank", Math.random()*20).append("updated",1));
//                ArrayList<String> temp = new ArrayList<>();
//
//                    bulk.find(url).upsert().update(new BasicDBObject("$set",
//                            url.append("outLinks", currentFile.outlinks).append("myHash", currentFile.myHash).append("rank", Math.random() * 20).append("updated", 1).append("title", currentFile.title).append("headers", currentFile.headers).append("body", currentFile.body).append("inLinks", temp.toArray()))
//                            .append("$currentDate", new BasicDBObject("lastModified", true)));
//
//
//
//
//
//            }
//            bulk.execute();
//        }
//        catch (BulkWriteException e) {
//            System.out.println("url must be unique");
//        }
//    }

//    public static void updateInlinks()
//    {
//        System.out.println("updating links");
//
//        BulkWriteOperation bulk = db.getCollection("urls").initializeUnorderedBulkOperation();
//        for(String page:Resources.downloaded)
//        {
//            BasicDBObject q = new BasicDBObject("url", page);
//            System.out.println("Checking downloads");
//            Set<String> inLinks=Resources.inLinks.get(page);
//            if(inLinks!=null) {
//                System.out.println("inlinks: "+inLinks.size());
//
//               // BasicDBObject arrayUpdate = new BasicDBObject("inLinks", new BasicDBObject("$addToSet", new BasicDBObject("$each", inLinks.toArray())));
//                BasicDBObject arrayUpdate=new BasicDBObject("$addToSet",new BasicDBObject("inLinks",new BasicDBObject("$each", inLinks.toArray())));
//                bulk.find(q).upsert().update(new BasicDBObject(arrayUpdate));
//            }
////            else
////            {
////                BasicDBObject arrayUpdate=new BasicDBObject("$set",new BasicDBObject("$currentDate",new BasicDBObject("lastModified",true)));
////                bulk.find(q).upsert().update(new BasicDBObject(arrayUpdate));
////            }
//        }
//        bulk.execute();
//    }


    public static void updateInLinks2()
    {
        System.out.println("updating links");

        BulkWriteOperation bulk = db.getCollection("urls").initializeUnorderedBulkOperation();
         Iterator i=Resources.inLinks.entrySet().iterator();
         try {


             while (i.hasNext()) {
                 ConcurrentHashMap.Entry pair = (ConcurrentHashMap.Entry) i.next();

                 Set<String> inLinks = (Set<String>) pair.getValue();
                 String key = (String) pair.getKey();
                 BasicDBObject q = new BasicDBObject("url", key);
                 System.out.println("Checking downloads");

                 BasicDBObject arrayUpdate = new BasicDBObject("$addToSet", new BasicDBObject("inLinks", new BasicDBObject("$each", inLinks.toArray())));
                 bulk.find(q).upsert().update(new BasicDBObject(arrayUpdate));
             }

         }
         catch (BulkWriteException e)
         {
             e.printStackTrace();
         }
         try {
             bulk.execute();
         }catch(IllegalStateException e)
         {
             System.out.println("Nothing to write in db");
         }
    }


    public static void updateCrawlerState(String cState)
    {
        DBCollection state=db.getCollection("State");
        state.update(new BasicDBObject(),new BasicDBObject("$set",new BasicDBObject("crawlerState",cState)),true,true);


    }
}
