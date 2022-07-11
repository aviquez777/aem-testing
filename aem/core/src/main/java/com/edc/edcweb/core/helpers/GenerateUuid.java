package com.edc.edcweb.core.helpers;

import com.adobe.cq.sightly.WCMUsePojo;
import java.util.UUID;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * This class will generate a UUID that can be used in components as part of the EDC web site.
 * 
 * 
 */
public class GenerateUuid extends WCMUsePojo
{

	/**
	 * Will generate a Universally Unique IDentifier (UUID). 
	 *
	 * @return  String  Generated UUID.
	 */	
	public String generateUuid()
	{
		return UUID.randomUUID().toString();
	}
	
	@Override
	public void activate() throws Exception
	{
		//---------------------------------------------------------------------
		// This class simply generates a new UUID whenever generateUuid() is
		// called. Nothing to be done in the activate() method.
		//---------------------------------------------------------------------
	}

}
