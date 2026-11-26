/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package carl.lightrace;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author carln
 */
public class LaunchWindow implements Initializable {

    @FXML private Button startButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void enterLevels(MouseEvent mouseEvent) throws IOException {
        App.setRoot("LevelSelectWindow");
    }

    public void enterStartButton(MouseEvent mouseEvent) throws IOException {
        startButton.setStyle("-fx-background-color: #6fa3d8");
    }

    public void exitStartButton(MouseEvent mouseEvent) throws IOException {
        startButton.setStyle("-fx-background-color: #4A7FB5");
    }

}