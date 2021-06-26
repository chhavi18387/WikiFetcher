package codingmafia.project2;

import codingmafia.HttpURLConnectionExamples;
import codingmafia.TaskManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;


public class wikipediaFetcher implements Runnable{
    private String keyword;
    private String result;
    private String imageURL;
    public String uurrll;
    public wikipediaFetcher() { }
    public wikipediaFetcher(String keyword){
        this.keyword=keyword;
    }
    private String getWikipediaUrlForQuery(String keyword){
        String url = "https://en.wikipedia.org/wiki/" + keyword;
        return url; }
    @Override
    public void run() {
        /*
        get clean keyword
        get url for wikipedia
        make a get request
        parse the page using jsoup
        display output
         */
        if (this.keyword==null || this.keyword.length()==0){
            return ; }
        /*
        trim is to replace space from beginning or ending
        [ ]+   (means removing all the continuous space withing the string by an underscore
         */
        this.keyword= this.keyword.trim().replaceAll("[ ]+","_");
        String wikiUrl = getWikipediaUrlForQuery(keyword);
        try {
            String wikipediaResponseHTML= HttpURLConnectionExamples.sendGetRequest(wikiUrl);
            /*
            System.out.println(wikipediaResponseHTML);
            parse the page using jsoup
            Creating a document object so we can parse it using jsoup
            */

            Document document = (Document) Jsoup.parse(wikipediaResponseHTML,"https://en.wikipedia.org/wiki/");
            /*
            Out task is to fetch the very first paragraph text from wikipedia page. So when we inspect the document in wikipedia page,
            we can see that the required paragraph is present inside "mw-parser-output" class. But there are so many <p> tage inside
            this class. How do we fetch that one particular para? Well, if you notice carefully, in wikipedia page for any query
            required para is present just below the table tag. So, what we need to do is fetch all the children within this class
            iterate over them till we find a table tag. Then stop and print the next <p> tag to get the desired result.

             */
            Elements childElements = document.body().select(".mw-parser-output > *");
            /*
            using automata (state) for implementation
            */
            int state =0;
            String response ;
            for (Element ce : childElements){
                if (state ==0){
                    if (ce.tagName().equals("table")){
                        state=1;}}
                else if (state == 1){
                    if(ce.tagName().equals("p")){
                        state=2;
                        response = ce.text();
                        this.result=response;
                        break;}}
                try{
                    String url = document.body().select(".infobox img").get(0).attr("src");
                    this.imageURL=url;}
                catch(Exception e){ }}}
        catch (Exception e) {
            e.printStackTrace();}
        if(imageURL.startsWith("//")){
            imageURL="https:"+imageURL;}
        this.uurrll=imageURL;
        WikiResult wikiresult = new WikiResult (this.result,this.imageURL);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(wikiresult);
        System.out.println(json);

        // save image code
        SaveImageFromUrl s = new SaveImageFromUrl();
        try {
            s.saveImage(uurrll,"image.png");
            System.out.println("done"); }
        catch (IOException e) {
            e.printStackTrace(); } }

    public static void main(String[] args) {
        //we don't want every object to have a task manager, hence static
        TaskManager taskmanager = new TaskManager(20);
        wikipediaFetcher wf =new wikipediaFetcher("Albert Einstein");
        taskmanager.waitTillQueueIsFreeThenAddTask(wf); }}
