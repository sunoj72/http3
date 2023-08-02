package com.lgcns.dna.http3.demo.models;

public class FileChunkMetainfo {
  long index;
  long offset;
  String checksum;
  long length;
  int status; /* READY, TRANSFERRING, COMPLETED, FAILURE */
}
