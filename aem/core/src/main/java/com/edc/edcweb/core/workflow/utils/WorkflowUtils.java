package com.edc.edcweb.core.workflow.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

public class WorkflowUtils {

    static final Logger log = LoggerFactory.getLogger(WorkflowUtils.class);

    private WorkflowUtils() {
        // Sonar Lint
    }

    /**
     * getMetadaArgForEmail Parse the metada args from the step check the
     * WorkflowConstants for key names ARGS_* Add the defaults for an Email
     * Notification Step if not present
     * 
     * @param metaDataMap to get the arguments string Expected sting is
     *                    name1=value1,name2=value2
     * @return map with name as key, value as value. empty otherwise
     */
    public static Map<String, String> getMetadaArgForNotifications(MetaDataMap metaDataMap,
            ResourceResolver resourceResolver) {
        // Get the Args
        Map<String, String> argsMap = getMetadaArgs(metaDataMap);
        // Add the defaults
        argsMap.putIfAbsent(WorkflowConstants.ARGS_USER_GROUP_ID_KEY,
                WorkflowParticipantUtils.getCurrentUser(resourceResolver));
        argsMap.putIfAbsent(WorkflowConstants.ARGS_SUBJECT_KEY, WorkflowConstants.EMAIL_SUBJECT);
        argsMap.putIfAbsent(WorkflowConstants.ARGS_TEMPLATE_KEY, WorkflowConstants.DEFAULT_TEMPLATE);
        argsMap.putIfAbsent(WorkflowConstants.ARGS_EMAIL_KEY, WorkflowConstants.EMAIL_TYPE_NONE);
        return argsMap;
    }

    /**
     * getMetadaArgs Parse the metada args from the step check the WorkflowConstants
     * for key names ARGS_*
     * 
     * @param metaDataMap to get the arguments string Expected sting is
     *                    name1=value1,name2=value2
     * @return map with name as key, value as value. empty otherwise
     */
    public static Map<String, String> getMetadaArgs(MetaDataMap metaDataMap) {
        Map<String, String> argsMap = new HashMap<>();
        if (metaDataMap.containsKey(WorkflowConstants.PROCESS_ARGS)) {
            // Split the string coming from the dialog
            // Expected sting is name1=value1,name2=value2
            String argsStr = metaDataMap.get(WorkflowConstants.PROCESS_ARGS, WorkflowConstants.EMPTY_STRING).trim();
            String[] argsArr = StringUtils.split(argsStr, WorkflowConstants.ARGS_DELIMTER);
            // Split now every arg to get the ValueName Pair
            for (String arg : argsArr) {
                // split by the equal
                String[] nameValuePair = arg.split(WorkflowConstants.ARGS_NAME_VALUE_DELIMTER);
                if (nameValuePair.length == 2) {
                    // Get the name and value
                    argsMap.put(nameValuePair[0], nameValuePair[1]);
                }
            }
        }
        return argsMap;
    }

    /**
     * getMessageData Get any message entered by person assigning the task.
     * 
     * @param workItem
     * @param resourceResolver
     * @return The Message and author info, if no message it will return "Message
     *         box was intentionally left blank." as proof that nothing was entered,
     *         and avoid any communication issues
     */
    public static Map<String, String> getMessageData(WorkItem workItem, WorkflowSession workflowSession,
            ResourceResolver resourceResolver) {
        Map<String, String> result = new HashMap<>();
        // Get the userInfo
        String userFullName = workItem.getCurrentAssignee();
        userFullName = getUserFullName(userFullName, resourceResolver);
        if (StringUtils.isBlank(userFullName)) {
            try {
                result = getPreviousStepData(workflowSession.getHistory(workItem.getWorkflow()), resourceResolver);
            } catch (WorkflowException e) {
                log.error("WorkflowUtils getMessageData getPreviousStepData error {}", e.getStackTrace());
            }
        } else {
            // get or add default "No Message left in comment box." and clean it.
            String message = workItem.getMetaDataMap().get(WorkflowConstants.COMMENT, WorkflowConstants.SPACE)
                    .replaceAll(WorkflowConstants.REPLACE_EOL, WorkflowConstants.REPLACE_BR).trim();
            // Use default message if blank
            if (StringUtils.isBlank(message)) {
                message = WorkflowConstants.HISTORY_NO_MESSAGE_ENTERED;
            }
            result.put(WorkflowConstants.HISTORY_USER_FULL_NAME_KEY, userFullName);
            result.put(WorkflowConstants.HISTORY_MESSAGE_KEY, message);
        }
        return result;
    }

    /**
     * getPreviousMessageData: Gets the user any message from the previous step
     * 
     * @param historyList
     * @return map with user and message only if there's message, empty otherwise
     */
    public static Map<String, String> getPreviousStepData(List<HistoryItem> historyList,
            ResourceResolver resourceResolver) {
        Map<String, String> result = new HashMap<>();
        int listSize = historyList.size();
        // Get previous step to get the dialog's value
        HistoryItem lastItem = historyList.get(listSize - 1);
        if (lastItem != null) {
            // Get the userInfo
            String userFullName = getUserFullName(lastItem.getUserId(), resourceResolver);
            // get or add default "No Message left in comment box." and clean it.
            String message = lastItem.getWorkItem().getMetaDataMap()
                    .get(WorkflowConstants.COMMENT, WorkflowConstants.SPACE)
                    .replaceAll(WorkflowConstants.REPLACE_EOL, WorkflowConstants.REPLACE_BR).trim();
            // Use default message if blank
            if (StringUtils.isBlank(message)) {
                message = WorkflowConstants.HISTORY_NO_MESSAGE_ENTERED;
            }
            result.put(WorkflowConstants.HISTORY_USER_FULL_NAME_KEY, userFullName);
            result.put(WorkflowConstants.HISTORY_MESSAGE_KEY, message);
        }
        return result;
    }

    /**
     * getUserFullName get the user data from Authorizable
     * 
     * @param userId
     * @param resourceResolver
     * @return Full user data, empty if not found
     */
    private static String getUserFullName(String userId, ResourceResolver resourceResolver) {
        try {
            Map<String, String> userInfo = WorkflowParticipantUtils.getUserInfo(userId, resourceResolver);
            userId = userInfo.get(WorkflowConstants.FIRST_NAME).concat(WorkflowConstants.SPACE)
                    .concat(userInfo.get(WorkflowConstants.LAST_NAME));
            String userEmail = userInfo.get(WorkflowConstants.EMAIL);
            // Don't append email if empty
            if (StringUtils.isNotBlank(userEmail)) {
                userId = userId.concat(WorkflowConstants.SPACE).concat(WorkflowConstants.HTML_LESS_THAN_ENTITY)
                        .concat(userEmail).concat(WorkflowConstants.HTML_GREATER_THAN_ENTITY);
            }
        } catch (RepositoryException e) {
            log.error("WorkflowUtils getPreviousMessageData userInfo error for {} , {}", userId, e.getStackTrace());
        }
        return userId;
    }
}
