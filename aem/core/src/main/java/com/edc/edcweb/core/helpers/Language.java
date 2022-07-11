package com.edc.edcweb.core.helpers;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * This class provides utility methods for languages supported by the EDC web site.
 * 
 * 
 */
public class Language
{
	private Constants.SupportedLanguages currentLanguage;
	private Constants.SupportedLanguages toggledLanguage;

	/**
	 * Populates the default values of this Language. 
	 *
	 * @param  lang  Language of this object.
	 * @param  toggledLang  The toggled language of this object (i.e., if <code>lang</code> is English, this value will be French).
	 */	
	public Language(Constants.SupportedLanguages lang, Constants.SupportedLanguages toggledLang)
	{
		currentLanguage = lang;
		toggledLanguage = toggledLang;
	}

	/**
	 * Determines if this object is the same language as that in the given <code>searchString</code>. 
	 *
	 * @param  searchString  String to search for language code and used to determine if this object is the same language.
	 * @return boolean  <code>true</code> is the language in the search string matches this object.
	 */	
    public boolean isLanguage(String searchString)
    {
    	String stringLanguage="";
    	//---------------------------------------------------------------------
    	if(searchString != null)
    	{
    		stringLanguage = LanguageUtil.getLanguageAbbreviationFromPath(searchString, null);
    	}
    	//---------------------------------------------------------------------
    	return this.currentLanguage.getLanguageAbbreviation().equalsIgnoreCase(stringLanguage);
    }

	/**
	 * Toggles the language in the given <code>path</code>. In other words, if it is current English, it is toggled to French.
	 *
	 * @param  path  Path whose language code is to be toggled.
	 * @return String  <code>path</code> with its language toggled.
	 */	
    public String getToggledPath(String path)
    {
    	String toggled=path;
    	String stringLanguage="";
    	//---------------------------------------------------------------------
    	if(path != null)
    	{
    		stringLanguage = LanguageUtil.getLanguageAbbreviationFromPath(path, null);
    		if(this.currentLanguage.getLanguageAbbreviation().equalsIgnoreCase(stringLanguage))
    		{
    			//-------------------------------------------------------------
    			// If the language abbreviation in path is lowercase, replace
    			// it with lowercase letters. Already know that stringLanguage
    			// has two characters as it equals the language abbreviation
    			// above.
    			//-------------------------------------------------------------
    			boolean isLowerCase = Character.isLowerCase(stringLanguage.charAt(0)) && Character.isLowerCase(stringLanguage.charAt(1)); 
	    		toggled = path.replaceFirst("/" + stringLanguage,
	    							"/" + (isLowerCase ? this.toggledLanguage.getLanguageAbbreviation().toLowerCase() :
	    								                 this.toggledLanguage.getLanguageAbbreviation()));
	    	}
    	}
    	//---------------------------------------------------------------------
    	return toggled;
    }
    
	/**
	 * Get the language of this object.
	 *
	 * @return SupportedLanguages
	 */	
    public Constants.SupportedLanguages getCurrentLanguage()
    {
    	return currentLanguage;
    }
    
	/**
	 * Get the toggled language of this object (i.e., if this object's language is English, the returned language is French).
	 *
	 * @return SupportedLanguages
	 */	
    public Constants.SupportedLanguages getToggledLanguage()
    {
    	return toggledLanguage;
    }
}
