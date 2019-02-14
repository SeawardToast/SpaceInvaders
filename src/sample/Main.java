package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application {

    //sounds
    String laserGunSound = "laser_gun_shot.wav";
    Media laserGun = new Media(new File(laserGunSound).toURI().toString());

    String laserGunSound2 = "laser_sound_2.mp3";
    Media laserGun2 = new Media(new File(laserGunSound2).toURI().toString());

    String destroySound = "ship_destroy.wav";
    Media shipDestroy = new Media(new File(destroySound).toURI().toString());

    private Pane root = new Pane();

    private double t = 0.0;

    private Sprite player = new Sprite(300, 750, 40, 40, "player", Color.BLUE);

    private Parent createContent() {
        root.setPrefSize(600, 800);
        root.getChildren().add(player);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
        nextLevel();

        return root;
    }

    private void nextLevel(){
        for(int i = 0; i < 5; i++) {
            Sprite s = new Sprite(90 + i*100, 150, 30, 30, "enemy", Color.RED);
            root.getChildren().add(s);
        }
    }

    private List<Sprite> sprites() {
        return root.getChildren().stream().map(n -> (Sprite)n).collect(Collectors.toList());
    }

    private void update(){
        t += 0.016;
        sprites().forEach(s -> {
            switch (s.type) {
                case "enemybullet" :
                    s.moveDown();
                    if (s.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        player.dead = true;
                        s.dead = true;
                        MediaPlayer mediaPlayer = new MediaPlayer(shipDestroy);
                        mediaPlayer.play();
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                    }
                    break;
                case "playerbullet" :
                    s.moveUp();

                    sprites().stream().filter(e -> e.type.equals("enemy")).forEach(enemy -> {
                        if (s.getBoundsInParent().intersects(enemy.getBoundsInParent())){
                            enemy.dead = true;
                            s.dead = true;
                            MediaPlayer mediaPlayer = new MediaPlayer(shipDestroy);
                            mediaPlayer.play();
                            mediaPlayer.seek(mediaPlayer.getStartTime());
                        }
                    });
                    break;
                case "enemy" :
                    if (t > 2 && Math.random() < 0.3) {
                            shoot(s);
                        }
                    break;
            }
        });

        root.getChildren().removeIf(n -> {
            Sprite s = (Sprite) n;
            return s.dead;
        });

        if (t > 2) {
            t=0;
        }
    }

    private void shoot(Sprite shooter){
        Sprite s = new Sprite((int) shooter.getTranslateX()+12, (int) shooter.getTranslateY()+30, 5, 20, shooter.type + "bullet", Color.BLACK);
        root.getChildren().add(s);
        if(shooter.type.equals("enemy")) {
            MediaPlayer mediaPlayer = new MediaPlayer(laserGun2);
            mediaPlayer.play();
            mediaPlayer.seek(mediaPlayer.getStartTime());
        } else if (shooter.type.equals("player")) {
            MediaPlayer mediaPlayer = new MediaPlayer(laserGun);
            mediaPlayer.play();
            mediaPlayer.seek(mediaPlayer.getStartTime());
        }

    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Space Invaders");
        Scene scene = new Scene(createContent());

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case A :
                    player.moveLeft();
                    break;
                case D :
                    player.moveRight();
                    break;
                case SPACE:
                    if(!player.dead)
                        shoot(player);
                    break;
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static class Sprite extends Rectangle {
        boolean dead = false;
        final String type;

        Sprite(int x, int y, int w, int h, String type, Color color) {
            super(w, h, color);

            this.type = type;
            setTranslateX(x);
            setTranslateY(y);
        }

        void moveLeft() {
            setTranslateX(getTranslateX() - 5);
        }

        void moveRight() {
            setTranslateX(getTranslateX() + 5);
        }

        void moveUp() {
            setTranslateY(getTranslateY() - 5);
        }

        void moveDown() {
            setTranslateY(getTranslateY() + 5);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
