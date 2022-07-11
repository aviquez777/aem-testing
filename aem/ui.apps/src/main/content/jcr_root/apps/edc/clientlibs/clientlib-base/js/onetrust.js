/*
(function(){
    var jsonp = document.createElement('script');
    jsonp.setAttribute('src', 'https://geolocation.onetrust.com/cookieconsentpub/v1/geo/countries/EU');
    document.head.appendChild(jsonp);

    window.jsonFeed = function () {};
    var OneTrust = OneTrust || {};
    OneTrust.Location = {
        'eu': 'EU',
        'other': 'Non-EU'
    }

    window.jsonFeed = function (options) {
        var userLocation = OneTrust.Location[options.displayPopup ? 'eu' : 'other'];
        var path = window.location.pathname;
        
        if (path.indexOf("/fr/") !== -1) {
            injectOneTrustScriptTag("fr", userLocation);
        } else {
            injectOneTrustScriptTag("en", userLocation);
        }
        
        var otCookie = getCookie("OptanonConsent");
        
        var otGroups = '';

        if (otCookie){
          var otCookieArray = otCookie.split("&"); 

          for (var i=0; i < otCookieArray.length; i++) {
            if (otCookieArray[i].startsWith("group")) {
             otGroups = decodeURIComponent(otCookieArray[i]);
              otGroups = otGroups.replace("groups=",",");
            }
          }
        }
        
        var str = otGroups;

        if (str) { 
            var n = str.indexOf(",2:1,");  //user has allowed to be tracked
            if (n > -1) {
                setCookie("sat_track", 'true', 365);
            } else {
                setCookie("sat_track", 'false', 365);
            }
        } else {   //this is the default state
            if (userLocation == "Non-EU") {
                setCookie("sat_track", 'true', 365);
            } else {
                setCookie("sat_track", 'false', 365);
            }
        }
        
        
    };
    
    function injectOneTrustScriptTag(language, location) {
        var sc = document.createElement("script");
        
        if (window.OneTrustSource) {
	        var source = window.OneTrustSource[location][language];
	    
	        sc.setAttribute("src", source);
	        sc.setAttribute("type", "text/javascript");
	        sc.setAttribute("onload", "Optanon.LoadBanner()");
	        sc.onreadystatechange = function () {
	            if (this.readyState == 'complete' || this.readyState == 'loaded') {
	                Optanon.LoadBanner();
	            }
	        };
	        document.querySelector('head').appendChild(sc);
        }
    }
    
    function setCookie(name, value, days) {
        var d = new Date();
        d.setTime(d.getTime() + 24 * 60 * 60 * 1000 * days);
        document.cookie = name + "=" + value + ";path=/;expires=" + d.toGMTString();
    }

    function getCookie(name) {
        var v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
        return v ? v[2] : null;
    }
    
})();

*/

(function ($) {
    // Select the node that will be observed for mutations
    var targetNode = document.querySelector('body');

    // Options for the observer (which mutations to observe)
    var config = { attributes: true, attributeFilter: ['style'] };

    // Callback function to execute when mutations are observed
    var callback = function() {
        var body = document.querySelector('body'),
            bodyStyle = body.style || '',
            bodyTransform = bodyStyle.getPropertyValue('transform'),
            transformPixels = bodyTransform.match(/\d+/g),
            bodyPadding;

        observer.disconnect();

        bodyStyle.removeProperty('padding-top');

        if (transformPixels) {
            bodyPadding = parseInt((window.getComputedStyle(document.querySelector('body')).getPropertyValue("padding-top")).replace('px', ''));

            document.querySelector('body').style.paddingTop = ((transformPixels ? parseInt(transformPixels[0]) : 0) + bodyPadding) + 'px'; //NOSONAR
        }
        observer.observe(targetNode, config);
    };

    // Create an observer instance linked to the callback function
    var observer = new MutationObserver(callback);

    // Start observing the target node for configured mutations
    observer.observe(targetNode, config);
})($);