package com.lgcns.dna.http3.demo.util;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.NumberFormat;

public class FileUtil {
    public static void main(String[] args) {
        String path = args[0];
        long fileSize = Long.parseLong(args[1]);
        NumberFormat formatter = NumberFormat.getNumberInstance();

        fileSize = 100;
        System.out.println("Create pre-allocated File: " + path + " (" + formatter.format(fileSize) + ")");
        allocateFile(path, fileSize);

        byte[] a = new byte[10];
        for (int i=0;i<a.length;i++) {
            a[i] = 65;
        }

        ByteBuffer buff = ByteBuffer.wrap(a, 0, a.length);

        writeChunk(path, buff, 10, a.length);
        // writeChunk(path, buff, 30, a.length);
    }

    // https://www.oracle.com/technical-resources/articles/javase/perftuning.html
    public static void allocateFile(String fileName, long fileSize) {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            raf.setLength(fileSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeChunk(String fileName, ByteBuffer buffer, long offset, long size) {
        try (FileChannel outputChannel = new FileOutputStream(fileName).getChannel()) {
            buffer.flip();
            outputChannel.position(offset);
            outputChannel.write(buffer, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
