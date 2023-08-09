package com.lgcns.dna.http3.demo.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMetainfo implements Serializable {
  byte[] filename = new byte[2048];
  long length; // 8
  long chunkSize; // 8
  long chunkCount; // 8
  boolean completed; // 1

  ChunkInfo[] chunks; //
}
