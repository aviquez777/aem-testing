var valuePropositionBannerJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var videoBtn = element.querySelectorAll('.c-interaction-button.video'),
            imageBtn = element.querySelectorAll('.c-interaction-button:not(.video.primary-outline)'),
            imageSecondaryBtns = element.querySelectorAll('.c-interaction-button.primary-outline:not(.video)'),
            anchorContainer = element.querySelector('.anchor-container'),
            anchorButton = anchorContainer ? anchorContainer.querySelector('.circle-button') : null,
            playBtn = element.querySelectorAll('.start-video'),
            videoModal = element.querySelectorAll('.video-modal'),
            videoClose = element.querySelector('.video-close button'),
            video = element.querySelectorAll('.video'),
            videoId,
            buttonText,
            carousel = element.querySelector('.carousel'),
            carouselSlides = carousel.querySelectorAll('.slide'),
            carouselActions = carousel.querySelector('.carousel-actions'),
            isCampaign = element.classList.contains('campaign'),
            hasColorBlock = element.classList.contains('color-block');

        // Data Layer
        function _trackEvent(data) {
            var obj = {
                eventInfo: {
                    eventComponent: data.comp,
                    eventType: data.type,
                    eventName: data.name,
                    eventText: data.text,
                    eventAction: element.dataset.eventAction,
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName()
                }
            };

            if (data.engagement) {
                obj.eventInfo.engagementType = data.engagement;
            } else {
                obj.eventInfo.engagementType = '1';
            }

            if (data.level) {
                obj.eventInfo.eventLevel = data.level;
            } else {
                obj.eventInfo.eventLevel = 'primary';
            }

            if (data.destinationPage) {
                obj.eventInfo.destinationPage = data.destinationPage;
            } else {
                obj.eventInfo.destinationPage = '#';
            }

            EDC.utils.dataLayerTracking(obj);
        }

        // Subscribing to the video percentage publication for Data Layer
        PubSub.subscribe('video-percentage', function (msg, data) {
            if (data.id === videoId) {
                _trackEvent({
                    comp: 'video',
                    type: 'video play',
                    name: 'video play - ' + data.percentage + '%',
                    text: data.id
                });
            }
        });

        // If video player is active share bar has to be hidden
        function _shareBarHideShow(action) {
            var sharebars = document.querySelectorAll('.addthis-smartlayers');

            sharebars.forEach(function (share) {
                if (action === 'hide') {
                    share.classList.add('video-active');
                } else if (action === 'show') {
                    share.classList.remove('video-active');
                }
            });
        }

        function _closeVideo(e) {
            var index = parseInt(carousel.querySelector('.slide.active').getAttribute('data-slide-position'), 10);

            e.preventDefault();
            PubSub.publish('stop-video', true);

            if (videoModal[index]) {
                if (videoModal[index].classList.contains('show')) {
                    videoModal[index].classList.remove('show');
                }
            }

            if (video[index]) {
                if (video[index].classList.contains('show')) {
                    video[index].classList.remove('show');
                }
            }

            _shareBarHideShow('show');
        }

        function _playVideo(e) {
            var index = parseInt(carousel.querySelector('.slide.active').getAttribute('data-slide-position'), 10);

            e.preventDefault();

            if (videoModal[index]) {
                if (!videoModal[index].classList.contains('touched')) {
                    videoModal[index].classList.add('touched');
                    playBtn[index].click();
                } else {
                    setTimeout(function () {
                        playBtn[index].click();
                    }, 300);
                }

                if (!videoModal[index].classList.contains('show')) {
                    videoModal[index].classList.add('show');
                }

                _shareBarHideShow('hide');
            }

            _trackEvent({
                comp: 'video',
                type: 'button',
                name: 'button click - ' + videoId,
                text: buttonText,
                engagement: element.dataset.eventEngagement,
                level: element.dataset.eventLevel
            });
        }

        function _imageBtnClicked(e) {
            var anchorTarget,
                buttonAnchor = e.currentTarget ? e.currentTarget.getAttribute('data-target') : null;

            _trackEvent({
                comp: 'hero banner',
                type: 'button',
                name: 'button click - ' + buttonText,
                text: buttonText,
                destinationPage: '#',
                engagement: element.dataset.eventEngagement,
                level: element.dataset.eventLevel
            });

            if (buttonAnchor) {
                anchorTarget = document.querySelector('a.anchor[data-target="' + buttonAnchor + '"]');
                if (anchorTarget) {
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(anchorTarget).y, 1000);
                }
            }
        }

        function _anchorButtonClicked(e) {
            var anchorTarget,
                buttonAnchor = e.currentTarget.getAttribute('data-target');

            if (buttonAnchor) {
                anchorTarget = document.querySelector('a.anchor[data-target="' + buttonAnchor + '"]');

                if (anchorTarget) {
                    _trackEvent({
                        comp: 'hero banner',
                        type: 'button',
                        name: 'button click - ' + buttonText,
                        text: 'go-to-content',
                        destinationPage: '#',
                        engagement: element.dataset.eventEngagement,
                        level: element.dataset.eventLevel
                    });
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(anchorTarget).y, 1000);
                }
            }
        }

        // Set buttons based on component configuration
        function _setElementData() {
            var slide = carousel.querySelector('.slide.active'),
                index = slide ? parseInt(slide.getAttribute('data-slide-position'), 10) : 0,
                vid = video[index],
                vidBtn = videoBtn[index],
                imgBtn = imageBtn[index];

            if (vid) {
                videoId = vid.getAttribute('data-video-id') ? vid.getAttribute('data-video-id') : '';
            }

            if (vidBtn) {
                buttonText = vidBtn.textContent;
            }

            if (!imgBtn) {
                imgBtn = element.querySelector('.c-interaction-button.primary-outline:not(.video)');
            }

            if (imgBtn) {
                buttonText = imgBtn.getAttribute('aria-label');
            }
        }

        function _detectEsc(e) {
            if (e.keyCode === EDC.props.escapeKeyCode) {
                _closeVideo(e);
            }
        }

        function _activateCarousel() {
            var slidesLength = carouselSlides.length;

            if (!hasColorBlock) {
                carouselActions.classList.remove('keep-hidden');
            }

            EDC.utils.initCarousel(carousel, 1, 1);

            if (!hasColorBlock) {
                if (isCampaign && slidesLength >= 3 && slidesLength <= 5) {
                    carousel.classList.add('active-carousel');
                    carouselActions.classList.remove('hide');

                    if (anchorContainer) {
                        anchorContainer.classList.add('hide');
                    }
                } else if (slidesLength >= 2 && slidesLength <= 5) {
                    carousel.classList.add('active-carousel');
                    carouselActions.classList.remove('hide');
                    carouselActions.classList.add('show');

                    if (anchorContainer) {
                        anchorContainer.classList.add('hide');
                    }
                } else {
                    carousel.classList.remove('active-carousel');
                    carousel.querySelector('.carousel-ballons').classList.add('hide');
                }
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(videoBtn, 'click', _playVideo);
            EDC.utils.attachEvents(imageBtn, 'click', _imageBtnClicked);
            EDC.utils.attachEvents(imageSecondaryBtns, 'click', _imageBtnClicked);
            EDC.utils.attachEvents(anchorButton, 'click', _anchorButtonClicked);
            EDC.utils.attachEvents(videoClose, 'click', _closeVideo);
            EDC.utils.attachEvents(document, 'keyup', _detectEsc);
            EDC.utils.attachEvents(carousel.querySelectorAll('.carousel-actions .carousel-button, .carousel-ballons .ballon-for-slide'), 'click', function (e) {
                _setElementData();
                _trackEvent({
                    comp: 'Home Page Hero Banner',
                    type: 'Hero Banner',
                    name: 'Hero Banner Clicks',
                    text: e.target.closest('.carousel-actions') ? 'Carousal Arrow' : 'Pagination ' + e.target.getAttribute('data-slide-position') + 1,
                    destinationPage: EDC.props.pageNameURL,
                    engagement: '1',
                    level: '1'
                });
            });
        }

        _activateCarousel();
        _setElementData();
        _attachEvents();
    }

    // Public methods
    function init() {
        var valuePositionBanner = document.querySelectorAll('.c-value-proposition-banner:not([data-component-state="initialized"])');

        if (valuePositionBanner) {
            valuePositionBanner.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', valuePropositionBannerJS.init);