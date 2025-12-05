package Service;

import io.github.cdimascio.dotenv.Dotenv;
import Model.Song;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;


public class SpotifyGetSong {

    public static ArrayList<Song> getSongFromTrack() throws IOException, InterruptedException {
        Dotenv dotenv = Dotenv.load();
        String accessToken = dotenv.get("Access_Token");
        String json = getSong(accessToken);
        return parseSong(json);

    }

    public static String getSong(String accessToken) throws IOException, InterruptedException {

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/albums?ids=7j7iPq5rokadGr1ZdJRGgE"))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        }
    }
    public static ArrayList<Song> parseSong(String json) {
        boolean isFav = false;
        JSONObject root = new JSONObject(json);
        ArrayList<Song> songs = new ArrayList<>();
        JSONArray items = root.getJSONArray("albums")
                              .getJSONObject(0)
                              .getJSONObject("tracks")
                              .getJSONArray("items");

        for(int i =0; i < items.length(); i++){
            JSONObject item = items.getJSONObject(i);
            String id = item.getString("id");
            String name = item.getString("name");
            String artist = item.getJSONArray("artists").getJSONObject(0).getString("name");
            Song song = new Song(id, name, artist,isFav);
            songs.add(song);
        }

        return songs;

    }

}
