package com.yusei.common;

public class PageParam {

  private Integer currentPage=1;

  private Integer pageSize=10;



  public PageParam() {
  }



  public PageParam(Integer currentPage, Integer pageSize) {
    this.currentPage = currentPage;
    this.pageSize = pageSize;
  }


  public Integer getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(Integer currentPage) {
    this.currentPage = currentPage;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    if (pageSize!=null&&pageSize>=0) {
      this.pageSize = pageSize;
    }
  }

  public Integer getOffset() {
    return (currentPage-1)*pageSize;
  }

  public Integer getEndIndex() {
    return currentPage * pageSize - 1;
  }
}
