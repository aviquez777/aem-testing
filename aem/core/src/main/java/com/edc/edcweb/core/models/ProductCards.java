package com.edc.edcweb.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class ProductCards
{
	@Inject
	@Named("bulletitem")
	@Optional
	private String bulletItem;

	public String getBulletItem() {
		return bulletItem;
	}
	
	public void setBulletItem(String dialogBulletItem) {
		bulletItem = dialogBulletItem;
	}

}
