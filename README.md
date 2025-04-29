# Huffman Compression-Decompression Tool

This is a Java-based file compression and decompression tool using **Huffman Coding** — a popular algorithm for lossless data compression.

## 📌 Features

- ✅ Compress any text file into a `.huff` encoded file.
- ✅ Decompress `.huff` files to restore original content.
- ✅ GUI-based file chooser for selecting input/output files.
- ✅ Displays statistics like original size, compressed size, and compression ratio.
- ✅ Implements binary tree for Huffman code generation.
- ✅ Includes efficient bit-level file writing.

## 🧠 How It Works

1. **Compression:**
   - Reads the input file and counts character frequencies.
   - Builds a Huffman Tree and generates codes.
   - Encodes content using Huffman codes and writes it in binary form.

2. **Decompression:**
   - Reads encoded file header to rebuild the Huffman Tree.
   - Decodes the binary content to retrieve original data.

