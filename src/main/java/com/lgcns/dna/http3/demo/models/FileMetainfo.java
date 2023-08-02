package com.lgcns.dna.http3.demo.models;

import java.util.List;

public class FileMetainfo {
  String filename;
  long length;
  long chunkSize;
  long chunkCount;
  boolean completed;

  List<FileChunkMetainfo> chunks;
}
