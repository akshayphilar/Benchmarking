package com.zyte.compression;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream;
import com.aayushatharva.brotli4j.encoder.Encoder;

import java.io.*;

public class BrotliCompressor implements Compressor {

    static {
        Brotli4jLoader.ensureAvailability();
    }

    @Override
    public void compress(InputStream inputStream, OutputStream outputStream) throws IOException {
        Encoder.Parameters params = new Encoder.Parameters().setQuality(3);
        BrotliOutputStream brotliOutputStream = new BrotliOutputStream(outputStream, params);

        int read;
        byte[] buffer = new byte[4096];

        while ((read = inputStream.read(buffer)) != -1) {
            brotliOutputStream.write(buffer, 0, read);
        }

        brotliOutputStream.close();
        inputStream.close();
    }

    public static void main(String[] args) throws IOException {
        BrotliCompressor brotliCompressor = new BrotliCompressor();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("amazon.html");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            brotliCompressor.compress(inputStream, outputStream);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time taken: " + totalTime + " ms");
    }
}