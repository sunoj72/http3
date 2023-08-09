package com.lgcns.dna.http3.demo.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lgcns.dna.http3.demo.models.ChunkInfo;
import com.lgcns.dna.http3.demo.models.FileMetainfo;

public class FileUtil {
    @Value("${file.chunk.size:4194304}")
    private static int chunkSize = 1000000; // 4194304;

    public static void main(String[] args) {

        FileMetainfo finfo = generateFileMetainfo("bin/main/static/sample-190.csv");
        System.out.println(finfo);

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

    public static FileMetainfo generateFileMetainfo(String filePath) {
        FileMetainfo info = null;
        List<ChunkInfo> chunkInfos = new ArrayList<>();

        byte[] buffer = new byte[chunkSize];
        
        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
            long fileSize = Files.size(Paths.get(filePath));
            
            int bytesRead;
            int chunkIndex = 0;
            
            while ((bytesRead = bis.read(buffer)) != -1) {
                if (bytesRead == 0) {
                    continue;
                }
                
                sha512.update(buffer, 0, bytesRead);
                ChunkInfo chunkInfo = new ChunkInfo(chunkIndex, chunkIndex * chunkSize, bytesRead, sha512.digest(), 0);
                chunkInfos.add(chunkInfo);
                
                chunkIndex++;
            }

            info = new FileMetainfo(filePath.getBytes("UTF-8"), fileSize, chunkSize, chunkInfos.size(), false, chunkInfos.toArray(new ChunkInfo[chunkInfos.size()]));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return info;
    }

    public static void saveFileMetainfo(String filePath, FileMetainfo info) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(info);
            System.out.println("ChunkInfo saved to file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileMetainfo loadFileMetainfo(String filePath) {
        FileMetainfo info = null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            info = (FileMetainfo) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return info;
    }

    // public static ChunkInfo loadChunkInfo(String filePath, int index) {
    //     ChunkInfo chunkInfo = null;
    //     try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
    //         if (index >= 0 && index < raf.length() / ChunkInfo.SIZE) {
    //             raf.seek(index * ChunkInfo.SIZE);
    //             byte[] buffer = new byte[ChunkInfo.SIZE];
    //             raf.read(buffer);
    //             ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
    //             int chunkIndex = byteBuffer.getInt();
    //             long offset = byteBuffer.getLong();
    //             int bufferSize = byteBuffer.getInt();
    //             byte[] digestBytes = new byte[64];
    //             byteBuffer.get(digestBytes);
    //             String messageDigest = new String(digestBytes);
    //             chunkInfo = new ChunkInfo(chunkIndex, offset, bufferSize, messageDigest);
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    //     return chunkInfo;
    // }

    // public static void saveChunkInfo(String filePath, int index, ChunkInfo chunkInfo) {
    //     try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
    //         raf.seek(index * ChunkInfo.SIZE);
    //         ByteBuffer byteBuffer = ByteBuffer.allocate(ChunkInfo.SIZE);
    //         byteBuffer.putInt(chunkInfo.getIndex());
    //         byteBuffer.putLong(chunkInfo.getOffset());
    //         byteBuffer.putInt(chunkInfo.getBufferSize());
    //         byteBuffer.put(chunkInfo.getMessageDigest().getBytes());
    //         raf.write(byteBuffer.array());
    //         System.out.println("ChunkInfo saved at index " + index);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }    


    public static String byteArrayToHex(byte[] buff) {
        return String.format("%0128x", new BigInteger(1, buff));
    }    
}


