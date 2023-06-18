import java.util.List;

public class Pojo_PostRequest2 {
    int id;
    String name;
    String avatarURL;
    Answers answers;
    List<String> questions;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getAvatarURL(){
        return avatarURL;
    }
    public void setAvatarURL(String avatarURL){
        this.avatarURL = avatarURL;
    }
    public Answers getAnswers(){
        return (Answers) answers;
    }
    public void setAnswers(Answers answers){
        this.answers = answers;
    }
    public List<String> getQuestions(){
        return questions;
    }
    public void setQuestions(List<String> questions){
        this.questions = questions;
    }

}
