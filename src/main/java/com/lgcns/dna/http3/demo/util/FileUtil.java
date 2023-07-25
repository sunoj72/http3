package com.lgcns.dna.http3.demo.util;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;

public class FileUtil {
    public static void main(String[] args) {
        String path = args[0];
        long fileSize = Long.parseLong(args[1]);
        NumberFormat formatter = NumberFormat.getNumberInstance();

        fileSize = 160;
        System.out.println("Create pre-allocated File: " + path + " (" + formatter.format(fileSize) + ")");
        allocateFile(path, fileSize);

        byte[] a = new byte[16];
        for (int i=0;i<a.length;i++) {
            a[i] = 65;
        }

        ByteBuffer buff = ByteBuffer.wrap(a, 0, a.length);

        writeChunkByFileChannel(path, buff, 16, a.length);
        writeChunkByFileChannel(path, buff, 48, a.length);

        // writeChunkByRandomAccessFile(path, a, 16, a.length);
        // writeChunkByRandomAccessFile(path, a, 48, a.length);
    }

    // https://www.oracle.com/technical-resources/articles/javase/perftuning.html
    public static void allocateFile(String fileName, long fileSize) {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            raf.setLength(fileSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeChunkByFileChannel(String fileName, ByteBuffer buffer, long offset, long size) {
        try (FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            buffer.rewind();
            channel.position(offset);

            while(buffer.hasRemaining()) {
                channel.write(buffer);
            }            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeChunkAsyncFileChannel(String fileName, ByteBuffer buffer, long offset, long size) {
        // TODO
    }

    public static void writeChunkByRandomAccessFile(String fileName, byte[] buffer, long offset, long size) {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            raf.seek(offset);
            raf.write(buffer);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
