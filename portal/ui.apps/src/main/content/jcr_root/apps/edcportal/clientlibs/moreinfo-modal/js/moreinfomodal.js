var moreInformationModalJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var body = document.querySelector('body'),
            d = document,
            modalTriggerer = element.querySelector('.modal-triggerer'),
            modalDialog = element.querySelector('.modal-section'),
            closeBtn = modalDialog.querySelector('.modal-close'),
            device = window.EDC.utils.getDeviceViewPort(),
            sibling = element.previousElementSibling,
            isInsideQuestionnaire = sibling ? sibling.closest('.c-questionnaire') : null,
            modalTitle = element.querySelector('.modal-title h2').innerHTML,
            modalTexts = element.querySelectorAll('.modal-text p'),
            modalText;

        // Private methods
        function _positionModal() {
            var viewportHeight,
                modalContainerHeight,
                modalOffset = 0,
                heightDiff,
                modalContainer,
                shareBarBottom,
                hasBottomShare,
                shareHeight,
                modalTextContainer;

            if (modalDialog) {
                modalContainer = element.querySelector('.modal-container');
                modalTextContainer = modalContainer.querySelector('.modal-text');
                shareBarBottom = document.querySelector('.at-share-dock');
                hasBottomShare = device === 'mobile' && shareBarBottom;
                shareHeight = hasBottomShare ? parseInt(window.getComputedStyle(shareBarBottom).getPropertyValue('height'), 10) : 0;
                viewportHeight = window.innerHeight;
                modalTextContainer.removeAttribute('style');
                modalContainerHeight = modalContainer.clientHeight;

                if (viewportHeight > modalContainerHeight) {
                    modalOffset = (viewportHeight - modalContainerHeight) / 2;
                } else if (viewportHeight < modalContainerHeight) {
                    heightDiff = modalContainerHeight + 65 - viewportHeight;
                    modalTextContainer.style.maxHeight = modalTextContainer.clientHeight - heightDiff - shareHeight + 'px';
                    modalTextContainer.style.overflow = 'auto';
                }
                if (device === 'mobile') {
                    modalDialog.style.top = '0px';
                } else {
                    modalDialog.style.top = modalOffset + 'px';
                }
            }
        }

        function _hide(el) {
            el.classList.remove('show');
            el.classList.add('hide');
        }

        function _resetBody() {
            EDC.utils.unAttachEvents(window, 'resize', _positionModal);
            body.classList.remove('modal-is-open');
            bodyScrollLock.enableBodyScroll(modalDialog);
        }

        function _detectEsc(e) {
            if (e.keyCode === EDC.props.escapeKeyCode && modalDialog) {
                _hide(modalDialog);
                EDC.utils.unAttachEvents(d, 'keyup', _detectEsc);
                _resetBody();
            }
        }

        function _closeModal(e) {
            e.preventDefault();
            _hide(modalDialog);
            _resetBody();
        }

        function _showModal(e) {
            e.preventDefault();

            modalDialog.classList.remove('hide');
            modalDialog.classList.add('show');
            bodyScrollLock.disableBodyScroll(modalDialog);
            EDC.utils.attachEvents(window, 'resize', _positionModal);
            EDC.utils.attachEvents(d, 'keyup', _detectEsc);
            body.classList.add('modal-is-open');
            _positionModal();
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(modalTriggerer, 'click', _showModal);
            EDC.utils.attachEvents(closeBtn, 'click', _closeModal);
        }

        _attachEvents();

        if (sibling && !isInsideQuestionnaire) {
            sibling.classList.add('has-more-info-btn');
            if (sibling.tagName.toLowerCase() !== 'label') {
                element.classList.add('on-title');
            }
        }

        modalTexts.forEach(function (text) {
            if (text.innerHTML !== '') {
                modalText = true;
            }
        });

        modalTitle !== '' && modalText ? element.classList.remove('hide') : element.classList.add('hide');
    }

    // Public methods
    function init() {
        var moreInformationModals = document.querySelectorAll('.c-more-information-modal:not([data-component-state="initialized"])');

        if (moreInformationModals) {
            moreInformationModals.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', moreInformationModalJS.init);