package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {

    public enum Direction{
        UP,DOWN,LEFT,RIGHT
    }

    public static final int BLOCK_SIZE = 40;
    public static final int APP_H = 20 * BLOCK_SIZE;
    public static final int APP_W = 15 * BLOCK_SIZE;

    private Direction direction = Direction.RIGHT;
    private boolean moved = false;
    private boolean runing = false;

    private Timeline timeline = new Timeline();
    private ObservableList <Node> snake;

    private Parent createContent(){
        Pane root  = new Pane();
        root.setPrefSize(APP_W, APP_H);

        Group snakeBody = new Group();
        snake = snakeBody.getChildren();

        Rectangle food  = new Rectangle(BLOCK_SIZE,BLOCK_SIZE);
        Image image = new Image("sample/Food.png");
        ImagePattern imagePattern = new ImagePattern(image);
        food.setFill(imagePattern);
        food.setTranslateX((int) (Math.random() * (APP_W - BLOCK_SIZE)) / BLOCK_SIZE * BLOCK_SIZE);
        food.setTranslateY((int) (Math.random() * (APP_H - BLOCK_SIZE)) / BLOCK_SIZE * BLOCK_SIZE);

        KeyFrame frame = new KeyFrame(Duration.seconds(0.2), event -> {
            if (!runing)
                return;

            boolean toRemove = snake.size() > 1;

            Node tail = toRemove ? snake.remove(snake.size()-1) : snake.get(0);

            double tailX = tail.getTranslateX();
            double tailY = tail.getTranslateY();

            switch (direction){
                case UP:
                    tail.setTranslateX(snake.get(0).getTranslateX());
                    tail.setTranslateY(snake.get(0).getTranslateY() - BLOCK_SIZE);
                    break;
                case DOWN:
                    tail.setTranslateX(snake.get(0).getTranslateX());
                    tail.setTranslateY(snake.get(0).getTranslateY() + BLOCK_SIZE);
                    break;
                case LEFT:
                    tail.setTranslateX(snake.get(0).getTranslateX() - BLOCK_SIZE);
                    tail.setTranslateY(snake.get(0).getTranslateY());
                    break;
                case RIGHT:
                    tail.setTranslateX(snake.get(0).getTranslateX() + BLOCK_SIZE);
                    tail.setTranslateY(snake.get(0).getTranslateY());
                    break;
            }

            moved = true;

            if (toRemove)
                snake.add(0, tail);

            for (Node rect : snake){
                if (rect != tail && tail.getTranslateX() == rect.getTranslateX()
                        && tail.getTranslateY() == rect.getTranslateY()){
                    restartGame();
                    break;
                }
            }

            if (tail.getTranslateX() < 0 || tail.getTranslateX() >= APP_W
                    || tail.getTranslateY() < 0 || tail.getTranslateY() >= APP_H){
                restartGame();
            }


            if (tail.getTranslateX() == food.getTranslateX()
                    && tail.getTranslateY() == food.getTranslateY()){
                food.setTranslateX((int) (Math.random() * APP_W) / BLOCK_SIZE * BLOCK_SIZE);
                food.setTranslateY((int) (Math.random() * APP_H) / BLOCK_SIZE * BLOCK_SIZE);

                Rectangle rect = new Rectangle(BLOCK_SIZE,BLOCK_SIZE);
                rect.setTranslateX(tailX);
                rect.setTranslateY(tailY);
                rect.setFill(Color.GREEN);

                snake.add(rect);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);

        root.getChildren().addAll(food,snakeBody);

        return root;
    }


    private void restartGame(){
        stopGame();
        startGame();
    }

    private void stopGame(){
        runing = false;
        timeline.stop();
        snake.clear();
    }

    private void startGame(){
        direction = Direction.RIGHT;
        Rectangle head = new Rectangle(BLOCK_SIZE,BLOCK_SIZE);
        snake.add(head);
        timeline.play();
        runing = true;
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        Scene scene = new Scene(createContent());
        scene.getStylesheets().add("sample/Style.css");
        scene.setOnKeyPressed(event -> {
            if (!moved)
                return;

            switch (event.getCode()) {
                case W:
                    if (direction != Direction.DOWN)
                        direction = Direction.UP;
                    break;
                case S:
                    if (direction != Direction.UP)
                        direction = Direction.DOWN;
                    break;
                case A:
                    if (direction != Direction.RIGHT)
                        direction = Direction.LEFT;
                    break;
                case D:
                    if (direction != Direction.LEFT)
                        direction = Direction.RIGHT;
                    break;
            }
            moved = false;
        });



        primaryStage.setTitle("SnakeGameFX - zet694 - SCREEN SIZE = " + APP_H + " НА " + APP_W);
        primaryStage.setScene(scene);
        primaryStage.show();
        startGame();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
