package com.example.waiuscheduler;

import androidx.annotation.NonNull;

import com.example.waiuscheduler.database.PaperTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private final DatabaseController dbController;

    public CourseOutlineScraper(DatabaseController dbController) {
        this.dbController = dbController;   // Connects controller to scraper
    }

    public void getCourseOutline(HttpUrl url) throws IOException {
        OkHttpClient client = new OkHttpClient();   // Initialise the client
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")   // As written on website for request headers
                .header("Accept-Language", "en-US,en;q=0.9,es;q=0.8,ru;q=0.7")
                .header("Origin", "https://paperoutlines.waikato.ac.nz")
                .header("Referer", "https://paperoutlines.waikato.ac.nz/")
                .header("sec-ch-ua", "\"Opera\";v=\"127\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("sec-fetch-dest", "empty")
                .header("sec-fetch-mode", "cors")
                .header("sec-fetch-site", "cross-site")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 OPR/127.0.0.0")
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
                    if (!response.isSuccessful()) throw new IOException("Unexpected code" + response); // Error for unsuccessful calls
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

                    // Extract key information from the paper and save to database
                    getInformation(paperOutline);
                }
            }
        });

    }

    // Function to retrieve outline information from the paper outline
    private void getInformation(Document paper) {
        // Labels of each element
        List<String> labelNames = new ArrayList<>();
        labelNames.add("Paper Title");
        labelNames.add("Paper Occurrence Code");
        labelNames.add("Points");
        labelNames.add("When taught");
        labelNames.add("Start Week");
        labelNames.add("End Week");

        // Tables of each element
        List<String> tableNames = new ArrayList<>();
        tableNames.add("staff");
        tableNames.add("timetable");
        tableNames.add("assessments");

        ArrayList<String> itemsData = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<String>>> tablesData = new ArrayList<>();

        // Retrieve main information from paper outline
        for (String name : labelNames) {
            String query = "label:contains(" + name + ")";
            // Find the label
            Element label = paper.select(query).first();
            // Get the information of the element
            if (label != null) {
                Element span = label.nextElementSibling();
                if (span != null) {
                    String item = span.text();
                    itemsData.add(item);
                }
            }
        }

        // Retrieve information fromm each table
        for (String table: tableNames) {
            tablesData.add(retrieveTableData(table, paper));
        }

        // Store data to the paper table
        toPaperData(itemsData);

    }

    // Helping function to retrieve information from tables
    private ArrayList<ArrayList<String>> retrieveTableData(String tableName, Document paper) {
        ArrayList<String> colData = new ArrayList<>();
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();
        String query = "table:contains(" + tableName + ")";
        // Find the table
        Element table = paper.select(query).first();
        // Find the rows in the table
        assert table != null;       // Make sure there is a table
        Elements rows = table.select("tr");
        // Get the columns from the rows
        for (Element row: rows) {
            Elements cols = row.select("td");
            // Process each cell in the row
            for (Element col: cols) {
                String cellData = col.text();
                colData.add(cellData);
            }
            if (!colData.isEmpty()) {
                tableData.add(colData);
            }
        }
        return tableData;
    }

    // Store information for paper database
    private void toPaperData(ArrayList<String> paperData) {
        // Retrieve information from array list
        // For now just for papers that successfully parse all information
        if (paperData.size() == 6) {
            String paperName = paperData.get(0);
            String paperCode = paperData.get(1).split("-")[0];
            int points = Integer.parseInt(paperData.get(2));
            String startWeek = paperData.get(4);
            String endWeek = paperData.get(5);
            String semesterCode_fk = paperData.get(3);

            dbController.savePaper(new PaperTable(paperCode, paperName, points, startWeek, endWeek, semesterCode_fk));
        }
    }

}
