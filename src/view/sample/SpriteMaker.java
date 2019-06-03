package view.sample;

import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.FileInputStream;

public class SpriteMaker {
    private static SpriteMaker ourInstance = new SpriteMaker();

    public static SpriteMaker getInstance() {
        return ourInstance;
    }

    private SpriteMaker() {
    }

    public void makeSpritePic(String path, int x, int y,Parent root, int count,
                              int numberOfPicInEachColumn, long millis,
                              int widthOfEachFrame, int heightOfEachFrame, int totalHeight) {
        try {
            Image image = new Image(new FileInputStream(path));
            ImageView imageView = new ImageView(image);
            imageView.setX(x);
            imageView.setY(y);
            imageView.setScaleX(2);
            imageView.setScaleY(2);

            imageView.setViewport(new Rectangle2D(0, 0, widthOfEachFrame * numberOfPicInEachColumn, totalHeight));
//sprite animation  useful for your project
            if(root instanceof Group){
                 ((Group) root).getChildren().add(imageView);
            }
            if(root instanceof HBox){
                ((HBox) root).getChildren().add(imageView);
            }
            //root.getChildrenUnmodifiable().add(imageView);
            final Animation animation = new view.SpriteAnimation(
                    imageView,
                    Duration.millis(millis),
                    count, numberOfPicInEachColumn,
                    1, 0,
                    widthOfEachFrame, heightOfEachFrame
            );
            animation.setCycleCount(Animation.INDEFINITE);
            animation.play();
        } catch (Exception e) {

        }
    }
}
