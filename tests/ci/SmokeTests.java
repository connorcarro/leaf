package ci;

import converter.DayConverter;
import converter.MonthConverter;
import converter.TimeFormat;
import editor.Settings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.tree.DefaultMutableTreeNode;
import treeNodes.CreateChildNodes;
import treeNodes.FileNode;

public final class SmokeTests {
    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        testConverters();
        testSettings();
        testTreeBuilder();
        testFileNode();

        if (failures > 0) {
            throw new AssertionError(failures + " smoke test(s) failed");
        }

        System.out.println("All smoke tests passed.");
    }

    private static void testConverters() {
        check("Day 1 maps to Sunday", "SUNDAY".equals(DayConverter.convertDay(1)));
        check("Day formatting", "Sunday".equals(DayConverter.capsFormat("SUNDAY")));
        check("Month 0 maps to January", "JANUARY".equals(MonthConverter.convertMonth(0)));
        check("Month formatting", "January".equals(MonthConverter.capsFormat("JANUARY")));

        TimeFormat format = new TimeFormat();
        check("Morning conversion", "1:5 AM".equals(format.convert(1, 5)));
        check("Afternoon conversion", "01:5 PM".equals(format.convert(13, 5)));
        check("Short format is populated", !format.format("short").isEmpty());
        check("Long format is populated", !format.format("long").isEmpty());
    }

    private static void testSettings() {
        Settings settings = new Settings();
        check("Font name loads", "Consolas".equals(settings.getFont("Font Name")));
        check("Font style loads", "0".equals(settings.getFont("Font Style")));
        check("Font size loads", "18".equals(settings.getFont("Font Size")));
    }

    private static void testTreeBuilder() throws IOException {
        Path tempDir = Files.createTempDirectory("text-editor-tree");
        try {
            Path childDir = Files.createDirectory(tempDir.resolve("child"));
            Files.write(childDir.resolve("nested.txt"), new byte[] { 'h', 'i' });

            DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileNode(tempDir.toFile()));
            new CreateChildNodes(tempDir.toFile(), root).run();

            check("Root has one child", root.getChildCount() == 1);
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(0);
            check("Nested directory has one file", child.getChildCount() == 1);
        } finally {
            deleteRecursively(tempDir);
        }
    }

    private static void testFileNode() throws IOException {
        Path tempFile = Files.createTempFile("text-editor-file", ".txt");
        try {
            FileNode node = new FileNode(tempFile.toFile());
            check("FileNode returns file name", tempFile.getFileName().toString().equals(node.toString()));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (root == null || !Files.exists(root)) {
            return;
        }

        try (java.util.stream.Stream<Path> paths = Files.walk(root)) {
            paths.sorted((a, b) -> b.compareTo(a)).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void check(String name, boolean condition) {
        if (!condition) {
            failures++;
            System.err.println("FAIL: " + name);
        }
    }
}
