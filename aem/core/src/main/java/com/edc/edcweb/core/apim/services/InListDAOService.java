package com.edc.edcweb.core.apim.services;

import com.edc.edcweb.core.apim.pojo.inlist.filter.SupplierCardsAndFiltersDO;
import com.edc.edcweb.core.apim.pojo.inlist.filter.SupplierFiltersDO;
import com.edc.edcweb.core.apim.pojo.inlist.supplier.SupplierProfileDO;

public interface InListDAOService {
	public SupplierCardsAndFiltersDO getSupplierFilter(String langAbbr) ;
	public SupplierProfileDO getSupplierProfile(String langAbbr, String id);
}
