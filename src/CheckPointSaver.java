import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

public class CheckPointSaver extends Thread {

    @Override
    public void run()
    {
        //opens a file backup.txt
        //Saves :
        //1- Resources
        //2- Current number of pages downloaded
        BufferedWriter writer=null;
        try {
            writer = new BufferedWriter(new FileWriter("../backup.txt"));
            writer.write(Resources.getCount());
            writer.newLine();
            Queue<Pair<String,String>>q=Resources.getLinkQueue();
            Set<String>s=Resources.getVisited();
            writer.write(q.size());
            writer.newLine();

            while(!q.isEmpty()){ Pair<String,String>link=q.poll();
            writer.write(link.getKey());  writer.newLine(); writer.write(link.getValue()); writer.newLine();}

            writer.write(s.size());
            Iterator iter=s.iterator();
            while(iter.hasNext()){writer.write( iter.next().toString()); writer.newLine();}

            writer.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
