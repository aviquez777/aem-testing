package com.edc.edcweb.core.models;

import com.edc.edcweb.core.helpers.LinkResolver;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

/**
 * Defines the {@code MultiColumnTextModelElement} Sling Model used for the
 * {@code /apps/edc/content/multicolumtext} component.
 *
 * @author steven.mendez
 * @since Feb 02 2018
 * @version 1.0
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MultiColumnTextElement {

  @Inject
  @Named("text")
  private String text;

  @Inject
  @Named("title")
  private String title;

  @Inject
  @Named("linkEnable")
  @Optional
  private boolean linkEnable;

  @Inject
  @Named("linkValue")
  @Optional
  private String linkValue;

  @Inject
  @Named("linktarget")
  @Optional
  private String linktarget;

  @PostConstruct
  protected void init() {
    this.linkEnable = false;
    if (null == this.linkValue || "".equals(this.linkValue)) {
      this.linkEnable = false;
    } else {
      String resultUrl = LinkResolver.addHtmlExtension(this.linkValue);
      if (!"".equals(resultUrl)) {
        this.linkValue = resultUrl;
        this.linkEnable = true;
      }
    }
  }

  public String getTitle() {
    return title;
  }

  public String getText() {
    return text;
  }

  public String getLinkValue() {
    return linkValue;
  }

  public String getLinktarget() {
    return linktarget;
  }

  public boolean isLinkEnable() {
    return linkEnable;
  }
}
