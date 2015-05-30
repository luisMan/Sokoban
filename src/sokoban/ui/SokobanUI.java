package sokoban.ui;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JEditorPane;

import application.Main.SokobanPropertyType;

import properties_manager.PropertiesManager;

import sokoban.file.SokobanFileLoader;
import sokoban.game.SokobanGameStateManager;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SokobanUI extends Pane {

    private Stage primaryStage;

    /**
     * The SokobanUIState represents the four screen states that are possible
     * for the Sokoban game application. Depending on which state is in current
     * use, different controls will be visible.
     */
    public enum SokobanUIState {

        SPLASH_SCREEN_STATE,
        PLAY_GAME_STATE,
        VIEW_STATS_STATE,
        VIEW_HELP_STATE,
        LEVEL1_STATE,
        LEVEL2_STATE,
        LEVEL3_STATE,
        LEVEL4_STATE,
        LEVEL5_STATE,
        LEVEL6_STATE,
        LEVEL7_STATE,
    }

    private BorderPane mainPane;

    // mainPane weight && height
    private int paneWidth;
    private int paneHeigth;

    // SplashScreen
    private ImageView splashScreenImageView;
    private StackPane splashScreenPane;
    private Label splashScreenImageLabel;
    private GridPane levelSelectionPane;
    private ArrayList<Button> levelButtons;

    // NorthToolBar
    private HBox northToolbar;
    private Button backButton;
    private Button statsButton;
    private Button undoButton;
    private Button timeButton;

    // GamePane
    private StackPane gamePane;

    // HelpPane
    private BorderPane helpPane;
    private ScrollPane helpScrollPane;
    private JEditorPane helpPanel;
    private Button homeButton;

    // StatsPane
    //private ScrollPane statsScrollPane;
    //private JEditorPane statsPane;
    private GridPane statsPane = new GridPane();
    private ArrayList<Label> labels = new ArrayList<>();
    private Button clearButton;

    // Padding
    private Insets marginlessInsets;
    // Image path
    private String ImgPath = "file:images/";

    // THIS CLASS WILL HANDLE ALL ACTION EVENTS FOR THIS PROGRAM
    private SokobanEventHandler eventHandler;
    private SokobanErrorHandler errorHandler;
    private SokobanDocumentManager docManager;
    private SokobanGameStateManager gsm;
    private SokobanFileLoader fileLoader;

    // GRID Renderer
    private GridRenderer gridRenderer;
    private GraphicsContext gc;

    // AND HERE IS THE GRID WE'RE MAKING
    private ArrayList<Position> terminals;
    private int LevelState = 0;
    private boolean[] direction = new boolean[4];

    // ANIMATION
    double AnimaLength = 0.5;

    public class Position {

        private int x;
        private int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public SokobanUI(Stage stage) throws IOException {
        setPrimaryStage(stage);
        gsm = new SokobanGameStateManager(this);
        eventHandler = new SokobanEventHandler(this);
        errorHandler = new SokobanErrorHandler(getPrimaryStage());
        docManager = new SokobanDocumentManager(this);
        fileLoader = new SokobanFileLoader(this);
    }

    // Methods
    public BorderPane getMainPane() {
        return mainPane;
    }

    public int getpaneWidth() {
        return paneWidth;
    }

    public int getpaneHeight() {
        return paneHeigth;
    }

    public SokobanGameStateManager getGSM() {
        return gsm;
    }

    public SokobanDocumentManager getDocManager() {
        return docManager;
    }

    public SokobanErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public SokobanFileLoader getFileLoader() {
        return fileLoader;
    }

    public GridRenderer getgridRenderer() {
        return this.gridRenderer;
    }

    public ArrayList<Position> getTerminals() {
        return terminals;
    }

    public void setTerminals(ArrayList<Position> terminals) {
        this.terminals = terminals;
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Button gettimeButton() {
        return this.timeButton;
    }

    public void setgridColumns(int gc) {
        gsm.getGameInProgress().setGridColumns(gc);
    }

    public void setgridRows(int gr) {
        gsm.getGameInProgress().setGridRows(gr);
    }

    public int getGridColumns() {
        return gsm.getGameInProgress().getGridColumns();
    }

    public int getGridRows() {
        return gsm.getGameInProgress().getGridRows();
    }

    public void setgrid(int g[][]) {
        gsm.getGameInProgress().setgrid(g);
    }

    public int[][] getgrid() {
        return gsm.getGameInProgress().getgrid();
    }

    public void StartUI() {
        mainPane = new BorderPane();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        props.addProperty(SokobanPropertyType.INSETS, "5");
        paneWidth = Integer.parseInt(props
                .getProperty(SokobanPropertyType.WINDOW_WIDTH));
        paneHeigth = Integer.parseInt(props
                .getProperty(SokobanPropertyType.WINDOW_HEIGHT));
        mainPane.resize(paneHeigth, paneHeigth);
        int insetsvalue = Integer.parseInt(props
                .getProperty(SokobanPropertyType.INSETS));
        marginlessInsets = new Insets(insetsvalue);
        mainPane.setPadding(marginlessInsets);
        initSplashScreen();
    }

    public void initSplashScreen() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String splashScreenImagePath = props.getProperty(SokobanPropertyType.SPLASH_SCREEN_IMAGE_NAME);

        // INIT THE SPLASH SCREEN CONTROLS
        splashScreenPane = new StackPane();

        Image splashScreenImage = loadImage(splashScreenImagePath);
        splashScreenImageView = new ImageView(splashScreenImage);

        splashScreenImageLabel = new Label();
        splashScreenImageLabel.setGraphic(splashScreenImageView);
        splashScreenPane.getChildren().add(splashScreenImageLabel);

        // GET THE LIST OF LEVEL OPTIONS
        ArrayList<String> levels = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_OPTIONS);
        ArrayList<String> levelImages = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_IMAGE_NAMES);

        levelSelectionPane = new GridPane();
        levelSelectionPane.setAlignment(Pos.CENTER);
        // add key listener
        levelButtons = new ArrayList<Button>();
        for (int i = 0; i < levels.size(); i++) {
            // GET THE LIST OF LEVEL OPTIONS
            String levelImageName = levelImages.get(i);
            Image levelImage = loadImage(levelImageName);
            ImageView levelImageView = new ImageView(levelImage);
            levelImageView.setFitWidth(200.0);
            levelImageView.setFitHeight(200.0);
            // AND BUILD THE BUTTON
            Button levelButton = new Button();
            levelButton.setGraphic(levelImageView);
            String levelstate = "level" + Integer.toString(i + 1) + ".sok";
            LevelState = i + 1;
            // CONNECT THE BUTTON TO THE EVENT HANDLER
            levelButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        eventHandler.respondToSelectGameLevel(levelstate);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            levelButtons.add(levelButton);
            levelSelectionPane.add(levelButton, i % 3, i / 3);
        }
        splashScreenPane.getChildren().add(levelSelectionPane);
        mainPane.setCenter(splashScreenPane);
    }

    /**
     * This method initializes the language-specific game controls, which
     * includes the three primary game screens.
     *
     * @throws IOException
     */
    public void initSokobanUI() throws IOException {
        // FIRST REMOVE THE SPLASH SCREEN
        mainPane.getChildren().clear();
        // GET THE UPDATED TITLE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String title = props.getProperty(SokobanPropertyType.GAME_TITLE_TEXT);
        getPrimaryStage().setTitle(title);
        // THEN ADD ALL THE STUFF WE MIGHT NOW USE
        initNorthToolbar();
        // OUR WORKSPACE WILL STORE EITHER THE GAME, STATS,
        // OR HELP UI AT ANY ONE TIME
        initGameScreen();
        initStatsPane();
        // WE'LL START OUT WITH THE GAME SCREEN
        eventHandler.respondToSwitchScreenRequest(SokobanUIState.PLAY_GAME_STATE);
    }

    /**
     * This function initializes all the controls that go in the north toolbar.
     */
    private void initNorthToolbar() {
        // MAKE THE NORTH TOOLBAR, WHICH WILL HAVE FOUR BUTTONS
        northToolbar = new HBox();
        northToolbar.setStyle("-fx-background-color:lightgray");
        northToolbar.setAlignment(Pos.CENTER);
        northToolbar.setPadding(marginlessInsets);
        northToolbar.setSpacing(10.0);

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        props.addProperty(SokobanPropertyType.BACK_IMG_NAME, "back.jpg");
        props.addProperty(SokobanPropertyType.STATS_IMG_NAME, "stats.jpg");
        props.addProperty(SokobanPropertyType.UNDO_IMG_NAME, "undo.jpg");
        props.addProperty(SokobanPropertyType.TIME_IMG_NAME, "time.jpg");

        // MAKE AND INIT THE GAME BUTTON
        backButton = initToolbarButton(northToolbar, SokobanPropertyType.BACK_IMG_NAME);
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (gsm.getGameInProgress().gameState != 1) {
                    gsm.getGameInProgress().gameState = 0;
                }
                gsm.endGame();
                try {
                    eventHandler
                            .respondToSwitchScreenRequest(SokobanUIState.SPLASH_SCREEN_STATE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // MAKE AND INIT THE STATS BUTTON
        statsButton = initToolbarButton(northToolbar, SokobanPropertyType.STATS_IMG_NAME);
        statsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (gsm.getGameInProgress().gameState != 1) {
                    gsm.getGameInProgress().gameState = 0;
                }
                gsm.endGame();
                try {
                    eventHandler
                            .respondToSwitchScreenRequest(SokobanUIState.VIEW_STATS_STATE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // MAKE AND INIT THE HELP BUTTON
        undoButton = initToolbarButton(northToolbar, SokobanPropertyType.UNDO_IMG_NAME);
        // setTooltip(helpButton, SokobanPropertyType.HELP_TOOLTIP);
        undoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eventHandler.respondToUnDoButtonClick();
            }
        });

        // MAKE AND INIT THE EXIT BUTTON
        timeButton = initToolbarButton(northToolbar, SokobanPropertyType.TIME_IMG_NAME);
        timeButton.setFont(new Font("Arial", 30));

        // AND NOW PUT THE NORTH TOOLBAR IN THE FRAME
        mainPane.setTop(northToolbar);
    }

    /**
     * This method helps to initialize buttons for a simple toolbar.
     *
     * @param toolbar The toolbar for which to add the button.
     *
     * @param prop The property for the button we are building. This will
     * dictate which image to use for the button.
     *
     * @return A constructed button initialized and added to the toolbar.
     */
    private Button initToolbarButton(HBox toolbar, SokobanPropertyType prop) {
        // GET THE NAME OF THE IMAGE, WE DO THIS BECAUSE THE
        // IMAGES WILL BE NAMED DIFFERENT THINGS FOR DIFFERENT LANGUAGES
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imageName = props.getProperty(prop);

        // LOAD THE IMAGE
        Image image = loadImage(imageName);
        ImageView imageIcon = new ImageView(image);

        // MAKE THE BUTTON
        Button button = new Button();
        button.setGraphic(imageIcon);
        button.setPadding(marginlessInsets);

        // PUT IT IN THE TOOLBAR
        toolbar.getChildren().add(button);

        // AND SEND BACK THE BUTTON
        return button;
    }

    /**
     * The workspace is a panel that will show different screens depending on
     * the user's requests.
     */
    public void initGameScreen() {
        gamePane = new StackPane();
        gridRenderer = new GridRenderer();
        gamePane.getChildren().addAll(gridRenderer);
    }

    public void initStatsPane() {
        if (labels.size() == 0) {
            String headText = "Level#\t\tGame played\t\tWons\t\tLosts\t\tWinning percentage\t\tFastest";
            Label head = new Label(headText);
            head.setFont(new Font("Arial", 15));

            for (int i = 1; i < 8; i++) { // all levels on the left
                String initString = String.format("Level%d\t\t0\t\t\t\t0\t\t\t0\t\t\t0.00%%\t\t\t\t\t00:00", i);
                Label label = new Label(initString);
                label.setFont(new Font("Arial", 15));
                statsPane.add(label, 1, i + 1);

                labels.add(label);
            }
            statsPane.setVgap(6);
            statsPane.setHgap(10);
            statsPane.add(head, 1, 1, 6, 1);
            clearButton = new Button("Clear Records");
            clearButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    int[] played = new int[7];
                    int[] wins = new int[7];
                    int[] loss = new int[7];
                    int[] fast = new int[7];
                    try {
                        fileLoader.saveStat(played, wins, loss, fast);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    gsm.gamesHistory = new ArrayList<>();
                    try {
                        updateStat();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            statsPane.add(clearButton, 1, 9);
        }
    }

    public void updateStat() throws IOException {
        int[] played = gsm.getGamesPlayed();
        int[] wins = gsm.getWins();
        int[] loss = gsm.getLosses();
        int[] fast = gsm.getFastestWin();
        for (int i = 1; i < 8; i++) {
            Label label = labels.get(i - 1);
            String data = String.format("Level%d\t\t%d\t\t\t\t%d\t\t\t%d\t\t\t%.2f%%\t\t\t\t\t%02d:%02d",
                    i,
                    played[i - 1],
                    wins[i - 1],
                    loss[i - 1],
                    (played[i - 1] == 0 ? 0 : wins[i - 1] / (double) played[i - 1] * 100),
                    fast[i - 1] / 60, fast[i - 1] % 60);
            label.setText(data);
        }
        fileLoader.saveStat(played, wins, loss, fast);
        gsm.gamesHistory = new ArrayList<>();
    }

    public void ClearCanvas() {
        gamePane.getChildren().remove(gridRenderer);
        gridRenderer = new GridRenderer();
        gamePane.getChildren().addAll(gridRenderer);
    }

    public Image loadImage(String imageName) {
        Image img = new Image(ImgPath + imageName);
        return img;
    }

    /**
     * This function selects the UI screen to display based on the uiScreen
     * argument. Note that we have 3 such screens: game, stats, and help.
     *
     * @param uiScreen The screen to be switched to.
     * @throws IOException
     */
    public void changeWorkspace(SokobanUIState uiScreen) throws IOException {
        switch (uiScreen) {
            case SPLASH_SCREEN_STATE:
                eventHandler.getTimeline().stop();
                mainPane.getChildren().clear();
                mainPane.setCenter(splashScreenPane);
                break;
            case PLAY_GAME_STATE:
                mainPane.setCenter(gamePane);
                getPrimaryStage().getScene().setOnKeyPressed(
                        new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent t) {
                                eventHandler.respondToKeyEvent(t);
                            }
                        });
                break;
            case VIEW_STATS_STATE:
                updateStat();
                mainPane.setCenter(statsPane);
                getPrimaryStage().getScene().setOnKeyPressed(null);
                break;
            default:
        }
    }

    /**
     * This class renders the grid for us. Note that we also listen for mouse
     * clicks on it.
     */
    class GridRenderer extends Canvas {

        // PIXEL DIMENSIONS OF EACH CELL
        int cellWidth;
        int cellHeight;

        // images
        Image wallImage = new Image("file:images/wall.png");
        Image boxImage = new Image("file:images/box.png");
        Image placeImage = new Image("file:images/place.png");
        Image sokobanImage = new Image("file:images/Sokoban.png");

        /**
         * Default constructor.
         */
        public GridRenderer() {
            this.setWidth(700);
            this.setHeight(650);
        }

        public void repaint() {
            gc = this.getGraphicsContext2D();
            gc.clearRect(0, 0, this.getWidth(), this.getHeight());

            // CALCULATE THE GRID CELL DIMENSIONS
            double w = this.getWidth() / getGridColumns();
            double h = this.getHeight() / getGridRows();

            gc = this.getGraphicsContext2D();

            // NOW RENDER EACH CELL
            int x = 0, y = 0;
            int sokobanx = 0;
            int sokobany = 0;
            int grid[][] = getgrid();
            for (int i = 0; i < getGridColumns(); i++) {
                y = 0;
                for (int j = 0; j < getGridRows(); j++) {
                    // DRAW THE CELL
                    gc.setFill(Color.LIGHTBLUE);
                    gc.strokeRoundRect(x, y, w, h, 10, 10);

                    switch (grid[i][j]) {
                        case 0:
                            gc.strokeRoundRect(x, y, w, h, 10, 10);
                            break;
                        case 1:
                            gc.drawImage(wallImage, x, y, w, h);
                            break;
                        case 2:
                            gc.drawImage(boxImage, x, y, w, h);
                            break;
                        case 3:
                            gc.drawImage(placeImage, x, y, w, h);
                            break;
                        case 4:
                            gc.drawImage(sokobanImage, x, y, w, h);
                            sokobanx = i;
                            sokobany = j;
                            break;
                    }

                    // THEN RENDER THE TEXT
                    String numToDraw = "" + grid[i][j];
                    double xInc = (w / 2) - (10 / 2);
                    double yInc = (h / 2) + (10 / 4);
                    x += xInc;
                    y += yInc;
                    gc.setFill(Color.RED);
                    //gc.fillText(numToDraw, x, y);
                    x -= xInc;
                    y -= yInc;

                    // ON TO THE NEXT ROW
                    y += h;
                }
                // ON TO THE NEXT COLUMN
                x += w;
            }
            int i = sokobanx;
            int j = sokobany;
            if (grid[i][j - 1] == 2) {
                direction[0] = true;
            } else {
                direction[0] = false;
            }
            if (grid[i][j + 1] == 2) {
                direction[1] = true;
            } else {
                direction[1] = false;
            }
            if (grid[i - 1][j] == 2) {
                direction[2] = true;
            } else {
                direction[2] = false;
            }
            if (grid[i + 1][j] == 2) {
                direction[3] = true;
            } else {
                direction[3] = false;
            }
        }

        public void repaint(String direct) {
            gc = this.getGraphicsContext2D();
            gc.clearRect(0, 0, this.getWidth(), this.getHeight());

            // CALCULATE THE GRID CELL DIMENSIONS
            double w = this.getWidth() / getGridColumns();
            double h = this.getHeight() / getGridRows();

            gc = this.getGraphicsContext2D();

            // NOW RENDER EACH CELL
            int x = 0, y = 0;
            int sokobanx = 0;
            int sokobany = 0;
            int grid[][] = getgrid();
            for (int i = 0; i < getGridColumns(); i++) {
                y = 0;
                for (int j = 0; j < getGridRows(); j++) {
                    // DRAW THE CELL
                    gc.setFill(Color.LIGHTBLUE);
                    gc.strokeRoundRect(x, y, w, h, 10, 10);

                    switch (grid[i][j]) {
                        case 0:
                            gc.strokeRoundRect(x, y, w, h, 10, 10);
                            break;
                        case 1:
                            gc.drawImage(wallImage, x, y, w, h);
                            break;
                        case 2:
                            if (direct == "") {
                                gc.drawImage(boxImage, x, y, w, h);

                            } else {
                                moveAnimation(direct, gc, i, j, x, y, w, h);
                            }
                            break;
                        case 3:
                            gc.drawImage(placeImage, x, y, w, h);
                            break;
                        case 4:
                            if (direct == "") {
                                gc.drawImage(sokobanImage, x, y, w, h);

                            } else {
                                moveAnimation(direct, gc, i, j, x, y, w, h);
                            }
                            sokobanx = i;
                            sokobany = j;

                            break;
                    }

                    // THEN RENDER THE TEXT
                    double xInc = (w / 2) - (10 / 2);
                    double yInc = (h / 2) + (10 / 4);
                    x += xInc;
                    y += yInc;
                    x -= xInc;
                    y -= yInc;

                    // ON TO THE NEXT ROW
                    y += h;
                }
                // ON TO THE NEXT COLUMN
                x += w;
            }
            int i = sokobanx;
            int j = sokobany;
            if (grid[i][j - 1] == 2) {
                direction[0] = true;
            } else {
                direction[0] = false;
            }
            if (grid[i][j + 1] == 2) {
                direction[1] = true;
            } else {
                direction[1] = false;
            }
            if (grid[i - 1][j] == 2) {
                direction[2] = true;
            } else {
                direction[2] = false;
            }
            if (grid[i + 1][j] == 2) {
                direction[3] = true;
            } else {
                direction[3] = false;
            }
        }

        public void moveAnimation(String direct, GraphicsContext gc, int i, int j, int x, int y, double w, double h) {
            DoubleProperty a = new SimpleDoubleProperty();
            DoubleProperty b = new SimpleDoubleProperty();
            DoubleProperty c = new SimpleDoubleProperty();
            int grid[][] = getgrid();
            switch (direct) {
                case "LEFT": {
                    if ((grid[i][j] == 2 && grid[i + 1][j] == 4 && direction[2])
                            || grid[i][j] == 4) {
                        Timeline timeline = new Timeline(
                                new KeyFrame(
                                        Duration.seconds(0),
                                        new KeyValue(a, x + w),
                                        new KeyValue(b, x + 2 * w),
                                        new KeyValue(c, 0)),
                                new KeyFrame(Duration.seconds(AnimaLength),
                                        new KeyValue(a, x),
                                        new KeyValue(b, x + w),
                                        new KeyValue(c, w)));
                        timeline.setCycleCount(1);
                        timeline.setAutoReverse(false);
                        int sign = grid[i][j];
                        int yy = y;
                        AnimationTimer timer = new AnimationTimer() {
                            @Override
                            public void handle(long now) {
                                if (sign == 4) {
                                    gc.drawImage(sokobanImage, a.doubleValue(), yy, w, h);
                                    gc.setFill(Color.WHITE);
                                    gc.fillRect(b.doubleValue(), yy, c.doubleValue(), h);
                                } else if (sign == 2) {
                                    gc.drawImage(boxImage, a.doubleValue(), yy, w, h);
                                }
                            }
                        };
                        timer.start();
                        timeline.play();
                    } else {
                        gc.drawImage(boxImage, x, y, w, h);
                    }
                    break;
                }
                case "RIGHT": {
                    if ((grid[i][j] == 2 && grid[i - 1][j] == 4 && direction[3])
                            || grid[i][j] == 4) {
                        Timeline timeline = new Timeline(
                                new KeyFrame(
                                        Duration.seconds(0),
                                        new KeyValue(a, x - w),
                                        new KeyValue(b, x - w),
                                        new KeyValue(c, 0)),
                                new KeyFrame(
                                        Duration.seconds(AnimaLength),
                                        new KeyValue(a, x),
                                        new KeyValue(b, x - w),
                                        new KeyValue(c, w)));
                        timeline.setCycleCount(1);
                        timeline.setAutoReverse(false);
                        int sign = grid[i][j];
                        int yy = y;
                        AnimationTimer timer = new AnimationTimer() {
                            @Override
                            public void handle(long now) {
                                if (sign == 4) {
                                    gc.drawImage(sokobanImage, a.doubleValue(), yy, w, h);
                                    gc.setFill(Color.WHITE);
                                    gc.fillRect(b.doubleValue(), yy, c.doubleValue(), h);
                                } else if (sign == 2) {
                                    gc.drawImage(boxImage, a.doubleValue(), yy, w, h);
                                }
                            }
                        };
                        timer.start();
                        timeline.play();
                    } else {
                        gc.drawImage(boxImage, x, y, w, h);
                    }
                    break;
                }
                case "UP": {
                    if ((grid[i][j] == 2 && grid[i][j + 1] == 4 && direction[0])
                            || grid[i][j] == 4) {
                        Timeline timeline = new Timeline(
                                new KeyFrame(
                                        Duration.seconds(0),
                                        new KeyValue(a, y + h),
                                        new KeyValue(b, y + 2 * h),
                                        new KeyValue(c, 0)),
                                new KeyFrame(
                                        Duration.seconds(AnimaLength),
                                        new KeyValue(a, y),
                                        new KeyValue(b, y + h),
                                        new KeyValue(c, h)));
                        timeline.setCycleCount(1);
                        timeline.setAutoReverse(false);
                        int sign = grid[i][j];
                        int xx = x;
                        AnimationTimer timer = new AnimationTimer() {
                            @Override
                            public void handle(long now) {
                                if (sign == 4) {
                                    gc.drawImage(sokobanImage, xx, a.doubleValue(), w, h);
                                    gc.setFill(Color.WHITE);
                                    gc.fillRect(xx, b.doubleValue(), w, c.doubleValue());
                                } else if (sign == 2) {
                                    gc.drawImage(boxImage, xx, a.doubleValue(), w, h);
                                }
                            }
                        };
                        timer.start();
                        timeline.play();
                    } else {
                        gc.drawImage(boxImage, x, y, w, h);
                    }
                    break;
                }
                case "DOWN": {
                    if ((grid[i][j] == 2 && grid[i][j - 1] == 4 && direction[1])
                            || grid[i][j] == 4) {
                        Timeline timeline = new Timeline(
                                new KeyFrame(
                                        Duration.seconds(0),
                                        new KeyValue(a, y - h),
                                        new KeyValue(b, y - h),
                                        new KeyValue(c, 0)),
                                new KeyFrame(
                                        Duration.seconds(AnimaLength),
                                        new KeyValue(a, y),
                                        new KeyValue(b, y - h),
                                        new KeyValue(c, h)));
                        timeline.setCycleCount(1);
                        timeline.setAutoReverse(false);
                        int sign = grid[i][j];
                        int xx = x;
                        AnimationTimer timer = new AnimationTimer() {
                            @Override
                            public void handle(long now) {
                                if (sign == 4) {
                                    gc.drawImage(sokobanImage, xx, a.doubleValue(), w, h);
                                    gc.setFill(Color.WHITE);
                                    gc.fillRect(xx, b.doubleValue(), w, c.doubleValue());
                                } else if (sign == 2) {
                                    gc.drawImage(boxImage, xx, a.doubleValue(), w, h);
                                }
                            }
                        };
                        timer.start();
                        timeline.play();
                    } else {
                        gc.drawImage(boxImage, x, y, w, h);
                    }
                    break;
                }
            }
        }
    }
}
