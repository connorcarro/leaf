package editor;
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && "--self-test".equals(args[0])) {
            Settings settings = new Settings();
            if (settings.getFont("Font Name").isBlank() || new converter.TimeFormat().format("short").isBlank()) {
                throw new IllegalStateException("Application self-test failed.");
            }
            System.out.println("Application self-test passed.");
            return;
        }

        new TextEdit();
    }
}
