package editor;

import java.awt.Font;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class Settings {
    private static final String CONFIG_DIR_PROPERTY = "leaf.config.dir";
    private static final String DEFAULT_FONT_NAME = "Consolas";
    private static final String DEFAULT_FONT_STYLE = "PLAIN";
    private static final String DEFAULT_FONT_SIZE = "18";

    private String[] fetch() {
        Path settingsPath = getSettingsPath();
        try {
            if (Files.notExists(settingsPath)) {
                saveFontSettings(DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, Integer.parseInt(DEFAULT_FONT_SIZE));
            }

            String text = new String(Files.readAllBytes(settingsPath), StandardCharsets.UTF_8).trim();
            String[] values = text.split(",");
            if (values.length == 3 && isValidFontSize(values[2])) {
                return values;
            }
        } catch (IOException | NumberFormatException e) {
            return new String[] { DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE };
        }

        return new String[] { DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE };
    }

    private boolean isValidFontSize(String fontSize) {
        try {
            return Integer.parseInt(fontSize) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getFont(String type) {
        String[] list = fetch();
        switch (type) {
            case "Font Name":
                return list[0];

            case "Font Style":
                return String.valueOf(fontStyleFromName(list[1]));

            case "Font Size":
                return list[2];
        }
        return "";
    }

    public void saveFontSettings(String fontName, String fontStyle, int fontSize) throws IOException {
        Path settingsPath = getSettingsPath();
        Files.createDirectories(settingsPath.getParent());
        Files.write(settingsPath, (fontName + "," + fontStyle.toUpperCase(Locale.ROOT) + "," + fontSize).getBytes(StandardCharsets.UTF_8));
    }

    private int fontStyleFromName(String fontStyle) {
        switch (fontStyle.toUpperCase(Locale.ROOT)) {
            case "BOLD":
                return Font.BOLD;
            case "ITALIC":
                return Font.ITALIC;
            case "BOLD ITALIC":
                return Font.BOLD | Font.ITALIC;
            default:
                return Font.PLAIN;
        }
    }

    private Path getSettingsPath() {
        String overrideDir = System.getProperty(CONFIG_DIR_PROPERTY);
        if (overrideDir != null && !overrideDir.trim().isEmpty()) {
            return Paths.get(overrideDir, "settings_data.txt");
        }

        return getConfigDirectory().resolve("settings_data.txt");
    }

    private Path getConfigDirectory() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.trim().isEmpty()) {
                return Paths.get(appData, "Leaf");
            }
        }

        if (os.contains("mac")) {
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "Leaf");
        }

        String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
        if (xdgConfigHome != null && !xdgConfigHome.trim().isEmpty()) {
            return Paths.get(xdgConfigHome, "leaf");
        }

        return Paths.get(System.getProperty("user.home"), ".config", "leaf");
    }
}
