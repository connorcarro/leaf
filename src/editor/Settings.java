package editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.nio.file.Path;

public class Settings {

    private String[] fetch() {
        File f = Path.of("src", "editor", "settings_data.txt").toFile();
        String text = "";
        try {
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()) {
                text += sc.nextLine();
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String[] font = text.trim().split(",");
        return font;
    }

    public String getFont(String type) {
        String[] list = fetch();
        switch (type) {
            case "Font Name":
                return list[0];
            
            case "Font Style":
                switch (list[1]) {
                    case "PLAIN":
                        return "0";
                    case "BOLD":
                        return "1";
                    case "ITALIC":
                        return "2";
                    case "BOLD ITALIC":
                        return "3";
                }

            case "Font Size":
                return list[2];
        }
        return "";
    }
}
