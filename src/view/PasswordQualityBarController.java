package view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

public class PasswordQualityBarController extends HBox {

    static private final String[] PERCENTAGE_TO_COLOR = new String[]{
            "#000000",
            "#d32f2f",
            "#d3322f",
            "#d3362f",
            "#d3392f",
            "#d33d30",
            "#d34030",
            "#d34430",
            "#d34730",
            "#d34b31",
            "#d34e31",
            "#d35231",
            "#d35531",
            "#d35832",
            "#d35c32",
            "#d35f32",
            "#d46332",
            "#d46633",
            "#d46a33",
            "#d46d33",
            "#d47034",
            "#d47434",
            "#d47734",
            "#d47b34",
            "#d47e35",
            "#d48135",
            "#d48535",
            "#d48835",
            "#d48b36",
            "#d48f36",
            "#d59236",
            "#d59536",
            "#d59937",
            "#d59c37",
            "#d59f37",
            "#d5a337",
            "#d5a638",
            "#d5a938",
            "#d5ac38",
            "#d5b039",
            "#d5b339",
            "#d5b639",
            "#d5b939",
            "#d5bd3a",
            "#d6c03a",
            "#d6c33a",
            "#d6c63a",
            "#d6c93b",
            "#d6cd3b",
            "#d6d03b",
            "#d6d33c",
            "#d6d63c",
            "#d3d63c",
            "#d0d63c",
            "#cdd63d",
            "#cad63d",
            "#c7d63d",
            "#c4d63d",
            "#c1d63e",
            "#bed73e",
            "#bbd73e",
            "#b8d73f",
            "#b5d73f",
            "#b2d73f",
            "#afd73f",
            "#add740",
            "#aad740",
            "#a7d740",
            "#a4d740",
            "#a1d741",
            "#9ed741",
            "#9bd741",
            "#98d742",
            "#96d842",
            "#93d842",
            "#90d842",
            "#8dd843",
            "#8ad843",
            "#88d843",
            "#85d843",
            "#82d844",
            "#7fd844",
            "#7cd844",
            "#7ad845",
            "#77d845",
            "#74d845",
            "#71d845",
            "#6fd946",
            "#6cd946",
            "#69d946",
            "#67d946",
            "#64d947",
            "#61d947",
            "#5fd947",
            "#5cd948",
            "#59d948",
            "#57d948",
            "#54d948",
            "#51d949",
            "#4fd949",
            "#4cd949"
    };

    @FXML
    private ProgressBar qualityBar;

    public PasswordQualityBarController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PasswordQualityBar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Define the password quality. Updates the underlying progress bar and sets its color accordingly.
     *
     * @param value Define the password quality; takes values from 0 (bad quality) to 1 (good quality).
     */
    public void setQuality(double value) {
        assert value >= 0 && value <= 1 : "'value' must be in range [0,1]!";
        qualityBar.setProgress(value);

        int colorIndex = (int) (value * 100);

        qualityBar.setStyle("-fx-accent: " + PERCENTAGE_TO_COLOR[colorIndex]);
    }
}
