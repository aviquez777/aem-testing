/*
* forEach
* This polyfill adds compatibility to all Browsers supporting.
*/
if (window.NodeList && !NodeList.prototype.forEach) {
    NodeList.prototype.forEach = function (callback, thisArg) {
        thisArg = thisArg || window;
        for (var i = 0; i < this.length; i++) {
            callback.call(thisArg, this[i], i, this);
        }
    };
}

if (window.HTMLCollection && !HTMLCollection.prototype.forEach) {
    HTMLCollection.prototype.forEach = function (callback, thisArg) {
        thisArg = thisArg || window;
        for (var i = 0; i < this.length; i++) {
            callback.call(thisArg, this[i], i, this);
        }
    };
}

if (!Array.prototype.forEach) {
    Array.prototype.forEach = function forEach(callback, thisArg) {
        'use strict';
         var T, k;

        if (this == null) {
            throw new TypeError("this is null or not defined");
        }

        var kValue,
            O = Object(this),
            len = O.length >>> 0;

        if ({}.toString.call(callback) !== "[object Function]") {
            throw new TypeError(callback + " is not a function");
        }

        if (arguments.length >= 2) {
            T = thisArg;
        }

        k = 0;

        while (k < len) {
            if (k in O) {
                kValue = O[k];
                callback.call(T, kValue, k, O);
            }
            k++;
        }
    };
}

/*
* matches
* This polyfill adds compatibility to all Browsers supporting.
*/
if (!Element.prototype.matches) {
    Element.prototype.matches = Element.prototype.msMatchesSelector || Element.prototype.webkitMatchesSelector;
}

/*
* closest
* This polyfill adds compatibility to all Browsers supporting.
*/
if (!Element.prototype.closest) {
    Element.prototype.closest = function (s) {
        var el = this;
        if (!document.documentElement.contains(el)) {
            return null;
        }
        do {
            if (el.matches(s)) {
                return el;
            }
            el = el.parentElement || el.parentNode;
        } while (el !== null && el.nodeType === 1);
        return null;
    };
}

/*
* Find
* This polyfill adds compatibility to all Browsers supporting.
*/
// https://tc39.github.io/ecma262/#sec-array.prototype.find
if (!Array.prototype.find) {
    Object.defineProperty(Array.prototype, 'find', {
      value: function(predicate) {
       // 1. Let O be ? ToObject(this value).
        if (this == null) {
          throw new TypeError('"this" is null or not defined');
        }

        var o = Object(this);

        // 2. Let len be ? ToLength(? Get(O, "length")).
        var len = o.length >>> 0;

        // 3. If IsCallable(predicate) is false, throw a TypeError exception.
        if (typeof predicate !== 'function') {
          throw new TypeError('predicate must be a function');
        }

        // 4. If thisArg was supplied, let T be thisArg; else let T be undefined.
        var thisArg = arguments[1];

        // 5. Let k be 0.
        var k = 0;

        // 6. Repeat, while k < len
        while (k < len) {
          // a. Let Pk be ! ToString(k).
          // b. Let kValue be ? Get(O, Pk).
          // c. Let testResult be ToBoolean(? Call(predicate, T, « kValue, k, O »)).
          // d. If testResult is true, return kValue.
          var kValue = o[k];
          if (predicate.call(thisArg, kValue, k, o)) {
            return kValue;
          }
          // e. Increase k by 1.
          k++;
        }

        // 7. Return undefined.
        return undefined;
      },
      configurable: true,
      writable: true
    });
}

// Vanilla version of jQuery extend.
// Merges multiple objects into the first in the lineup
// Pass true as first argument to perform a "deep" merge
if (!Object.prototype.extend) {
    Object.defineProperty(Object.prototype, 'extend', {
        value: function () {
            // Variables
            var extended = this;
            var deep = false;
            var i = 0;
            var length = arguments.length;

            // Check if a deep merge
            if ( Object.prototype.toString.call( arguments[0] ) === '[object Boolean]' ) {
                deep = arguments[0];
                i++;
            }

            // Merge the object into the extended object
            var merge = function (obj) {
                for ( var prop in obj ) {
                    if ( Object.prototype.hasOwnProperty.call( obj, prop ) ) {
                        // If deep merge and property is an object, merge properties
                        if ( deep && Object.prototype.toString.call(obj[prop]) === '[object Object]' ) {
                            extended[prop] = extend( true, extended[prop], obj[prop] );
                        } else {
                            extended[prop] = obj[prop];
                        }
                    }
                }
            };

            // Loop through each object and conduct a merge
            for ( ; i < length; i++ ) {
                var obj = arguments[i];
                merge(obj);
            }

            return extended;
        },
        configurable: true,
        enumerable: false,
        writable: true
    });
}

/*
* CustomEvent
* This polyfill adds compatibility to all Browsers supporting.
*/
//https://developer.mozilla.org/es/docs/Web/API/CustomEvent/CustomEvent
if (typeof window.CustomEvent != 'function') {
    function CustomEvent ( event, params ) {
        params = params || { bubbles: false, cancelable: false, detail: null };
        var evt = document.createEvent( 'CustomEvent' );
        evt.initCustomEvent( event, params.bubbles, params.cancelable, params.detail );
        return evt;
    }
    CustomEvent.prototype = window.Event.prototype;
    window.CustomEvent = CustomEvent;
}

/*
 * MouseEvent
 * This polyfill adds compatibility to all Browsers supporting.
 */
//https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent/MouseEvent#Polyfill
(function (window) {
    try {
        new MouseEvent('test');
        return false; // No need to polyfill
    } catch (e) {
        // Need to polyfill - fall through
    }

    // Polyfills DOM4 MouseEvent
    var MouseEventPolyfill = function (eventType, params) {
        params = params || {bubbles: false, cancelable: false};
        var mouseEvent = document.createEvent('MouseEvent');
            mouseEvent.initMouseEvent(eventType,
                    params.bubbles,
                    params.cancelable,
                    window,
                    0,
                    params.screenX || 0,
                    params.screenY || 0,
                    params.clientX || 0,
                    params.clientY || 0,
                    params.ctrlKey || false,
                    params.altKey || false,
                    params.shiftKey || false,
                    params.metaKey || false,
                    params.button || 0,
                    params.relatedTarget || null
            );

        return mouseEvent;
    };

    MouseEventPolyfill.prototype = Event.prototype;

    window.MouseEvent = MouseEventPolyfill;
})(window);


/**
* @license MIT, GPL, do whatever you want
* @requires polyfill: Array.prototype.slice fix {@link https://gist.github.com/brettz9/6093105}
*/
if (!Array.from) {
    Array.from = function (object) {
        'use strict';
        return [].slice.call(object);
    };
}