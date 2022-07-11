package com.edc.edcweb.core.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;

/**
 * @author Peter Crummey
 * @version 0.0.5
 * @since 0.0.5
 * 
 * 
 * Retrieve the replication status for components used by the EDC web site.
 * 
 */
public class ReplicationStatusHelper
{
	private static final Logger log = LoggerFactory.getLogger(ReplicationStatusHelper.class);
	
	private ReplicationStatusHelper()
	{
		
	}
	
	/**
	 * Returns the replication status object for the given resource.
	 *
	 * @param  resource  Resource whose replication status is to be retrieved
	 * @return The replication status for this resource.  If a valid replication status
	 * cannot be found, null is returned.
	 */	
	public static ReplicationStatus getReplicationStatus(Resource resource)
	{
		ReplicationStatus repStatus = null;
		if(resource != null)
		{
			ValueMap properties = resource.getValueMap();
			//-----------------------------------------------------------------
			log.debug("properties: {}", properties);
			//-----------------------------------------------------------------
			repStatus = resource.adaptTo(ReplicationStatus.class);
			if(!isValidReplicationStatus(repStatus, properties))
			{
				repStatus = null;
			}
		}
		return repStatus;
	}
	
	/**
	 * Returns the replication status object for the given page.
	 *
	 * @param  page  Page whose replication status is to be retrieved
	 * @return The replication status for this page.  If a valid replication status
	 * cannot be found, null is returned.
	 */	
	public static ReplicationStatus getReplicationStatus(Page page)
	{
		ReplicationStatus repStatus = null;
		if(page != null)
		{
			repStatus = ReplicationStatusHelper.getReplicationStatus(page.getContentResource());
		}
		return repStatus;
	}
	
	/**
	 * Determines if the replication status is valid for the given properties by comparing the create date in the properties
	 * with the last published date from the replication status. If the last published date is less than the create date,
	 * the replication status is not valid.
	 *
	 * @param  repStatus  Replication status
	 * @param  properties  ValueMap with the page/resource properties
	 * @return True if the replication status is valid.
	 */	
	private static boolean isValidReplicationStatus(ReplicationStatus repStatus, ValueMap properties)
	{
		//---------------------------------------------------------------------
		// Okay, I'll give it to you, this check looks weird. Why would the
		// last replication date ever be before resource create date? There is
		// an apparent bug in AEM that if the replication information is set
		// for the base template's "initial" child node when a page is created,
		// the page is assigned those values. A brand new page that was never
		// replicated would have replication status detail. In that scenario,
		// the replication date is before the page create date. If that is the
		// case, the replication status is not valid for the given properties.
		//---------------------------------------------------------------------
		boolean isValidRepStatus = false;
		//---------------------------------------------------------------------
		if(properties.containsKey(Constants.Properties.JCR_COLON_CREATED))
		{
			logCalendarDate(properties.get(Constants.Properties.JCR_COLON_CREATED, Calendar.class), "Created date: ");
		}
		//---------------------------------------------------------------------
		if((repStatus != null)  &&  (repStatus.getLastPublished() != null))
		{
			logCalendarDate(repStatus.getLastPublished(), "Last published date: ");
		}
		//---------------------------------------------------------------------
		if((repStatus != null)  &&  (repStatus.getLastPublished() != null)   &&
		   (properties.containsKey(Constants.Properties.JCR_COLON_CREATED))  &&
		   (repStatus.getLastPublished().compareTo(properties.get(Constants.Properties.JCR_COLON_CREATED, Calendar.class)) > 0))
		{
			isValidRepStatus = true;
		}
		log.debug("Replication status is valid: {}", isValidRepStatus);
		return isValidRepStatus;
	}
	
	/**
	 * Write a calendar to the log file.
	 *
	 * @param  calendar  Calendar to be written
	 * @param  message  Message to be prepended to the <code>calendar</code> value
	 */	
	private static void logCalendarDate(Calendar calendar, String message)
	{
		if(log.isDebugEnabled())
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			log.debug(message + "{}", calendar != null ? formatter.format(calendar.getTime()) : null);
		}
	}
}