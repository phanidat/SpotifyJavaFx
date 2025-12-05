package Controllers;

import Model.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import Service.SpotifyGetSong;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class FindSongsController implements Initializable {

    @FXML
    private ListView<Song> listSong;
    @FXML
    private Label SongName;
    @FXML
    private Label ArtistName;
    @FXML
    private Button btnFav;
    private Song selectedSong;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        createSongTable();
        createFavSongTable();
        btnFav.setVisible(false);
        btnFav.setManaged(false);
        ObservableList<Song> songList = showSong();
        listSong.setItems(songList);
        listSong.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);
                if (empty || song == null) {
                    setText(null);
                } else {
                    if (song.isFav()) {
                        setText("⭐ " + song.getSongName() + " - " + song.getArtistName());
                    } else {
                        setText(song.getSongName() + " - " + song.getArtistName());
                    }
                }
            }
        });

        listSong.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showSongDetail(newValue);
        });

    }

    private void fetchSongFromSpotify(){
        try {
            ArrayList<Song> songs = SpotifyGetSong.getSongFromTrack();
            for (Song song : songs) {
                insertSong(song);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public Connection getConnection(){
        Connection conn;
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:identifier.favSongDB");
            return conn;
        }catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
            return null;
        }
    }

    private void executeQuery(String query) {
        Connection conn = getConnection();
        Statement st;
        try{
            st = conn.createStatement();
            st.executeUpdate(query);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    private void createSongTable(){
        String sql = """
                CREATE TABLE IF NOT EXISTS songs (
                 id VARCHAR(200) PRIMARY KEY,
                 songName VARCHAR(50),
                 artistName VARCHAR(50)
                );
                """;
        executeQuery(sql);
    }
    private void createFavSongTable(){
        String sql = """
                CREATE TABLE IF NOT EXISTS favSongs (
                id VARCHAR(200) PRIMARY KEY,
                songName VARCHAR(50),
                artistName VARCHAR(50),
                favDate Real);
        """;
        executeQuery(sql);
    }

    private void insertSong(Song song){
        String id = song.getId().replace("'", "''");;
        String songName = song.getSongName().replace("'", "''");;
        String artistName = song.getArtistName().replace("'", "''");;
        String sql = "INSERT INTO songs (id, songName, artistName) VALUES (" +
                "'" + id + "', " +
                "'" + songName + "', " +
                "'" + artistName + "'" +
                ");";
        executeQuery(sql);

    }
    private ObservableList<Song> showSong(){
        ObservableList<Song> songList = FXCollections.observableArrayList();
        String sql = "SELECT s.id, s.songName,s.artistName,(f.id IS NOT NULL) AS isFav FROM songs as s left join favSongs as f on s.id = f.id;";
        try( Connection conn = getConnection();
            Statement st =  conn.createStatement();
            ResultSet rs = st.executeQuery(sql);)
        {
            while(rs.next()){
                String id = rs.getString("id");
                String songName = rs.getString("songName");
                String artistName = rs.getString("artistName");
                boolean isFav = rs.getBoolean("isFav");
                Song song = new Song(id, songName, artistName,isFav);
                songList.add(song);
            }

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return songList;
    }
    private void refreshSongsList() {
        ObservableList<Song> updated = showSong(); // JOIN กับ favSongs
        listSong.setItems(updated);
    }

    private void showSongDetail(Song song){
        if (song == null) {
            SongName.setText("");
            ArtistName.setText("");

            btnFav.setVisible(false);
            btnFav.setManaged(false);

            return;
        }

        SongName.setText(song.getSongName());
        ArtistName.setText(song.getArtistName());

        btnFav.setDisable(song.isFav());

        btnFav.setVisible(true);
        btnFav.setManaged(true);

        selectedSong = song;

    }

    public void OnfavClick(ActionEvent actionEvent) {
        if(actionEvent.getSource() == btnFav) {
            if (selectedSong == null) {
                return;
            }
            String songId = selectedSong.getId();
            String songName = selectedSong.getSongName();
            String artistName = selectedSong.getArtistName();

            String sql = "INSERT INTO favSongs (id, songName, artistName, favDate) VALUES (" +
                    "'" + songId + "', " +
                    "'" + songName.replace("'", "''") + "', " +
                    "'" + artistName.replace("'", "''") + "', " +
                    "julianday('now')" +
                    ");";
            executeQuery(sql);
        }
        refreshSongsList();
    }
    @FXML
    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
