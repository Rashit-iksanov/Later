public class ConsoleApp {
    public static void main(String[] args) {
        String symbol = "*";

        int width = 10;
        int height = 5;

        for (int i = 1; i <= height; i++) {
            for (int j = 1; j <= width; j++) {
                System.out.print(symbol);
            }
            System.out.println();
        }
    }
}
