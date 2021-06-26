package codingmafia.project2;

public class WikiResult {
    String text;
    String url;
    WikiResult(String text, String url){
        this.text=text;
        this.url=url;
    }
    public String getText(){
        return this.text;
    }
    public String getUrl(){
        return this.url;
    }
}
