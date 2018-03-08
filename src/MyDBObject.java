import com.mongodb.DB;

public interface MyDBObject  {

    public boolean insert(DB db ,String collectionName);
    public void update(DB db,String collectionName,String key,Object val,String op);
    public void delete(DB db,String collectionName);

}