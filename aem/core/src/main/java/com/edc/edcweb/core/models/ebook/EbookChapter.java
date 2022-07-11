package com.edc.edcweb.core.models.ebook;

public class EbookChapter {

    /**
     * POJO for ebook chapter component. Will ensure only the required fields from
     * the ebook pages are used
     */

    private String chapterTitle;

    private int chapterTimeToRead;

    private String chapterIntro;

    private String chapterUrl;

    private int chapterNumber;
    
    private int currentChapter;

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public int getChapterTimeToRead() {
        return chapterTimeToRead;
    }

    public void setChapterTimeToRead(int chapterTimeToRead) {
        this.chapterTimeToRead = chapterTimeToRead;
    }

    public String getChapterIntro() {
        return chapterIntro;
    }

    public void setChapterIntro(String chapterIntro) {
        this.chapterIntro = chapterIntro;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public int getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(int currentChapter) {
        this.currentChapter = currentChapter;
    }
}
