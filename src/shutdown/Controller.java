package shutdown;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable {

    @FXML
    private Slider secSlideID;

    @FXML
    private Slider minSlideID;

    @FXML
    private Button shudownID;

    @FXML
    private Button stopID;

    @FXML
    private TextField minID;

    @FXML
    private TextField secID;

    @FXML
    private Label minTextID;

    @FXML
    private Label secTextID;

    @FXML
    private CheckBox rebootID;

    private static int time;
    private static Timer timer;

    private void countdown() {
        if (time == 1)
            timer.cancel();
        --time;
        int min = time/60;
        int sec = time - min * 60;
        secTextID.setText(sec + "s");
        minTextID.setText(min + "m");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        secSlideID.setMax(59); // Limit slide to 59 seconds

        secSlideID.valueProperty().addListener((ob, o, n) -> {
            secTextID.textProperty().setValue(
                String.valueOf((int) secSlideID.getValue()) + "s");
            secID.textProperty().setValue(
                String.valueOf((int) secSlideID.getValue()));
        });

        minSlideID.valueProperty().addListener((ob, o, n) -> {
            minTextID.textProperty().setValue(
                    String.valueOf((int) minSlideID.getValue()) + "m");
            minID.textProperty().setValue(
                    String.valueOf((int) minSlideID.getValue()));
        });

        secID.textProperty().addListener((ob, o, n) -> {
            if (!n.matches("\\d*"))
                secID.setText(n.replaceAll("[^\\d]", ""));
            if (n.equals("")) {
                secID.setText(0 + "");
            } else {
                if (Integer.parseInt(n) > 60)
                    secID.setText(59 + "");
            }

            secSlideID.valueProperty().setValue(
                    Integer.parseInt(secID.getText())
            );
        });

        minID.textProperty().addListener((ob, o, n) -> {
            if (n.equals(""))
                minID.setText(0 + "");
            minSlideID.valueProperty().setValue(
                    Integer.parseInt(minID.getText())
            );
        });

        shudownID.setOnAction(event -> {
            time = (int) (secSlideID.getValue() + minSlideID.getValue() * 60);
            Runtime runtime = Runtime.getRuntime();
            try {
                if (rebootID.isSelected()) {
                    runtime.exec("shutdown -r -t " + String.valueOf(time));
                } else {
                    runtime.exec("shutdown -s -t " + String.valueOf(time));
                }
                shudownID.setDisable(true);
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        Platform.runLater(() ->  countdown());
                    }
                },1000,1000);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        stopID.setOnAction(event -> {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("shutdown -a");
                try {
                    timer.cancel();
                    shudownID.setDisable(false);
                    secID.setText(0 + "");
                    minID.setText(0 + "");
                } catch (NullPointerException ignored) {
                    System.out.println("The program does not know of any scheduled shutdown tasks.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
