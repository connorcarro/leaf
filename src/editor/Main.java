package editor;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "--self-test".equals(args[0])) {
            Settings settings = new Settings();
            if (settings.getFont("Font Name").trim().isEmpty() || new converter.TimeFormat().format("short").trim().isEmpty()) {
                throw new IllegalStateException("Application self-test failed.");
            }
            System.out.println("Application self-test passed.");
            return;
        }

        if (args.length > 0 && "--startup-test".equals(args[0])) {
            SwingUtilities.invokeAndWait(() -> {
                new TextEdit();
                TextEdit.disposeForStartupTest();
            });
            System.out.println("Application startup test passed.");
            System.exit(0);
        }

        new TextEdit();
    }
}
