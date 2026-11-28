package cellularautomata.cli;

import cellularautomata.core.BoundaryFactory;
import cellularautomata.core.ICellularAutomata;
import cellularautomata.core.LookupTable;
import cellularautomata.core.LookupTableFactory;
import cellularautomata.core.NeighborhoodFactory;
import cellularautomata.core.Setup;
import cellularautomata.display.BlueColorMap;
import cellularautomata.display.ColorMap;
import cellularautomata.display.EmojiTileImageWriter;
import cellularautomata.display.GrayLevelColorMap;
import cellularautomata.display.HotColorMap;
import cellularautomata.display.IndexedColorMap;
import cellularautomata.display.JetColorMap;
import cellularautomata.examples.Cyclic;
import cellularautomata.examples.Diffusion;
import cellularautomata.examples.FallingSand;
import cellularautomata.examples.ForestFire;
import cellularautomata.examples.GameOfLife;
import cellularautomata.examples.HPP;
import cellularautomata.examples.HeightFieldWater;
import cellularautomata.examples.HodgePodge;
import cellularautomata.examples.LangtonsAnt;
import cellularautomata.examples.Parity;
import cellularautomata.examples.TimeTunnel;
import cellularautomata.examples.West;
import evolutionaryAutomata.GeneticCellularAutomata;

/**
 * Headless CA construction mirroring {@code DemoApp.createExample} without {@code CanvasExporter}.
 *
 * @author Daniel Lagrava
 */
public final class ExampleFactory {

    private static final int LANGTON_ANTS = 5;
    private static final int GENETIC_CROSSOVER_STEP = 30;
    /** Ill threshold so states 0..N map 1:1 onto {@link EmojiTileImageWriter#FACE_COUNT} faces. */
    public static final int EMOTICON_N = EmojiTileImageWriter.FACE_COUNT - 1;
    private static final int EMOTICON_G = 1;

    /** Maps LUT integer codes to RGB the same way DemoApp does for genetic CA. */
    private static final ColorMap IDENTITY_RGB = new ColorMap(0, 1) {
        @Override
        public int convertValue(double value) {
            return ((int) value) & 0xFFFFFF;
        }
    };

    private ExampleFactory() {
    }

    /**
     * Build a CA for the given CLI id and grid size.
     *
     * @param caId same ids as {@code BatchCli} {@code --ca} (e.g. {@code game-of-life})
     * @param size width and height
     */
    public static ICellularAutomata create(String caId, int size) {
        return switch (caId) {
            case "game-of-life" -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.MOORE,
                        size, size);
                yield new GameOfLife(size, size, setup, 0);
            }
            case "falling-sand" -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.MARGOLUS,
                        size, size);
                yield new FallingSand(size, size, setup);
            }
            case "forest-fire" -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new ForestFire(size, size, setup);
            }
            case "langtons-ant" -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new LangtonsAnt(size, size, setup, LANGTON_ANTS);
            }
            case "hpp" -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new HPP(size, size, setup);
            }
            case "parity" -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new Parity(size, size, setup);
            }
            case "time-tunnel" -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new TimeTunnel(size, size, setup);
            }
            case "west" -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new West(size, size, setup);
            }
            case "diffusion" -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new Diffusion(size, size, setup);
            }
            case "height-field-water" -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        size, size);
                yield new HeightFieldWater(size, size, setup);
            }
            case "cyclic" -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.MOORE,
                        size, size);
                yield new Cyclic(size, size, setup);
            }
            case "emoticon-hodge" -> {
                Setup setup = new Setup(
                        BoundaryFactory.BoundaryType.PERIODIC,
                        NeighborhoodFactory.NeighborhoodType.MOORE,
                        size, size);
                HodgePodge ca = new HodgePodge(
                        size, size, setup, EMOTICON_N, HodgePodge.DEFAULT_K1, HodgePodge.DEFAULT_K2, EMOTICON_G);
                ca.paintNineSquareGroups();
                yield ca;
            }
            case "genetic-ca" -> {
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
                yield genetic;
            }
            default -> throw new IllegalArgumentException("Unknown CA id: " + caId);
        };
    }

    /**
     * Color map matching DemoApp for the given CA id (used by frame dumps).
     */
    public static ColorMap colorMap(String caId) {
        return switch (caId) {
            case "game-of-life", "west" -> new GrayLevelColorMap(0, 1);
            case "falling-sand" -> new IndexedColorMap(0xF5F0E6, 0xC4A35A, 0x2E2E2E);
            case "forest-fire" -> new IndexedColorMap(0x2B2118, 0x2E7D32, 0xE65100);
            case "langtons-ant" -> new HotColorMap(0, LANGTON_ANTS);
            case "hpp" -> new HotColorMap(0, HPP.NORTH + HPP.SOUTH + HPP.EAST + HPP.WEST);
            case "parity" -> new HotColorMap(0, 1);
            case "time-tunnel" -> new GrayLevelColorMap(0, 3);
            case "diffusion" -> new JetColorMap(0, 1);
            case "height-field-water" -> new BlueColorMap(0, 1);
            case "cyclic" -> new JetColorMap(0, Cyclic.DEFAULT_STATES - 1);
            case "emoticon-hodge" -> new IndexedColorMap(
                    0xF7D774, 0xE8D070, 0xD4C86A, 0xB8C96A, 0x8FBF6A, 0x6BBF7A, 0x5A9B6A);
            case "genetic-ca" -> IDENTITY_RGB;
            default -> throw new IllegalArgumentException("Unknown CA id: " + caId);
        };
    }
}
