package carl.lightrace;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class LevelSelect {
    @FXML private Label levelLabel;

    @FXML private Button lvl1Button;
    @FXML private Button lvl2Button;
    @FXML private Button lvl3Button;
    @FXML private Button lvl4Button;
    @FXML private Button lvl5Button;

    public void enterLevel1(MouseEvent mouseEvent) throws IOException {
        Button clicked = (Button) mouseEvent.getSource();
        int levelIndex = Integer.parseInt(clicked.getText()) - 1;
        Level1.setLevel(levelIndex);
        App.setRoot("Level1");
    }

    public void enterButton1(MouseEvent mouseEvent) throws IOException {
        lvl1Button.setStyle("-fx-background-color: #e66060");
    }

    public void exitButton1(MouseEvent mouseEvent) throws IOException {
        lvl1Button.setStyle("-fx-background-color: #e33030");
    }

    public void enterButton2(MouseEvent mouseEvent) throws IOException {
        lvl2Button.setStyle("-fx-background-color: #e66060");
    }

    public void exitButton2(MouseEvent mouseEvent) throws IOException {
        lvl2Button.setStyle("-fx-background-color: #e33030");
    }

    public void enterButton3(MouseEvent mouseEvent) throws IOException {
        lvl3Button.setStyle("-fx-background-color: #e66060");
    }

    public void exitButton3(MouseEvent mouseEvent) throws IOException {
        lvl3Button.setStyle("-fx-background-color: #e33030");
    }

    public void enterButton4(MouseEvent mouseEvent) throws IOException {
        lvl4Button.setStyle("-fx-background-color: #e66060");
    }

    public void exitButton4(MouseEvent mouseEvent) throws IOException {
        lvl4Button.setStyle("-fx-background-color: #e33030");
    }

    public void enterButton5(MouseEvent mouseEvent) throws IOException {
        lvl5Button.setStyle("-fx-background-color: #e66060");
    }

    public void exitButton5(MouseEvent mouseEvent) throws IOException {
        lvl5Button.setStyle("-fx-background-color: #e33030");
    }

}
