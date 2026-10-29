package carl.lightrace;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;

public class Level1 {
    @FXML private GridPane gridPane;
    @FXML private Pane levelTimePane;
    @FXML private Pane placementPane;

    @FXML private Label LevelLabel;
    @FXML private Label TimeLabel;
    @FXML private Label toolBoxLabel;

    @FXML private Button PlayButton;
    @FXML private Button ResetButton;

    @FXML private ListView<?> ToolBoxListView;

    @FXML private Circle goal;
    @FXML private Line laser;
    @FXML private Circle source;
    @FXML private Rectangle straightMirror;


}
