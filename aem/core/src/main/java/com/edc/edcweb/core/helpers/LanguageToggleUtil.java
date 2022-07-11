package com.edc.edcweb.core.helpers;

import java.util.ArrayList;
import java.util.List;

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
public class LanguageToggleUtil
{
	//-------------------------------------------------------------------------
	private List<Language> languages = new ArrayList<>();
	private Language nullableLang    = new LanguageUnknown(Constants.SupportedLanguages.ENGLISH, Constants.SupportedLanguages.FRENCH);
	//-------------------------------------------------------------------------
	private static final LanguageToggleUtil langUtil = new LanguageToggleUtil();

	/**
	 * Singleton constructor to retrieve the <code>LanguageToggleUtil</code> object. 
	 *
	 * @return  LanguageToggleUtil  Fully initialized <code>LanguageToggleUtil</code> object.
	 */	
	public static LanguageToggleUtil getInstance()
	{
		return langUtil;
	}
	
	private LanguageToggleUtil()
	{
		languages.add(new Language(Constants.SupportedLanguages.ENGLISH, Constants.SupportedLanguages.FRENCH));
		languages.add(new Language(Constants.SupportedLanguages.FRENCH,  Constants.SupportedLanguages.ENGLISH));
	}
    
	/**
	 * Given <code>path</code>, return the corresponding <code>SupportedLanguages</code> object. 
	 *
	 * @param  path  Path whose language is to be retrieved.
	 * @return  SupportedLanguages  Corresponding <code>SupportedLanguages</code> object.
	 */	
    public Constants.SupportedLanguages getCurrentLanguageFromPath(String path)
    {
    	//---------------------------------------------------------------------
    	// If none of the above match, "language" is already English
    	//---------------------------------------------------------------------
    	Language oneLang = this.findLanguage(path);
    	return oneLang.getCurrentLanguage();
    }
    
	/**
	 * Given <code>path</code>, return the corresponding <code>Language</code> object. 
	 *
	 * @param  path  Path whose <code>Language</code> is to be retrieved.
	 * @return  Language  Corresponding <code>Language</code> object.
	 */	
    public Language findLanguage(String path)
    {
    	Language oneLang=nullableLang;
        //---------------------------------------------------------------------
    	// Iterate my languages and look for one that matches the path language  
        //---------------------------------------------------------------------
    	for(int i = 0; i < this.languages.size(); i++)
    	{
    		if(this.languages.get(i).isLanguage(path))
    		{
    			oneLang = this.languages.get(i);
    			break;
    		}
    	}
        //---------------------------------------------------------------------
    	return oneLang;
    }
    
	/**
	 * Given <code>path</code>, determine if its language is French. 
	 *
	 * @param  path  Path whose language is to be determined.
	 * @return  boolean  <code>true</code> if <code>path</code>'s language is French.
	 */	
    public boolean isFrench(String path)
    {
    	Language oneLang = this.findLanguage(path);
    	return((oneLang != null)  &&  (oneLang.getCurrentLanguage() == Constants.SupportedLanguages.FRENCH));
    }
    
	/**
	 * Given <code>path</code>, determine if its language is English. 
	 *
	 * @param  path  Path whose language is to be determined.
	 * @return  boolean  <code>true</code> if <code>path</code>'s language is English.
	 */	
    public boolean isEnglish(String path)
    {
    	Language oneLang = this.findLanguage(path);
    	return((oneLang != null)  &&  (oneLang.getCurrentLanguage() == Constants.SupportedLanguages.ENGLISH));
    }

	/**
	 * Given <code>path</code>, return <code>path</code> with its language toggled. 
	 *
	 * @param  path  Path whose language is to be toggled.
	 * @return  String  A language-toggled version of <code>path</code>.
	 */	
    public String getToggledPath(String path)
    {
    	Language oneLang = findLanguage(path);
    	return oneLang.getToggledPath(path);
    }

	/**
	 * Given <code>path</code>, return the textual description of <code>path</code>'s language. 
	 *
	 * @param  path  Path whose language is to be described.
	 * @return  String  The description of <code>path</code>'s language.
	 */	
    public String getLanguageTextFromPath(String path)
    {
    	Language oneLang = findLanguage(path);
    	return oneLang.getCurrentLanguage().getLanguageText();
    }

	/**
	 * Given <code>path</code>, return the abbreviation of <code>path</code>'s language. 
	 *
	 * @param  path  Path whose language abbreviation is to be returned.
	 * @return  String  The abbreviation of <code>path</code>'s language.
	 */	
    public String getLanguageAbbreviationFromPath(String path)
    {
    	Language oneLang = findLanguage(path);
    	return oneLang.getCurrentLanguage().getLanguageAbbreviation();
    }

	/**
	 * Given <code>path</code>, return the textual description of <code>path</code>'s toggled language. 
	 *
	 * @param  path  Path whose toggled language is to be described.
	 * @return  String  The textual description of <code>path</code>'s toggle language.
	 */	
    public String getToggledLanguageFromPath(String path)
    {
    	Language oneLang = findLanguage(path);
    	return oneLang.getToggledLanguage().getLanguageText();
    }
}
