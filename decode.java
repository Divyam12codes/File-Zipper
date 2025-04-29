import java.io.IOException;

public class decode {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Failed to detect Files");
            System.exit(1);
        }

        huffman f = new huffman(args[0], args[1]);
        try {
            f.decompress();
            System.out.println("Decompressed successfully");
        } catch (Exception e) {
            System.out.println("An error occurred during decompression: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
