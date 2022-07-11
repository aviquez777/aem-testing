;(function (window, document) {
    'use strict';

    window = (typeof window != 'undefined' && window.Math == Math)
    ? window
    : (typeof self != 'undefined' && self.Math == Math)
        ? self
        : Function('return this')()
    ;

    window.bodyScrollLock = (function () {
        var bodyScrollLock = {},
            isIosDevice = typeof window !== 'undefined' && window.navigator && window.navigator.platform && /iPad|iPhone|iPod|(iPad Simulator)|(iPhone Simulator)|(iPod Simulator)/.test(window.navigator.platform),
            firstTargetElement = null,
            allTargetElements = [],
            documentListenerAdded = false,
            initialClientY = -1,
            documentBody = document.body,
            documentHTML = document.querySelector('html'),
            previousBodyOverflowSetting = void 0,
            previousHTMLOverflowSetting = void 0,
            previousBodyPaddingRight = void 0;

        function _toConsumableArray(arr) {
            if (Array.isArray(arr)) {
                for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) {
                    arr2[i] = arr[i];
                }
                return arr2;
            } else {
                return Array.from(arr);
            }
        }
        // Adopted and modified solution from Bohdan Didukh (2017)
        // https://stackoverflow.com/questions/41594997/ios-10-safari-prevent-scrolling-behind-a-fixed-overlay-and-maintain-scroll-posi
        function preventDefault(rawEvent) {
            var e = rawEvent || window.event;

            if (e.preventDefault) e.preventDefault();
            return false;
        }

        function setOverflowHidden(options) {
            // Setting overflow on body/documentElement synchronously in Desktop Safari slows down
            // the responsiveness for some reason. Setting within a setTimeout fixes this.
            setTimeout(function() {
                documentBody = documentBody || document.body;
                // If previousBodyPaddingRight is already set, don't set it again.
                if (previousBodyPaddingRight === undefined) {
                    var _reserveScrollBarGap = !!options && options.reserveScrollBarGap === true;
                    var scrollBarGap = window.innerWidth - document.documentElement.clientWidth;

                    if (_reserveScrollBarGap && scrollBarGap > 0) {
                        previousBodyPaddingRight = documentBody.style.paddingRight;
                        documentBody.style.paddingRight = scrollBarGap + 'px';
                    }
                }

                // If previousBodyOverflowSetting is already set, don't set it again.
                if (previousBodyOverflowSetting === undefined && previousHTMLOverflowSetting === undefined) {
                    previousBodyOverflowSetting = documentBody.style.overflow;
                    previousHTMLOverflowSetting = documentHTML.style.overflow;
                    documentBody.style.overflow = 'hidden';
                    documentHTML.style.overflow = 'hidden';
                }
            });
        }

        function restoreOverflowSetting() {
            // Setting overflow on body/documentElement synchronously in Desktop Safari slows down
            // the responsiveness for some reason. Setting within a setTimeout fixes this.
            setTimeout(function() {
                documentBody = documentBody || document.body;
                if (previousBodyPaddingRight !== undefined) {
                    documentBody.style.paddingRight = previousBodyPaddingRight;

                    // Restore previousBodyPaddingRight to undefined so setOverflowHidden knows it
                    // can be set again.
                    previousBodyPaddingRight = undefined;
                }

                if (previousBodyOverflowSetting !== undefined && previousHTMLOverflowSetting !== undefined) {
                    documentBody.style.overflow = previousBodyOverflowSetting;
                    documentHTML.style.overflow = previousHTMLOverflowSetting;

                    // Restore previousBodyOverflowSetting to undefined
                    // so setOverflowHidden knows it can be set again.
                    previousBodyOverflowSetting = undefined;
                    previousHTMLOverflowSetting = undefined;
                }
            });
        }

        // https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollHeight#Problems_and_solutions
        function isTargetElementTotallyScrolled(targetElement) {
            return targetElement ? targetElement.scrollHeight - targetElement.scrollTop <= targetElement.clientHeight : false;
        }

        function handleScroll(event, targetElement) {
            var clientY = event.targetTouches[0].clientY - initialClientY;

            if (targetElement && targetElement.scrollTop === 0 && clientY > 0) {
                // element is at the top of its scroll
                return preventDefault(event);
            }

            if (isTargetElementTotallyScrolled(targetElement) && clientY < 0) {
                // element is at the top of its scroll
                return preventDefault(event);
            }

            event.stopPropagation();
            return true;
        }

        bodyScrollLock.disableBodyScroll = function disableBodyScroll(targetElement, options) {
            if (isIosDevice) {
                // targetElement must be provided, and disableBodyScroll must not have been
                // called on this targetElement before.
                if (targetElement && !allTargetElements.includes(targetElement)) {
                    allTargetElements = [].concat(_toConsumableArray(allTargetElements), [targetElement]);

                    targetElement.ontouchstart = function(event) {
                        if (event.targetTouches.length === 1) {
                            // detect single touch
                            initialClientY = event.targetTouches[0].clientY;
                        }
                    };
                    targetElement.ontouchmove = function(event) {
                        if (event.targetTouches.length === 1) {
                            // detect single touch
                            handleScroll(event, targetElement);
                        }
                    };

                    if (!documentListenerAdded) {
                        document.addEventListener('touchmove', preventDefault, {
                            passive: false
                        });
                        documentListenerAdded = true;
                    }
                }
            } else {
                setOverflowHidden(options);

                if (!firstTargetElement) firstTargetElement = targetElement;
            }
        };

        bodyScrollLock.clearAllBodyScrollLocks = function clearAllBodyScrollLocks() {
            if (isIosDevice) {
                // Clear all allTargetElements ontouchstart/ontouchmove handlers, and the references
                allTargetElements.forEach(function(targetElement) {
                    targetElement.ontouchstart = null;
                    targetElement.ontouchmove = null;
                });

                if (documentListenerAdded) {
                    document.removeEventListener('touchmove', preventDefault, {
                        passive: false
                    });
                    documentListenerAdded = false;
                }

                allTargetElements = [];

                // Reset initial clientY
                initialClientY = -1;
            } else {
                restoreOverflowSetting();

                firstTargetElement = null;
            }
        };

        bodyScrollLock.enableBodyScroll = function enableBodyScroll(targetElement) {
            if (isIosDevice) {
                targetElement.ontouchstart = null;
                targetElement.ontouchmove = null;

                allTargetElements = allTargetElements.filter(function(element) {
                    return element !== targetElement;
                });

                if (documentListenerAdded && allTargetElements.length === 0) {
                    document.removeEventListener('touchmove', preventDefault, {
                        passive: false
                    });
                    documentListenerAdded = false;
                }
            } else if (firstTargetElement === targetElement) {
                restoreOverflowSetting();

                firstTargetElement = null;
            }
        };

        bodyScrollLock.isIosDevice = isIosDevice;

        return bodyScrollLock;
    })();
})(window, document);
