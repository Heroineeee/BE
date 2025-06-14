package com.kkinikong.be.util.s3.type;

import lombok.Getter;

@Getter
public enum S3Bucket {
  COMMUNITY_POST_IMAGE("kkini-kong-community-img"),
  STORE_REVIEW_IMAGE("kkini-kong-review-img");
  private final String bucketName;

  S3Bucket(String bucketName) {
    this.bucketName = bucketName;
  }
}
