/**
 * Lazy Load for Images
 */
(function () {
    'use strict';

    window.EDC = window.EDC || {};

    EDC.utils = EDC.utils || {};

    /**
     * @name EDC.utils.lazyLoad
     * @description Add Lazy Load functionality into the global EDC.utils object.
     * @returns {Object} Lazy Load methods
     */
    EDC.utils.lazyLoad = (function () {

        /**
         * @name _loadImages
         * @description Trigger the Lazy Load for all the images with only data-lazy attribute when the DOM is ready.
         * @returns {void}
         */
        function _loadImages() {
            var lazyImages = document.querySelectorAll('img[data-lazy-src]:not([src]):not([data-lazy-load-delayed])');

            if (lazyImages) {
                lazyImages.forEach(function (image) {
                    image.setAttribute('src', image.dataset.lazySrc || '');
                });
            }
        }

        /**
         * @name _loadImagesByTarget
         * @description Trigger the Lazy load for images with data-lazy and data-lazy-load-delayed attributes when and event is fired.
         * @param {NodeElement} element - optional, allow to specify the element of the DOM where looking for images with data-lazy and data-lazy-load-delayed attributes. Default document
         * @param {number} numItems - optional, allow to specify how many images should be loaded with Lazy Load. Default all the images.
         * @returns {void}
         */
        function _loadImagesByTarget(element, numItems) {
            var counter = 1,
                elem = element || document,
                lazyImages = elem.querySelectorAll('img[data-lazy-src][data-lazy-load-delayed]:not([src])'),
                currentCount;

            if (lazyImages) {
                currentCount = numItems || lazyImages.length;

                lazyImages.forEach(function (image) {
                    if (counter <= currentCount) {
                        image.setAttribute('src', (image.dataset.lazySrc || ''));
                    }

                    counter++;
                });
            }
        }

        /**
         * @name _domObserver
         * @description Trigger the Lazy load for images with data-lazy and data-lazy-load-delayed attributes when and DOM is updated.
         * @returns {void}
         */
        function _domObserver() {
            var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver,
                body = document.querySelector('body'),
                observer;

            if (typeof ContextHub !== 'undefined' && ContextHub !== null) {
                ContextHub.eventing.on(ContextHub.Constants.EVENT_TEASER_LOADED, _loadImages);
            }

            observer = new MutationObserver(function (mutations) {
                mutations.forEach(function (mutation) {
                    // needed for IE
                    var nodesArray = [].slice.call(mutation.addedNodes),
                        lazyImages;

                    if (nodesArray.length > 0) {
                        nodesArray.forEach(function (addedNode) {
                            if (addedNode.querySelectorAll) {
                                lazyImages = addedNode.querySelectorAll('img[data-lazy-src]:not([src]):not([data-lazy-load-delayed])');

                                if (lazyImages) {
                                    _loadImages();
                                }
                            }
                        });
                    }
                });
            });

            observer.observe(body, {
                subtree: true,
                childList: true,
                characterData: true
            });
        }

        /**
         * @name _attachEvents
         * @description Attach initial events.
         * @returns {void}
         */
        function _attachEvents() {
            EDC.utils.attachEvents(document, 'DOMContentLoaded', _loadImages);
        }

        _attachEvents();
        _domObserver();

        // Public methods
        return {
            loadImagesByTarget: _loadImagesByTarget
        };
    })();
})();
