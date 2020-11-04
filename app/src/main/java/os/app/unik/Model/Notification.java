package os.app.unik.Model;

public class Notification {
    private String userid;
    private String text;
    private String postid;
    private boolean ispost;
    private boolean isquestion;



    public Notification(String userid, String text, String postid, boolean ispost, boolean isquestion) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.ispost = ispost;
        this.isquestion = isquestion;
    }

    public Notification() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }
    public boolean isIsquestion() {
        return isquestion;
    }

    public void setIsquestion(boolean isquestion) {
        this.isquestion = isquestion;
    }
}
