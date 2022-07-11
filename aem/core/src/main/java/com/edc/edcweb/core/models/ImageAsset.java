package com.edc.edcweb.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.edc.edcweb.core.helpers.Constants;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Model to retrieve Image Assets used by the EDC web site.
 * 
 * 
 */
@Model(adaptables = Resource.class)
public class ImageAsset
{
	@Inject
	@Named("imagealttext")
	@Optional
    private String imgAltText;

	@Inject
	@Named("fileReference")
	@Optional
    private String fileRef;

	@Inject
	@Source("sling-object")
	private ResourceResolver resolver;
	
	private String phoneImage="";
    private String tabletImage=""; 
    private String desktopImage="";

    public String getImageAltText()
    {
    	return this.imgAltText;
    }
    
	/**
	 * Get the phone image.  
	 *
	 * @return  String  Image source for the phone breakpoint. Note that if the phone image is not found, the tablet image is returned. If the tablet image also cannot be found, the desktop image is returned.
	 */	
    public String getPhoneImage()
    {
        if (this.phoneImage.isEmpty())
        {
        	this.phoneImage = getImageSourceFromResource(Constants.PHONE_RENDITION);
	        //---------------------------------------------------------------------
	        // If phone image could not be found, try to get tablet image instead
	        //---------------------------------------------------------------------
	        if(this.phoneImage.isEmpty())
	        {
	        	this.phoneImage = this.getTabletImage();
	        }
        }
        //---------------------------------------------------------------------
        return this.phoneImage;
    }

	/**
	 * Get the tablet image.  
	 *
	 * @return  String  Image source for the tablet breakpoint. Note that if the tablet image is not found, the desktop image is returned.
	 */	
    public String getTabletImage()
    {
        if (this.tabletImage.isEmpty())
        {
        	this.tabletImage = getImageSourceFromResource(Constants.TABLET_RENDITION);
	        //---------------------------------------------------------------------
	        // If tablet image could not be found, try to get desktop image instead
	        //---------------------------------------------------------------------
	        if(this.tabletImage.isEmpty())
	        {
	        	this.tabletImage = this.getDesktopImage();
	        }
        }
        //---------------------------------------------------------------------
        return this.tabletImage;
    }

	/**
	 * Get the desktop image.  
	 *
	 * @return  String  Image source for the desktop breakpoint.
	 */	
    public String getDesktopImage()
    {
        //---------------------------------------------------------------------
        if (desktopImage.isEmpty())
        {
        	desktopImage = getImageSourceFromResource(Constants.DESKTOP_RENDITION);
        }
        //---------------------------------------------------------------------
        // If desktop image not found, try to get default image instead
        //---------------------------------------------------------------------
        if(this.desktopImage.isEmpty())
        {
        	this.desktopImage = getImageSourceFromResource(Constants.DEFAULT_RENDITION);
        }
        //---------------------------------------------------------------------
        return desktopImage;
    }

    private String getImageSourceFromResource(String renditionText)
    {
    	String src="";
    	Rendition rendition=null;
        //---------------------------------------------------------------------
        Resource rsrc = resolver.getResource(this.fileRef);
        if (rsrc != null)
        {
	        Asset asset = rsrc.adaptTo(Asset.class);
	        if(asset != null)
	        {
	        	rendition = asset.getRendition(renditionText);
	        }
			if(rendition != null)
			{
		        //-------------------------------------------------------------
				src = Text.escapePath(rendition.getPath());
        	}
        }
        //---------------------------------------------------------------------
        return src;
    }
}