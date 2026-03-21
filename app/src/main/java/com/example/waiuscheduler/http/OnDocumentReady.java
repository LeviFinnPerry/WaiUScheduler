package com.example.waiuscheduler.http;

import org.jsoup.nodes.Document;

public interface OnDocumentReady {
    void onReady(Document document);
    void onError(Exception e);
}
