package com.kkinikong.be.batch.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class FileUtil {

  // 파일의 SHA-256 해시값을 계산하는 함수
  public static String getFileHash(String filePath) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      FileInputStream fileInputStream = new FileInputStream(new File(filePath));
      byte[] byteArray = new byte[1024];
      int bytesRead;

      while ((bytesRead = fileInputStream.read(byteArray)) != -1) {
        digest.update(byteArray, 0, bytesRead);
      }

      byte[] hashBytes = digest.digest();
      StringBuilder hexString = new StringBuilder();

      for (byte b : hashBytes) {
        hexString.append(Integer.toHexString(0xFF & b));
      }

      return hexString.toString(); // SHA-256 해시값 반환
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
