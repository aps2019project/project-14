package view;

import com.gilecode.yagson.YaGson;
import com.gilecode.yagson.YaGsonBuilder;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import model.land.LandOfGame;
import view.enums.StateType;
import view.sample.StageLauncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;

public class BattleScene {
    private static BattleScene singleInstance = null;
    private Scene battleScene = StageLauncher.getScene(StateType.BATTLE);
    private Group root = (Group) battleScene.getRoot();
    private Group board = null; //!!!! Har chi roo board gharare bashe be in add she
    private double width = StageLauncher.getWidth();
    private double height = StageLauncher.getHeight();
    private int numberOfMap;
    private double cellWidth = 82;
    private double cellHeight = 77;
    private double gap = 5;
    private Rectangle[][] gameGrid;
    private MapProperties mapProperties;

    private BattleScene() {
    }

    public static BattleScene getSingleInstance() {
        if (singleInstance == null)
            singleInstance = new BattleScene();
        return singleInstance;
    }

    public void setBattleScene(int numberOfMap) {
        root.getChildren().clear();
        this.numberOfMap = numberOfMap;
        setMapProperties();
        setMapBackground();
        addGrid();
    }

    private void setMapProperties(){
        String path = "pics/maps_categorized/map" + numberOfMap + "/property.json";
        YaGson yaGson = new YaGson();
        try {
            yaGson.fromJson(new FileReader(path), MapProperties.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setMapBackground() {
        //System.out.println("numberOfMap = " + numberOfMap);
        String pathOfFile = "pics/maps_categorized/map" + numberOfMap + "/background";
        File file = new File(pathOfFile);
        File[] files = file.listFiles();
        if (files != null) {
            Arrays.sort(files);
            for (File file1 : files) {
                //System.out.println("file1.getName() = " + file1.getName());
                ImageView imageView = GeneralGraphicMethods.setBackground(root, file1.getPath(), false, 0, 0);
                if (file1.getName().contains("middleground") || file1.getName().contains("midground")) {
                    //todo duration ya ye chiz dige
                    moveBackgrounds(imageView, false, false);
                }
                if (file1.getName().contains("foreground")) {
                    moveBackgrounds(imageView, false, true);
                }
            }
        }
    }

    private void moveBackgrounds(ImageView imageView, boolean horizontal, boolean vertical) {
        int randomNumber = (new Random().nextInt(3)) - 1;
        if (randomNumber == 0) randomNumber = 1;
        if (vertical)
            randomNumber *= 15;
        else
            randomNumber *= 10;
        if (vertical && (imageView.getX() - randomNumber > width || imageView.getX() - randomNumber < 0))
            randomNumber *= -1;
        if (horizontal && (imageView.getY() - randomNumber > height || imageView.getY() - randomNumber < 0))
            randomNumber *= -1;

        final int moveDistance = randomNumber;

        imageView.setOnMouseEntered(event -> {
            double primaryX = imageView.getX();
            double primaryY = imageView.getY();
            if (vertical)
                imageView.setX(primaryX + moveDistance);
            if (horizontal) {
                imageView.setY(primaryY + moveDistance);
            }
        });
        imageView.setOnMouseExited(event -> {
            double primaryX = imageView.getX();
            double primaryY = imageView.getY();
            if (vertical)
                imageView.setX(primaryX - moveDistance);
            if (horizontal)
                imageView.setY(primaryY - moveDistance);
        });
    }

    public Rectangle getCell(int row, int column){
        return gameGrid[row][column];
    }

    public Pair<Double, Double> getCellPosition(int row, int column){
        return new Pair<>(gameGrid[row][column].getX(), gameGrid[row][column].getY());
    }

    private void addGrid() {
        board = new Group();
        int numberOfColumns = LandOfGame.getNumberOfColumns();
        int numberOfRows = LandOfGame.getNumberOfRows();
        double primaryX = 330, primaryY = 230;
        double currentX = primaryX, currentY = primaryY;

        gameGrid = new Rectangle[numberOfRows][numberOfColumns];

        for (int i = 0; i < numberOfRows; i++)
            for (int j = 0; j < numberOfColumns; j++) {
                if(j == 0) {
                    currentY += cellHeight + gap;
                    currentX = primaryX;
                }
                else
                    currentX += cellWidth + gap;
                Rectangle rectangle = new Rectangle(cellWidth, cellHeight);
                rectangle.setFill(Color.rgb(0, 0, 0, 0.2));
                rectangle.setX(currentX);
                rectangle.setY(currentY);
                gameGrid[i][j] = rectangle;
                board.getChildren().add(rectangle);
            }

        root.getChildren().add(board);

        PerspectiveTransform perspectiveTransform = new PerspectiveTransform();

        if (mapProperties == null) {
            System.out.println("map is null");
            return;
        }
        perspectiveTransform.setUlx(mapProperties.ulx);
        perspectiveTransform.setUly(mapProperties.uly);
        perspectiveTransform.setUrx(mapProperties.urx);
        perspectiveTransform.setUry(mapProperties.ury);
        perspectiveTransform.setLlx(mapProperties.llx);
        perspectiveTransform.setLly(mapProperties.lly);
        perspectiveTransform.setLrx(mapProperties.lrx);
        perspectiveTransform.setLry(mapProperties.lry);
        /*
        perspectiveTransform.setUlx(primaryX + 50);
        perspectiveTransform.setUly(primaryY);
        perspectiveTransform.setUrx(primaryX + cellWidth * numberOfColumns + gap * (numberOfColumns - 1) - 50);
        perspectiveTransform.setUry(primaryY);
        perspectiveTransform.setLlx(primaryX - 50);
        perspectiveTransform.setLly(primaryY + cellHeight * numberOfRows + gap * (numberOfRows - 1));
        perspectiveTransform.setLrx(primaryX + cellWidth * numberOfColumns + gap * (numberOfColumns - 1) + 50);
        perspectiveTransform.setLry(primaryY + cellHeight * numberOfRows + gap * (numberOfRows - 1));
        */


        board.setEffect(perspectiveTransform);
    }

}
