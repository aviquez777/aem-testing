/*!
* # _satellite mock
* # _elqQ mock
*/

;(function (window) {
    'use strict';

    window = (typeof window != 'undefined' && window.Math == Math)
    ? window
    : (typeof self != 'undefined' && self.Math == Math)
        ? self
        : Function('return this')()
    ;

    window._satellite = window._satellite || (function () {
        var initialized = false,
            data = {
                customVars: {}
            },
            configurationSettings = {
                pageLoadRules: [],
                settings: {
                    isStaging: false
                }
            },
            visitor = {
                visitorId: '',
                VisitorIDsTo: [],
                getMarketingCloudVisitorID: function () {
                    return '';
                },
                appendVisitorIDsTo: function (domain) {
                    VisitorIDsTo.push(domain);
                }
            },
            pending = [];

        return {
            getVar: function (dataElement) {
                return data.customVars[dataElement] || '';
            },
            setVar: function (dataElement) {
                data.customVars[dataElement] = dataElement;
            },
            pageBottom: function () {
                return '';
            },
            track: function (data) { // NOSONAR
                return false;
            },
            readCookie: function (cookieName) {
                return false;
            },
            setCookie: function (cookieName, value, days) {
                return false;
            },
            removeCookie: function (cookieName) {
                return false;
            },
            buildDate: function () {
                return new Date().toString();
            },
            publishDate: function () {
                return new Date().toString();
            },
            notify: function(text, importance) {
                return false;
            },
            text: function(text) {
                return '';
            },
            cleanText: function(text) {
                return '';
            },
            getVisitorId: function() {
                return visitor;
            },
            isLinked: function(text) {
                return false;
            },
            getToolsByType: function(sc) {
                return false;
            },
            each: function(array, callback) {
                return false;
            },
            data: data,
            configurationSettings: configurationSettings,
            initialized: initialized,
            pending: pending
        };
    })();

    window._elqQ = window._elqQ || (function () {
        var elqQData = []; // NOSONAR

        return {
            push: function (data) {
                elqQData.push(data);
            }
        };
    })();
})( window );