import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;

public class PrimaryController {

    @FXML
    private Label LevelLabel;

    @FXML
    private Button PlayButton;

    @FXML
    private Button ResetButton;

    @FXML
    private Label TimeLabel;

    @FXML
    private ListView<?> ToolBoxListView;

    @FXML
    private QuadCurve concaveMirror;

    @FXML
    private Ellipse convexeMirror;

    @FXML
    private GridPane gameGrid;

    @FXML
    private Rectangle glassBlock;

    @FXML
    private Circle goal;

    @FXML
    private GridPane gridPane;

    @FXML
    private Line laser;

    @FXML
    private Pane levelTimePane;

    @FXML
    private Pane placementPane;

    @FXML
    private Circle source;

    @FXML
    private Rectangle straightMirror;

    @FXML
    private Label toolBoxLabel;

    @FXML
    private Rectangle waterBlock;

}

