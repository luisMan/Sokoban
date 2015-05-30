package sokoban.ui;

import java.io.DataInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;

import application.Main.SokobanPropertyType;

import properties_manager.PropertiesManager;

import sokoban.file.SokobanFileLoader;
import sokoban.game.SokobanGameStateManager;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SokobanEventHandler {

    private SokobanUI ui;
    private Stack<int[][]> MoveRecorder;
    private Timeline timeline;
    public long timefinal;

    public Timeline getTimeline() {
        return timeline;
    }

    /**
     * Constructor that simply saves the ui for later.
     *
     * @param initUI
     */
    public SokobanEventHandler(SokobanUI initUI) {
        ui = initUI;
        MoveRecorder = new Stack<int[][]>();
    }

    /**
     * This method responds to when the user wishes to switch between the Game,
     * Stats, and Help screens.
     *
     * @param uiState The ui state, or screen, that the user wishes to switch
     * to.
     * @throws IOException
     */
    public void respondToSwitchScreenRequest(SokobanUI.SokobanUIState uiState) throws IOException {
        ui.changeWorkspace(uiState);
    }

    /**
     * This method responds to when the user presses the new game method.
     */
    public void respondToNewGameRequest() {
        SokobanGameStateManager gsm = ui.getGSM();
    }

    public void respondToSelectGameLevel(String levelstate) throws IOException {
        MoveRecorder.clear();
        ui.initSokobanUI();
        SokobanFileLoader fileloader = ui.getFileLoader();
        DataInputStream dis = fileloader.readLevelFile(levelstate);
        try {
            // NOTE THAT WE NEED TO LOAD THE DATA IN THE SAME
            // ORDER AND FORMAT AS WE SAVED IT
            // FIRST READ THE GRID DIMENSIONS
            int initGridColumns = dis.readInt();
            int initGridRows = dis.readInt();
            int[][] newGrid = new int[initGridColumns][initGridRows];

            // AND NOW ALL THE CELL VALUES
            for (int i = 0; i < initGridColumns; i++) {
                for (int j = 0; j < initGridRows; j++) {
                    newGrid[i][j] = dis.readInt();
                }
            }

            ArrayList<SokobanUI.Position> terminals = new ArrayList<SokobanUI.Position>();

            for (int i = 0; i < newGrid.length; i++) {
                for (int j = 0; j < newGrid[0].length; j++) {
                    if (newGrid[i][j] == 3) {
                        terminals.add(ui.new Position(i, j));
                    }
                }
            }

            ui.gettimeButton().setText("00:00:00");
            Calendar cal = Calendar.getInstance();
            long startTime = cal.getTimeInMillis();

            KeyFrame timer = new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    long duration = (Calendar.getInstance().getTimeInMillis() - startTime) / 1000;
                    timefinal = duration;
                    String time = String.format("%02d:%02d:%02d", duration / 3600, duration / 60, duration % 60);
                    ui.gettimeButton().setText(time);
                }
            }
            );

            timeline = new Timeline(timer);
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            ui.getGSM().startNewGame(levelstate);
            ui.getGSM().getGameInProgress().setgrid(newGrid);
            ui.getGSM().getGameInProgress().setGridColumns(initGridColumns);
            ui.getGSM().getGameInProgress().setGridRows(initGridRows);
            ui.getgridRenderer().repaint();
            ui.setTerminals(terminals);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * This method responds to when the user requests to exit the application.
     *
     * @param window The window that the user has requested to close.
     */
    public void respondToExitRequest(Stage primaryStage) {
        String options[] = new String[]{"Yes", "No"};
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        options[0] = props.getProperty(SokobanPropertyType.DEFAULT_YES_TEXT);
        options[1] = props.getProperty(SokobanPropertyType.DEFAULT_NO_TEXT);
        String verifyExit = props.getProperty(SokobanPropertyType.DEFAULT_EXIT_TEXT);

        // NOW WE'LL CHECK TO SEE IF LANGUAGE SPECIFIC VALUES HAVE BEEN SET
        if (props.getProperty(SokobanPropertyType.YES_TEXT) != null) {
            options[0] = props.getProperty(SokobanPropertyType.YES_TEXT);
            options[1] = props.getProperty(SokobanPropertyType.NO_TEXT);
            verifyExit = props
                    .getProperty(SokobanPropertyType.EXIT_REQUEST_TEXT);
        }

        // FIRST MAKE SURE THE USER REALLY WANTS TO EXIT
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane exitPane = new BorderPane();
        HBox optionPane = new HBox();
        Button yesButton = new Button(options[0]);
        Button noButton = new Button(options[1]);
        optionPane.setSpacing(10.0);
        optionPane.getChildren().addAll(yesButton, noButton);
        Label exitLabel = new Label(verifyExit);
        exitPane.setCenter(exitLabel);
        exitPane.setBottom(optionPane);
        Scene scene = new Scene(exitPane, 50, 100);
        dialogStage.setScene(scene);
        dialogStage.show();
        // WHAT'S THE USER'S DECISION?
        yesButton.setOnAction(e -> {
            // YES, LET'S EXIT
            System.exit(0);
        });
        noButton.setOnAction(e -> {
            dialogStage.close();
        });
    }

    public void respondToKeyEvent(KeyEvent t) {
        if (ui.getGSM().isGameInProgress() == true) {
            SokobanGameStateManager gsm = ui.getGSM();

            int[][] newgrid = new int[ui.getgrid().length][ui.getgrid()[0].length];
            for (int i = 0; i < newgrid.length; i++) {
                newgrid[i] = ui.getgrid()[i].clone();
            }
            MoveRecorder.push(newgrid);

            if (gsm.getGameInProgress().isValidMove(t, ui.getgrid(),
                    ui.getTerminals())) {
                ui.ClearCanvas();
                ui.getgridRenderer().repaint(t.getCode().toString());

                if (gsm.getGameInProgress().isWon()) {
                    Stage dialogStage = new Stage();
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(ui.getPrimaryStage());
                    BorderPane exitPane = new BorderPane();

                    // HBox optionPane = new HBox();
                    Button okbtn = new Button("OK");

                    Label resultlab = new Label("You Win!");
                    exitPane.setCenter(resultlab);
                    exitPane.setBottom(okbtn);
                    exitPane.setAlignment(okbtn, Pos.CENTER);
                    Scene scene = new Scene(exitPane, 50, 100);
                    dialogStage.setScene(scene);
                    dialogStage.show();
                    // WHAT'S THE USER'S DECISION?
                    okbtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            dialogStage.hide();
                        }
                    });
                    timeline.stop();
                    gsm.getGameInProgress().setplaytime(timefinal);
                    gsm.endGame();
                } else if (gsm.getGameInProgress().isLost()) {
                    Stage dialogStage = new Stage();
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(ui.getPrimaryStage());
                    BorderPane exitPane = new BorderPane();
                    Button okbtn = new Button("OK");
                    Label resultlab = new Label("You Lost!");
                    exitPane.setCenter(resultlab);
                    exitPane.setBottom(okbtn);
                    exitPane.setAlignment(okbtn, Pos.CENTER);
                    Scene scene = new Scene(exitPane, 50, 100);
                    dialogStage.setScene(scene);
                    dialogStage.show();
                    // WHAT'S THE USER'S DECISION?
                    okbtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            dialogStage.hide();
                        }
                    });
                    timeline.stop();
                    gsm.getGameInProgress().setplaytime(timefinal);
                    gsm.endGame();
                }
            } else {
                MoveRecorder.pop();
            }
        }
    }

    public void respondToUnDoButtonClick() {
        if (ui.getGSM().isGameInProgress() == true) {
            if (!MoveRecorder.empty()) {
                ui.setgrid(MoveRecorder.pop());
                ui.ClearCanvas();
                ui.getgridRenderer().repaint();
            }
        }
    }
}
