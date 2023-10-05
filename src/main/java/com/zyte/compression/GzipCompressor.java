package com.zyte.compression;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class GzipCompressor {
    private void compress(InputStream inputStream, OutputStream outputStream) throws IOException {
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);

        int read;
        byte[] buffer = new byte[4096];

        while ((read = inputStream.read(buffer)) != -1) {
            gzipOutputStream.write(buffer, 0, read);
        }

        gzipOutputStream.close();
        inputStream.close();
    }

    public static void main(String[] args) throws IOException {
        GzipCompressor gzipCompressor = new GzipCompressor();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            // Load the input stream for each iteration
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("amazon.html");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            gzipCompressor.compress(inputStream, outputStream);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time taken: " + totalTime + " ms");
    }
}
