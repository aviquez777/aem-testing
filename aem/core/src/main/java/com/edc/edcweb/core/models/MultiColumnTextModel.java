package com.edc.edcweb.core.models;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import com.edc.edcweb.core.helpers.LinkResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

/**
 *
 * @author steven.mendez
 * @since Feb 02 2018
 * @version 1.0
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MultiColumnTextModel {

  @Inject
  @Named("titleGeneral")
  @Optional
  private String titleGeneral;

  @Inject
  @Named("enableBackground")
  @Optional
  private String enableBackground;

  @Inject
  @Named("enableLine")
  @Optional
  private String enableLine;

  @Inject
  @Named("items")
  @Optional
  private List<MultiColumnTextElement> multiColumnTextModels;

  @Inject
  @Named("description")
  @Optional
  private String description;

  @Inject
  @Named("linkurl")
  @Optional
  private String linkurl;

  @Inject
  @Named("linktarget")
  @Optional
  private String linktarget;

  @Inject
  @Named("alignment")
  @Optional
  private String alignment;

  public String getTitleGeneral() {
    return titleGeneral;
  }

  public String getEnableBackground() {
    return enableBackground;
  }

  public List<MultiColumnTextElement> getMultiColumnTextModels() {
    return multiColumnTextModels;
  }

  public String getEnableLine() {
    return enableLine;
  }

  public String getDescription() {
    return description;
  }

  public String getLinkurl() {
    return LinkResolver.addHtmlExtension(linkurl);
  }

  public String getLinktarget() {
    return linktarget;
  }

  public String getAlignment() {
    return alignment;
  }
}