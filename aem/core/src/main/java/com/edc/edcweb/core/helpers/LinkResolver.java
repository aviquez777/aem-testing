package com.edc.edcweb.core.helpers;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 *        Utility class to resolve links within the EDC web site when the AEM
 *        server cannot be relied upon to perform this action. The most
 *        significant use for this is to remove the "/content/edc" string at the
 *        beginning of links on pages being delivered via the Dispatcher.
 *
 *
 */
public class LinkResolver {

	private static final Logger log = LoggerFactory.getLogger(LinkResolver.class);
	public static final String URL_PATTERN = "^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
	public static final String CONTENT = "/content";

	private LinkResolver() {

	}

	/**
	 * Will reverse map the <code>link</code> using the AEM API and reverse mapping
	 * defined in the /etc/map/http(s) configuration.
	 *
	 * @param resolver
	 *            ResourceResolver to use in resolving this link.
	 * @param link
	 *            The link (URL) to be reverse mapped.
	 * @return String The resolved link.
	 */
	public static String reverseMapLink(ResourceResolver resolver, String link) {

		String mappedLink = "";
		if (link != null) {
			log.debug("reverseMapLink: {}", link);
			// ---------------------------------------------------------------------
			// If the unresolved link starts with "/content/edc", we want to add an
			// extension of "html". Otherwise, just return the resolved link.
			// ---------------------------------------------------------------------
			boolean isFromContentEdc = link.startsWith(Constants.Paths.CONTENT_EDC);
			mappedLink = link;
			// ---------------------------------------------------------------------
			if (isFromContentEdc) {
				mappedLink = resolver.map(link);
				log.debug("mappedLink resolved:{}", mappedLink);
				// -----------------------------------------------------------------
				// If the resolved link does not end with ".html", append it now
				// -----------------------------------------------------------------
				if (!mappedLink.endsWith(Constants.HTML_EXTENTION)) {
					String[] listOfPaths = mappedLink.split("[#]");
					log.debug("listOfPaths.length: {}", listOfPaths.length); //NOSONAR
					if (listOfPaths.length == 1 && (!mappedLink.endsWith(Constants.HTML_EXTENTION))) {
						mappedLink = mappedLink + Constants.HTML_EXTENTION;
						log.debug("mappedLink: {}", mappedLink);
					} else if (listOfPaths.length == 2 && (!listOfPaths[0].endsWith(Constants.HTML_EXTENTION))) {
						StringBuilder builder = new StringBuilder(listOfPaths[0]);
						builder.append(Constants.HTML_EXTENTION);
						builder.append("#");
						builder.append(listOfPaths[1]);
						mappedLink = builder.toString();

					}
				}
			}
		}
		log.debug("mappedLink updated {}", mappedLink);
		return mappedLink;
	}

	/**
	 * Will add a ".html" extension to the given <code>link</code> if the link
	 * begins with "/content/edc".
	 *
	 * @param link
	 *            The link (URL) to which an extension is added (if missing).
	 * @return String The link with an extension.
	 */
	public static String addHtmlExtension(String link) {
		String mappedLink = "";
		if (link != null) {
			mappedLink = link;
			log.debug("addHtmlExtension: {}", link);
			String[] listOfPaths = mappedLink.split("[#]");
			log.debug("listOfPaths.length: {}", listOfPaths.length);
			if ((link.startsWith(Constants.Paths.CONTENT_EDC) || link.startsWith(Constants.Paths.CONTENT_MYEDC)) && listOfPaths.length == 1
					&& (!mappedLink.endsWith(Constants.HTML_EXTENTION))) {
				mappedLink = mappedLink + Constants.HTML_EXTENTION;
				log.debug("mappedLink 0: {}", mappedLink);
			} else if ((link.startsWith(Constants.Paths.CONTENT_EDC) || link.startsWith(Constants.Paths.CONTENT_MYEDC)) && listOfPaths.length == 2
					&& (!listOfPaths[0].endsWith(Constants.HTML_EXTENTION))) {
				log.debug("listOfPaths[0]: {}", listOfPaths[0]);
				log.debug("listOfPaths[1]: {}", listOfPaths[1]);
				StringBuilder builder = new StringBuilder(listOfPaths[0]);
				builder.append(Constants.HTML_EXTENTION);
				builder.append("#");
				builder.append(listOfPaths[1]);
				mappedLink = builder.toString();
			}
			// else the link is already good
			// ---------------------------------------------------------------------

		}
		log.debug("mappedLink: {}", mappedLink);
		return mappedLink;
	}
	// remove the #XXX which should be last part of the string.

	public static String removeBookmarkSuffix(String iURL) {
		String result = iURL;

		log.debug("removeBookmarkSuffix: {}", iURL);
		String[] listOfPaths = result.split("[#]");
		log.debug("listOfPaths.length: {}", listOfPaths.length);

		if (listOfPaths.length > 1) {
			log.debug("listOfPaths[0]{}", listOfPaths[0]);
			result = listOfPaths[0];
			log.debug("result: {}", result);
		}

		return result;
	}

	public static boolean isURL(String url) {
		if (url == null) {
			return false;
		}
		return url.matches(URL_PATTERN);
	}

	public static String changeInternalURLtoExternal(SlingHttpServletRequest request, String resourcePath) {
		String requestPath = "";

		ResourceResolver resResolver = request.getResourceResolver();
		requestPath = resResolver.map(resourcePath);

		log.debug("Resource resolver path mapper: resourcePath= {}; requestPath= {}", resourcePath, requestPath);

		return requestPath;
	}

    public static String changeManualExternalURLtoInternal(String resourcePath) {
        String requestPath = resourcePath;

        if (!resourcePath.startsWith(Constants.Paths.CONTENT_EDC)) {
            requestPath = Constants.Paths.CONTENT_EDC + resourcePath;
        }

        return requestPath;
    }

}
