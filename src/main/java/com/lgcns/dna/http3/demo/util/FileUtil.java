package com.lgcns.dna.http3.demo.util;

import java.io.RandomAccessFile;
import java.text.NumberFormat;

public class FileUtil {
    public static void main(String[] args) {
        String path = args[0];
        long fileSize = Long.parseLong(args[1]);
        NumberFormat formatter = NumberFormat.getNumberInstance();

        System.out.println("Create pre-allocated File: " + path + " (" + formatter.format(fileSize) + ")");
        allocateFile(path, fileSize);
    }

    // https://www.oracle.com/technical-resources/articles/javase/perftuning.html
    public static void allocateFile(String fileName, long fileSize) {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            raf.setLength(fileSize);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}


// https://www.baeldung.com/java-filechannel
// private void copyUsingChannel() throws IOException {
//   try (
//       FileChannel inputChannel = new FileInputStream(source).getChannel();
//       FileChannel outputChannel = new FileOutputStream(target).getChannel();
//   ) {
//     ByteBuffer buffer = ByteBuffer.allocateDirect(4 * 1024);
//     while (inputChannel.read(buffer) != -1) {
//       buffer.flip();
//       outputChannel.write(buffer);
//       buffer.clear();
//     }
//   } 
// }
