package cellularautomata.cli;

import cellularautomata.cli.collectors.DensityCollector;
import cellularautomata.cli.collectors.GeneticRulesCollector;
import cellularautomata.cli.collectors.HistogramCollector;
import cellularautomata.cli.collectors.ImageFramesCollector;
import cellularautomata.core.ICellularAutomata;
import cellularautomata.core.IStatsCollector;
import evolutionaryAutomata.GeneticCellularAutomata;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Headless batch entry point: parse args, build CA + collectors, run {@link BatchRunner}.
 *
 * @author Daniel Lagrava
 */
public final class BatchCli {

    private static final Set<String> KNOWN_CA = Set.of(
            "game-of-life",
            "falling-sand",
            "forest-fire",
            "langtons-ant",
            "hpp",
            "parity",
            "time-tunnel",
            "west",
            "diffusion",
            "genetic-ca"
    );

    private static final Set<String> KNOWN_COLLECTORS = Set.of(
            "histogram",
            "density",
            "genetic-rules"
    );

    private static final int DEFAULT_SIZE = 200;
    private static final int DEFAULT_STEPS = 100;

    private BatchCli() {
    }

    public static void main(String[] args) {
        int code = run(args, System.out, System.err);
        if (code != 0) {
            System.exit(code);
        }
    }

    /**
     * Parse args and run a batch (or print usage / errors). Package-visible for tests later.
     *
     * @return process exit code (0 = success)
     */
    static int run(String[] args, PrintStream out, PrintStream err) {
        Options options;
        try {
            options = parse(args);
        } catch (UsageException e) {
            err.println(e.getMessage());
            printUsage(err);
            return 2;
        }

        if (options.help) {
            printUsage(out);
            return 0;
        }

        ICellularAutomata ca;
        try {
            ca = createCa(options);
        } catch (IllegalArgumentException e) {
            err.println(e.getMessage());
            return 1;
        }

        PrintStream csvOut = out;
        boolean closeCsv = false;
        try {
            if (needsCsv(options)) {
                if (options.out != null) {
                    Path csvPath = Path.of(options.out);
                    Path parent = csvPath.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                    OutputStream stream = Files.newOutputStream(csvPath);
                    csvOut = new PrintStream(stream, true, StandardCharsets.UTF_8);
                    closeCsv = true;
                }
            }

            List<IStatsCollector> collectors = createCollectors(options, ca, csvOut);
            new BatchRunner().run(ca, options.steps, collectors);
            return 0;
        } catch (UsageException e) {
            err.println(e.getMessage());
            return 2;
        } catch (IOException e) {
            err.println(e.getMessage());
            return 1;
        } finally {
            if (closeCsv) {
                csvOut.close();
            }
        }
    }

    static Options parse(String[] args) {
        Options options = new Options();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-h", "--help" -> options.help = true;
                case "--ca" -> options.ca = requireValue(args, ++i, "--ca");
                case "--size" -> options.size = parsePositiveInt(requireValue(args, ++i, "--size"), "--size");
                case "--steps" -> options.steps = parseNonNegativeInt(requireValue(args, ++i, "--steps"), "--steps");
                case "--collect" -> options.collect = splitCsv(requireValue(args, ++i, "--collect"));
                case "--out" -> options.out = requireValue(args, ++i, "--out");
                case "--frames" -> options.framesDir = requireValue(args, ++i, "--frames");
                case "--histogram-file" -> options.histogramFile = requireValue(args, ++i, "--histogram-file");
                default -> throw new UsageException("Unknown argument: " + arg);
            }
        }

        if (options.help) {
            return options;
        }
        if (options.ca == null || options.ca.isBlank()) {
            throw new UsageException("Missing required --ca");
        }
        String caId = options.ca.toLowerCase(Locale.ROOT);
        if (!KNOWN_CA.contains(caId)) {
            throw new UsageException("Unknown --ca '" + options.ca + "'. Expected one of: " + String.join(", ", KNOWN_CA));
        }
        options.ca = caId;

        if (options.collect.isEmpty() && options.framesDir == null) {
            throw new UsageException("Specify --collect and/or --frames");
        }
        for (String id : options.collect) {
            if (!KNOWN_COLLECTORS.contains(id)) {
                throw new UsageException(
                        "Unknown collector '" + id + "'. Expected one of: " + String.join(", ", KNOWN_COLLECTORS));
            }
        }
        return options;
    }

    private static ICellularAutomata createCa(Options options) {
        return ExampleFactory.create(options.ca, options.size);
    }

    private static boolean needsCsv(Options options) {
        return !options.collect.isEmpty();
    }

    private static List<IStatsCollector> createCollectors(
            Options options, ICellularAutomata ca, PrintStream csvOut) throws IOException {
        List<IStatsCollector> collectors = new ArrayList<>();
        for (String id : options.collect) {
            switch (id) {
                case "histogram" -> collectors.add(new HistogramCollector(csvOut));
                case "density" -> collectors.add(new DensityCollector(csvOut));
                case "genetic-rules" -> {
                    if (!(ca instanceof GeneticCellularAutomata)) {
                        throw new UsageException("genetic-rules requires --ca genetic-ca");
                    }
                    collectors.add(new GeneticRulesCollector(csvOut, options.histogramFile));
                }
                default -> throw new UsageException("Unknown collector '" + id + "'");
            }
        }
        if (options.framesDir != null) {
            Path dir = Path.of(options.framesDir);
            Files.createDirectories(dir);
            collectors.add(new ImageFramesCollector(dir, ExampleFactory.colorMap(options.ca)));
        }
        return collectors;
    }

    private static String requireValue(String[] args, int index, String flag) {
        if (index >= args.length) {
            throw new UsageException(flag + " requires a value");
        }
        return args[index];
    }

    private static int parsePositiveInt(String raw, String flag) {
        int value = parseInt(raw, flag);
        if (value <= 0) {
            throw new UsageException(flag + " must be positive: " + raw);
        }
        return value;
    }

    private static int parseNonNegativeInt(String raw, String flag) {
        int value = parseInt(raw, flag);
        if (value < 0) {
            throw new UsageException(flag + " must be non-negative: " + raw);
        }
        return value;
    }

    private static int parseInt(String raw, String flag) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new UsageException(flag + " must be an integer: " + raw);
        }
    }

    private static List<String> splitCsv(String raw) {
        LinkedHashSet<String> ids = new LinkedHashSet<>();
        for (String part : raw.split(",")) {
            String id = part.trim().toLowerCase(Locale.ROOT);
            if (!id.isEmpty()) {
                ids.add(id);
            }
        }
        return new ArrayList<>(ids);
    }

    private static void printUsage(PrintStream out) {
        out.println("Usage: BatchCli --ca <id> [--size N] [--steps N] --collect <ids> [--out path] [--frames dir] [--histogram-file path]");
        out.println("  --ca                one of: " + String.join(", ", KNOWN_CA));
        out.println("  --size              grid size (default " + DEFAULT_SIZE + ")");
        out.println("  --steps             steps to run (default " + DEFAULT_STEPS + ")");
        out.println("  --collect           comma list: " + String.join(", ", KNOWN_COLLECTORS));
        out.println("  --out               CSV path (stdout if omitted)");
        out.println("  --frames <dir>      also write GIF frames via ImageWriter");
        out.println("  --histogram-file    genetic-rules finish histogram path");
        out.println("  -h, --help          show this help");
    }

    static final class Options {
        boolean help;
        String ca;
        int size = DEFAULT_SIZE;
        int steps = DEFAULT_STEPS;
        List<String> collect = List.of();
        String out;
        String framesDir;
        String histogramFile;
    }

    private static final class UsageException extends RuntimeException {
        UsageException(String message) {
            super(message);
        }
    }

}
