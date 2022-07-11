package com.edc.edcportal.core.transactionhistory.pojo;

import java.util.List;

public class THSearchResults {
    List<THRecordDO> elements;
    int page = 0;
    int pageSize = 0;
    int total = 0;

    public List<THRecordDO> getElements() {
        return elements;
    }

    public void setElements(List<THRecordDO> elements) {
        this.elements = elements;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
