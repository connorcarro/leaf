package ci;

import converter.DayConverter;
import converter.MonthConverter;
import converter.TimeFormat;
import editor.Settings;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import treeNodes.CreateChildNodes;
import treeNodes.FileNode;
import editor.ColumnSelectCaret;
import editor.Find;

public final class SmokeTests {
    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        testConverters();
        testSettings();
        testTreeBuilder();
        testFileNode();
        testFindAndReplace();
        testColumnCaret();

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
        check("Morning conversion", "1:05 AM".equals(format.convert(1, 5)));
        check("Noon conversion", "12:05 PM".equals(format.convert(12, 5)));
        check("Afternoon conversion", "1:05 PM".equals(format.convert(13, 5)));
        check("Short format is populated", !format.format("short").isEmpty());
        check("Long format is populated", !format.format("long").isEmpty());
    }

    private static void testSettings() throws IOException {
        Path configDir = Files.createTempDirectory("leaf-config");
        String oldConfigDir = System.getProperty("leaf.config.dir");
        try {
            System.setProperty("leaf.config.dir", configDir.toString());
            Settings settings = new Settings();
            check("Default font name loads", "Consolas".equals(settings.getFont("Font Name")));
            check("Default font style loads", "0".equals(settings.getFont("Font Style")));
            check("Default font size loads", "18".equals(settings.getFont("Font Size")));

            settings.saveFontSettings("Dialog", "Bold", 20);
            check("Saved font name loads", "Dialog".equals(settings.getFont("Font Name")));
            check("Saved font style loads", "1".equals(settings.getFont("Font Style")));
            check("Saved font size loads", "20".equals(settings.getFont("Font Size")));
        } finally {
            if (oldConfigDir == null) {
                System.clearProperty("leaf.config.dir");
            } else {
                System.setProperty("leaf.config.dir", oldConfigDir);
            }
            deleteRecursively(configDir);
        }
    }

    private static void testTreeBuilder() throws IOException {
        Path tempDir = Files.createTempDirectory("leaf-tree");
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
        Path tempFile = Files.createTempFile("leaf-file", ".txt");
        try {
            FileNode node = new FileNode(tempFile.toFile());
            check("FileNode returns file name", tempFile.getFileName().toString().equals(node.toString()));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private static void testFindAndReplace() throws Exception {
        JTextPane area = new JTextPane();
        area.setText("alpha beta alpha");

        Find find = new Find(area);
        setField(find, "jtf_find", new JTextField("alpha"));
        setField(find, "jtf_replace", new JTextField("gamma"));

        invoke(find, "findNext");
        check("Find selects first match", area.getSelectionStart() == 0 && area.getSelectionEnd() == 5);

        invoke(find, "replaceText");
        check("Replace updates selected text", "gamma beta alpha".equals(area.getText()));

        invoke(find, "findNext");
        check("Find advances to next match", "alpha".equals(area.getSelectedText()));
    }

    private static void testColumnCaret() throws BadLocationException {
        JTextPane pane = new JTextPane();
        pane.setText("first\nsecond");
        ColumnSelectCaret caret = new ColumnSelectCaret();
        pane.setCaret(caret);

        check("Column selection starts empty", caret.selectionEmpty(pane));
        pane.getHighlighter().addHighlight(0, 5, javax.swing.text.DefaultHighlighter.DefaultPainter);
        check("Column selection detects highlight", !caret.selectionEmpty(pane));

        caret.removeText(pane);
        check("Column selection remove deletes text", "\nsecond".equals(pane.getText()));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void invoke(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
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
