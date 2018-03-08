import com.mongodb.*;

import java.util.ArrayList;
import java.util.UUID;

public class MyURL implements MyDBObject {


    String fileName;
    String url;
    int outLinks;
    ArrayList<String>inLinks=new ArrayList<>();

    /*------------------------------------------------------------------------------------------------------*/

    public MyURL(String url)
    {
        this.url=url;
        this.fileName=UUID.nameUUIDFromBytes(url.getBytes()).toString()+".txt";
    }

    public MyURL(String url,String fileName)
    {
        this.url=url;
        this.fileName=fileName;
    }

    public MyURL(String url,String fileName,String inLink)
    {

        this.url=url;
        this.fileName=fileName;
        inLinks.add(inLink);
    }

    public MyURL(String url,int num)
    {
        this.url=url;
        this.fileName=UUID.nameUUIDFromBytes(url.getBytes()).toString()+".txt";
        this.outLinks=num;

    }


    public MyURL()
    {

    }

    /*------------------------------------------------------------------------------------------------------*/

    public void setName(String url,String fileName)
    {
        this.url=url;
        this.fileName=fileName;


    }

    public void setOutLinksNum(int num)
    {
        outLinks=num;
    }

    public void addInLink(String link)
    {
        inLinks.add(link);
    }


    /*------------------------------------------------------------------------------------------------------*/

    @Override
    public boolean insert(DB db,String collectionName)
    {
        try {
            DBCollection col = db.getCollection(collectionName);
            col.insert(new BasicDBObject("url", url).append("fileName", fileName).append("outLinks", outLinks).append("inLinks", inLinks.toArray()));
        }
        catch(MongoException e)
        {
            //e.printStackTrace();

            return false; //in case insertion was not successful
        }
        return true; //successful insertion in database
    }

    @Override
    public void update(DB db,String collectionName,String key,Object val,String op)
    {

        DBCollection col = db.getCollection(collectionName);
        BasicDBObject which=new BasicDBObject("url",url).append("fileName",fileName);
        BasicDBObject what = new BasicDBObject("$"+op, new BasicDBObject(key,val));

        col.update(which,what);


    }


    @Override
    public void delete(DB db,String collectionName)
    {

        DBCollection col = db.getCollection(collectionName);
        BasicDBObject which=new BasicDBObject("url",url).append("fileName",fileName);
        col.remove(which);
    }


}
