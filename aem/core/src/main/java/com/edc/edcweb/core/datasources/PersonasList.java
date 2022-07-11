package com.edc.edcweb.core.datasources;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.edc.edcweb.core.helpers.Constants;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import org.apache.sling.api.resource.ResourceResolver;

@Model(adaptables = SlingHttpServletRequest.class)
public class PersonasList {

    private static final Logger log = LoggerFactory.getLogger(PersonasList.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Default(values = "")
    private Resource resource;

    /**
     * Populates the Model with values from the applicable ContentPolicy (based
     * on current path language).
     *
     */
    @PostConstruct
    public void initModel() {
        final ResourceResolver resolver = this.resource.getResourceResolver();
        TagManager tm = resolver.adaptTo(TagManager.class);

        if (tm != null) {
            Tag tag = tm.resolve(Constants.Paths.PERSONA_TAGS);

            @SuppressWarnings("unchecked")
            DataSource ds = new SimpleDataSource(new TransformIterator(tag.listChildren(), new Transformer() {
                @Override
                public Object transform(Object o) {
                    Tag persona = (Tag) o;

                    ValueMap vm = new ValueMapDecorator(new HashMap<>());

                    vm.put(Constants.VALUE, persona.getName());
                    vm.put(Constants.TEXT, persona.getTitle());

                    return new ValueMapResource(resolver, new ResourceMetadata(), Constants.Properties.NT_UNSTRUCTURED, vm);
                }
            }));
            this.request.setAttribute(DataSource.class.getName(), ds);
        }
    }
}
