
package com.edc.edcweb.core.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.models.Tiles;


/**
 * Tiles Collection to be used for retrieving /apps/edc/content/tiles component properties
 * Uses Sling Model com.edc.edcweb.core.models.Tiles  
 */

public class TilesCollection extends WCMUsePojo {

	private java.util.List<Tiles> tiles;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public List<Tiles> getTiles()
	{
		return this.tiles;
	}

	public java.util.List<Tiles> populateModel(Resource resource) {
		java.util.List<Tiles> itemTiles = new ArrayList<>();
		if (resource != null) {
			logger.debug("Resource is " + resource.getPath());
			
			//get all the child elements of 'items' i.e. items0, items1, items2
			Iterator<Resource> tileResources = resource.listChildren();
			
			while (tileResources.hasNext()) {
				Tiles tile = tileResources.next().adaptTo(Tiles.class);
				itemTiles.add(tile);
			}
		}
		return itemTiles;
	}

	
	@Override
	public void activate() throws Exception
	{
		/* get the 'items' nodes from the content */
		Resource childResource = getResource().getChild(Constants.Properties.ITEMS);
		this.tiles = populateModel(childResource);
	}

}