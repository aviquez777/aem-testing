window.EDC = window.EDC || {};

EDC.datalayerObj = 'digitalData';

if (!window.digitalData) {
    window[EDC.datalayerObj] = {};
    window[EDC.datalayerObj].events = window[EDC.datalayerObj].events || [];
    window[EDC.datalayerObj].user = window[EDC.datalayerObj].user || {};
    window[EDC.datalayerObj].user.segment = window[EDC.datalayerObj].user.segment || {};
}

EDC.props = EDC.props || {
    lang: document.documentElement.lang ? document.documentElement.lang : 'en',
    userData: {},
    pageNameURL: window.location.href.replace(/\?.*/gi, ''),
    labels: {},
    media: {
        sm: 375,
        md: 720,
        lg: 960,
        xl: 1140
    },
    escapeKeyCode: 27,
    enterKeyCode: 13,
    arrowDownKeyCode: 40,
    arrowUpKeyCode: 38,
    arrowRightCode: 39,
    arrowLeftCode: 37,
    spaceKeyCode: 32,
    tabKeyCode: 9
};

EDC.components = EDC.components || {};

EDC.utils = EDC.utils || new function () {
    'use strict';

    var defaultConsole = console,
        isDeviceMobile = false,
        resizeEventInitialized = false,
        userStatusUrl = '/bin/myedc/userstate';

    this.debug = function (enabled) {
        var console = {};

        if (!enabled) {
            Object.keys(defaultConsole).forEach(function (key) {
                if (key === 'log') {
                    // eslint-disable-next-line no-empty-function
                    console[key] = function () {};
                } else {
                    console[key] = defaultConsole[key];
                }
            });

            window.console = console;
        } else {
            window.console = defaultConsole;
        }
    };

    this.getPageName = function () {
        return window.digitalData.page ? window.digitalData.page.pageInfo.pageName : '';
    };

    this.dataLayerTracking = function (obj) {
        if (window[window.EDC.datalayerObj] && obj) {
            window[window.EDC.datalayerObj].events = [];
            window[window.EDC.datalayerObj].events.push(obj);
        }

        // check if _satellite is defined
        if (typeof _satellite !== 'undefined') {
            _satellite.track('dl-event tracking');
        }
    };

    this.userSegmentTracking = function (obj, join) {
        if (window[window.EDC.datalayerObj] && obj) {
            if (!window[window.EDC.datalayerObj].user) {
                window[window.EDC.datalayerObj].user = {};
            }

            if (!join) {
                window[window.EDC.datalayerObj].user.segment = obj;
            } else {
                (window[window.EDC.datalayerObj].user.segment).extend((window[window.EDC.datalayerObj].user.segment), obj);
            }
        }
    };

    this.pageInfoTracking = function (obj, join) {
        if (window[window.EDC.datalayerObj] && obj) {
            if (!window[window.EDC.datalayerObj].page) {
                window[window.EDC.datalayerObj].page = {};
            }

            if (!window[window.EDC.datalayerObj].page.pageInfo) {
                window[window.EDC.datalayerObj].page.pageInfo = {};
            }

            if (!join) {
                window[window.EDC.datalayerObj].page.pageInfo = obj;
            } else {
                (window[window.EDC.datalayerObj].page.pageInfo).extend((window[window.EDC.datalayerObj].page.pageInfo), obj);
            }
        }
    };

    this.pageCategoryTracking = function (obj, join) {
        if (window[window.EDC.datalayerObj] && obj) {
            if (!window[window.EDC.datalayerObj].page) {
                window[window.EDC.datalayerObj].page = {};
            }

            if (!window[window.EDC.datalayerObj].page.category) {
                window[window.EDC.datalayerObj].page.category = {};
            }

            if (!join) {
                window[window.EDC.datalayerObj].page.category = obj;
            } else {
                (window[window.EDC.datalayerObj].page.category).extend((window[window.EDC.datalayerObj].page.category), obj);
            }
        }
    };

    // This function returns the status of the user (logged in or not)
    this.userStatus = function () {
        EDC.utils.fetchJSON('GET', userStatusUrl, '',
            function (data) {
                EDC.props.userData = data;
                if (Object.keys(data).length > 0) {
                    PubSub.publish('user-status', true);
                    document.querySelector('body').classList.add('user-logged-in');
                } else {
                    PubSub.publish('user-status', false);
                }
            }, function () {
                PubSub.publish('user-status', false);
            }
        );
    };

    // Function created for MyEDC's success message toggle
    this.toggleElement = function (el) {
        if (!el.classList.contains('hide')) {
            el.classList.add('hide');
        } else if (el.classList.contains('hide')) {
            el.classList.remove('hide');
        }
    };

    // This function returns a string containing all the values of a multiple dropdown for data layer tracking.
    this.getMultiDropdownDataForTracking = function (el) {
        var ddItems = el ? el.closest('.dropdown').querySelectorAll('.item.selected-item') : [],
            ddElsValue = '';

        if (ddItems.length > 0) {
            ddItems.forEach(function (item, index) {
                ddElsValue += item.textContent;
                if ((index + 1) < ddItems.length) {
                    ddElsValue += '|';
                }
            });
        }

        return ddElsValue.toLowerCase();
    };

    this.getPosition = function (element) {
        var documentElement = document.documentElement ? document.documentElement : document.body,
            startX = typeof window.pageXOffset !== 'undefined' ? window.pageXOffset : documentElement.scrollLeft ? documentElement.scrollLeft : 0,
            startY = typeof window.pageYOffset !== 'undefined' ? window.pageYOffset : documentElement.scrollTop ? documentElement.scrollTop : 0,
            node = element,
            x = node.offsetLeft,
            y = node.offsetTop,
            styles,
            position;

        node = node.parentNode;

        do {
            if (node !== document) {
                styles = getComputedStyle(node);

                if (styles) {
                    position = styles.getPropertyValue('position');

                    x -= node.scrollLeft;
                    y -= node.scrollTop;

                    if (/relative|absolute|fixed/.test(position)) {
                        x += node.offsetLeft;
                        y += node.offsetTop;
                    }

                    node = position === 'fixed' ? null : node.parentNode;
                } else {
                    node = node.parentNode;
                }
            } else {
                node = null;
            }
        } while (node);

        return {
            x: (x + startX - documentElement.clientLeft),
            y: (y + startY - documentElement.clientTop)
        };
    };

    this.scrollTo = function (position, to, duration) {
        var documentElement = document.documentElement ? document.documentElement : document.body,
            start = typeof window.pageYOffset !== 'undefined' ? window.pageYOffset : documentElement.scrollTop ? documentElement.scrollTop : 0,
            body = document.querySelector('body'),
            html = document.querySelector('html'),
            change = to - start,
            currentTime = 0,
            increment = 20;

        duration = duration || 200;
        position = position === 'top' || position === 'left' ? position : 'top';

        this.animateScroll = function () {
            var val;

            if (body && html) {
                currentTime += increment;
                val = Math.easeInOutQuad(currentTime, start, change, duration);

                if (position === 'top') {
                    body.scrollTop = val;
                    html.scrollTop = val;
                } else if (position === 'left') {
                    body.scrollLeft = val;
                    html.scrollLeft = val;
                }

                if (currentTime < duration) {
                    setTimeout(EDC.utils.animateScroll, increment);
                }
            }
        };

        // t = current time, b = start value, c = change in value, d = duration
        Math.easeInOutQuad = function (t, b, c, d) {
            t /= d / 2;
            if (t < 1) {
                return c / 2 * t * t + b;
            }

            t--;
            return -c / 2 * (t * (t - 2) - 1) + b;
        };

        EDC.utils.animateScroll();
    };

    /**
     * Adding Swipe support
     * @param {DOM} elem - Dom element
     * @param {function} callback - Function to execute.
     * @returns {void}
     */
    this.Swipe = function (elem, callback) {
        var self = this;

        this.callback = callback;

        function handleEvent(e) {
            self.touchHandler(e);
        }

        elem.addEventListener('touchstart', handleEvent, false);
        elem.addEventListener('touchmove', handleEvent, false);
        elem.addEventListener('touchend', handleEvent, false);
    };

    this.Swipe.prototype.touches = {
        touchstart: { x: -1, y: -1 },
        touchmove: { x: -1, y: -1 },
        touchend: false,
        direction: 'undetermined',
        minpixels: 50
    };

    this.Swipe.prototype.touchHandler = function (event) {
        var touch,
            x,
            y;

        if (typeof event !== 'undefined') {
            if (typeof event.touches !== 'undefined') {
                touch = event.touches[0];
                switch (event.type) {
                    case 'touchstart':
                    case 'touchmove':
                        this.touches[event.type].x = touch.pageX;
                        this.touches[event.type].y = touch.pageY;
                        break;
                    case 'touchend':
                        this.touches[event.type] = true;
                        x = (this.touches.touchstart.x - this.touches.touchmove.x);
                        y = (this.touches.touchstart.y - (this.touches.touchmove.y - this.touches.minpixels));

                        if (x < 0) {
                            x /= -1;
                        }

                        if (y < 0) {
                            y /= -1;
                        }

                        if (x > y) {
                            this.touches.direction = this.touches.touchstart.x < this.touches.touchmove.x ? 'right' : 'left';
                        } else {
                            this.touches.direction = this.touches.touchstart.y < this.touches.touchmove.y ? 'down' : 'up';
                        }

                        this.callback(event, this.touches.direction);
                        break;
                }
            }
        }
    };

    this.getDeviceViewPort = function () {
        var windowWidth = window.innerWidth || document.documentElement.clientWidth || document.querySelector('body').clientWidth;

        return (windowWidth >= window.EDC.props.media.lg) ? 'desktop' : (windowWidth < window.EDC.props.media.lg && windowWidth >= window.EDC.props.media.md) ? 'tablet' : 'mobile';
    };

    /**
     * Global Tracking for Card Grids and Sentence Builder.
     * Tracks sentence builder dropdown selections.
     * @param {DOM} element - Dom element
     * @param {Object} cardData - Informations of the Card
     * @returns {void}
     */
    this.sentenceBuilderCGTrackEvent = function (element, cardData) {
        var obj = {},
            sentenceBuilderSelects = element.querySelectorAll('select'),
            valueSelection,
            values = [],
            hasData = false;

        sentenceBuilderSelects.forEach(function (elem) {
            if (elem.value !== '') {
                hasData = true;
                values.push(elem.value);
            } else {
                values.push(null);
            }
        });

        if (hasData) {
            valueSelection = 'objective: ' + values[0] + ';topic: ' + values[1] + ';region: ' + values[2];
            obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: cardData.dataset.eventType,
                    eventName: cardData.dataset.eventType + ' click',
                    eventAction: element.dataset.eventAction,
                    eventText: cardData.querySelector('h4.title').innerHTML,
                    engagementType: cardData.dataset.eventEngagement,
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    destinationPage: cardData.getAttribute('href'),
                    eventLevel: element.dataset.eventLevel,
                    eventStage: '',
                    eventValue2: valueSelection
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }
    };

    /**
     * Global Tracking for Card Grids and Sentence Builder.
     * Tracks clicks on card grid link and values of the sentence builder is present.
     * @param {DOM} eventActivatedFrom - Dom element
     * @param {DOM} element - Dom element
     * @param {string} scrollCnt - Event stage
     * @param {Event} e - Event
     * @returns {void}
     */
    this.cardGridTrackEvent = function (eventActivatedFrom, element, scrollCnt, e) {
        var sentenceBuilder = document.querySelector('.sentence-builder'),
            clickedCard = e ? e.currentTarget : '',
            obj = {};

        // Tracks when the "View more" button is clicked or when the infinite scroll is activated
        if (eventActivatedFrom === 'load-next-page') {
            obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: element.dataset.eventName,
                    eventPage: EDC.props.pageNameURL,
                    eventStage: scrollCnt
                }
            };

        // Tracks when a card is clicked
        } else if (eventActivatedFrom === 'card-clicked' && clickedCard !== '') {
            obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventAction: clickedCard.dataset.eventAction,
                    eventType: clickedCard.dataset.eventType,
                    eventName: clickedCard.dataset.eventName,
                    eventText: clickedCard.querySelector('h4.title').innerHTML,
                    engagementType: clickedCard.dataset.eventEngagement,
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    destinationPage: clickedCard.getAttribute('href')
                }
            };

            if (sentenceBuilder) {
                EDC.utils.sentenceBuilderCGTrackEvent(sentenceBuilder, clickedCard);
            }
        }

        EDC.utils.dataLayerTracking(obj);
    };

    this.addListTags = function (category, list, obj) {
        list = list.replace(/\[|\]| /ig, '').split(',');

        if (list.length) {
            list.forEach(function (tag, index) {
                obj[category + (index + 1)] = tag;
            });
        }
    };

    this.getMinifiedText = function (str) {
        return str.replace(/[^\w\s]/gi, '');
    };

    /**
     * Attach Events to elements
     * @param {DOM} elem - Dom element
     * @param {string|Array} event - Event or events to attach
     * @param {function} fn - Function to execute
     * @returns {void}
     */
    this.attachEvents = function (elem, event, fn) {
        if ((event && (typeof event === 'string' || typeof event === 'object')) && (elem) && (fn && typeof fn === 'function')) {
            if (typeof event === 'string') {
                if (NodeList.prototype.isPrototypeOf(elem)) {
                    elem.forEach(function (el) {
                        el.addEventListener(event, fn, false);
                    });
                } else {
                    elem.addEventListener(event, fn, false);
                }
            } else {
                event.forEach(function (ev) {
                    if (NodeList.prototype.isPrototypeOf(elem) && typeof ev === 'string') {
                        elem.forEach(function (el) {
                            el.addEventListener(ev, fn, false);
                        });
                    } else if (typeof ev === 'string') {
                        elem.addEventListener(ev, fn, false);
                    }
                });
            }
        }
    };

    /**
     * unAttach Events to elements
     * @param {DOM} elem - Dom element
     * @param {string|Array} event - Event or events to attach
     * @param {function} fn - Function to execute.
     * @returns {void}
     */
    this.unAttachEvents = function (elem, event, fn) {
        if ((event && (typeof event === 'string' || typeof event === 'object')) && (elem) && (fn && typeof fn === 'function')) {
            if (typeof event === 'string') {
                if (NodeList.prototype.isPrototypeOf(elem)) {
                    elem.forEach(function (el) {
                        el.removeEventListener(event, fn, false);
                    });
                } else {
                    elem.removeEventListener(event, fn, false);
                }
            } else {
                event.forEach(function (ev) {
                    if (NodeList.prototype.isPrototypeOf(elem) && typeof ev === 'string') {
                        elem.forEach(function (el) {
                            el.removeEventListener(ev, fn, false);
                        });
                    } else if (typeof ev === 'string') {
                        elem.removeEventListener(ev, fn, false);
                    }
                });
            }
        }
    };

    // Helper function to fetch JSON
    this.fetchJSON = function (method, url, params, callbackSuccess, callbackError, multipart) {
        var httpRequest = new XMLHttpRequest();

        method = method || 'GET';
        params = params || '';
        params = params && method === 'GET' ? ('?' + params) : params;

        if (url) {
            httpRequest.onreadystatechange = function () {
                var data,
                    result = '';

                if (httpRequest.readyState === 4) {
                    if (httpRequest.status === 200) {
                        result = httpRequest.responseText.trim();

                        try {
                            data = (result !== '' && result !== null && result !== undefined) ? JSON.parse(result) : {};
                        } catch (err) {
                            data = {};
                            // Commented by Task Request: 47380 - JSON is invalid error in Dev Console
                            // console.error('JSON is invalid');
                        }

                        if (callbackSuccess) {
                            callbackSuccess(data);
                        }
                    } else {
                        if (httpRequest.statusText) {
                            console.error(httpRequest.statusText);
                        }

                        if (callbackError) {
                            callbackError(httpRequest);
                        }
                    }
                }
            };

            if (method === 'GET') {
                httpRequest.open(method, url + params, true);
            } else {
                httpRequest.open(method, url, true);
            }

            if (!multipart) {
                httpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                httpRequest.setRequestHeader('Accept', 'text/html,application/json,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8');
            }

            httpRequest.send(params);
        }
    };

    /**
     * Set Hash value.
     * @param {string} id - ID of the component.
     * @param {string} action - Action to execute after scrolling.
     * @param {number} duration - Delay before changing the hash.
     * @param {string} separator - Special character between component and tab.
     * @returns {void}
     */
    this.setHashTag = function (id, action, duration, separator) {
        var delimiter = separator ? separator : ':';

        duration = duration || 200;

        if (id) {
            setTimeout(function () {
                window.location.hash = (id + (action ? delimiter + action : ''));
            }, duration);
        }
    };

    /**
     * Get Hash value.
     * @param {string} separator - Special character between component and tab.
     * @returns {DOM} Target
     */
    this.getHashValue = function (separator) {
        var matches = window.location.href.match(/#([^&]+)(&|$)/),
            delimiter = separator ? separator : ':',
            parts = [],
            target = {},
            i;

        if (matches) {
            parts = matches[1].split(delimiter);
            target.id = parts[0];

            for (i = 1; i < parts.length; i++) {
                target['action' + i] = parts[i];
            }
        }

        return target;
    };

    /**
     * Gets all parameters from the given URL or from window.location.href
     * @param {DOM} url - The given URL to take the params from
     * Examples:
     * EDC.utils.getURLParams() --> Returns all parameters as an object from current window URL
     * EDC.utils.getURLParams(url) --> Returns all parameters as an object from the given URL
     * EDC.utils.getURLParams().query --> Returns the specified parameter value
     * EDC.utils.getURLParams(url).query --> Returns the specified parameter value from given URL
    */

    this.getURLParams = function (url) {
        var vars = {},
            href = url ? url : window.location.href;

        href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function (m, key, value) {
            vars[key] = value;
        });

        return vars;
    };

    /**
     * Simulate a click event.
     * @param {DOM} elem - The element to simulate a click on
     * @returns {void}
     */
    this.simulateClick = function (elem) {
        // Create our event (with options)
        var evt = new MouseEvent('click', {
                bubbles: true,
                cancelable: true,
                view: window
            }),
            // If cancelled, don't dispatch our event
            // eslint-disable-next-line no-unused-vars
            canceled = !elem.dispatchEvent(evt); // NOSONAR
    };

    /**
     * Scroll to the selected tab.
     * @param {DOM} component - The current component
     * @param {DOM} tabs - List of tabs
     * @param {string} separator - Special character between component and tab.
     * @returns {void}
     */
    this.scrollToSelectedTab = function (component, tabs, separator) {
        var target = EDC.utils.getHashValue(separator),
            cmpTarget = component.getAttribute('data-component-target') || component.getAttribute('data-target') || '';

        if (cmpTarget === target.id) {
            EDC.utils.scrollTo('top', EDC.utils.getPosition(component).y);

            if (tabs !== undefined) {
                tabs.forEach(function (tab) {
                    if (target.action1 && target.action1 === tab.getAttribute('data-link-target')) {
                        EDC.utils.simulateClick(tab);
                    }
                });
            }
        }
    };

    /**
     * Scroll to deep link.
     * NOTE: it reuse scrollToSelectedTab function. This function is created to have a consistent name
     * @param {DOM} target - The current target
     * @returns {void}
     */
    this.scrollToDeepLink = function (target) {
        this.scrollToSelectedTab(target);
    };

    this.handleSwipeLeft = function (tabLabels, i) {
        if (i < tabLabels.length - 1) {
            tabLabels[i + 1].click();
        }
    };

    this.handleSwipeRight = function (tabLabels, i) {
        if (i >= 1) {
            tabLabels[i - 1].click();
        }
    };

    this.initializeSwipe = function (el) {
        var tabPanels = el.querySelectorAll('.tab-panel');

        tabPanels.forEach(function (tab) {
            // eslint-disable-next-line no-new
            new window.EDC.utils.Swipe(tab, function (event, direction) {
                var tabLabels,
                    index;

                if (event.cancelable) {
                    event.preventDefault();
                }

                tabLabels = el.querySelectorAll('.tab-label');
                index = [].slice.call(el.querySelectorAll('.tab-panel')).indexOf(el.querySelector('.tab-panel.show'));

                // Trigger Click in case element is clickable if is not any swipe event
                if (event.target.closest('a') || event.target.closest('button')) {
                    event.target.click();
                    return;
                }

                switch (direction) {
                    case 'left':
                        // Handle Swipe Left
                        EDC.utils.handleSwipeLeft(tabLabels, index);
                        break;
                    case 'right':
                        // Handle Swipe Right
                        EDC.utils.handleSwipeRight(tabLabels, index);
                        break;
                    default:
                        break;
                }
            });
        });
    };

    this.displayTabs = function (e) {
        var el = e.target,
            tabSection = el.closest('.tabs'),
            parent = el.closest('ul'),
            tabSelected = parent.querySelector('.tab-label.selected'),
            tabPanelSelected = tabSection.querySelector('.tab-panel.show'),
            tabPanelShow = '#' + el.getAttribute('aria-controls');

        e.preventDefault();

        tabSelected.setAttribute('aria-selected', 'false');
        tabSelected.classList.toggle('selected');
        el.setAttribute('aria-selected', 'true');
        el.classList.toggle('selected');

        // HACK for the first element click // can be improved
        if (el.offsetLeft < 50) {
            tabSection.querySelector('.tab-labels-container').scrollLeft = 0;
        } else {
            tabSection.querySelector('.tab-labels-container').scrollLeft = el.offsetLeft;
        }

        tabPanelSelected.classList.toggle('show');
        tabSection.querySelector(tabPanelShow).classList.toggle('show');
    };

    this.resizeTabLabels = function (labels) {
        var tabLabelsTotalWidth = 0,
            el = labels[0].closest('.tab-labels');

        labels.forEach(function (label) {
            tabLabelsTotalWidth += label.clientWidth + 2;
        });

        el.style.width = tabLabelsTotalWidth + 'px';
    };

    /**
     * Delete a cookie
     * @param {string} name - Cookie's name
     * @returns {void}
     */
    this.deleteCookie = function (name) {
        var date = new Date(),
            expires = '';

        if (name) {
            // days, hours, minutes, seconds, milliseconds
            date.setTime(date.getTime() - (1 * 24 * 60 * 60 * 1000));
            expires = '; Expires=' + date.toUTCString();

            document.cookie = encodeURIComponent(name) + '=' + expires;
        }
    };

    /**
     * Set a Cookie
     * @param {string} name - Cookie's name
     * @param {string} value - Cookie's value
     * @param {integer} exDays - Number of days until the cookie should expire
     * @param {string} path - Indicates a URL path that must exist in the requested resource before sending the Cookie header
     * @param {boolean} secure - Cookie to only be transmitted over secure protocol as https
     * @returns {void}
     */
    this.setCookie = function setCookie(name, value, exDays, path, secure) {
        var date = new Date(),
            expires = '',
            sCookie;

        if (name) {
            if (exDays) {
                // days, hours, minutes, seconds, milliseconds
                date.setTime(date.getTime() + (exDays * 24 * 60 * 60 * 1000));
                expires = '; Expires=' + date.toUTCString();
            }

            sCookie = encodeURIComponent(name) + '=' + encodeURIComponent(value) + expires;

            // Path
            sCookie += (path ? '; Path=' + path : '; Path=/');

            // Secure
            sCookie += (secure ? '; Secure' : '');

            document.cookie = sCookie;
        }
    };

    /**
     * Get a Cookie
     * @param {string} name - Cookie's name
     * @returns {void}
     */
    this.getCookie = function (name) {
        var oCrumbles = document.cookie.split(';'),
            oPair,
            key,
            i,
            value;

        if (name) {
            for (i = 0; i < oCrumbles.length; i++) {
                oPair = oCrumbles[i].split('=');
                key = decodeURIComponent(oPair[0].trim());
                value = oPair.length > 1 ? oPair[1] : '';

                if (key === name) {
                    return decodeURIComponent(value);
                }
            }
        }

        return false;
    };

    // Loading Scripts via AJAX
    this.loadScriptHead = function (url, callback, content) {
        var script;

        if (url || content) {
            script = document.createElement('script');

            if (url) {
                script.type = 'text/javascript';
                script.src = url;

                if (callback && typeof callback === 'function') {
                    script.onload = function () {
                        callback();
                    };
                }
            } else if (typeof callback === 'string') {
                script.text = content;
            }

            document.querySelector('head').append(script);
        }
    };

    // Loading Scripts via AJAX
    this.loadScripBody = function (url, callback, content) {
        var script;

        if (url || content) {
            script = document.createElement('script');

            if (url) {
                script.type = 'text/javascript';
                script.src = url;

                if (callback && typeof callback === 'function') {
                    script.onload = function () {
                        callback();
                    };
                }
            } else if (typeof callback === 'string') {
                script.text = content;
            }

            document.querySelector('body').append(script);
        }
    };

    // BROWSER DETECTION HACK
    this.detectDevice = function () {
        var html,
            isDevice;

        if ((navigator.userAgent.match(/(iPad|iPhone|iPod)/i)) || (navigator.userAgent.match(/android/i))) {
            html = document.querySelector('html');
            html.classList.add('device');

            if (navigator.userAgent.match(/android/i)) {
                html.classList.add('device-android');
            }

            isDevice = true;

        } else {
            isDevice = false;
        }

        return isDevice;
    };

    this.isDeviceMobile = function () {
        return window.innerWidth < EDC.props.media.lg;
    };

    // TABS NAVIGATION
    this.checkTabsScroll = function (e) {
        var container = e.currentTarget,
            tabLabelsContainer = container.closest('.tab-set:not(.country-grid)') ? container.closest('.tabs') : container,
            scrollDiff = container.scrollWidth - container.clientWidth;

        e.preventDefault();

        if (container.scrollLeft >= scrollDiff) {
            tabLabelsContainer.classList.remove('fade-shadow-right');
        } else if (container.scrollLeft === 0) {
            tabLabelsContainer.classList.remove('fade-shadow-left');
        } else {
            tabLabelsContainer.classList.add('fade-shadow-right');
            tabLabelsContainer.classList.add('fade-shadow-left');
        }
    };

    this.verifyScrollPos = function (elem) {
        var containerTabs = elem.closest('.tabs'),
            tabLabelsContainer = elem.closest('.tab-set:not(.country-grid)') ? containerTabs : elem,
            tabLabels = elem.querySelector('.tab-labels'),
            arrowLeft = containerTabs.querySelector('.icon-arrow-left'),
            arrowRight = containerTabs.querySelector('.icon-arrow-right'),
            end = Math.ceil(tabLabels.scrollLeft + elem.clientWidth) >= tabLabels.scrollWidth,
            start = Math.floor(tabLabels.scrollLeft) === 0;

        if (start) {
            tabLabelsContainer.classList.remove('fade-shadow-left');
            arrowLeft.classList.remove('show');
        } else {
            tabLabelsContainer.classList.add('fade-shadow-left');
            arrowLeft.classList.add('show');
        }

        if (end) {
            tabLabelsContainer.classList.remove('fade-shadow-right');
            arrowRight.classList.remove('show');
        } else {
            tabLabelsContainer.classList.add('fade-shadow-right');
            arrowRight.classList.add('show');
        }
    };

    this.scrollToHorizontal = function (elem, to, duration, direction) {
        var differencestep,
            perTick,
            tabLabelsContainer = elem.closest('.tab-labels-container'),
            end = Math.ceil(elem.scrollLeft + tabLabelsContainer.clientWidth) >= elem.scrollWidth,
            isLeft = direction === 'left',
            left;

        to = to > 0 ? to : 0;

        if (duration > 0 && (isLeft || !end)) {
            differencestep = isLeft ? elem.scrollLeft - to : to - elem.scrollLeft;
            perTick = differencestep / duration * 10;

            setTimeout(function () {
                elem.scrollLeft = isLeft ? elem.scrollLeft - perTick : elem.scrollLeft + perTick;
                left = elem.scrollLeft;

                if ((isLeft && left > to) || (!isLeft && left < to)) {
                    EDC.utils.scrollToHorizontal(elem, to, duration - 10, direction);
                } else if (isLeft && (left <= to)) {
                    EDC.utils.verifyScrollPos(tabLabelsContainer);
                }
            }, 10);
        } else if (direction === 'right') {
            EDC.utils.verifyScrollPos(tabLabelsContainer);
        }
    };

    this.scrollTabs = function (elem, direction, doTabScroll) {
        var tabLabels = elem.querySelector('.tab-labels'),
            tabLabelsContainer = elem.querySelector('.tab-labels-container'),
            scrollOffset = doTabScroll ? tabLabels.querySelector('.tab-label').clientWidth : tabLabelsContainer.clientWidth,
            scrollMove = direction === 'right' ? tabLabels.scrollLeft + scrollOffset : tabLabels.scrollLeft - scrollOffset,
            scrollRight = direction === 'right' && scrollMove < tabLabels.scrollWidth,
            scrollLeft = direction === 'left' && tabLabels.scrollLeft !== 0;

        if (scrollRight || scrollLeft) {
            EDC.utils.scrollToHorizontal(tabLabels, scrollMove, 1000, direction);
        }
    };

    this.scrollContent = function (elem, direction) {
        EDC.utils.scrollTabs(elem, direction, false);
    };

    this.setAttributes = function (el, attrs) {
        var key;

        for (key in attrs) {
            if (attrs.hasOwnProperty(key)) {
                el.setAttribute(key, attrs[key]);
            }
        }
    };

    this.setClasses = function (el, classes) {
        classes.forEach(function (cls) {
            if (cls !== '') {
                el.classList.add(cls);
            }
        });
    };

    this.setMobileText = function (element) {
        var textElements = element ? element.querySelectorAll('.utils-change-text') : document.querySelectorAll('.utils-change-text');

        isDeviceMobile = EDC.utils.isDeviceMobile();

        if (textElements) {
            textElements.forEach(function (elem) {
                if (isDeviceMobile) {
                    elem.textContent = elem.getAttribute('data-mobile-text') ? elem.getAttribute('data-mobile-text') : elem.getAttribute('data-text');
                } else {
                    elem.textContent = elem.getAttribute('data-text');
                }
            });
        }
    };

    this.changeMobileTextOnResize = function () {
        isDeviceMobile = EDC.utils.isDeviceMobile();
        if (!resizeEventInitialized) {
            EDC.utils.attachEvents(window, 'resize', function () {
                if (isDeviceMobile !== EDC.utils.isDeviceMobile()) {
                    EDC.utils.setMobileText();
                }
            });

            resizeEventInitialized = true;
        }
    };

    this.removeContentEdcFromLink = function (url) {
        var content = '/content/edc';

        if (window.location.href.indexOf(content) === -1) {
            url = url.replace(content, '');
        }

        return url;
    };

    this.activateParallax = function (els, centered, skew) {
        var left = centered ? '-50%' : '0';

        Array.from(els).forEach(function (item) {
            var img = item,
                imgPrnt = img.closest('.img-wrapper'),
                hasGraphic = imgPrnt.classList.contains('chevrons') || imgPrnt.classList.contains('backslashes') || imgPrnt.classList.contains('pyramids') || imgPrnt.classList.contains('waves');

            function _parallaxing() {
                var speed = parseInt(img.getAttribute('data-speed'), 10),
                    imgY = imgPrnt.getBoundingClientRect().top,
                    winY = img.scrollTop,
                    winH = img.clientHeight,
                    parentH = imgPrnt.clientHeight,
                    winBottom = winY + winH,
                    imgBottom,
                    imgTop,
                    imgPercent,
                    scrollingGraphic,
                    isTablet = window.innerWidth >= window.EDC.props.media.md && window.innerWidth < window.EDC.props.media.lg,
                    isMobile = window.innerWidth < window.EDC.props.media.md,
                    skewing = skew && !item.closest('section.blur') ? (isTablet || isMobile ? 'skew(6deg)' : 'skew(0)') : '',
                    graphic = hasGraphic ? imgPrnt.querySelector('.graphic') : '';

                if (winBottom > imgY && winY < imgY + parentH) {
                    imgBottom = ((winBottom - imgY) * speed);
                    imgTop = winH + parentH;
                    imgPercent = ((imgBottom / imgTop) * 100) + (50 - (speed * 50));
                    scrollingGraphic = -((imgBottom / imgTop) * 100) + (50 - (speed * 50) * 2) + 100;
                }

                img.style.top = imgPercent + '%';
                img.style.transform = 'translate(' + left + ', -' + imgPercent + '%) ' + skewing;

                if (graphic && !isMobile && !isTablet) {
                    graphic.style.top = scrollingGraphic + '%';
                } else if (isMobile || isTablet) {
                    graphic.removeAttribute('style');
                }
            }

            EDC.utils.attachEvents(window, 'resize', _parallaxing);
            EDC.utils.attachEvents(document, 'scroll', _parallaxing);
            EDC.utils.attachEvents(document, 'ready', _parallaxing);
        });
    };

    this.animateInElWhenScrolled = function (els, fn, an) {
        var isTablet = window.innerWidth >= window.EDC.props.media.md && window.innerWidth < window.EDC.props.media.lg,
            isMobile = window.innerWidth < window.EDC.props.media.md,
            displayPosition = !isTablet && !isMobile ? 1.3 : isTablet ? 1.15 : 1.1;

        if (els.length > 0) {
            Array.from(els).forEach(function (el) {
                if (el.getBoundingClientRect().top <= (window.innerHeight / displayPosition)) {
                    if (el.classList.contains('animate-content-on-scroll')) {
                        el.classList.remove('animate-content-on-scroll');
                    } else if (el.classList.contains('animate-quotes-on-scroll')) {
                        el.classList.remove('animate-quotes-on-scroll');
                    } else if (el.classList.contains('animate-keyline-on-scroll')) {
                        el.classList.remove('animate-keyline-on-scroll');
                    }

                    el.classList.add(an);
                }
            });
        } else {
            window.removeEventListener('scroll', fn);
        }
    };

    this.initCarousel = function (carousel, tabletNumb, desktopNumb) {
        var visibleSlides = carousel.querySelectorAll('.c-triage-result-card:not(.hide), .campaign-product-card:not(.hide), .slide'),
            carouselBallons = carousel.querySelector('.carousel-ballons'),
            isTriageTool = carousel.closest('.c-triage-tool'),
            isProductCampaign = carousel.closest('.c-campaign-product-carousel'),
            slidesPerScreenTablet = parseInt(tabletNumb ? tabletNumb : 1, 10),
            slidesPerScreenDesktop = parseInt(desktopNumb ? desktopNumb : 1, 10),
            cardWidthPercentage,
            carouselBallonsBtns,
            carouselActions = carousel.querySelector('.carousel-actions'),
            btnLeft = carouselActions.querySelector('.carousel-button[data-action="left"]'),
            btnRight = carouselActions.querySelector('.carousel-button[data-action="right"]'),
            isTablet,
            isMobile,
            currentPosition = 0,
            initialTouchPos = 0,
            finalTouchPos = 0;

        function _resetCarouselPosition() {
            currentPosition = 0;

            if (isTriageTool) {
                btnLeft.classList.add('hide');
                btnRight.classList.remove('hide');
            } else {
                btnLeft.setAttribute('disabled', 'disabled');
                btnRight.removeAttribute('disabled');
            }

            visibleSlides.forEach(function (slide) {
                slide.style.left = '0';
            });
        }

        function _isIE() {
            var ua = window.navigator.userAgent,
                msie = ua.indexOf('MSIE ');

            return msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./);
        }

        // Carousel adjustment for Triage Tool
        function _adjustCarousel() {
            isTablet = window.innerWidth >= window.EDC.props.media.md && window.innerWidth < window.EDC.props.media.lg;
            isMobile = window.innerWidth < window.EDC.props.media.md;
            _resetCarouselPosition();
            carouselActions.classList.add('hide');

            if (!carouselActions.classList.contains('keep-hidden')) {
                carousel.style.height = 'auto';
                carouselActions.classList.remove('hide');

                if (isMobile) {
                    cardWidthPercentage = 100;
                    btnRight.classList.remove('hide');

                    if (isTriageTool) {
                        carouselActions.classList.add('hide');
                    }
                } else if (isTablet) {
                    cardWidthPercentage = Math.floor(100 / slidesPerScreenTablet) - (isProductCampaign ? 1 : 0);

                    if (visibleSlides.length <= slidesPerScreenTablet) {
                        btnRight.classList.add('hide');
                    } else {
                        btnRight.classList.remove('hide');
                    }
                } else {
                    cardWidthPercentage = Math.floor(100 / slidesPerScreenDesktop - (isProductCampaign ? 1 : 0));

                    if (visibleSlides.length <= slidesPerScreenDesktop) {
                        btnRight.classList.add('hide');
                    } else {
                        btnRight.classList.remove('hide');
                    }
                }

                if (!isMobile && !isProductCampaign) {
                    carousel.style.height = parseInt(window.getComputedStyle(visibleSlides[0]).getPropertyValue('height'), 10) + (_isIE() ? 70 : 0) + 'px';
                }
            }
        }

        // Buttons functionality
        function _disableEnableBtn(btn, action) {
            if (action === 'enable') {
                btn.classList.remove('hide');
                btn.classList.remove('disabled');
                btn.removeAttribute('disabled');
            } else {
                btn.classList.add('hide');
                btn.classList.add('disabled');
                btn.setAttribute('disabled', 'disabled');
            }
        }

        function _hideBtnsForAccessibility() {
            var btn = carousel.querySelector('.slide.active .btn-container .button');

            visibleSlides.forEach(function (slide) {
                var slideBtn = slide.querySelector('.btn-container .button');

                if (slideBtn) {
                    slideBtn.setAttribute('tabindex', '-1');
                }
            });

            if (btn) {
                btn.removeAttribute('tabindex');
            }
        }

        function _btnClicked(e) {
            var action = e.target.getAttribute('data-action'),
                divider = 100;

            if (action === 'right') {
                _disableEnableBtn(btnLeft, 'enable');
                currentPosition -= cardWidthPercentage;
            } else if (action === 'left') {
                _disableEnableBtn(btnRight, 'enable');
                currentPosition += cardWidthPercentage;
            }

            visibleSlides.forEach(function (slide) {
                slide.style.left = currentPosition + (isTriageTool || isProductCampaign ? '%' : 'vw');
                slide.classList.remove('active');
            });

            if (isProductCampaign) {
                if (!isTablet && !isMobile) {
                    divider = Math.floor(divider / desktopNumb) - (isProductCampaign ? 1 : 0);
                } else if (isTablet) {
                    divider = Math.floor(divider / tabletNumb) - (isProductCampaign ? 1 : 0);
                }
            }

            if (carouselBallonsBtns) {
                carouselBallonsBtns.forEach(function (ballon, index) {
                    ballon.classList.remove('active');

                    if ((Math.abs(currentPosition) / divider) === parseInt(ballon.getAttribute('data-slide-position'), 10)) {
                        ballon.classList.add('active');
                        visibleSlides[index].classList.add('active');
                    }
                });
            }

            if (currentPosition === 0) {
                _disableEnableBtn(btnLeft, 'disable');
            } else if ((visibleSlides.length * cardWidthPercentage) + currentPosition <= divider) {
                _disableEnableBtn(btnRight, 'disable');
            }

            _hideBtnsForAccessibility();
        }

        // Ballons functionality
        function _activateSlideFromBallon(e) {
            var thisBallon = e.target,
                thisBallonPos,
                oldActiveBallon,
                oldActiveBallonPos,
                countOfSteps = 0,
                i;

            carouselBallonsBtns.forEach(function (ballon) {
                if (ballon.classList.contains('active')) {
                    oldActiveBallon = ballon;
                }
                ballon.classList.remove('active');
            });

            thisBallon.classList.add('active');
            thisBallonPos = parseInt(thisBallon.getAttribute('data-slide-position'), 10);
            oldActiveBallonPos = parseInt(oldActiveBallon.getAttribute('data-slide-position'), 10);
            countOfSteps = thisBallonPos - oldActiveBallonPos;

            for (i = 0; i < Math.abs(countOfSteps); i++) {
                carouselActions.querySelector('.carousel-button[data-action="' + (countOfSteps > 0 ? 'right' : 'left') + '"]').click();
            }
        }

        function _createBallons() {
            var d;

            if (carouselBallons) {
                d = document;
                carouselBallons.classList.remove('hide');
                visibleSlides.forEach(function (slide, index) {
                    var button = d.createElement('button');

                    button.setAttribute('data-slide-position', slide.getAttribute('data-slide-position'));
                    button.setAttribute('type', 'button');
                    button.classList.add('ballon-for-slide');
                    EDC.utils.attachEvents(button, 'click', _activateSlideFromBallon);

                    if (index === 0) {
                        button.classList.add('active');
                    }

                    carouselBallons.appendChild(button);
                });
            }

            carouselBallonsBtns = carouselBallons.querySelectorAll('.ballon-for-slide');
        }

        function _refreshBallons() {
            if (carouselBallons) {
                while (carouselBallons.firstChild) {
                    carouselBallons.removeChild(carouselBallons.lastChild);
                }
                _createBallons();
            }
        }

        // Functionality for touch devices
        function _handleTouchStart(e) {
            initialTouchPos = e.touches[0].pageX;
        }

        function _handleTouchMove(e) {
            finalTouchPos = e.touches[0].pageX;
        }

        function _handleTouchEnd() {
            var direction = (initialTouchPos !== finalTouchPos && initialTouchPos !== 0 && finalTouchPos !== 0) ? initialTouchPos > finalTouchPos ? 'right' : 'left' : null;

            if (direction) {
                carouselActions.querySelector('.carousel-button[data-action="' + direction + '"]').click();
            }

            initialTouchPos = 0;
            finalTouchPos = 0;
        }

        // Global events attachment
        function _attachEvents() {
            EDC.utils.attachEvents(btnLeft, 'click', _btnClicked);
            EDC.utils.attachEvents(btnRight, 'click', _btnClicked);
        }

        _attachEvents();

        if (isTriageTool || isProductCampaign) {
            if (isProductCampaign) {
                _createBallons();
                cardWidthPercentage = 100;
            }

            btnLeft.classList.add('hide');
            _adjustCarousel();
            EDC.utils.attachEvents(window, 'resize', _adjustCarousel);
            EDC.utils.attachEvents(window, 'resize', _refreshBallons);
        } else if (visibleSlides.length > 1) {
            cardWidthPercentage = 100;
            _createBallons();
            EDC.utils.attachEvents(carousel, 'touchstart', _handleTouchStart);
            EDC.utils.attachEvents(carousel, 'touchmove', _handleTouchMove);
            EDC.utils.attachEvents(carousel, 'touchend', _handleTouchEnd);
            _hideBtnsForAccessibility();
        }
    };

    this.addModalSearchListeners = function () {
        var d = document,
            body = d.querySelector('body'),
            modal = d.querySelector('.c-modal-search[data-modal-to-trigger="modal-search"]'),
            modalDialog = modal.querySelector('.modal-dialog'),
            inputFocus = modal.querySelector('.cmp-search__input'),
            modalClose = modal.querySelector('.modal-close'),
            modalTriggerBtns = d.querySelectorAll('.trigger-modal-search[data-modal-to-trigger="modal-search"]');

        function _resetBody() {
            EDC.utils.unAttachEvents(window, 'resize');
            body.classList.remove('modal-is-open');
            if (modal) {
                bodyScrollLock.enableBodyScroll(modal);
            }
        }

        function _closeModal(e) {
            e.preventDefault();
            modal.classList.remove('show');
            modalDialog.classList.remove('show');
            _resetBody();
        }

        function _detectEsc(e) {
            var modalOpened = d.querySelector('.modal-dialog.show');

            if (e.keyCode === EDC.props.escapeKeyCode && modalOpened) {
                modalOpened.classList.remove('show');
                EDC.utils.unAttachEvents(d, 'keyup', _detectEsc);
                _resetBody();
            }
        }

        function _attachEvents() {
            if (modal && modalDialog && modalTriggerBtns.length > 0) {
                modalTriggerBtns.forEach(function (btn) {
                    EDC.utils.attachEvents(btn, 'click', function () {
                        EDC.utils.attachEvents(d, 'keyup', _detectEsc);
                        EDC.utils.attachEvents(modalClose, 'click', _closeModal);
                        modal.classList.add('show');
                        modalDialog.classList.add('show');
                        body.classList.add('modal-is-open');
                        inputFocus.focus();
                    });
                });
            }
        }

        _attachEvents();
    };

    this.refreshCountryForLoqate = function () {
        var countryDropdownWrappers = document.querySelectorAll('form [data-form-field="companyCountry"]'),
            countryDropdown,
            selectedCountry,
            countryList;

        if (countryDropdownWrappers.length) {
            countryDropdownWrappers.forEach(function (wrapper) {
                countryDropdown = wrapper.querySelector('.form-control.ui.selection.dropdown');
                selectedCountry = countryDropdown.querySelector('.text').textContent;
                countryList = countryDropdown.querySelectorAll('.menu .item');

                countryList.forEach(function (item, index) {
                    if (selectedCountry === item.textContent) {
                        if (countryList[index - 1]) {
                            countryList[index - 1].click();
                        } else if (countryList[index + 1]) {
                            countryList[index + 1].click();
                        }

                        item.click();
                    }
                });
            });
        }
    };

    this.showModal = function (classToShow, trackWhenOpened) {
        var modal = document.querySelector('.' + classToShow),
            obj;

        modal.classList.remove('hide');
        modal.classList.add('show');

        if (trackWhenOpened) {
            obj = {
                eventInfo: {
                    eventComponent: modal.dataset.eventComponent,
                    eventType: modal.dataset.eventType,
                    eventName: modal.dataset.eventName,
                    eventAction: modal.dataset.eventAction,
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    engagementType: modal.dataset.engagementType,
                    eventText: modal.dataset.eventText,
                    eventValue: modal.dataset.eventValue,
                    destinationPage: modal.dataset.destinationPage
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }
    };

    this.elContainsCl = function (el, cl) {
        return el.classList.contains(cl);
    };

    this.preventingKeydownForAccessibility = function (e, isMenuOpen) {
        var keyCode = e.keyCode || e.which;

        if (keyCode === EDC.props.arrowUpKeyCode || keyCode === EDC.props.arrowDownKeyCode || (isMenuOpen && keyCode === EDC.props.tabKeyCode)) {
            e.preventDefault();
        }
    };

    this.focusingOnFirstElementForAccessibility = function (e, arr) {
        if (e.type === 'keydown') {
            arr.some(function (thisAnchor) {
                var value = thisAnchor.offsetWidth > 0 && thisAnchor.offsetHeight > 0 && !thisAnchor.classList.contains('close') && !thisAnchor.classList.contains('back');

                if (value) {
                    setTimeout(function () {
                        thisAnchor.focus();
                    }, 100);
                }

                return value;
            });
        }
    };

    this.enableFocusTrap = function (e, arr, triggerers, closeFn) {
        var d = document,
            eTarget = e.target,
            keyCode = e.keyCode || e.which,
            upKey = keyCode === EDC.props.arrowUpKeyCode,
            downKey = keyCode === EDC.props.arrowDownKeyCode,
            escapeKey = keyCode === EDC.props.escapeKeyCode || keyCode === EDC.props.tabKeyCode || (e.shiftKey && keyCode === EDC.props.tabKeyCode),
            activateKey = keyCode === EDC.props.enterKeyCode || keyCode === EDC.props.spaceKeyCode,
            arrLength = arr.length - 1,
            parent = e.target.closest('[class^="c-"]') || e.target.closest('.show, .hover'),
            subitemsParent = eTarget.parentNode,
            submenu = subitemsParent ? subitemsParent.querySelector('.submenu') : null;

        if (upKey || downKey || escapeKey || (activateKey && eTarget.classList.contains('modal-close'))) {
            e.preventDefault();
        }

        arr.some(function (thisAnchor, index) {
            var value = thisAnchor === d.activeElement;

            if (value) {
                if (upKey) {
                    if (index === 0) {
                        arr[arrLength].focus();
                    } else {
                        arr[index - 1].focus();
                    }
                } else if (downKey) {
                    if (index === arrLength) {
                        arr[0].focus();
                    } else {
                        arr[index + 1].focus();
                    }
                } else if (escapeKey || (activateKey && eTarget.classList.contains('modal-close'))) {
                    if (closeFn) {
                        closeFn();
                    }

                    if (parent) {
                        parent.classList.remove('show');
                        parent.classList.remove('hover');
                    }

                    if (triggerers) {
                        if (Array.isArray(triggerers)) {
                            triggerers.some(function (triggerer) {
                                var trg = triggerer.offsetWidth > 0 && triggerer.offsetHeight > 0;

                                if (trg) {
                                    triggerer.setAttribute('aria-expanded', 'false');
                                    triggerer.focus();
                                }

                                return trg;
                            });
                        } else {
                            triggerers.setAttribute('aria-expanded', 'false');
                            triggerers.focus();
                        }
                    }

                    PubSub.publish('submenu-opened', false);
                } else if (activateKey && eTarget) {
                    if (submenu) {
                        submenu.classList.add('show');
                    } else if (eTarget.href !== undefined && !eTarget.classList.contains('close')) {
                        e.preventDefault();
                        window.location = eTarget.href;
                    }
                }
            }

            return value;
        });
    };

    this.checkForAutoScroll = function () {
        var self = this,
            el,
            params = self.getURLParams(),
            timer,
            counter = 0,
            param = params.dataAnchorForScroll;

        if (param) {
            timer = setInterval(function () {
                el = document.querySelector('[data-target="' + param + '"]');
                counter++;

                if (el) {
                    clearInterval(timer);
                    setTimeout(function () {
                        self.scrollTo('top', self.getPosition(el).y - 150);
                    }, 1000);
                } else if (counter >= 20) {
                    clearInterval(timer);
                }
            }, 100);
        }
    };

    this.checkForAutoScroll();

    this.pdfsGlobalDataLayer = function () {
        var allPdfAnchors;

        function _trackPdf(e) {
            var el = e.currentTarget,
                elComponentName = el.getAttribute('data-event-component'),
                span = el ? el.querySelector('span') : null,
                parent = el.closest('[data-event-component]'),
                obj = {
                    eventInfo: {
                        destinationPage: el.href,
                        engagementType: '2',
                        eventAction: 'main body',
                        eventComponent: elComponentName ? elComponentName : parent ? parent.getAttribute('data-event-component') : '',
                        eventLevel: 'primary',
                        eventName: 'button click -download',
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        eventText: span ? span.textContent : '',
                        eventType: 'link',
                        eventValue: el.href,
                        eventValue2: ''
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        window.onload = function () {
            allPdfAnchors = document.querySelectorAll('a[href*=".pdf"]');
            EDC.utils.attachEvents(allPdfAnchors, 'click', _trackPdf);
        };
    };

    this.pdfsGlobalDataLayer();

    this.addLoggedInUserEmailToField = function (fieldElement) {
        var userEmail = EDC.props.userData.email ? EDC.props.userData.email : '';

        if (fieldElement) {
            fieldElement.value = userEmail;
        }
    };
};
