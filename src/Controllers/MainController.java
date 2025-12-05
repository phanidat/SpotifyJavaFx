package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private AnchorPane contentPane;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    @FXML
    private void showFindPage(ActionEvent event) throws IOException {
        switchScence(event,"/fxml/FindSongs.fxml");
    }
    @FXML
    private void showFavPage(ActionEvent event) throws IOException {
        switchScence(event,"/fxml/MyFavSongs.fxml");
    }
    private void switchScence(ActionEvent event,String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
  }
}
