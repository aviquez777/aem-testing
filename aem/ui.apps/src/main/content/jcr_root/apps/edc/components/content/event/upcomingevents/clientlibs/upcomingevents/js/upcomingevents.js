var upcomingWebinarsJs = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            loadingScreen = element.querySelector('.loading-screen'),
            errorMessage = element.querySelector('.error-message'),
            cardsContainer = element.querySelector('.cards-container');

        function _renderCards(data) {
            var pageDiv = d.createElement('div');
            if (data && data.pageItems.length > 0) {
                pageDiv.classList.add('cards-wrapper');
                data.pageItems.forEach(function (card) {
                var wrapperA,
                    eventContentDiv,
                    imgDiv,
                    img,
                    contentDiv,
                    tagSectionDiv2,
                    tagTextP2,
                    dateTextP2,
                    titleCard,
                    descriptionDiv,
                    descriptionP;

                wrapperA = d.createElement('div');
                eventContentDiv = d.createElement('div');
                imgDiv = d.createElement('div');
                img = d.createElement('img');
                contentDiv = d.createElement('div');
                tagSectionDiv2 = d.createElement('div');
                titleCard = d.createElement('a');
                titleCard.href = card.linkUrl;
                titleCard.target = card.linkTarget;
                descriptionDiv = d.createElement('div');
                descriptionP = d.createElement('p');
                wrapperA.classList.add('content-piece');
                eventContentDiv.classList.add('anchor-content');

                tagTextP2 = d.createElement('span');
                dateTextP2 = d.createElement('span');
                tagTextP2.classList.add('tag-text');
                tagTextP2.textContent = card.eventType;
                dateTextP2.classList.add('date-text');
                dateTextP2.textContent = card.webinarDateText;
                tagSectionDiv2.appendChild(tagTextP2);
                tagSectionDiv2.appendChild(dateTextP2);

                contentDiv.classList.add('event-content');
                imgDiv.classList.add('event-img');
                img.setAttribute('alt', card.imageAlt);
                img.src = card.image;
                titleCard.textContent = card.linkText;
                descriptionP.textContent = card.description;
                tagSectionDiv2.classList.add('tag-section');
                titleCard.classList.add('title');
                descriptionDiv.classList.add('description');
                contentDiv.appendChild(tagSectionDiv2);
                contentDiv.appendChild(titleCard);
                contentDiv.appendChild(descriptionDiv);
                descriptionDiv.appendChild(descriptionP);
                imgDiv.appendChild(img);
                wrapperA.appendChild(imgDiv);
                wrapperA.appendChild(contentDiv);
                eventContentDiv.appendChild(wrapperA);
                pageDiv.appendChild(eventContentDiv);
                    });
                    cardsContainer.appendChild(pageDiv);
            }
        }

        function _toggleScreen(screen, toHide) {
            if (!toHide) {
                cardsContainer.classList.add('hide');
                screen.classList.remove('hide');
            } else {
                cardsContainer.classList.remove('hide');
                screen.classList.add('hide');
            }
        }

        function _loadDataJson() {
            var jsonUrl = cardsContainer.getAttribute('data-json-url');

            _toggleScreen(errorMessage, true);

            if (jsonUrl) {
                console.log("Json url is: ", jsonUrl);
                
                _toggleScreen(errorMessage, true);
                _toggleScreen(loadingScreen, false);
                cardsContainer.innerHTML = '';

                EDC.utils.fetchJSON('GET', jsonUrl, '', function (data) {
                    if (data.errorMessage != "") {
                        _toggleScreen(loadingScreen, true);
                        _toggleScreen(errorMessage, false);
                    } else {
                        _renderCards(data);
                        _toggleScreen(loadingScreen, true);
                    }
                }, function () {
                    _toggleScreen(loadingScreen, true);
                    _toggleScreen(errorMessage, false);
                });
            }
        }

        _loadDataJson();
    }


    // Public methods
    function init() {
        var upcomingWebinars = document.querySelectorAll('.c-upcoming-webinars:not([data-component-state="initialized"])');

        if (upcomingWebinars) {
            upcomingWebinars.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', upcomingWebinarsJs.init);