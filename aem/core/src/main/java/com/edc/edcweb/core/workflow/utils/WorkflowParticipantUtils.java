package com.edc.edcweb.core.workflow.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * WorkflowUtils: Utilities to help look up participants
 *
 **/

public class WorkflowParticipantUtils {

    protected static final Logger log = LoggerFactory.getLogger(WorkflowParticipantUtils.class);

    private WorkflowParticipantUtils() {
        // Sonar Lint
    }

    /**
     * Method to get the user's info .
     * 
     * @param userId
     * @param resourceResolver
     * @return
     * @throws RepositoryException
     */
    public static Map<String, String> getUserInfo(String userId, ResourceResolver resourceResolver)
            throws RepositoryException {
        Map<String, String> userInfo = new HashMap<>();
        Authorizable userCheck = getAuthorizable(userId, resourceResolver);
        // if it is not group, get the user's info
        if (userCheck != null) {
            if (!userCheck.isGroup()) {
                // It's an user Get the User's data
                userInfo = getUserData(userCheck);
            } else {
                // It's a group, get the group user's info
                getGroupEmails(userId, resourceResolver);
            }
        }
        // Return at least the user id as email
        if (userInfo.isEmpty()) {
            userInfo.put(WorkflowConstants.FIRST_NAME, "");
            userInfo.put(WorkflowConstants.LAST_NAME, "");
            userInfo.put(WorkflowConstants.EMAIL, userId);
        }
        return userInfo;
    }

    /**
     * Get the User Data
     * 
     * @param userCheck
     * @return
     * @throws RepositoryException
     */
    private static Map<String, String> getUserData(Authorizable userCheck) throws RepositoryException {
        Map<String, String> userData = new HashMap<>();
        User user = (User) userCheck;
        String firstName = user.getProperty("./profile/givenName") != null
                ? user.getProperty("./profile/givenName")[0].getString()
                : "";
        userData.put(WorkflowConstants.FIRST_NAME, firstName);
        String lastName = user.getProperty("./profile/familyName") != null
                ? user.getProperty("./profile/familyName")[0].getString()
                : "";
        userData.put(WorkflowConstants.LAST_NAME, lastName);
        String email = user.getProperty("./profile/email") != null ? user.getProperty("./profile/email")[0].getString()
                : "";
        userData.put(WorkflowConstants.EMAIL, email);
        return userData;
    }

    /**
     * Method to get the group's email
     * 
     * @param groupId
     * @param resourceResolver
     * @return
     * @throws RepositoryException
     */
    public static List<String> getGroupEmails(String groupId, ResourceResolver resourceResolver)
            throws RepositoryException {
        List<String> emails = new LinkedList<>();
        Authorizable group = getAuthorizable(groupId, resourceResolver);
        if (group != null) {
            // if it is group, loop over it's members
            if (group.isGroup()) {
                Iterator<Authorizable> members = ((Group) group).getDeclaredMembers();
                while (members.hasNext()) {
                    Authorizable thisMember = members.next();
                    String userId = thisMember.getID();
                    emails.add(getUserInfo(userId, resourceResolver).get(WorkflowConstants.EMAIL));
                }
            } else {
                // return the user info
                emails.add(getUserInfo(groupId, resourceResolver).get(WorkflowConstants.EMAIL));
            }
        }
        return emails;
    }

    /**
     * Method to get the group
     * 
     * @param groupId
     * @param resourceResolver
     * @return
     * @throws RepositoryException
     */
    public static Authorizable getAuthorizable(String userGroupId, ResourceResolver resourceResolver)
            throws RepositoryException {
        UserManager manager = resourceResolver.adaptTo(UserManager.class);
        Authorizable authorizable = null;
        if (manager != null) {
            authorizable = manager.getAuthorizable(userGroupId);
        }
        return authorizable;
    }

    /**
     * getCurrentUser get userID from session
     * @param resourceResolver
     * @return userID from session
     */
    public static String getCurrentUser (ResourceResolver resourceResolver) {
        Session session = resourceResolver.adaptTo(Session.class);
        return session.getUserID();
    }
}
