package com.lgcns.dna.http3.demo.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChunkInfo implements Serializable {
  private long index; // 8
  private long offset; // 8
  private long length;  // 8
  private byte[] checksum; // 64
  private int status; // 4 /* READY, TRANSFERRING, COMPLETED, FAILURE */
}
