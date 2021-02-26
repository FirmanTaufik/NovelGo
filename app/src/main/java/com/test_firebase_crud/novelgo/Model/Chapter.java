package com.test_firebase_crud.novelgo.Model;

public class Chapter {
    String chapter, link;

    public Chapter(String chapter, String link) {
        this.chapter = chapter;
        this.link = link;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
