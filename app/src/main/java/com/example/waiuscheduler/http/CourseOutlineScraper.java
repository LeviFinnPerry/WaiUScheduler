package com.example.waiuscheduler.http;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.waiuscheduler.cleaner.DataCleaner;
import com.example.waiuscheduler.cleaner.ScrapedData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

// TODO: OPTIMISE THE HANDLING ON PARSING
// TODO: Need a converted to handle date types

// Collects information from the paper outline on the waikato university website
public class CourseOutlineScraper {
    // Database controller
    private OnDocumentReady listener;

    private ScrapedData paperData;

    public CourseOutlineScraper() {
        startOnDocumentReadyListener();          // Initialise ondocumentready listener
    }

    // Start the onDocument ready listener
    private void startOnDocumentReadyListener() {
        this.listener = new OnDocumentReady() {
            @Override
            public void onReady(Document document) {
                // TODO: Pass document to cleaning
                // Possibly needing to rethink threads for foreign key
                //DataCleaner cleaner = new DataCleaner();
                //setPaperData(cleaner.clean(document));
            }

            @Override
            public void onError(Exception e) {
                Log.e("Listener", "Fail parse document");
            }
        };
    }

    public void getCourseOutline(HttpUrl url) throws IOException {
        OkHttpClient client = new OkHttpClient();   // Initialise the client
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")   // As written on website for request headers
                .header("Accept-Language", "en-US,en;q=0.9,es;q=0.8,ru;q=0.7")
                .header("Origin", "https://paperoutlines.waikato.ac.nz")
                .header("Referer", "https://paperoutlines.waikato.ac.nz/")
                .header(
                        "sec-ch-ua",
                        "\"Opera\";v=\"127\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\""
                )
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("sec-fetch-dest", "empty")
                .header("sec-fetch-mode", "cors")
                .header("sec-fetch-site", "cross-site")
                .header(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 OPR/127.0.0.0"
                )
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // TODO: Handling of errors
                e.printStackTrace();
            }

            @Override
            public void onResponse(
                    @NonNull Call call,
                    @NonNull Response response
            ) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code" + response); // Error for unsuccessful calls
                    assert responseBody != null;    // Make sure the document is not empty
                    String result = responseBody.string();  // Get the document string

                    // Parse the result into json forms
                    JsonElement parsed = JsonParser.parseString(result);

                    // Format the json element
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String pretty = gson.toJson(parsed);

                    // Retrieve the html element from json file
                    JsonObject root = JsonParser.parseString(pretty).getAsJsonObject();
                    String escapedHtml = root.get("html").getAsString();

                    // Unescape the html
                    String html = escapedHtml
                            .replace("\\r\\n", "\n")
                            .replace("\\\"", "\"")
                            .replace("\\u003c", "<")
                            .replace("\\u003e", ">");

                    // Store the html as a document
                    Document paperOutline = Jsoup.parse(html);

                    // Notify the listener of complete status
                    notifyListener(listener, paperOutline);

                }
            }
        });
    }

    // Notify listener of the result
    private void notifyListener(OnDocumentReady listener, Document document) {
        if (listener != null) {
            try {
                listener.onReady(document);
            } catch (Exception e) {
                listener.onError(e);
            }
        }
    }

    // Get the paperData from the course outline
    public ScrapedData getPaperData() {
        return paperData;
    }

    public void setPaperData(ScrapedData paperData) {
        this.paperData = paperData;
    }
}

