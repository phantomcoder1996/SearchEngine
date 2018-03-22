import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;

public interface MyDBObject  {

    public boolean insert(DB db ,String collectionName,BulkWriteOperation writer);
    public void update(DB db,String collectionName,String key,Object val,String op,BulkWriteOperation writer);
    public void delete(DB db,String collectionName,BulkWriteOperation writer);

}