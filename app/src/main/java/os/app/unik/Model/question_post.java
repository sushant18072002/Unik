package os.app.unik.Model;

public class question_post {
    private String postid;
    private String postimage;
    private String publisher;
    private String question;
    private String answer;
    private String answerid;



    public question_post(String postid, String postimage, String description, String question, String answer, String answerid) {
        this.postid = postid;
        this.postimage = postimage;
        this.publisher = description;
        this.question = question;
        this.answer=answer;
        this.answerid=answerid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public question_post() {
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    public String getAnswerid() {
        return answerid;
    }

    public void setAnswerid(String answerid) {
        this.answerid = answerid;
    }
}
