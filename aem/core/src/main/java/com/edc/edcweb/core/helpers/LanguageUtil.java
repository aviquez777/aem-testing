package com.edc.edcweb.core.helpers;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * This class is used by the Language Toggle component that is part of the EDC web site.
 * 
 * 
 */
public class LanguageUtil
{
	private LanguageUtil()
	{
		
	}
	/**
	 * Given <code>path</code>, return its language abbreviation. 
	 *
	 * @param  path  Path whose language abbreviation is to be returned.
	 * @param  defaultLanguage  If the path does not contain a language abbreviation, return this as the default language.
	 * @return  String  Abbreviation of the <code>path</code>'s language (e.g., "EN" for English, "FR" for French).
	 */	
	public static String getLanguageAbbreviationFromPath(String path, String defaultLanguage)
	{
		String language = searchLanguageAbbreviation(path);
		//---------------------------------------------------------------------
		// if no language was found in the path, return the default
		//---------------------------------------------------------------------
		if(language.isEmpty())
		{
			language = defaultLanguage;
		}
		return language;
	}
	
	private static String searchLanguageAbbreviation(String path)
	{
		int lastSlashIndex = 0;
		String language="";
		//---------------------------------------------------------------------
		// While there is more string to search...
		//---------------------------------------------------------------------
		while((path != null)  &&  (lastSlashIndex < path.length()))
		{
			//-----------------------------------------------------------------
			// Find next "/"
			//-----------------------------------------------------------------
			int indexSlash = path.indexOf('/', lastSlashIndex);
			if(indexSlash != -1)
			{
				//-------------------------------------------------------------
				// Found a "/", check if third character away is either a "/"
				// or a "."  --OR--  there are only two characters remaining in
				// the path. If so, we (may) have found our language. If not,
				// we need to update our lastSlashIndex and search for the next
				// "/".
				//-------------------------------------------------------------
				if((((indexSlash + 3) <  path.length())  &&  ((path.charAt(indexSlash + 3) == '.')  ||  (path.charAt(indexSlash + 3) == '/')))  ||
				    ((indexSlash + 3) == path.length()))
				{
					//---------------------------------------------------------
					// Grab chars between indexSlash + 1 (don't want to include
					// starting "/") and indexSlash + 3 (the ending index here
					// is not included in sub-string).
					//---------------------------------------------------------
					language = path.substring(indexSlash + 1, indexSlash + 3);
					//---------------------------------------------------------
					// Language code must be only letters. 
					//---------------------------------------------------------
					if(Character.isLetter(language.charAt(0))  &&  Character.isLetter(language.charAt(1)))
					{
						break;
					}
				}
				//-------------------------------------------------------------
				// Not language code yet but more string to search. Update our
				// lastSlashIndex for the start of our next search.
				//-------------------------------------------------------------
				lastSlashIndex = indexSlash + 1;
				language       = "";
			} else {
				//-------------------------------------------------------------
				// No more "/". Set lastSlashIndex to stop iterations.
				//-------------------------------------------------------------
				lastSlashIndex = path.length();
			}
		}
		return language;
	}
}
