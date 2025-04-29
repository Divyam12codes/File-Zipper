import java.io.IOException;

public class encode {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Failed to detect Files");
            System.exit(1);
        }

        huffman f = new huffman(args[0], args[1]);
        try {
            f.compress();
            System.out.println("Compressed successfully");
        } catch (Exception e) {
            System.out.println("An error occurred during compression: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
