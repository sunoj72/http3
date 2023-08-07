package com.lgcns.dna.http3.demo.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class SampleDatasets {
  public byte[] smaple10;
  public String smaple10Chechsum = "";

  public SampleDatasets() {
    initDatasets();
  }

  public synchronized void initDatasets() {
    Random r = new Random();
    this.smaple10 = new byte[10 * 1024 * 1024];
    r.nextBytes(this.smaple10);
    smaple10Chechsum = createChecksum(this.smaple10);
  }

  public String createChecksum(byte[] buff) {
    MessageDigest digest;
    String result="";

    try {
      digest = MessageDigest.getInstance("SHA-512");
      digest.reset();
      digest.update(buff);
      result = String.format("%0128x", new BigInteger(1, digest.digest()));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return result;
  }
      

}
