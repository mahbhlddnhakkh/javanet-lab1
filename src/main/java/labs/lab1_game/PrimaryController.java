package labs.lab1_game;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;

public class PrimaryController {

    @FXML
    private HBox MainFrame;
    
    @FXML
    private VBox MainGameFrame;
    
    @FXML
    private HBox ButtonsFrame;
    
    @FXML
    private Pane GamePane;
    
    @FXML
    private VBox ScoreFrame;
    
    @FXML
    private Label PlayerScore;
    
    @FXML
    private Label PlayerShots;
    
    @FXML
    private ImageView Finger;
    
    @FXML
    private Circle PlayerCircle;
    
    @FXML
    private Circle Target1Circle;
    
    private double target1Direction = 1;
    
    @FXML
    private Line Target1Line;
    
    @FXML
    private Circle Target2Circle;
    
    @FXML
    private Line Target2Line;
    
    private double target2Direction = 1;
    
    @FXML
    private Polygon ArrowPoly;
    
    //@FXML
    //private Circle TmpHitbox;
    
    @FXML
    private Button PauseButton;
    
    
    private Thread animationThread = null;
    
    private Boolean shootingState = false;
    
    private Boolean gameRunning = false;
    
    private Boolean isPaused = false;
    
    private void initialize_dynamic_pos() {
        Target1Circle.setTranslateX(ButtonsFrame.getPrefWidth()*0.7);
        Target1Circle.setTranslateY(GamePane.getPrefHeight() / 2 - Target1Circle.getRadius() / 2);
        Target1Line.setStartX(Target1Circle.getTranslateX());
        Target1Line.setStartY(7);
        Target1Line.setEndX(Target1Circle.getTranslateX());
        Target1Line.setEndY(GamePane.getPrefHeight() - 11);
        
        Target2Circle.setTranslateX(ButtonsFrame.getPrefWidth()*0.9);
        Target2Circle.setTranslateY(GamePane.getPrefHeight() / 2 - Target2Circle.getRadius() / 2);
        Target2Line.setStartX(Target2Circle.getTranslateX());
        Target2Line.setStartY(Target1Line.getStartY());
        Target2Line.setEndX(Target2Circle.getTranslateX());
        Target2Line.setEndY(Target1Line.getEndY());
        
        ArrowPoly.setTranslateX(0);
        ArrowPoly.setVisible(false);
        
        target1Direction = 1;
        target2Direction = 1;
    }
    
    @FXML
    public void initialize() throws IOException {
        MainFrame.setPrefWidth(App.win_w);
        ScoreFrame.setPrefWidth(ScoreFrame.getPrefWidth()*1.25);
        ButtonsFrame.setPrefHeight(48);
        ButtonsFrame.setPrefWidth(App.win_w - ScoreFrame.getPrefWidth());
        GamePane.setPrefWidth(ButtonsFrame.getPrefWidth());
        GamePane.setPrefHeight(App.win_h - ButtonsFrame.getPrefHeight());
        String cssTranslate = "-fx-border-color: black;\n" +
                           "-fx-border-insets: 5;\n" +
                           "-fx-border-width: 1;\n";
        MainGameFrame.setStyle(cssTranslate);
        ButtonsFrame.setStyle(cssTranslate);
        GamePane.setStyle(cssTranslate);
        ScoreFrame.setStyle(cssTranslate);
        
        resetScore();
        
        PlayerCircle.setRadius(App.player_radius);
        PlayerCircle.setTranslateX(App.player_radius * 1.5);
        PlayerCircle.setTranslateY(GamePane.getPrefHeight() / 2 - App.player_radius / 2);
        PlayerCircle.setFill(new ImagePattern(getImage("Normal.png")));
        
        Finger.setImage(getImage("finger.png"));
        //Finger.setScaleX(0.25);
        //Finger.setScaleY(0.25);
        //Finger.setScaleZ(0.25);
        Finger.setFitWidth(57);
        Finger.setTranslateY(PlayerCircle.getTranslateY()-8);
        Finger.setTranslateX(App.player_radius*3);
        
        Target1Circle.setRadius(App.target_radius);
        Target1Circle.setFill(new ImagePattern(getImage("EasyDemon.png")));
        
        Target2Circle.setRadius(App.target_radius / 2);
        Target2Circle.setFill(new ImagePattern(getImage("ExtremeDemon.png")));
        
        initialize_dynamic_pos();
        
        Double[] arrow_line_start_end = new Double[] {
            PlayerCircle.getTranslateX() + PlayerCircle.getRadius() + 70, PlayerCircle.getTranslateY(),
            PlayerCircle.getTranslateX() + PlayerCircle.getRadius() + 70 + App.arrow_length, PlayerCircle.getTranslateY()
        };

        //TmpHitbox.setRadius(App.arrow_hitbox_radius);
        //TmpHitbox.setTranslateX(arrow_line_start_end[2] + App.arrow_hitbox_radius / 2);
        //TmpHitbox.setTranslateY(arrow_line_start_end[3]);
        ArrowPoly.getPoints().clear();
        ArrowPoly.getPoints().addAll(new Double[] {
            arrow_line_start_end[0], arrow_line_start_end[1] - App.arrow_width / 2,
            arrow_line_start_end[2], arrow_line_start_end[3] - App.arrow_width / 2,
            // top left corner
            arrow_line_start_end[2], arrow_line_start_end[3] - App.arrow_hitbox_radius * Math.sqrt(0.75),
            arrow_line_start_end[2] + App.arrow_hitbox_radius * 1.5, arrow_line_start_end[3],
            // down left corner
            arrow_line_start_end[2], arrow_line_start_end[3] + App.arrow_hitbox_radius * Math.sqrt(0.75),
            arrow_line_start_end[2], arrow_line_start_end[3] + App.arrow_width / 2,
            arrow_line_start_end[0], arrow_line_start_end[1] + App.arrow_width / 2,
        });
    }
    
    private static Image getImage(String path) throws IOException {
        return new Image(PrimaryController.class.getResource(path).toString());
    }
    
    @FXML
    private void startGame() {
        if (animationThread != null || gameRunning) {
            initialize_dynamic_pos();
            resetScore();
            return;
        }
        gameRunning = true;
        shootingState = false;
        animationThread = new Thread( () -> {
            while(gameRunning) {
                if (isPaused) {
                    try {
                        synchronized(this) {
                            this.wait();
                        }
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                Platform.runLater( () -> {
                    if (shootingState) {
                        double arrowHitboxCenterX = ArrowPoly.getTranslateX() + ArrowPoly.getPoints().get(2) + App.arrow_hitbox_radius / 2;
                        double dx = arrowHitboxCenterX - Target1Circle.getTranslateX();
                        double dy = PlayerCircle.getTranslateY() - Target1Circle.getTranslateY();
                        if (Math.sqrt(dx*dx + dy*dy) <=  App.arrow_hitbox_radius + Target1Circle.getRadius()) {
                            registerHit(1);
                        }
                        dx = arrowHitboxCenterX - Target2Circle.getTranslateX();
                        dy = PlayerCircle.getTranslateY() - Target2Circle.getTranslateY();
                        if (shootingState && (Math.sqrt(dx*dx + dy*dy) <=  App.arrow_hitbox_radius + Target2Circle.getRadius())) {
                            registerHit(2);
                        }
                        if (shootingState && (arrowHitboxCenterX + App.arrow_hitbox_radius > GamePane.getPrefWidth())) {
                            registerHit(0);
                        }
                        if (shootingState) {
                            ArrowPoly.setTranslateX(ArrowPoly.getTranslateX() + App.arrow_speed);
                        }
                    }
                    Target1Circle.setTranslateY(Target1Circle.getTranslateY() + App.target_speed * target1Direction);
                    if (Target1Circle.getTranslateY() > Target1Line.getEndY() - Target1Circle.getRadius()) {
                        target1Direction = -1;
                        Target1Circle.setTranslateY(Target1Line.getEndY() - Target1Circle.getRadius());
                    }
                    if (Target1Circle.getTranslateY() < Target1Line.getStartY() + Target1Circle.getRadius()) {
                        target1Direction = 1;
                        Target1Circle.setTranslateY(Target1Line.getStartY() + Target1Circle.getRadius());
                    }
                        
                    Target2Circle.setTranslateY(Target2Circle.getTranslateY() + App.target_speed * 2 * target2Direction);
                    if (Target2Circle.getTranslateY() > Target2Line.getEndY() - Target2Circle.getRadius()) {
                        target2Direction = -1;
                            Target2Circle.setTranslateY(Target2Line.getEndY() - Target2Circle.getRadius());
                    }
                    if (Target2Circle.getTranslateY() < Target2Line.getStartY() + Target2Circle.getRadius()) {
                        target2Direction = 1;
                            Target2Circle.setTranslateY(Target2Line.getStartY() + Target2Circle.getRadius());
                    }
                } );
                try {
                    Thread.sleep(App.sleep_time);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            initialize_dynamic_pos();
            resetScore();
        });
        animationThread.start();
    }
    
    @FXML
    private void shoot() {
        if (animationThread == null || shootingState) return;
        shootingState = true;
        ArrowPoly.setVisible(true);
        PlayerShots.setText("" + (Integer.parseInt(PlayerShots.getText()) + 1));
    }
    
    private void registerHit(int score) {
        if (score != 0) PlayerScore.setText("" + (Integer.parseInt(PlayerScore.getText()) + score));
        shootingState = false;
        ArrowPoly.setVisible(false);
        ArrowPoly.setTranslateX(0);
    }
    
    private void resetScore() {
        PlayerScore.setText("0");
        PlayerShots.setText("0");
    }
    
    @FXML
    private void stopGame() {
        gameRunning = false;
        animationThread = null;
    }
    
    @FXML
    private void pauseGame() {
        if (animationThread == null || !gameRunning) return;
        isPaused = !isPaused;
        if (isPaused) {
            // we pressed "Pause"
            PauseButton.setText("Продолжить");
        } else {
            // we pressed "Unpause"
            PauseButton.setText("Пауза");
            synchronized(this) {
                notifyAll();
            }
        }
    }
    
}
