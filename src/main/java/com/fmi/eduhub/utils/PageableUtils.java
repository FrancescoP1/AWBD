package com.fmi.eduhub.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PageableUtils {

  @Value("${pagination.maxPageSize}")
  private int MAX_PAGE_SIZE;

  @Value("${pagination.defaultPageSize}")
  private int DEFAULT_PAGE_SIZE;

  public Pageable getFilteredPageable(Pageable pageable) {
    if(pageable == null) {
      pageable = PageRequest.of(1, DEFAULT_PAGE_SIZE);
    }
    int pageSize = pageable.getPageSize();
    if(pageable.getPageSize() > MAX_PAGE_SIZE) {
      pageSize = DEFAULT_PAGE_SIZE;
    }
    return PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());
  }
}
