package cellularautomata.demo;

import cellularautomata.core.BoundaryFactory;
import cellularautomata.core.ICellularAutomata;
import cellularautomata.core.LookupTable;
import cellularautomata.core.LookupTableFactory;
import cellularautomata.core.NeighborhoodFactory;
import cellularautomata.core.Setup;
import cellularautomata.display.ColorMap;
import cellularautomata.display.GrayLevelColorMap;
import cellularautomata.display.HotColorMap;
import cellularautomata.display.JetColorMap;
import cellularautomata.examples.FallingSand;
import cellularautomata.examples.GameOfLife;
import cellularautomata.examples.HPP;
import cellularautomata.examples.LangtonsAnt;
import cellularautomata.examples.Parity;
import cellularautomata.examples.TimeTunnel;
import cellularautomata.examples.West;
import evolutionaryAutomata.GeneticCellularAutomata;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
 * JavaFX shell: pick a cellular automaton and run play / pause / step / reset.
 */
public class DemoApp extends Application {

    private static final int DEFAULT_SIZE = 200;
    private static final int MIN_SIZE = 100;
    private static final int MAX_SIZE = 400;
    private static final int VIEW_SIZE = 500;
    private static final int LANGTON_ANTS = 5;
    private static final int GENETIC_CROSSOVER_STEP = 30;

    private enum Example {
        GAME_OF_LIFE("Game of Life"),
        FALLING_SAND("Falling Sand"),
        LANGTONS_ANT("Langton's Ant"),
        HPP("HPP"),
        PARITY("Parity"),
        TIME_TUNNEL("Time Tunnel"),
        WEST("West"),
        GENETIC_CA("Genetic CA");

        private final String label;

        Example(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /** Maps LUT integer codes to RGB the same way the old GeneticCADisplayer did. */
    private static final ColorMap IDENTITY_RGB = new ColorMap(0, 1) {
        @Override
        public int convertValue(int value) {
            return value & 0xFFFFFF;
        }
    };

    private final AtomicBoolean playing = new AtomicBoolean(false);
    private final Object caLock = new Object();

    private ICellularAutomata ca;
    private CanvasExporter exporter;
    private ImageView imageView;
    private ComboBox<Example> examplePicker;
    private Spinner<Integer> sizeSpinner;
    private Slider speedSlider;
    private Stage stage;
    private volatile double framesPerSecond = 20;
    private ScheduledExecutorService loop;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(VIEW_SIZE);
        imageView.setFitHeight(VIEW_SIZE);
        imageView.setSmooth(false);

        examplePicker = new ComboBox<>();
        examplePicker.getItems().addAll(Example.values());
        examplePicker.getSelectionModel().select(Example.GAME_OF_LIFE);
        examplePicker.setOnAction(e -> resetCa(sizeSpinner.getValue()));

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
                new Label("CA"), examplePicker,
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
        Example example = examplePicker.getSelectionModel().getSelectedItem();
        if (example == null) {
            example = Example.GAME_OF_LIFE;
        }
        synchronized (caLock) {
            Created created = createExample(example, size);
            ca = created.ca;
            exporter = created.exporter;
            ca.exportValues(exporter);
            imageView.setImage(exporter.getImage());
        }
        stage.setTitle("Cellular Automata — " + example);
    }

    private Created createExample(Example example, int size) {
        return switch (example) {
            case GAME_OF_LIFE -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.MOORE,
                        size, size);
                yield new Created(
                        new GameOfLife(size, size, setup, 0),
                        new CanvasExporter(size, size, new GrayLevelColorMap(0, 1)));
            }
            case FALLING_SAND -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.MARGOLUS,
                        size, size);
                yield new Created(
                        new FallingSand(size, size, setup),
                        new CanvasExporter(size, size, new JetColorMap(0, 2)));
            }
            case LANGTONS_ANT -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new Created(
                        new LangtonsAnt(size, size, setup, LANGTON_ANTS),
                        new CanvasExporter(size, size, new HotColorMap(0, LANGTON_ANTS)));
            }
            case HPP -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new Created(
                        new HPP(size, size, setup),
                        new CanvasExporter(size, size,
                                new HotColorMap(0, HPP.NORTH + HPP.SOUTH + HPP.EAST + HPP.WEST)));
            }
            case PARITY -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new Created(
                        new Parity(size, size, setup),
                        new CanvasExporter(size, size, new HotColorMap(0, 1)));
            }
            case TIME_TUNNEL -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new Created(
                        new TimeTunnel(size, size, setup),
                        new CanvasExporter(size, size, new GrayLevelColorMap(0, 3)));
            }
            case WEST -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new Created(
                        new West(size, size, setup),
                        new CanvasExporter(size, size, new GrayLevelColorMap(0, 1)));
            }
            case GENETIC_CA -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.REFLEXIVE,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                GeneticCellularAutomata genetic = new GeneticCellularAutomata(
                        size, size, setup, GENETIC_CROSSOVER_STEP, false);
                LookupTable rule1 = LookupTableFactory.createLookupTable(
                        LookupTableFactory.Type.WEST, setup.getNeighborNumber());
                LookupTable rule2 = LookupTableFactory.createLookupTable(
                        LookupTableFactory.Type.PARITY, setup.getNeighborNumber());
                genetic.initializeRules(
                        GeneticCellularAutomata.RuleDistribution.HORIZONTALSPLIT, rule1, rule2);
                genetic.selectCrossoverType(GeneticCellularAutomata.CrossoverType.UNIFORM, 0.5);
                yield new Created(genetic, new CanvasExporter(size, size, IDENTITY_RGB));
            }
        };
    }

    private void shutdown() {
        playing.set(false);
        if (loop != null) {
            loop.shutdownNow();
            loop = null;
        }
    }

    private record Created(ICellularAutomata ca, CanvasExporter exporter) {
    }

    public static void main(String[] args) {
        launch(args);
    }
}
