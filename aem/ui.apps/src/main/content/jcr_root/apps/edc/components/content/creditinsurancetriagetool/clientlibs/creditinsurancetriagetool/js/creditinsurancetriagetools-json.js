use(function () {
    var CONTENT_EDC = "/content/edc/",
        defineJson = {
            global: {
                ctaBackToStart: properties.backtostart,
                ctaBackAStep: properties.backastep,
                ctaNextQuestion: properties.nextquestion,
                yes: properties.yes,
                no: properties.no,
                ctaTellMeMore: properties.tellmemore,
                publisher: properties.isPublisher,
            },
            step1: {
                heading: properties.heading1,
                question: properties.text1,
                responseA: properties.response1a,
                responseB: properties.response1b,
                responseC: properties.response1c,
            },
            step2: {
                heading: properties.heading2,
                message: removeContentEdc(properties.rtftext2),
                cta1: properties.linktext2 ? properties.linktext2 : properties.tellmemore,
                linkTarget: properties.linktarget2,
                cta1Url: resolveUrl(properties.linkurl2)
            },
            step3: {
                question: properties.text3
            },
            step4: {
                question: properties.text4
            },
            step5: {
                question: properties.text5,
                responseA: properties.response5a,
                responseB: properties.response5b,
                responseC: properties.response5c,
            },
            step6: {
                heading: properties.heading6,
                message: properties.text6,
                linkText: properties.linktext6 ? properties.linktext6 : properties.tellmemore,
                linkTarget: properties.linktarget6,
                ctaURL: resolveUrl(properties.linkurl6)
            },
            step7: {
                question: properties.text7,
                label1: '',
                label2: '',
                label3: ''
            },
            step8: {
                heading: properties.heading8,
                message: properties.text8,
                linkText: properties.linktext8 ? properties.linktext8 : properties.tellmemore,
                linkTarget: properties.linktarget8,
                ctaURL: resolveUrl(properties.linkurl8)
            }
        };

    //---------------------------------------------------------------------
    // Will resolve given Url by adding ".html" extension if it's an AEM
    // Url, and removing the CONTENT_EDC if we are on the Publisher
    // (wcmmode is disabled).
    //---------------------------------------------------------------------
    function resolveUrl(linkurl) {
        var linkUrlToReturn = linkurl;

        if (linkurl) {
            linkUrlToReturn = removeContentEdc(provideExtension(linkurl));
        }
        return linkUrlToReturn;
    }

    //---------------------------------------------------------------------
    // This method removes the CONTENT_EDC from anywhere in the Url. Call
    // provideExtension() method BEFORE calling this method, if applicable
    // to your use case.
    //---------------------------------------------------------------------
    function removeContentEdc(linkurl) {
        var linkUrlToReturn = linkurl;
        var regEx = new RegExp(CONTENT_EDC, "g");
        //---------------------------------------------------------------------
        if ((linkurl) && wcmmode.isDisabled()) {
            linkUrlToReturn = linkurl.replace(regEx, "/");
        }
        //---------------------------------------------------------------------
        return linkUrlToReturn;
    }

    //---------------------------------------------------------------------
    // This method looks for CONTENT_EDC at the start of the Url. Call this
    // method BEFORE calling removeContentEdc(), if applicable to your use
    // case.
    //---------------------------------------------------------------------
    function provideExtension(linkurl) {
        var linkUrlToReturn = linkurl;
        //---------------------------------------------------------------------
        // If linkurl starts with CONTENT_EDC, this is an AEM Url; add ".html"
        //---------------------------------------------------------------------
        if ((linkurl) && (linkurl.startsWith(CONTENT_EDC))) {
            linkUrlToReturn = linkurl + ".html";
        }
        return linkUrlToReturn;
    }

    return {
        json: defineJson,
        jsonString: JSON.stringify(defineJson),
    };
});