package com.yl.spring.bean.inheritance.domain;

public class EPubBook extends  Book {
    private String downloadUrl;
    public EPubBook() { }
    public EPubBook(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    public String getDownloadUrl() {
        return downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    @Override
    public String toString() {
        return "EPubBook{" +
                "downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}
