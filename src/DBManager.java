import com.mongodb.DB;
import com.mongodb.MongoClient;
import javafx.util.Pair;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class DBManager
{

    static MongoClient mongoClient;
    static String host;
    static int port;
    static DB db;

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
        return obj.insert(db,collectionName);
    }


    public static void delete(MyDBObject obj,String collectionName)
    {
        obj.delete(db,collectionName);
    }

    public static void update(MyDBObject obj,String collectionName,String key,Object val,String op)
    {
        obj.update(db,collectionName,key,val,op);
    }



}
