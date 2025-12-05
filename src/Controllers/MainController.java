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
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class MainController implements Initializable {

    private static final java.util.logging.Logger LOGGER =
            java.util.logging.Logger.getLogger(MainController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    @FXML
    private void showFindPage(ActionEvent event) {
        switchScence(event,"/fxml/FindSongs.fxml");
    }
    @FXML
    private void showFavPage(ActionEvent event){
        switchScence(event,"/fxml/MyFavSongs.fxml");
    }
    private void switchScence(ActionEvent event,String fxml) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error message", e);
        }
  }
}
