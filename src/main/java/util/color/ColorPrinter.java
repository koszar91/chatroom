package util.color;

public class ColorPrinter {

    public static void printInColor(Color color, String text) {
        System.out.println(color.ansiCode + text + Color.RESET.ansiCode);
    }

}
