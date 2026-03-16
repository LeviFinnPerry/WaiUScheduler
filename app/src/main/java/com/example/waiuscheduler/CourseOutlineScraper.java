package com.example.waiuscheduler;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;

import com.example.waiuscheduler.dao.PaperDao;
import com.example.waiuscheduler.database.AssessmentTable;
import com.example.waiuscheduler.database.PaperTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

                    // Unescape the html
                    String html = escapedHtml
                            .replace("\\r\\n", "\n")
                            .replace("\\\"", "\"")
                            .replace("\\u003c", "<")
                            .replace("\\u003e", ">");

                    // Store the html as a document
                    Document paperOutline = Jsoup.parse(html);

                    // Extract key information from the paper
                    RetrieveInformation(paperOutline);
                }

            }
        });
    }

    public void RetrieveInformation(Document paper) {
        // Labels of each element
        List<String> labels = new ArrayList<>();
        labels.add("Paper Title");
        labels.add("Paper Occurrence Code");
        labels.add("Points");
        labels.add("When taught");
        labels.add("Start Week");
        labels.add("End Week");

        List<String> items = new ArrayList<>();

        // Retrieve main information from paper outline
        for (String name : labels) {
            String query = "label:contains(" + name + ")";
            // Find the label
            Element label = paper.select(query).first();
            // Get the information of the element
            if (label != null) {
                Element span = label.nextElementSibling();
                if (span != null) {
                    String item = span.text();
                    items.add(item);
                }

            }

        }

        // TODO: Handle retrieving information from tables

        // TODO: Store the information in the correct databases


    }

}
