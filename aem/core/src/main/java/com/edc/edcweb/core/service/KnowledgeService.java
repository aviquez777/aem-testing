package com.edc.edcweb.core.service;

import java.util.ArrayList;

import org.apache.sling.api.SlingHttpServletRequest;

import com.edc.edcweb.core.helpers.ehh.KnowledgeServiceAnswer;

public interface KnowledgeService {

	ArrayList<KnowledgeServiceAnswer> generateAnswer(SlingHttpServletRequest request, String question, String langCode, String top, String scorethreshold);
	String findAllQuestions(String languageCode);
}
