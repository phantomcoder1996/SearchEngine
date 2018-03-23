

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class StopWords {
    private String result;
    private final String INPUT_FILE=System.getProperty("user.dir")+"stop.txt";
    ArrayList<String>words ;

    //constructor to make some initialization
    public StopWords()
    {
        result="";
        words=new ArrayList<String>();
        init();

    }
    //this function read the stop words from a file and init an array
    private void init ()
    {
        FileReader fr = null;
        BufferedReader br = null;
        String line;
        //initialize buffer and open file
        try {
            fr = new FileReader(INPUT_FILE);
            br =new BufferedReader(fr);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        //reading from file untill the end of file
        try {

            while((line=br.readLine())!=null)
            {
                words.add(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        finally{
            try {
                if(br != null)
                    br.close();
                if(fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        }
    }
    //this function test if the str is a stop word or not
    public boolean isStop(String str)
    {
        for(int i=0;i<words.size();i++)
            if((words.get(i)).equals(str)) return true;

        return false;
    }
    //this function searches for the stop words in the input string then removes it
    public String remove(String str)
    {
        StringBuilder builder = new StringBuilder("");
        String[] splited = str.split("\\s+");
        int len=splited.length;
        for(int i =0;i<len;i++)
        {
            if(!(isStop(splited[i])) )
            {
                if(i!=(len-1))
                    builder.append(splited[i]+" ");
                else
                    builder.append(splited[i]);
            }

        }
        result=builder.toString();
        return result;
    }

}
