package com.kkinikong.be.batch.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.kkinikong.be.batch.exception.BatchException;
import com.kkinikong.be.batch.exception.errorcode.BatchErrorCode;

public class FileUtil {

  // 파일의 SHA-256 해시값을 계산
  public static String getFileHash(String filePath) {

    try (FileInputStream fis = new FileInputStream(new File(filePath))) {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      FileInputStream fileInputStream = new FileInputStream(new File(filePath));
      byte[] buffer = new byte[1024];
      int bytesRead;

      while ((bytesRead = fis.read(buffer)) != -1) {
        digest.update(buffer, 0, bytesRead);
      }

      byte[] hashBytes = digest.digest();
      StringBuilder hexString = new StringBuilder();

      for (byte b : hashBytes) {
        hexString.append(String.format("%02x", b));
      }

      return hexString.toString();
    } catch (IOException | NoSuchAlgorithmException e) {
      throw new BatchException(BatchErrorCode.FILE_HASH_FAILED);
    }
  }
}
