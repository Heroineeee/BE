package com.kkinikong.be.batch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreCsv {
  private String name;
  private String address;
  private String latitude;
  private String longitude;
  private String updatedDate;
  private String category;
}
