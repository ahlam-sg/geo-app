package com.example.geo_app;

public class Questions {
    private String code;
    private String continent;

    //country name
    private String answer;

    //capital, flag, or map
    private String question;

    //random countries' names
    private String option1;
    private String option2;
    private String option3;

    public Questions(String code, String answer, String question, String continent) {
        this.code = code;
        this.answer = answer;
        this.question = question;
        this.continent = continent;
    }

    //setters
    public void setOption1(String option1) {
        this.option1 = option1;
    }
    public void setOption2(String option2) {
        this.option2 = option2;
    }
    public void setOption3(String option3) {
        this.option3 = option3;
    }

    //getters
    public String getCode() {
        return code;
    }
    public String getAnswer() {
        return answer;
    }
    public String getQuestion() {
        return question;
    }
    public String getContinent() {
        return continent;
    }
    public String getOption1() {
        return option1;
    }
    public String getOption2() {
        return option2;
    }
    public String getOption3() {
        return option3;
    }
}
