var edctransactions = (function () {
    'use strict';

    function _initialize(element) {
        var pagePath, pageLang, externalID = '', email = '';
        var edctransactionsURL = '/bin/contentTransaction';
        var isWebinar = false;

        function _getExternalID() {
            if (Object.keys(EDC.props.userData).length > 1) {
                externalID = EDC.props.userData.externalID;
                email = EDC.props.userData.email;
                if(!isWebinar) {
                    _postEdctransaction();
                }
            }
        }

        function _getPagePath() {
            var langIndex,
                extIndex;

            pageLang = document.documentElement.lang;

            if (pageLang == "en") {
                langIndex = document.location.pathname.indexOf("/en/") + 3;
                extIndex = document.location.pathname.indexOf(".html");
                pagePath = document.location.pathname.substring(langIndex, extIndex);
            } else if (pageLang == "fr") {
                langIndex = element.href.indexOf("/en/") + 3;
                extIndex = element.href.indexOf(".html");
                pagePath = element.href.substring(langIndex, extIndex);
            }

            pagePath = encodeURIComponent(pagePath);
            isWebinar = (pagePath.indexOf('webinar') !== -1);
        }

        function _postEdctransaction() {
            if (externalID) {
                var params = "ExternalID=" + externalID + "&Path=" + pagePath + "&Email=" + email;
                EDC.utils.fetchJSON('GET', edctransactionsURL, params,
                    function (data) {
                        //console.log(data);
                    }
                );
            }
        }

        // make public function bug 95588
        window.EDC = window.EDC || {};

        EDC.edctransactions = EDC.edctransactions || new function () {
            'use strict';

            this.postEdcTransaction = function () {
                _postEdctransaction();
            }
        }

        _getPagePath();
        _getExternalID();
    }

    // Public methods
    function init() {
        var languageToggle = document.querySelector('.main-header .language-toggle');

        if (languageToggle) {
            _initialize(languageToggle);
        }
    }

    return {
        init: init
    };
})();

// This should be uncommented to load component on AEM
document.addEventListener('DOMContentLoaded', edctransactions.init);