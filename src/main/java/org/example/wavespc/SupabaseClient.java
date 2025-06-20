package org.example.wavespc;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class SupabaseClient {
    private final String apiUrl;
    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    public SupabaseClient(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Song> getSongs() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new java.net.URI(apiUrl + "/rest/v1/songs"))
                .header("apikey", apiKey)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<Song>>(){}.getType());
        } else {
            throw new Exception("Ошибка при загрузке песен: " + response.statusCode());
        }
    }
}
