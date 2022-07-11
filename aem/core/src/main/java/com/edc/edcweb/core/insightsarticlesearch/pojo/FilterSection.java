package com.edc.edcweb.core.insightsarticlesearch.pojo;

import java.util.List;
import com.edc.edcweb.core.models.TagFilter;

public class FilterSection {

    private String filterName;
    private String filterLabel;
    private List<TagFilter> subFilters;
    private boolean showMore;
    private boolean isTrending;
    private String trendingLabel;

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterLabel() {
        return filterLabel;
    }

    public void setFilterLabel(String filterLabel) {
        this.filterLabel = filterLabel;
    }

    public List<TagFilter> getSubFilters() {
        return subFilters;
    }

    public void setSubFilters(List<TagFilter> subFilters) {
        this.subFilters = subFilters;
    }

    public boolean isShowMore() {
        return showMore;
    }

    public void setShowMore(boolean showMore) {
        this.showMore = showMore;
    }

    public boolean isTrending() {
        return isTrending;
    }

    public void setTrending(boolean isTrending) {
        this.isTrending = isTrending;
    }

    public String getTrendingLabel() {
        return trendingLabel;
    }

    public void setTrendingLabel(String trendingLabel) {
        this.trendingLabel = trendingLabel;
    }

}
