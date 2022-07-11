package com.edc.edcweb.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import com.edc.edcweb.core.helpers.LinkResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

/**

 * Defines the {@code Tiles} Sling Model used for the {@code /apps/edc/content/tiles} component.

 */

@Model(adaptables = Resource.class)

public class Tiles {

	@Inject
	@Named("text")
	@Optional
	private String text;

	@Inject
	@Named("title")
	@Optional
    private String title;
    
    @Inject
    @Named("titlecolors")
    @Optional
    private String titlecolors;

    @Inject
	@Named("date")
	@Optional
	private String date;

	@Inject
	@Named("imagepath")
	@Optional
	private String imagePath;	

	@Inject
	@Named("imagealttext")
	@Optional
	private String imageAltText;

    @Inject
    @Named("linktext")
    @Optional
    private String linkText;

    @Inject
    @Named("linkurl")
    @Optional
    private String linkUrl;

    @Inject
    @Named("linktarget")
    @Optional
    private String linkTarget;
	

	public String getTitle() {
		return title;
    }
    public String getTitlecolors() {
		return titlecolors;
	}

	public String getDate() {
		return date;
	}

	public String getText() {
		return text;
    }

	public String getImagePath() {
		return imagePath;
	}
	
	public String getImageAltText() {
		return imageAltText;
	}

    /**
     * Get the link text.
     *
     */
    public String getLinkText() {
        return linkText;
    }

    /**
     * Get the resolved link URL. (Uses LinkResolver to resolve the link.)
     *
     */
    public String getLinkUrl() {
        return LinkResolver.addHtmlExtension(linkUrl);
    }

    /**
     * Get the link's target (e.g., existing window, new window, etc.).
     *
     */
    public String getLinkTarget() {
        return linkTarget;
    }
}
