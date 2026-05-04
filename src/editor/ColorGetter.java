package editor;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

public class ColorGetter {

    private static Color getPixel(int x, int y) throws AWTException {
        Robot rb = new Robot();
        return rb.getPixelColor(x, y);
    }
    public static void main(String[] args) throws AWTException, InterruptedException {
        while (true) {
            Point mouse = MouseInfo.getPointerInfo().getLocation();
            int x = mouse.x, y = mouse.y;
            System.out.println("x = " + x + " y = " + y);
            System.out.println("Color: " + getPixel(x, y));
            Thread.sleep(1000);
        }
    }
}
