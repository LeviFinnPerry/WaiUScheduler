package com.example.waiuscheduler.parsing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// Collects information from the paper outline on the waikato university website
public class CourseOutlineScraper {

    private final OkHttpClient client = new OkHttpClient();     // Initialise http client


    public Document getCourseOutline(HttpUrl url) throws IOException {
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

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code" + response); // Error for unsuccessful calls

            // Parse the result into json forms
            assert response.body() != null;
            JsonElement parsed = JsonParser.parseString(response.body().string());

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

            // Return the html as a document
            return Jsoup.parse(html);
        }
    }
}

