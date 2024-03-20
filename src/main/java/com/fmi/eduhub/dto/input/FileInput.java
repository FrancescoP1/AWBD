package com.fmi.eduhub.dto.input;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FileInput {
  private MultipartFile file;
}
