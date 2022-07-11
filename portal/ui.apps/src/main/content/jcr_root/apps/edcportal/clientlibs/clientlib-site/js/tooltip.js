var toolTipJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var toolTipTriggerer = element.querySelector('.tool-tip-triggerer'),
            toolTipText = element.querySelector('.tool-tip-text'),
            forceOpen = false,
            allToolTips = document.querySelectorAll('.c-tool-tip'),
            preOpenedByHover = false,
            fixedByClick = false;

        // Private methods
        function _closeToolTip(e) {
            var type = e.type;

            if (type === 'click' || type === 'blur' || (type === 'mouseout' && !forceOpen)) {
                element.classList.add('hide-tool-text');
                fixedByClick = false;
            }

            preOpenedByHover = false;
        }

        function _checkToolTipTextPosition() {
            var placement = toolTipText.getBoundingClientRect(),
                winWidth = window.innerWidth || document.documentElement.clientWidth;

            if (window.EDC.utils.getDeviceViewPort() !== 'mobile') {
                if (placement.left < 0) {
                    toolTipText.style.left = (-placement.left + 45) + 'px';
                } else if (placement.right > winWidth) {
                    toolTipText.style.left = (winWidth - placement.right - 30) + 'px';
                }
            } else {
                toolTipText.style.left = 0;
            }
        }

        function _showToolTip(e) {
            var type = e.type;

            e.preventDefault();
            _checkToolTipTextPosition();

            if (!(type === 'mouseover' && fixedByClick)) {
                forceOpen = type === 'click' || type === 'focus';

                allToolTips.forEach(function (toolTip) {
                    if (toolTip.querySelector('button') !== e.target) {
                        toolTip.classList.add('hide-tool-text');
                    }
                });

                if (!preOpenedByHover && type === 'click' && !element.classList.contains('hide-tool-text')) {
                    _closeToolTip(e);
                } else {
                    element.classList.remove('hide-tool-text');
                    fixedByClick = type === 'click';
                }

                if (type !== 'focus') {
                    preOpenedByHover = type === 'mouseover';
                }
            }
        }

        function _prepareLabel() {
            var parentLabel = element.closest('label');

            if (parentLabel) {
                parentLabel.style.position = 'relative';
            }
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(toolTipTriggerer, 'click', _showToolTip);
            EDC.utils.attachEvents(toolTipTriggerer, 'focus', _showToolTip);
            EDC.utils.attachEvents(toolTipTriggerer, 'mouseover', _showToolTip);
            EDC.utils.attachEvents(toolTipTriggerer, 'mouseout', _closeToolTip);
            EDC.utils.attachEvents(toolTipTriggerer, 'blur', _closeToolTip);
            EDC.utils.attachEvents(toolTipText, 'click', function (e) {
                e.preventDefault();
            });
        }

        _prepareLabel();
        _attachEvents();
    }

    // Public methods
    function init() {
        var toolTips = document.querySelectorAll('.c-tool-tip:not([data-component-state="initialized"])');

        if (toolTips) {
            toolTips.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', toolTipJS.init);