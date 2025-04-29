import java.io.*;
import java.util.*;

class Node {
    char data;
    int freq;
    String code;
    Node left, right;

    Node() {
        this.data = '\0';
        this.freq = 0;
        this.code = "";
        this.left = null;
        this.right = null;
    }
}

class CompareNodes implements Comparator<Node> {
    public int compare(Node n1, Node n2) {
        return n1.freq - n2.freq;
    }
}

public class huffman {
    private List<Node> arr;
    private PriorityQueue<Node> minHeap;
    private Node root;
    private String inFileName;
    private String outFileName;

    public huffman(String inFileName, String outFileName) {
        this.inFileName = inFileName;
        this.outFileName = outFileName;
    }

    private void createArr() {
        arr = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) {
            Node node = new Node();
            node.data = (char) i;
            node.freq = 0;
            arr.add(node);
        }
    }

    private void traverse(Node r, String str) {
        if (r.left == null && r.right == null) {
            r.code = str;
            return;
        }
        if (r.left != null) traverse(r.left, str + '0');
        if (r.right != null) traverse(r.right, str + '1');
    }

    private int binToDec(String inStr) {
        int res = 0;
        for (char c : inStr.toCharArray()) {
            res = res * 2 + (c - '0');
        }
        return res;
    }

    private String decToBin(int inNum) {
        StringBuilder temp = new StringBuilder();
        while (inNum > 0) {
            temp.append((char) ((inNum % 2) + '0'));
            inNum /= 2;
        }
        while (temp.length() < 8) {
            temp.append('0');
        }
        return temp.reverse().toString();
    }

    private void buildTree(char aCode, String path) {
        Node curr = root;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '0') {
                if (curr.left == null) curr.left = new Node();
                curr = curr.left;
            } else {
                if (curr.right == null) curr.right = new Node();
                curr = curr.right;
            }
        }
        curr.data = aCode;
    }

    private void createMinHeap() throws IOException {
        createArr();
        minHeap = new PriorityQueue<>(new CompareNodes());
        try (FileInputStream inFile = new FileInputStream(inFileName)) {
            int id;
            while ((id = inFile.read()) != -1) {
                arr.get(id).freq++;
            }
        }
        for (Node node : arr) {
            if (node.freq > 0) {
                minHeap.add(node);
            }
        }
    }

    private void createTree() {
        PriorityQueue<Node> tempPQ = new PriorityQueue<>(minHeap);
        while (tempPQ.size() > 1) {
            Node left = tempPQ.poll();
            Node right = tempPQ.poll();
            Node parent = new Node();
            parent.freq = left.freq + right.freq;
            parent.left = left;
            parent.right = right;
            tempPQ.add(parent);
        }
        root = tempPQ.poll();
    }

    private void createCodes() {
        traverse(root, "");
    }

    private void saveEncodedFile() throws IOException {
        try (FileInputStream inFile = new FileInputStream(inFileName);
             FileOutputStream outFile = new FileOutputStream(outFileName)) {

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            // Write number of characters
            outStream.write(minHeap.size());

            PriorityQueue<Node> tempPQ = new PriorityQueue<>(minHeap);
            while (!tempPQ.isEmpty()) {
                Node curr = tempPQ.poll();
                outStream.write(curr.data);

                // Fix: Pad Huffman code to 128 bits
                StringBuilder codeStr = new StringBuilder("1").append(curr.code);
                while (codeStr.length() < 128) {
                    codeStr.insert(0, '0');
                }

                for (int i = 0; i < 16; i++) {
                    String byteStr = codeStr.substring(i * 8, (i + 1) * 8);
                    outStream.write(binToDec(byteStr));
                }

                arr.get((int) curr.data).code = curr.code;
            }

            StringBuilder bits = new StringBuilder();
            int id;
            while ((id = inFile.read()) != -1) {
                bits.append(arr.get(id).code);
                while (bits.length() >= 8) {
                    String byteStr = bits.substring(0, 8);
                    outStream.write(binToDec(byteStr));
                    bits.delete(0, 8);
                }
            }

            int padBits = 8 - bits.length();
            if (bits.length() > 0) {
                bits.append("0".repeat(padBits));
                outStream.write(binToDec(bits.toString()));
            }

            outStream.write(padBits); // padding info

            outFile.write(outStream.toByteArray());
        }
    }

    private void saveDecodedFile() throws IOException {
        try (FileInputStream inFile = new FileInputStream(inFileName);
             FileOutputStream outFile = new FileOutputStream(outFileName)) {

            int size = inFile.read();
            root = new Node();

            for (int i = 0; i < size; i++) {
                int aCode = inFile.read();
                byte[] hCodeC = new byte[16];
                inFile.read(hCodeC);
                StringBuilder hCodeStr = new StringBuilder();
                for (byte b : hCodeC) {
                    hCodeStr.append(decToBin(b & 0xFF));
                }
                int j = 0;
                while (hCodeStr.charAt(j) == '0') j++;
                String path = hCodeStr.substring(j + 1);
                buildTree((char) aCode, path);
            }

            ByteArrayOutputStream text = new ByteArrayOutputStream();
            int b;
            while ((b = inFile.read()) != -1) {
                text.write(b);
            }

            byte[] textArr = text.toByteArray();
            int padBits = textArr[textArr.length - 1];

            Node curr = root;
            for (int i = 0; i < textArr.length - 1; i++) {
                String bits = decToBin(textArr[i] & 0xFF);
                if (i == textArr.length - 2) {
                    bits = bits.substring(0, 8 - padBits);
                }
                for (char bit : bits.toCharArray()) {
                    curr = (bit == '0') ? curr.left : curr.right;
                    if (curr.left == null && curr.right == null) {
                        outFile.write(curr.data);
                        curr = root;
                    }
                }
            }
        }
    }

    public void compress() {
        try {
            createMinHeap();
            createTree();
            createCodes();
            saveEncodedFile();
            System.out.println("Compression completed successfully.");
        } catch (IOException e) {
            System.err.println("Compression failed: " + e.getMessage());
        }
    }

    public void decompress() {
        try {
            saveDecodedFile();
            System.out.println("Decompression completed successfully.");
        } catch (IOException e) {
            System.err.println("Decompression failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage:\n  java huffman compress input.txt output.huff");
            System.out.println("  java huffman decompress input.huff output.txt");
            return;
        }

        huffman h = new huffman(args[1], args[2]);
        if (args[0].equalsIgnoreCase("compress")) {
            h.compress();
        } else if (args[0].equalsIgnoreCase("decompress")) {
            h.decompress();
        } else {
            System.out.println("Invalid command. Use 'compress' or 'decompress'.");
        }
    }
}
