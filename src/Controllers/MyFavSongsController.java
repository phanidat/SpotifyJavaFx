package Controllers;

import Model.FavSong;
import Service.ExportMyFavSongs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.ResourceBundle;


public class MyFavSongsController implements Initializable {
    @FXML
    private TableView<FavSong> MyFavTable;
    @FXML
    private TableColumn<FavSong, String> nameSong;
    @FXML
    private TableColumn<FavSong, String> artistName;
    @FXML
    private TableColumn<FavSong, String> date;
    @FXML
    private TableColumn<FavSong, Void> delete;
    @FXML
    private TableColumn<FavSong, String> comment;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showFavSongs();
        MyFavTable.setEditable(true);
        comment.setEditable(true);

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

    private ObservableList<FavSong> loadFavSongs() {
        ObservableList<FavSong> favSongs = FXCollections.observableArrayList();
        String sql = "SELECT id,songName,artistName,comment,DATE(favDate) as favdate FROM favSongs";
        try (Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql))
        {
            while (rs.next()) {
                String id = rs.getString("id");
                String songName = rs.getString("songName");
                String artistName = rs.getString("artistName");
                String favDate = rs.getString("favdate");
                String comments = rs.getString("comment");
                FavSong favSong = new FavSong(id, songName, artistName, favDate,comments);
                favSongs.add(favSong);
            }

        }catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
            }
        return favSongs;
    }

    private void showFavSongs() {
        ObservableList<FavSong> favList = loadFavSongs();
        nameSong.setCellValueFactory(new PropertyValueFactory<>("songName"));
        artistName.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        date.setCellValueFactory(new PropertyValueFactory<>("favDate"));
        MyFavTable.setItems(favList);
        comment.setCellValueFactory(new PropertyValueFactory<>("comments"));
        comment.setCellFactory(TextFieldTableCell.forTableColumn());

        comment.setOnEditCommit(event -> {
            FavSong fav = event.getRowValue();
            String newComment = event.getNewValue();

            fav.setComments(newComment);
            updateCommentInDB(fav);
        });

        MyFavTable.setEditable(true);

        delete.setCellFactory(col -> new TableCell<FavSong, Void>() {
            private final Button btn = new Button("Remove");

            {
                btn.setOnAction(e -> {
                    FavSong fav = getTableView().getItems().get(getIndex());
                    removeFromFav(fav);
                    getTableView().getItems().remove(fav);
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

    }
    private void removeFromFav(FavSong favSong) {
        String sql = "DELETE FROM favSongs WHERE id = '" + favSong.getId() + "'";
        executeQuery(sql);
    }

    private void updateCommentInDB(FavSong fav) {
        String sql = "UPDATE favSongs SET comment =  '" + fav.getComments() + "' WHERE id = '" + fav.getId() + "'";
        executeQuery(sql);
    }

    @FXML
    private void Back(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/Main.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void Export(ActionEvent actionEvent) {
        String sql = "SELECT artistName,count(*) as FavCount FROM favSongs GROUP BY artistName ORDER BY FavCount DESC;";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            new ExportMyFavSongs(rs);

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
