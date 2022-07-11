var onPageNavigationJS = (function () {
    'use strict';

    // Tracking purposes
    function trackEvent(el) {
        var navEl = el.target.closest('.on-page-navigation'),
            navElList = el.target.closest('li'),
            obj = {
                eventInfo: {
                    eventComponent: navEl.dataset.eventComponent,
                    eventType: navEl.dataset.eventType,
                    eventName: 'anchor link ' + el.type,
                    eventAction: navElList.dataset.index, // order of link as it appears in the cluster
                    eventText: el.target.textContent.toLowerCase(),
                    eventValue: '',
                    eventPage: EDC.props.pageNameURL,
                    destinationPage: '',
                    engagementType: navEl.dataset.eventEngagement
                }
            };

        EDC.utils.dataLayerTracking(obj);
    }

    // Helper function to scroll to an specific position
    function scrollTo(to, duration) {
        var documentElement = document.documentElement ? document.documentElement : document.body,
            start = typeof window.pageYOffset !== 'undefined' ? window.pageYOffset : documentElement.scrollTop ? documentElement.scrollTop : 0,
            body = document.querySelector('body'),
            html = document.querySelector('html'),
            change = to - start,
            currentTime = 0,
            increment = 20;

        function animateScroll() {
            var val;

            if (body && html) {
                currentTime += increment;
                val = Math.easeInOutQuad(currentTime, start, change, duration);

                body.scrollTop = val;
                html.scrollTop = val;

                if (currentTime < duration) {
                    setTimeout(animateScroll, increment);
                }
            }
        }

        // t = current time, b = start value, c = change in value, d = duration
        Math.easeInOutQuad = function (t, b, c, d) {
            t /= d / 2;
            if (t < 1) {
                return c / 2 * t * t + b;
            }

            t--;
            return -c / 2 * (t * (t - 2) - 1) + b;
        };

        animateScroll();
    }

    // Helper function to get an element's exact position
    function getPosition(element) {
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
    }

    function scrollToSection(e) {
        var target = this.getAttribute('data-target') || '',
            section = document.querySelector('.section-title a[data-target="' + target + '"]'),
            position;

        e.preventDefault();
        trackEvent(e); //

        if (section) {
            position = getPosition(section);
            scrollTo(position.y, 200);
        }
    }

    // Public methods
    function init() {
        var links = document.querySelectorAll('.on-page-navigation .link'),
            i;

        if (links) {
            for (i = 0; i < links.length; i++) {
                links[i].addEventListener('click', scrollToSection);
            }
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', onPageNavigationJS.init);