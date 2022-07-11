package com.edc.edcweb.core.helpers.gatedleadgenform;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.edc.edcweb.core.helpers.Constants;

public class CglgfUtils {

    private CglgfUtils() {
        // Sonar Lint
    }

    public static String getUsedQuestions (Resource cpRes, boolean usePath) {
        StringBuilder sb = new StringBuilder();
        Resource multifield = cpRes.getChild(GlgfConstants.QUESTION_CONFIGURED_MULTIFIELD);
        boolean isFirst = true;
        if (multifield != null) {
            Iterator<Resource> questions = multifield.listChildren();
            while (questions.hasNext()) {
                Resource question = questions.next();
                String questionPath = question.getValueMap().get(GlgfConstants.QUESTION_TO_USE, String.class);
                if (usePath) {
                    questionPath = question.getPath();
                }
                if (isFirst) {
                    sb.append(GlgfConstants.ESCAPED_QUOTE);
                    isFirst = false;
                } else {
                    sb.append(GlgfConstants.COMMA_ESCAPED_QUOTE);
                }
                sb.append(questionPath).append(GlgfConstants.ESCAPED_QUOTE);
            }
        }
        return sb.toString();
    }

    public static DataSource getFromResource(ResourceResolver resolver, Iterator<Resource> questionList) {
        @SuppressWarnings("unchecked")
        DataSource ds = new SimpleDataSource(new TransformIterator(questionList, new Transformer() {
            @Override
            public Object transform(Object o) {
                Resource question = (Resource) o;
                ValueMap vm = new ValueMapDecorator(new HashMap<>());
                vm.put(Constants.VALUE, question.getPath());
                vm.put(Constants.TEXT, buildQuestionName(question));
                return new ValueMapResource(resolver, new ResourceMetadata(), Constants.Properties.NT_UNSTRUCTURED, vm);
            }
        }));
        return ds;
    }

    public static DataSource getFromArray(ResourceResolver resolver, Iterator<String> arrayList) {
        @SuppressWarnings("unchecked")
        DataSource ds = new SimpleDataSource(new TransformIterator(arrayList, new Transformer() {
            @Override
            public Object transform(Object o) {
                String confivalue = (String) o;
                String[] confivaluesArr = confivalue.split(GlgfConstants.MULTIPLE_DELIMITER);
                ValueMap vm = new ValueMapDecorator(new HashMap<>());
                vm.put(Constants.VALUE, confivaluesArr[0]);
                vm.put(Constants.TEXT, confivaluesArr[1]);
                return new ValueMapResource(resolver, new ResourceMetadata(), Constants.Properties.NT_UNSTRUCTURED, vm);
            }
        }));
        return ds;
    }

    private static String buildQuestionName(Resource question) {
        String text = question.getValueMap().get(GlgfConstants.QUESTION_NAME, String.class);
        String type = question.getValueMap().get(GlgfConstants.QUESTION_TYPE, String.class);
        if (StringUtils.isNotBlank(type)) {
            text = text.concat(" (").concat(type).concat(")");
        }
        return text;
    }
}
