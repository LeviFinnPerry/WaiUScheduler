package com.example.waiuscheduler.http;

import com.example.waiuscheduler.cleaner.ScrapedData;

import org.jsoup.nodes.Document;

public interface OnDocumentReady {
    void onReady(Document document);
    void onError(Exception e);
}
