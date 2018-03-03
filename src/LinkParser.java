import java.net.HttpURLConnection;
import java.net.URL;

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

}
