package com.example.waiuscheduler;

import androidx.annotation.NonNull;

import com.example.waiuscheduler.database.AssessmentTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


// Collects information from the paper outline on the waikato university website
public class CourseOutlineScraper {
    public CourseOutlineScraper(HttpUrl url) throws IOException {
        OkHttpClient client = new OkHttpClient();   // Initialise the client
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")   // Request header
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
                    if (!response.isSuccessful()) throw new IOException("Unexpected code"); // Error for unsuccessful calls
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

                    // Unescape the json html
                    String html = escapedHtml
                            .replace("\\r\\n", "\n")
                            .replace("\\\"", "\"")
                            .replace("\\u003c", "<")
                            .replace("\\u003e", ">");

                    // TODO: Parse information from the url


                }

            }
        });
    }
}
