package cellularautomata.demo;

import cellularautomata.core.BoundaryFactory;
import cellularautomata.core.NeighborhoodFactory;
import cellularautomata.core.Setup;
import cellularautomata.display.HueColorMap;
import cellularautomata.examples.HodgePodge;
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
 * HodgePodge-only JavaFX shell: play / pause / step / reset with tunable n, k1, k2, g.
 */
public class HodgePodgeApp extends Application {

    private static final int DEFAULT_SIZE = 200;
    private static final int MIN_SIZE = 100;
    private static final int MAX_SIZE = 512;
    private static final int PIXEL_SCALE = 3;
    private static final double DEFAULT_FPS = 8;

    private final AtomicBoolean playing = new AtomicBoolean(false);
    private final Object caLock = new Object();

    private HodgePodge ca;
    private CanvasExporter exporter;
    private ImageView imageView;
    private Spinner<Integer> sizeSpinner;
    private Spinner<Integer> nSpinner;
    private Spinner<Integer> k1Spinner;
    private Spinner<Integer> k2Spinner;
    private Spinner<Integer> gSpinner;
    private Slider speedSlider;
    private Stage stage;
    private volatile double framesPerSecond = DEFAULT_FPS;
    private ScheduledExecutorService loop;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);

        Button play = new Button("Play");
        Button pause = new Button("Pause");
        Button step = new Button("Step");
        Button reset = new Button("Reset");

        sizeSpinner = new Spinner<>(MIN_SIZE, MAX_SIZE, DEFAULT_SIZE, 10);
        sizeSpinner.setEditable(true);
        sizeSpinner.setPrefWidth(80);

        nSpinner = new Spinner<>(2, 255, HodgePodge.DEFAULT_STATES, 1);
        nSpinner.setEditable(true);
        nSpinner.setPrefWidth(70);

        k1Spinner = new Spinner<>(1, 100, HodgePodge.DEFAULT_K1, 1);
        k1Spinner.setEditable(true);
        k1Spinner.setPrefWidth(60);

        k2Spinner = new Spinner<>(1, 100, HodgePodge.DEFAULT_K2, 1);
        k2Spinner.setEditable(true);
        k2Spinner.setPrefWidth(60);

        gSpinner = new Spinner<>(1, 100, HodgePodge.DEFAULT_G, 1);
        gSpinner.setEditable(true);
        gSpinner.setPrefWidth(60);

        speedSlider = new Slider(1, 60, DEFAULT_FPS);
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
                new Label("n"), nSpinner,
                new Label("k1"), k1Spinner,
                new Label("k2"), k2Spinner,
                new Label("g"), gSpinner,
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

        stage.setTitle("HodgePodge");
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(e -> shutdown());
        stage.show();
        stage.sizeToScene();
    }

    @Override
    public void stop() {
        shutdown();
    }

    private void startLoop() {
        loop = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "hodgepodge-evolution");
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
            Platform.runLater(this::exportToView);
        }
    }

    private void exportToView() {
        synchronized (caLock) {
            if (ca != null && exporter != null) {
                ca.exportValues(exporter);
            }
        }
    }

    private void resetCa(int size) {
        playing.set(false);
        int n = nSpinner.getValue();
        int k1 = k1Spinner.getValue();
        int k2 = k2Spinner.getValue();
        int g = gSpinner.getValue();
        synchronized (caLock) {
            Setup setup = new Setup(
                    BoundaryFactory.BoundaryType.PERIODIC,
                    NeighborhoodFactory.NeighborhoodType.MOORE,
                    size, size);
            ca = new HodgePodge(size, size, setup, n, k1, k2, g);
            exporter = new CanvasExporter(size, size, new HueColorMap(0, n), PIXEL_SCALE);
            ca.exportValues(exporter);
            imageView.setImage(exporter.getImage());
        }
        if (stage != null) {
            stage.setTitle("HodgePodge — n=" + n + " k1=" + k1 + " k2=" + k2 + " g=" + g);
            if (stage.isShowing()) {
                stage.sizeToScene();
            }
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
