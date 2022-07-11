package com.edc.edcweb.core.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.models.MultiColumnTextElement;
import com.edc.edcweb.core.models.MultiColumnTextModel;

/**
 * Four Column Text to be used for retrieving the Title and Text
 * author-configurable values. Uses Sling Model
 * com.edc.edcweb.core.models.MultiColumnTextModel
 */
public class MultiColumnText extends WCMUsePojo {

  private MultiColumnTextModel multiColumnTextModel;

  @Override
  public void activate() throws Exception {
    multiColumnTextModel = getResource().adaptTo(MultiColumnTextModel.class);
  }

  public MultiColumnTextModel getMultiColumnTextModel() {
    return multiColumnTextModel;
  }

}
