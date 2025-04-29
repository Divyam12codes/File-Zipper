import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class HuffmanGUI {
    private JFrame frame;
    private JTextArea textArea;
    private JLabel originalSizeLabel;
    private JLabel compressedSizeLabel;
    private File selectedFile;

    public HuffmanGUI() {
        frame = new JFrame("Huffman Compressor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        JButton chooseButton = new JButton("Choose File");
        JButton compressButton = new JButton("Compress");
        JButton decompressButton = new JButton("Decompress");

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        originalSizeLabel = new JLabel("Original Size: ");
        compressedSizeLabel = new JLabel("Compressed Size: ");

        chooseButton.addActionListener(e -> chooseFile());
        compressButton.addActionListener(e -> compressFile());
        decompressButton.addActionListener(e -> decompressFile());

        JPanel panel = new JPanel();
        panel.add(chooseButton);
        panel.add(compressButton);
        panel.add(decompressButton);

        JPanel statsPanel = new JPanel(new GridLayout(2, 1));
        statsPanel.add(originalSizeLabel);
        statsPanel.add(compressedSizeLabel);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(statsPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            textArea.setText("Selected File: " + selectedFile.getAbsolutePath());
            originalSizeLabel.setText("Original Size: " + selectedFile.length() + " bytes");
        }
    }

    private void compressFile() {
        if (selectedFile == null) {
            textArea.setText("Please choose a file first.");
            return;
        }
        String outputPath = selectedFile.getAbsolutePath() + ".huff";
        huffman hf = new huffman(selectedFile.getAbsolutePath(), outputPath);
        hf.compress();
        File compressed = new File(outputPath);
        compressedSizeLabel.setText("Compressed Size: " + compressed.length() + " bytes");
        textArea.append("\nCompressed to: " + outputPath);
    }

    private void decompressFile() {
        if (selectedFile == null) {
            textArea.setText("Please choose a file first.");
            return;
        }
        String outputPath = selectedFile.getAbsolutePath().replace(".huff", ".decoded.txt");
        huffman hf = new huffman(selectedFile.getAbsolutePath(), outputPath);
        hf.decompress();
        textArea.append("\nDecompressed to: " + outputPath);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HuffmanGUI::new);
    }
}
