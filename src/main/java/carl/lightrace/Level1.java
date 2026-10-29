package carl.lightrace;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class Level1 implements Initializable {
    @FXML private GridPane gridPane;
    @FXML private GridPane gameGrid;
    @FXML private Pane levelTimePane;
    @FXML private Pane placementPane;

    @FXML private Label LevelLabel;
    @FXML private Label TimeLabel;
    @FXML private Label toolBoxLabel;

    @FXML private Button PlayButton;
    @FXML private Button ResetButton;

    @FXML private ListView<Shape> ToolBoxListView;

    @FXML private Circle goal;
    @FXML private Line laser;
    @FXML private Circle source;
    @FXML private Rectangle straightMirror;

    private final ObservableList<Shape> tools = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToolBoxListView.setItems(tools);
        tools.addAll(straightMirror);
    }

    @FXML
    void startLight(MouseEvent e) {
        ;
    }

    @FXML
    void resetArea(MouseEvent e) {
        ;
    }
}
