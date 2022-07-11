package com.edc.edcweb.core.helpers;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * This class provides a nullable object for the Language class.
 * 
 * 
 */
public class LanguageUnknown extends Language
{

	/**
	 * Populates the default values of this Language. 
	 *
	 * @param  lang  Language of this object.
	 * @param  toggledLang  The toggled language of this object (i.e., if <code>lang</code> is English, this value will be French).
	 */	
	public LanguageUnknown(Constants.SupportedLanguages lang, Constants.SupportedLanguages toggledLang)
	{
		super(lang, toggledLang);
	}

	/**
	 * Determines if this object is the same language as that in the given <code>searchString</code>. 
	 *
	 * @param  searchString  String to search for language code and used to determine if this object is the same language.
	 * @return boolean  <code>true</code> is the language in the search string matches this object.
	 */	
	@Override
    public boolean isLanguage(String searchString)
    {
    	return false;
    }

	/**
	 * Toggles the language in the given <code>path</code>. In other words, if it is current English, it is toggled to French.
	 *
	 * @param  path  Path whose language code is to be toggled.
	 * @return String  <code>path</code> with its language toggled.
	 */	
	@Override
    public String getToggledPath(String path)
    {
    	return path;
    }

}
