package shutdown;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable {

    private static int time;

    private static Timer timer;

    private boolean isDate = true;

    @FXML
    private Label minTextID;

    @FXML
    private Label secTextID;

    @FXML
    private JFXToggleButton rebootID;

    @FXML
    private JFXButton shudownID;

    @FXML
    private JFXButton stopID;

    @FXML
    private Pane slidersPaneID;

    @FXML
    private TextField minID;

    @FXML
    private TextField secID;

    @FXML
    private JFXSlider minSlideID;

    @FXML
    private JFXSlider secSlideID;

    @FXML
    private Pane datePaneID;

    @FXML
    private JFXDatePicker dateID;

    @FXML
    private JFXTimePicker timeID;

    @FXML
    private JFXToggleButton selectTimeID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        secSlideID.setValue(0);
        minSlideID.setValue(0);

        dateID.setValue(LocalDate.now());
        timeID.setValue(LocalTime.now());

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
            if (isDate) {
                int actualDate = ((int) LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
                int selectedDate = ((int) dateID.getValue().atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
                int actualTime = LocalTime.now().toSecondOfDay();
                int selectedTime = timeID.getValue().toSecondOfDay();

                time = selectedDate + selectedTime - actualDate - actualTime;
            } else {
                time = (int) (secSlideID.getValue() + minSlideID.getValue() * 60);
            }
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
                            Platform.runLater(() -> countdown());
                        }
                    }, 1000, 1000);

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
                    System.err.println("The program doesn't know of any scheduled shutdown tasks.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        rebootID.setOnAction(evento -> {
            if (rebootID.isSelected()) {
                shudownID.setText("Restart");
            } else {
                shudownID.setText("Shutdown");
            }
        });

        selectTimeID.setOnAction(event -> {
            if (selectTimeID.isSelected()) {
                isDate = true;
                datePaneID.setVisible(true);
                slidersPaneID.setVisible(false);
            } else {
                isDate = false;
                datePaneID.setVisible(false);
                slidersPaneID.setVisible(true);
            }
        });

    }

    private void countdown() {
        if (time == 1)
            timer.cancel();
        --time;
        int min = time / 60;
        int sec = time - min * 60;
        secTextID.setText(sec + "s");
        minTextID.setText(min + "m");
    }

}
