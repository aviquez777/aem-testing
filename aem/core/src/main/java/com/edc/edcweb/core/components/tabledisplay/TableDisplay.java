package com.edc.edcweb.core.components.tabledisplay;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import com.edc.edcweb.core.helpers.Constants;

@Model(adaptables = Resource.class,
resourceType="edc/components/content/tabledisplay/tabledisplay.html")
public class TableDisplay {

    @Inject
    @Default(values = "basic")
    public String tableType;

    @Inject
    @Default(values = "default")
    public String tableVariant;

    @Inject
    @Default(values = "evenodd")
    public String rowType;

    @Inject
    @Default(values = "1")
    public String rowsToRender;

    @Inject
    @Optional
    public String tableTitle;

    @Inject
    @Optional
    public String tableDescription;

    @Inject
    @Optional
    public String tableFooterNotes;

    @Inject
    @Default(values = "default")
    public String isTableSortable; 

    @Inject
    @Optional
    public Resource tableHeaderRows;

    @Inject
    @Optional
    public Resource tableBodyRows;

    @Inject
    @Optional
    public Resource tableFooterRows;

    public Boolean isTableFooterRowReady() {
        return this.tableFooterRows.hasChildren()&& Constants.Properties.NT_UNSTRUCTURED.equals(this.tableFooterRows.getResourceType());
    }

    public Boolean isStepThreeReady() {	
    	boolean h = this.tableHeaderRows.hasChildren() && Constants.Properties.NT_UNSTRUCTURED.equals(this.tableHeaderRows.getResourceType());
    	boolean b = this.tableBodyRows.hasChildren() && Constants.Properties.NT_UNSTRUCTURED.equals(this.tableBodyRows.getResourceType());
    	return (h && b);
    }
}











