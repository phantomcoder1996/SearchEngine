import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class LinkParser {

    public static String parseLink(URL pLink,String childLink)
    {

        String completeURL;

        if(childLink.startsWith("http")||childLink.startsWith("https"))
            completeURL=childLink;
        else if(childLink.startsWith("./"))
        {
            completeURL= pLink.getProtocol()+"://"+pLink.getHost()+childLink.substring(1);
        }
        else if(childLink.startsWith("//"))
        {
            completeURL=pLink.getProtocol()+":"+childLink;
        }
        else if(childLink.startsWith("../"))
        {
            completeURL=pLink.getProtocol()+"://"+pLink.getHost()+"/"+childLink;
        }
        else
            completeURL="";


        return completeURL;



    }

    public static String getDocumentType(HttpURLConnection urlC)
    {
        return urlC.getContentType().toString();
    }


    public static String hashLink(String url)
    {
        return UUID.nameUUIDFromBytes(url.getBytes()).toString();
    }


    public static boolean isBaseURL(String url)
    {
        return url.matches("https?:\\/\\/.+\\.[a-zA-Z]+\\/?");
    }

    public static boolean linksToDirectory(String url,String dir)
    {
        return url.contains(dir);
    }

}
