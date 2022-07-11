var featureGridLoadMore = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var viewMore = element.querySelector('.view-more'),
            startQty = viewMore.dataset.itemstoshow ? parseInt(viewMore.dataset.itemstoshow, 10) : 4,
            grid = element.querySelector('.content-videos-grid'),
            iconLinks = grid.querySelectorAll('.content-video .video-img'),
            anchorLinks = grid.querySelectorAll('.title a');

        function _trackEvents(e) {
            var clickedAnchor = e ? e.currentTarget : '',
                clickedCard = clickedAnchor.closest('.content-video'),
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: element.dataset.eventType,
                        eventName: clickedAnchor.dataset.eventName,
                        eventAction: clickedCard.dataset.eventAction,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: clickedAnchor.getAttribute('href').toLowerCase(),
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: element.dataset.eventLevel
                    }
                };

            if (clickedAnchor.className.indexOf('video-img') === -1) {
                obj.eventInfo.eventText = clickedAnchor.innerHTML;
            }

            EDC.utils.dataLayerTracking(obj);
        }

        function _loadMore(e) {
            var counter = 1,
                currentCount = startQty,
                videos,
                buttonShown = false,
                buttonHidden = false;

            if (e && e.preventDefault) {
                e.preventDefault();
            }

            // get the videos
            videos = element.querySelectorAll('.content-video.hide');

            if (videos) {
                // loop over the videos and show them as necessary
                videos.forEach(function (video) {
                    if (counter <= currentCount) {
                        video.classList.remove('hide');
                    }

                    // check if button needs to be shown
                    if (counter > currentCount && !buttonShown) {
                        viewMore.classList.remove('hide');
                        buttonShown = true;
                    }

                    // if we loaded all but next loop we have all records, hide button
                    if (currentCount >= videos.length && viewMore !== undefined && !buttonHidden) {
                        viewMore.classList.add('hide');
                        buttonHidden = true;
                    }

                    counter++;
                });
            }

            // Call lazy load for images
            EDC.utils.lazyLoad.loadImagesByTarget(element, currentCount);
        }

        function _attachEvents() {
            var loadMoreBtn = viewMore.querySelector('a');

            EDC.utils.attachEvents(loadMoreBtn, 'click', _loadMore);
            EDC.utils.attachEvents(iconLinks, 'click', _trackEvents);
            EDC.utils.attachEvents(anchorLinks, 'click', _trackEvents);
        }

        _attachEvents();
        _loadMore();
    }

    // Public methods
    function init() {
        var FeatureGrid = document.querySelectorAll('.c-feature-grid:not([data-component-state="initialized"])');

        if (FeatureGrid) {
            FeatureGrid.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', featureGridLoadMore.init);