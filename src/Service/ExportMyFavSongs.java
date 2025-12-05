package Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.ResultSet;

public class ExportMyFavSongs {
    public ExportMyFavSongs(ResultSet rs){
        exportMyFavSongs(rs);

    }
    private void exportMyFavSongs(ResultSet rs){
        File outFile = new File("/Users/phanidatana/2603275-2025-assignment-2/my_favorite_artists.csv");

        try (FileWriter fw = new FileWriter(outFile);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // header
            out.println("Artist,AmountsOfFavSongs");

            while (rs.next()) {
                String artist = rs.getString("artistName");
                int favCount = rs.getInt("FavCount");
                String safeArtist = artist.replace("\"", "\"\"");
                out.println("\"" + safeArtist + "\"," + favCount);
            }

            System.out.println("Export success → " + outFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
