package cellularautomata.demo;

import cellularautomata.core.BoundaryFactory;
import cellularautomata.core.NeighborhoodFactory;
import cellularautomata.core.Setup;
import cellularautomata.display.GrayLevelColorMap;
import cellularautomata.examples.GameOfLife;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Minimal JavaFX shell: Game of Life with play / pause / step / reset.
 */
public class DemoApp extends Application {

    private static final int DEFAULT_SIZE = 200;
    private static final int MIN_SIZE = 100;
    private static final int MAX_SIZE = 400;
    private static final int VIEW_SIZE = 500;

    private final AtomicBoolean playing = new AtomicBoolean(false);
    private final Object caLock = new Object();

    private GameOfLife ca;
    private CanvasExporter exporter;
    private ImageView imageView;
    private Spinner<Integer> sizeSpinner;
    private Slider speedSlider;
    private volatile double framesPerSecond = 20;
    private ScheduledExecutorService loop;

    @Override
    public void start(Stage stage) {
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(VIEW_SIZE);
        imageView.setFitHeight(VIEW_SIZE);
        imageView.setSmooth(false);

        Button play = new Button("Play");
        Button pause = new Button("Pause");
        Button step = new Button("Step");
        Button reset = new Button("Reset");

        sizeSpinner = new Spinner<>(MIN_SIZE, MAX_SIZE, DEFAULT_SIZE, 10);
        sizeSpinner.setEditable(true);
        sizeSpinner.setPrefWidth(80);

        speedSlider = new Slider(1, 60, 20);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(20);
        speedSlider.setPrefWidth(140);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                framesPerSecond = newVal.doubleValue());

        play.setOnAction(e -> playing.set(true));
        pause.setOnAction(e -> playing.set(false));
        step.setOnAction(e -> {
            playing.set(false);
            runStep();
        });
        reset.setOnAction(e -> resetCa(sizeSpinner.getValue()));

        sizeSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                resetCa(newVal);
            }
        });

        HBox controls = new HBox(8,
                play, pause, step, reset,
                new Label("Size"), sizeSpinner,
                new Label("FPS"), speedSlider);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(8));
        HBox.setHgrow(speedSlider, Priority.ALWAYS);

        StackPane center = new StackPane(imageView);
        center.setPadding(new Insets(8));
        center.setStyle("-fx-background-color: #1a1a1a;");

        BorderPane root = new BorderPane();
        root.setTop(controls);
        root.setCenter(center);

        resetCa(DEFAULT_SIZE);
        startLoop();

        stage.setTitle("Cellular Automata — Game of Life");
        stage.setScene(new Scene(root, VIEW_SIZE + 32, VIEW_SIZE + 80));
        stage.setOnCloseRequest(e -> shutdown());
        stage.show();
    }

    @Override
    public void stop() {
        shutdown();
    }

    private void startLoop() {
        loop = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ca-evolution");
            t.setDaemon(true);
            return t;
        });
        loop.scheduleWithFixedDelay(this::tick, 0, 5, TimeUnit.MILLISECONDS);
    }

    private void tick() {
        if (!playing.get()) {
            return;
        }
        long periodMs = Math.max(1, Math.round(1000.0 / framesPerSecond));
        long started = System.nanoTime();
        runStep();
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);
        long sleep = periodMs - elapsedMs;
        if (sleep > 0) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void runStep() {
        synchronized (caLock) {
            if (ca == null) {
                return;
            }
            ca.collisionAndPropagation();
            Platform.runLater(() -> {
                synchronized (caLock) {
                    if (ca != null && exporter != null) {
                        ca.exportValues(exporter);
                    }
                }
            });
        }
    }

    private void resetCa(int size) {
        playing.set(false);
        synchronized (caLock) {
            Setup setup = new Setup(
                    BoundaryFactory.BoundaryType.PERIODIC,
                    NeighborhoodFactory.NeighborhoodType.MOORE,
                    size,
                    size);
            ca = new GameOfLife(size, size, setup, 0);
            exporter = new CanvasExporter(size, size, new GrayLevelColorMap(0, 1));
            ca.exportValues(exporter);
            imageView.setImage(exporter.getImage());
        }
    }

    private void shutdown() {
        playing.set(false);
        if (loop != null) {
            loop.shutdownNow();
            loop = null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
