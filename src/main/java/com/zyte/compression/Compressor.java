package com.zyte.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Compressor {
    void compress(InputStream inputStream, OutputStream outputStream) throws IOException;
}
