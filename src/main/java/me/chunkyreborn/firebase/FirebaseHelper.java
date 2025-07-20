package me.chunkyreborn.firebase;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FirebaseHelper {

    private static final String DATABASE_URL = "https://chunkyreborn-default-rtdb.firebaseio.com/";

    public static void sendChunkUpdate(String dashboardId, int x, int z, String status) {
        try {
            String path = "dashboards/" + dashboardId + "/chunks/" + x + "_" + z + ".json";
            URL url = new URL(DATABASE_URL + path);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT"); // Replace or create
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String json = String.format("{\"x\":%d,\"z\":%d,\"status\":\"%s\"}", x, z, status);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            connection.getResponseCode(); // Force send
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
