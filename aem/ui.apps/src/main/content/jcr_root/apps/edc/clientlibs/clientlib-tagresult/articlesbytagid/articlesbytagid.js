/*
 * Will form the correct articles by tag id end point (URL) based on current window.location.href 
 */
function getArticlesByTagIdEndPoint()
{
	var JSON_EXTENSION = ".json";
	var HTML_EXTENSION = ".html";
	var AEM_SELECTOR   = "/_jcr_content.articlesbytagid";

	var path = window.location.href;
	var pathNoWcmmode  = "";
	var wcmMode        = "";
	var selectorPos    = path.lastIndexOf(AEM_SELECTOR);
	var insertPos      = path.lastIndexOf(HTML_EXTENSION);
	//-------------------------------------------------------------------------
	// If wcmmode is in the cookies, we are running on an author. We need to
	// append "?wcmmode=disabled" to the contentrec path to prevent debug
	// detail from being added by AEM.
	//-------------------------------------------------------------------------
	if(getCookie("wcmmode") != "")
	{
		wcmMode = "?wcmmode=disabled";
	}
	//-------------------------------------------------------------------------
	// If the path already has the selector in it, don't make any changes.
	//-------------------------------------------------------------------------
	if(selectorPos == -1)
	{
		//---------------------------------------------------------------------
		// If the path has an HTML extension, get the string up to that point.
		//---------------------------------------------------------------------
		if(insertPos != -1)
		{
			path = path.substr(0, insertPos) 
		}
		//---------------------------------------------------------------------
		// Now append the selector and the JSON extension
		//---------------------------------------------------------------------
		pathNoWcmmode = path + AEM_SELECTOR + JSON_EXTENSION;
	}

	return pathNoWcmmode + wcmMode;
}

/*
 * Given a cookie name, retrieve it's value 
 */
function getCookie(cname)
{
	var name = cname + "=";
	var decodedCookie = decodeURIComponent(document.cookie);
	var cookieArray = decodedCookie.split(';');
	for(var i = 0; i < cookieArray.length; i++)
	{
		var cookie = cookieArray[i];
		while (cookie.charAt(0) == ' ')
		{
			cookie = cookie.substring(1);
		}
		if (cookie.indexOf(name) == 0)
		{
			return cookie.substring(name.length, cookie.length);
		}
	}
	return "";
}
