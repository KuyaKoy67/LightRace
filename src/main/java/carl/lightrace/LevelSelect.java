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
}
