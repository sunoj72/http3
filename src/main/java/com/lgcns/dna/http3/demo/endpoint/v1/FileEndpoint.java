package com.lgcns.dna.http3.demo.endpoint.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lgcns.dna.http3.demo.service.SampleDatasets;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;

@RestController
@RequestMapping("files")
public class FileEndpoint {

    @Autowired
    private SampleDatasets datasets;

    @GetMapping("/csv")
    public byte[] getCsvFile() throws URISyntaxException, IOException {
        String csvFilePath = "static/sample.csv";
        URL url = getClass().getClassLoader().getResource(csvFilePath);
        if (url == null) {
            throw new IOException("File " + csvFilePath + " not found");
        }
        Path path = Paths.get(url.toURI());
        String fileContent = Files.readString(path);
        return fileContent.getBytes();
    }

    @PostMapping("/download-chunk")
    public ResponseEntity<ByteArrayResource> downloadChunk() throws URISyntaxException, IOException {
        return ResponseEntity.ok()
                .contentLength(datasets.smaple10.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("X-DNA-CHUNK-CHECKSUM", datasets.smaple10Chechsum)
                .header("X-DNA-CHUNK-SIZE", Integer.toString(datasets.smaple10.length))
                .body(new ByteArrayResource(datasets.smaple10));
    }

    @PostMapping(value = "/upload-chunk") // , consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE
    @ResponseBody    
    // public ResponseEntity<String> uploadChunk(HttpServletRequest request, @RequestBody InputStream chunkStream) throws IOException {
    public ResponseEntity<String> uploadChunk(HttpServletRequest request) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        int chunkSize = Integer.parseInt(request.getHeader("X-DNA-CHUNK-SIZE"));

        byte[] data = new byte[chunkSize]; // 버퍼 크기를 조정하세요.

        ServletInputStream chunkStream = request.getInputStream();

        while ((nRead = chunkStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        
        buffer.flush();
        byte[] buff = buffer.toByteArray();

        NumberFormat formatter = NumberFormat.getNumberInstance();

        System.out.println("[Chunk Checksums] " + request.getHeader("X-DNA-CHUNK-CHECKSUM") + ", " + datasets.createChecksum(buff));
        System.out.println("[Chunk Sizes] " + request.getHeader("X-DNA-CHUNK-SIZE") + ", " +  formatter.format(buff.length));

        return ResponseEntity.ok("Chunk uploaded successfully!");
    }
}