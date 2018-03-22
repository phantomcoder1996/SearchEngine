import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.TimerTask;

public class TimedSerialization extends TimerTask {


    CommanderThread commanderThread;

    public TimedSerialization(CommanderThread ct)
    {
        this.commanderThread=ct;
    }

//    public  void serializeCrawler()
//    {
//        try {
//            System.out.println("timer task entered");
//            if(commanderThread!=null)
//            {
//                System.out.println("Starting serialization");
//                ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream("backup.txt"));
//                Resources.serializeResources(os);
//
//                os.writeObject(commanderThread);
//                os.close();
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public void run()
    {
        //serializeCrawler();
    }
}
