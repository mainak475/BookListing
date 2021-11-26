package com.example.booklisting;

public class Book {

    private String title;
    private String authors;
    private String publisher;
    private String publishDate;
    private String buyLink;

    public Book(String title, String authors, String publisher, String publishDate, String buyLink) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.buyLink = buyLink;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getBuyLink() {
        return buyLink;
    }
}
