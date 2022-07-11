package com.edc.edcweb.core.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.models.ProductCards;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Backing Java code for the Product Cards component used by the EDC web site.
 * 
 * 
 */
public class ProductCardsCollection extends WCMUsePojo
{
	private List<ProductCards> firstCardBulletItems;
	private List<ProductCards> secondCardBulletItems;
	private String firstCtaUrl1;
	private String firstCtaUrl2;
	private String secondCtaUrl1;
	private String secondCtaUrl2;

	/**
	 * Get first <em>Call to Action</em> link # 1 (resolved).  
	 *
	 * @return  String  Link for first CTA button # 1.
	 */	
	public String getFirstCtaUrl1() {
		return firstCtaUrl1;
	}

	/**
	 * Get first <em>Call to Action</em> link # 2 (resolved).  
	 *
	 * @return  String  Link for first CTA button # 2.
	 */	
	public String getFirstCtaUrl2() {
		return firstCtaUrl2;
	}

	/**
	 * Get second <em>Call to Action</em> link # 1 (resolved).  
	 *
	 * @return  String  Link for second CTA button # 1.
	 */	
	public String getSecondCtaUrl1() {
		return secondCtaUrl1;
	}

	/**
	 * Get second <em>Call to Action</em> link # 2 (resolved).  
	 *
	 * @return  String  Link for second CTA button # 2.
	 */	
	public String getSecondCtaUrl2() {
		return secondCtaUrl2;
	}

	/**
	 * Get the bullet items for the first product card.  
	 *
	 * @return  List<ProductCards>  List of bullet items for this product card.
	 */	
	public List<ProductCards> getFirstBulletItems() {
		return this.firstCardBulletItems;
	}

	/**
	 * Get the bullet items for the second product card.  
	 *
	 * @return  List<ProductCards>  List of bullet items for this product card.
	 */	
	public List<ProductCards> getSecondBulletItems() {
		return this.secondCardBulletItems;
	}

	private List<ProductCards> populateBulletItemsModel(Resource resource) {
		List<ProductCards> bulletItems = new ArrayList<>();
		if (resource != null) {
			Iterator<Resource> tileResources = resource.listChildren();
			while (tileResources.hasNext()) {
				ProductCards bulletItem = tileResources.next().adaptTo(ProductCards.class);
				bulletItems.add(bulletItem);
			}
		}
		return bulletItems;
	}

	@Override
	public void activate() throws Exception
	{
		ValueMap properties = getResource().adaptTo(ValueMap.class);
		if(properties != null)
		{
			firstCtaUrl1  = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.FIRST_CTA_URL_1, ""));
			firstCtaUrl2  = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.FIRST_CTA_URL_2, ""));
			secondCtaUrl1 = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.SECOND_CTA_URL_1, ""));
			secondCtaUrl2 = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.SECOND_CTA_URL_2, ""));
		}
		
		/* get the 'firstbulletitems' nodes from the content */
		Resource childResource = getResource().getChild(Constants.Properties.FIRST_BULLET_ITEMS);
		this.firstCardBulletItems = populateBulletItemsModel(childResource);

		/* get the 'secondbulletitems' nodes from the content */
		childResource = getResource().getChild(Constants.Properties.SECOND_BULLET_ITEMS);
		this.secondCardBulletItems = populateBulletItemsModel(childResource);
	}
}
