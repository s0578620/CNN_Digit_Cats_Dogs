package org.htw.drawdigits;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.WHITE;
import static org.htw.drawdigits.util.Client.convertToJSON;
import static org.htw.drawdigits.util.Utils.getGreyScaleCSVFromCanvas;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;
import okhttp3.Response;
import org.htw.drawdigits.util.Client;
import org.htw.drawdigits.util.DigitCanvas;
import org.htw.drawdigits.util.Draw;
import org.htw.drawdigits.util.Gallery;
import org.json.JSONException;
import org.json.JSONObject;

public class PrimaryController implements Initializable {

  private static final String MODEL_BEING_LOADED = "%s is loading...";
  private static final String MODEL_READY = "%s is ready to go!";
  private static final String MODEL_FAILED = "loading %s failed!";
  private static final String MODEL_PLS_CHOOSE = "load a model please...";
  private static final String NOTHING_DRAWN_YET = "nothing drawn yet...";
  private static final String RESET_METHOD = "reset";
  private static final String MODEL_METHOD = "model";
  private static final String PREDICT_METHOD = "predict";
  private static final String CONNECTION_TO_SERVER_LOST = "CONNECTION TO SERVER LOST";

  private final Client CLIENT = new Client();
  private Gallery<DigitCanvas> canvae;
  private DigitCanvas canvasNow;
  private List<Pair<Double, Double>> drawNow;
  private Color background;
  private Color brush;
  private JSONObject models;
  private String oldText;
  private Model model;

  @FXML
  private ChoiceBox<Model> modelBox;
  @FXML
  private Canvas resultCanvas;
  @FXML
  private Slider brushSize;
  @FXML
  private Label modelLabel;
  @FXML
  private CheckBox erase;
  @FXML
  private Canvas canvas;
  @FXML
  private Label canvasLabel;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    Timeline timeline = new Timeline();
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.setAutoReverse(false);
    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> pingServer()));
    timeline.play();

    canvasNow = new DigitCanvas();
    canvae = Gallery.of("", canvasNow);

    background = BLACK;
    brush = WHITE;

    setModelLabelText(MODEL_PLS_CHOOSE);
    modelBox.getSelectionModel()
        .selectedItemProperty()
        .addListener(this::modelChanged);
  }


  @FXML
  private void checkModelOptions() {
    models = CLIENT.getModels();
    modelBox.setItems(FXCollections.observableList(
        models.keySet().stream()
            .map(key -> new Model(key, models.getString(key)))
            .collect(Collectors.toList())
    ));
  }

  private void pingServer() {
    try (Response response = CLIENT.get()) {
      if (response.code() == 200) {
        if (nonNull(oldText)) {
          setModelLabelText(oldText);
        }
        return;
      }
    } catch (IOException ignored) {
    }
    if (!modelLabel.getText().equals(CONNECTION_TO_SERVER_LOST)) {
      oldText = modelLabel.getText();
    }
    setModelLabelText(CONNECTION_TO_SERVER_LOST);
  }

  public void canvasOnMousePressed(MouseEvent mouseEvent) {
    List<Pair<Double, Double>> startPoint = List.of(
        new Pair<>(mouseEvent.getX(), mouseEvent.getY()));
    drawNow = new LinkedList<>(startPoint);
    draw(getDraw(startPoint));
  }

  public void canvasOnMouseDragged(MouseEvent mouseEvent) {
    Pair<Double, Double> point = new Pair<>(mouseEvent.getX(), mouseEvent.getY());
    drawNow.add(point);
    draw(getDraw(List.of(point)));
  }

  public void canvasOnMouseReleased(MouseEvent mouseEvent) throws IOException {
    drawNow.add(new Pair<>(mouseEvent.getX(), mouseEvent.getY()));
    Draw draw = getDraw(drawNow);
    canvasNow.addDraw(draw);
    draw(draw);
    evalCanvas();
  }

  public void onResetConnection(ActionEvent ignored) {
    setModelLabelText(MODEL_PLS_CHOOSE);
    writeResult();
    try {
      CLIENT.get(RESET_METHOD);
    } catch (IllegalStateException ex) {
      System.out.println("wtf");
    }
  }

  public void onInvertColors(ActionEvent ignored) throws IOException {
    background = background.equals(BLACK) ? WHITE : BLACK;
    brush = brush.equals(BLACK) ? WHITE : BLACK;
    cleanCanvas();
    canvasNow.getDraws().forEach(this::draw);
    evalCanvas();
  }

  public void onUndoDraw(ActionEvent ignored) throws IOException {
    try {
      cleanCanvas();
      canvasNow.undoDraw();
      if (canvasNow.getDraws().isEmpty()) {
        writeResult();
        return;
      }
      canvasNow.getDraws().forEach(this::draw);
      evalCanvas();
    } catch (NoSuchElementException ignored1) {
    }
  }

  private void evalCanvas() throws IOException {
    if (canvasNow.getDraws().isEmpty()) {
      return;
    }
    String image = getGreyScaleCSVFromCanvas(canvas);
    try (Response response = CLIENT.post(PREDICT_METHOD, convertToJSON("data", image))) {
      if (isNull(response.body())) {
        System.out.println("wtf");
        return;
      }
      String string = response.body().string();
      JSONObject data = convertToJSON(string);
      if (!data.has("table")) {
        System.out.println("wtf");
        return;
      }
      List<BigDecimal> table = data.getJSONArray("table")
          .toList().stream()
          .map(o -> ((BigDecimal) o))
          .collect(Collectors.toList());
      writeResult(table);
    } catch (JSONException e) {
      System.out.println("response did not contain JSON: " + e.getMessage());
    }
  }

  private void writeResult(List<BigDecimal> table) {
    GraphicsContext g = resultCanvas.getGraphicsContext2D();
    g.setFill(WHITE);
    g.fillRect(0, 0, 400, 400);
    Font font = g.getFont();
    int posX = 50;
    for (int i = 0; i < table.size(); i++) {
      double a = table.get(i).doubleValue();
      double size = a > 0.05d ? a * 300 : 15;
      g.setFont(Font.font(size));
      if (table.stream().mapToDouble(BigDecimal::doubleValue).max().orElseThrow() == a) {
        g.setFill(RED);
      } else {
        g.setFill(BLACK);
      }
      g.fillText(String.valueOf(i), posX + Math.sqrt(size) * 2, 300);
      posX += Math.sqrt(size) * 5;
    }
    g.setFont(font);
    g.setFill(BLACK);
  }

  public void writeResult(String string) {
    GraphicsContext g = resultCanvas.getGraphicsContext2D();
    g.setFill(WHITE);
    g.fillRect(0, 0, 400, 400);
    g.setFill(BLACK);
    g.setTextAlign(TextAlignment.CENTER);
    g.strokeText(string, 200, 200, 400);
  }

  public void writeResult() {
    writeResult(NOTHING_DRAWN_YET);
  }

  private void draw(Draw curve) {
    GraphicsContext g = canvas.getGraphicsContext2D();
    g.setFill(getColor(curve.getPaint()));
    curve.getPoints().forEach(point -> {
      double t = curve.getSize();
      g.fillRect(point.getKey() - t / 2, point.getValue() - t / 2, t, t);
    });
  }

  public void cleanCanvas() {
    canvas.getGraphicsContext2D().setFill(background);
    canvas.getGraphicsContext2D().fillRect(0, 0, 400, 400);
    writeResult();
  }

  private Color getColor(Draw.Paint paint) {
    return paint.equals(Draw.Paint.BACKGROUND) ? background : brush;
  }

  private Draw getDraw(List<Pair<Double, Double>> curve) {
    return new Draw(curve, brushSize.getValue(),
        erase.isSelected() ? Draw.Paint.BACKGROUND : Draw.Paint.BRUSH
    );
  }

  private void setModelLabelText(String s) {
    modelLabel.setText(format(s, nonNull(model) ? model : "null"));
  }

  private void modelChanged(ObservableValue<? extends Model> observable, Model old,
      Model selected) {
    if (isNull(selected)) {
      return;
    }
    model = selected;
    setModelLabelText(MODEL_BEING_LOADED);
    try (Response res = CLIENT.post(MODEL_METHOD, convertToJSON("num", selected.key))) {
      if (res.code() == 200) {
        setModelLabelText(MODEL_READY);
      } else {
        setModelLabelText(MODEL_FAILED);
      }
    }
  }

  public void onLeftButtonClicked(ActionEvent ignored) throws IOException {
    cleanCanvas();
    canvasNow = canvae.moveLeft();
    canvasNow.getDraws().forEach(this::draw);
    setCanvasLabel();
    evalCanvas();
  }

  public void onRightButtonClicked(ActionEvent ignored) throws IOException {
    cleanCanvas();
    canvasNow = canvae.moveRight();
    canvasNow.getDraws().forEach(this::draw);
    setCanvasLabel();
    evalCanvas();
  }

  public void onResetCanvas(ActionEvent ignored) {
    canvasNow.reset();
    cleanCanvas();
  }

  public void onNewCanvas(ActionEvent ignored) {
    cleanCanvas();
    canvae.addItem("", (canvasNow = new DigitCanvas()));
    canvasNow.getDraws().forEach(this::draw);
    setCanvasLabel();
  }

  public void onDeleteCanvas(ActionEvent ignored) throws IOException {
    cleanCanvas();
    if (canvae.removePresent()) {
      canvasNow = canvae.getPresentItem();
      canvasNow.getDraws().forEach(this::draw);
      setCanvasLabel();
    } else {
      onResetCanvas(ignored);
    }
    evalCanvas();
  }

  private void setCanvasLabel() {
    canvasLabel.setText("Canvas " + canvae.getPresentIndex() + " / " + canvae.size());
  }

  public void onFeatures(ActionEvent ignored) {
    CLIENT.get("features");
    // TODO
  }

  public static class Model {

    String key;
    String name;

    public Model(String key, String name) {
      this.key = key;
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

}
