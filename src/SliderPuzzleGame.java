import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.Random;

public class SliderPuzzleGame extends Application {

    private Pane mainPane;
    private Button startStop;
    private Button[][] buttons;
    private ListView<String> puzzlesList;
    private Timeline updateTimer;
    private TextField timeField;
    private Label thumbNail, timeLabel;

    private String[][] buttonImages;
    private boolean startState = true; // true : Start, false : Stop
    private String choice;
    private Point2D posBlankImg;
    private Point2D[] currentOrderOfTiles;
    private final int TotalTileCount = 16;

    private long startTime;

    public void start(Stage primaryStage)
    {

        Init();

        // Load the tiles with the blank image
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                ShowBlankTile(new Point2D(i,j));
            }
        }

        updateTimer = new Timeline(new KeyFrame(Duration.millis(1000),
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        // FILL IN YOUR CODE HERE THAT WILL GET CALLED ONCE PER SEC.
                        timeField.setText(getElapsedTimeStr());
                    }
                }));
        updateTimer.setCycleCount(Timeline.INDEFINITE);

        // Updates the thumbnail
        puzzlesList.setOnMouseClicked(new EventHandler<MouseEvent>(){
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    choice = puzzlesList.getSelectionModel().getSelectedItem();
                    // using the naming convention to figure out which thumbnail to show
                    String thumbName = choice + "_Thumbnail.png";
                    thumbNail.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(thumbName))));
                }
            }
        });

        startStop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    if (startState) { // start state
                        Start();
                        LoadPuzzle(choice);
                        if (IsCompleteImage()){
                            for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 4; j++) {
                                    ShowTile(choice, i, j, posBlankImg);
                                    buttons[i][j].setDisable(true);
                                    updateTimer.stop();
                                }
                            }
                        }
                        } else { // stop state
                        Stop();
                    }
                }
            }
        });

        mainPane.getChildren().addAll(thumbNail, puzzlesList, startStop, timeLabel, timeField);
        primaryStage.setTitle("Slider Puzzle Game");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(mainPane, 955, 748));
        primaryStage.show();
    }

    public void Init()
    {
        mainPane = new Pane();
        // Listviews
        puzzlesList = new ListView<>();
        String[] puzzles = {"Pets", "Scenery", "Lego", "Numbers"};
        puzzlesList.setItems(FXCollections.observableArrayList(puzzles));
        puzzlesList.relocate(768, 207);
        puzzlesList.setPrefSize(187, 150);
        puzzlesList.getSelectionModel().select(0);

        // Thumbnail
        thumbNail = new Label();
        thumbNail.setPrefSize(187, 187);
        thumbNail.relocate(768, 10);
        thumbNail.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("Pets_Thumbnail.png"))));

        // Start Button
        startStop = new Button();
        startStop.relocate(768, 367);
        startStop.setPrefSize(187, 25);
        startStop.setStyle("-fx-color: DARKGREEN");
        startStop.setText("Start");

        timeLabel = new Label("Time:");
        timeLabel.relocate(768, 406);
        timeField = new TextField();//

        // Time
        timeField.relocate(852, 402);
        timeField.setPrefSize(105, 25);
        timeField.setAlignment(Pos.CENTER_LEFT);
        timeField.setText("0:00");

        // Buttons imgaes
        buttons = new Button[4][4];
        buttonImages = new String[][] {

                {       "Lego_00.png", "Lego_01.png", "Lego_02.png", "Lego_03.png",
                        "Lego_10.png", "Lego_11.png", "Lego_12.png", "Lego_13.png",
                        "Lego_20.png", "Lego_21.png", "Lego_22.png", "Lego_23.png",
                        "Lego_30.png", "Lego_31.png", "Lego_32.png", "Lego_33.png"},

                {       "Numbers_00.png", "Numbers_01.png", "Numbers_02.png", "Numbers_03.png",
                        "Numbers_10.png", "Numbers_11.png", "Numbers_12.png", "Numbers_13.png",
                        "Numbers_20.png", "Numbers_21.png", "Numbers_22.png", "Numbers_23.png",
                        "Numbers_30.png", "Numbers_31.png", "Numbers_32.png", "Numbers_33.png"},

                {       "Pets_00.png", "Pets_01.png", "Pets_02.png", "Pets_03.png",
                        "Pets_10.png", "Pets_11.png", "Pets_12.png", "Pets_13.png",
                        "Pets_20.png", "Pets_21.png", "Pets_22.png", "Pets_23.png",
                        "Pets_30.png", "Pets_31.png", "Pets_32.png", "Pets_33.png"},

                {       "Scenery_00.png", "Scenery_01.png", "Scenery_02.png", "Scenery_03.png",
                        "Scenery_10.png", "Scenery_11.png", "Scenery_12.png", "Scenery_13.png",
                        "Scenery_20.png", "Scenery_21.png", "Scenery_22.png", "Scenery_23.png",
                        "Scenery_30.png", "Scenery_31.png", "Scenery_32.png", "Scenery_33.png"}
        };

        // init all the tiles
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i][j] = new Button();
                buttons[i][j].relocate(10 + j * 187, 10 + i * 187);
                buttons[i][j].setPrefSize(187, 187);
                buttons[i][j].setPadding(new Insets(1, 1, 1, 1));
                mainPane.getChildren().addAll(buttons[i][j]);

                buttons[i][j].setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // identify the clicked button
                        for (int row = 0; row<4; row++) {
                            for (int col = 0; col<4; col++){
                                if (event.getSource() == buttons[row][col]) {
                                    OnTileClick(new Point2D(row,col));
                                }
                            }
                        }
                    }
                });
            }
        }

        currentOrderOfTiles = new Point2D[TotalTileCount];
        int t = 0;
        while (t<TotalTileCount) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    currentOrderOfTiles[t] = new Point2D(i, j);
                    t++;
                }
            }
        }
    }

    private boolean IsCompleteImage()
    {
        Point2D[] correctOrderOfTiles = new Point2D[TotalTileCount];
        int t = 0;
        while (t<TotalTileCount) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    correctOrderOfTiles[t] = new Point2D(i, j);
                    t++;
                }
            }
        }

        return currentOrderOfTiles == (correctOrderOfTiles);
    }

    private void OnTileClick(Point2D clickedTilePos)
    {
        if (isAdjacentToBlankTile(clickedTilePos)) {
            SwapTiles(posBlankImg, clickedTilePos);
            posBlankImg = clickedTilePos;
            RefreshTiles(true);
        }
    }

    private boolean isAdjacentToBlankTile(Point2D tilePos) {

        int blankTileRow = (int) posBlankImg.getX();
        int blankTileCol = (int) posBlankImg.getY();

        Point2D[] validSwaps = new Point2D[4];

        validSwaps[0] = new Point2D(blankTileRow + 1, blankTileCol);
        validSwaps[1] = new Point2D(blankTileRow - 1, blankTileCol);
        validSwaps[2] = new Point2D(blankTileRow, blankTileCol + 1);
        validSwaps[3] = new Point2D(blankTileRow, blankTileCol - 1);

        for (int i = 0; i < 4; i++) {
            if (validSwaps[i].equals(tilePos))
                return true;
        }

        return false;

        }

    private void SwapTiles(Point2D src, Point2D dest)
    {
        int indexOfSrc = -1;
        int indexOfDest = -1;

        for (int i = 0; i < TotalTileCount; i++) {
            if (currentOrderOfTiles[i].equals(src) )
                indexOfSrc = i;
            if (currentOrderOfTiles[i].equals(dest))
                indexOfDest = i;
        }

        if(indexOfSrc>=0 && indexOfDest >= 0 )
        {
            Point2D temp = currentOrderOfTiles[indexOfSrc];
            currentOrderOfTiles[indexOfSrc] = currentOrderOfTiles[indexOfDest];
            currentOrderOfTiles[indexOfDest] = temp;
        }
    }

    public void LoadPuzzle(String listChoice)
    {
        choice = listChoice;
        ShuffleTiles();
        RefreshTiles(true);
    }

    public void RefreshTiles(boolean showBlankTile)
    {
        int t = 0;
        while (t < TotalTileCount) {
            // these for loops are to identify the various tiles by name
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if(currentOrderOfTiles[t].equals(posBlankImg) && showBlankTile)
                         ShowBlankTile(posBlankImg);
                    else
                        ShowTile(choice, i, j, currentOrderOfTiles[t]);
                    t++;
                }
            }
        }
    }

    public void ShowTile (String choice, int rowImg, int colImg, Point2D tilePos)
    {
        // choice + "_" + row + col;
        String imageName = choice + "_" + rowImg + colImg + ".png";
        buttons[(int)tilePos.getX()][(int)tilePos.getY()].setGraphic(new ImageView(new Image(getClass().getResourceAsStream(imageName))));
    }

    public void ShowBlankTile(Point2D BlankTilePos)
    {
        buttons[(int)BlankTilePos.getX()][(int)BlankTilePos.getY()].setGraphic(new ImageView(new Image(getClass().getResourceAsStream("BLANK.png"))));
    }

    public void ShuffleTiles()
    {
        // Insert blank tile at random
        Random rand = new Random();
        posBlankImg = new Point2D(rand.nextInt(3), rand.nextInt(3));
        ShowBlankTile(posBlankImg);

        Random random = new Random();
        Point2D random1 = new Point2D(random.nextInt(3), random.nextInt(3));
        Point2D random2 = new Point2D(random.nextInt(3), random.nextInt(3));
        SwapTiles(random1, random2);
    }

    public String getElapsedTimeStr()
    {
        long millis = System.currentTimeMillis() - startTime;
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

        String strDate = sdf.format(millis);
        return strDate;
    }

    private void ShowBlankTiles() {
        // load the array on to the tiles
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i][j].setGraphic(new ImageView(new Image(getClass().getResourceAsStream("BLANK.png"))));
            }
        }
    }

    public void Start()
    {
        startState = false;
        thumbNail.setDisable(true);
        startStop.setText("Stop");
        startStop.setStyle("-fx-color: DARKRED");
        updateTimer.play();
        startTime = System.currentTimeMillis();
        puzzlesList.setDisable(true);
    }

    public void Stop ()
    {
        startStop.setText("Start");
        startStop.setStyle("-fx-color: DARKGREEN");
        thumbNail.setDisable(false);
        puzzlesList.setDisable(false);
        updateTimer.stop();
        startState = true;
        ShowBlankTiles();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
