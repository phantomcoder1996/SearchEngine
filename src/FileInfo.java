import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import java.io.Serializable;

public class FileInfo implements Serializable,MyDBObject{

    public String url;
    public  String title;
    public String body;
    public  String headers;
    public String simHash;
    public int outlinks;
    public boolean updated=false;
    public String myHash;
    public boolean downloaded=false;

    public FileInfo(String url,String title,String body,String headers,String simHash,int outlinks,String myHash)
    {
        this.url=url;
        this.title=title;
        this.body=body;
        this.headers=headers;
        this.simHash=simHash;
        this.outlinks=outlinks;
        this.myHash=myHash;
    }

    public FileInfo(String url,int outlinks,String myHash)
    {
        this.url=url;


        this.outlinks=outlinks;
        this.myHash=myHash;
    }

    @Override
    public boolean insert(DB db, String collectionName, BulkWriteOperation writer) {

        DBCollection col=db.getCollection(collectionName);

        return false;
    }

    @Override
    public void update(DB db, String collectionName, String key, Object val, String op,BulkWriteOperation writer) {

    }

    @Override
    public void delete(DB db, String collectionName,BulkWriteOperation writer) {

    }
}
