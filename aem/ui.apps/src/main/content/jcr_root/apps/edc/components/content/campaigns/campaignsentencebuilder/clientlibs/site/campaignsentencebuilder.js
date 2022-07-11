var sentenceBuilderCampaignJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var isDevice = EDC.utils.detectDevice(),
            selectContainer = element.querySelector('.select'),
            dropdownContainer = element.querySelector('.dropdown-container'),
            dropdown = element.querySelector('select'),
            label = selectContainer.querySelector('label'),
            option = label.querySelector('.option'),
            cardGrid = element.querySelector('.card-grid'),
            sentence = element.querySelector('.sentence span').textContent,
            viewMoreElem = element.querySelector('.view-more'),
            timerId = 0;

        // Private methods

        function _trackDropdownEvent(selection) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: element.dataset.eventType + ' - rank ' + selection,
                    eventAction: element.dataset.eventAction,
                    eventValue: sentence + ': ' + dropdown.options[selection].value,
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    engagementType: element.dataset.eventEngagement
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _trackCardEvent(e) {
            var cardTitle = e.target.textContent ? e.target.textContent : e.target.getAttribute('alt'),
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: cardGrid.dataset.eventType,
                        eventName: cardGrid.dataset.eventName,
                        eventAction: element.dataset.eventAction,
                        eventText: cardTitle.trim(),
                        eventValue2: sentence + ': ' + dropdown.options[dropdown.selectedIndex].value,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: element.dataset.destinationPage,
                        engagementType: cardGrid.dataset.eventEngagement,
                        eventLevel: cardGrid.dataset.eventLevel
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _trackViewMore(e) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: viewMoreElem.dataset.eventType,
                    eventName: viewMoreElem.dataset.eventName,
                    eventAction: element.dataset.eventAction,
                    eventText: e.target.text,
                    eventValue2: sentence + ': ' + dropdown.options[dropdown.selectedIndex].value,
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    destinationPage: e.target.href,
                    engagementType: viewMoreElem.dataset.eventEngagement,
                    eventLevel: viewMoreElem.dataset.eventLevel
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _setLabelText() {
            var selectedOption = dropdown.options[dropdown.options.selectedIndex];

            option.textContent = selectedOption.text;
        }

        function _setActiveCard(e) {
            e.target.classList.toggle('active');
        }

        function _validateData(data) {
            return data ? data : '';
        }

        function _loadSentenceBuilderData() {
            if (EDC.props.sentenceBuilderCampaignData && cardGrid) {
                EDC.props.sentenceBuilderCampaignData.forEach(function (data) {
                    var card = document.createElement('div'),
                        cardData;

                    // Creates the HTML code with variables from sentenceBuilderCampaignData
                    cardData = '<div class="card-image">';
                    cardData += '<a class="' + _validateData(data.imagealignment) + '" href="' + _validateData(data.url) + '" target="' + _validateData(data.target) + '" aria-label="Link">';
                    cardData += '<img src="' + _validateData(data.teaserimage) + '" alt="' + _validateData(data.imagealttext) + '" /></a></div>';
                    cardData += '<div class="card-content"><div class="card-info">';
                    cardData += '<span class="category-tag ' + _validateData(data.contenttypeclassname) + ' stacked">' + _validateData(data.contenttypetitletext) + '</span>';
                    cardData += '<span class="date">' + _validateData(data.displaydate) + '</span></div>';
                    cardData += '<h4 class="title"><a href="' + _validateData(data.url) + '" target="' + _validateData(data.target) + '">' + _validateData(data.pageTitle) + '</a></h4>';
                    cardData += '<p class="description">' + _validateData(data.articlesynopsis) + '</p></div>';

                    card.classList.add('card');
                    card.classList.add('hide');

                    card.setAttribute('data-card-category', data.tags[0]);
                    card.innerHTML = cardData;
                    cardGrid.appendChild(card);

                    EDC.utils.attachEvents(card, ['mouseenter', 'mouseleave'], _setActiveCard);
                });
            }
        }

        function _showHideSelect() {
            if (selectContainer.classList.contains('open')) {
                selectContainer.classList.remove('open');
            } else {
                selectContainer.classList.add('open');
            }
        }

        function _showDropdown() {
            var menu,
                dropdownElement;

            if ($(dropdown).dropdown('is visible')) {
                clearInterval(timerId);
                timerId = 0;

                menu = element.querySelector('.menu');
                dropdownElement = element.querySelector('.ui.selection.dropdown');
                dropdownContainer.style.height = dropdownElement.offsetHeight + menu.offsetHeight + 'px';
            }
        }

        function _showSentenceBuilderOptions(e) {
            var options = dropdown.querySelectorAll('option'),
                chevron = label.querySelector('.chevron'),
                left,
                top;

            e.preventDefault();

            _showHideSelect();

            // Logic Based on Design specs
            if (!isDevice) {
                left = chevron.offsetLeft - dropdownContainer.offsetWidth + 65;
                top = chevron.offsetTop - 11;
                dropdownContainer.setAttribute('style', 'left: ' + left + 'px; top: ' + top + 'px;');
                $(dropdown).dropdown('show');
            } else {
                dropdown.size = options.length;
                dropdown.focus();
            }
        }

        function _displaySentenceBuilderCards() {
            var cards = element.querySelectorAll('.card'),
                selection = this.selectedIndex;

            _setLabelText();

            cards.forEach(function (card) {
                if (option.textContent === card.getAttribute('data-card-category')) {
                    card.classList.remove('hide');
                } else {
                    card.classList.add('hide');
                }
            });

            _trackDropdownEvent(selection);
        }

        function _initializeLibraries() {
            if (!isDevice) {
                $(dropdown).dropdown();

                // Helper functions to Change behavior of the dropdown
                $(dropdown).dropdown('setting', 'onShow', function () {
                    timerId = setInterval(_showDropdown, 100);
                });

                $(dropdown).dropdown('setting', 'onHide', function () {
                    _showHideSelect();
                });
            }
        }

        // Attach events
        function _attachEvents() {
            var defaultCards = element.querySelectorAll('.card'),
                cardLinks = element.querySelectorAll('.card a');

            if(viewMoreElem != null){
              var viewMoreCta = viewMoreElem.querySelector('a');

              EDC.utils.attachEvents(viewMoreCta, 'click', _trackViewMore);
            }

            EDC.utils.attachEvents(cardLinks, 'click', _trackCardEvent);
            EDC.utils.attachEvents(label, 'click', _showSentenceBuilderOptions);
            EDC.utils.attachEvents(dropdown, 'click', _showHideSelect);
            EDC.utils.attachEvents(dropdown, 'change', _displaySentenceBuilderCards);
            EDC.utils.attachEvents(defaultCards, ['mouseenter', 'mouseleave'], _setActiveCard);
            EDC.utils.attachEvents(dropdown, 'blur', function () {
                if (selectContainer.classList.contains('open')) {
                    _showHideSelect();
                }
            });
        }

        _loadSentenceBuilderData();
        _initializeLibraries();
        _attachEvents();
    }

    // Public methods
    function init() {
        var sentenceBuilderCampaign = document.querySelectorAll('.c-sentence-builder-campaign:not([data-component-state="initialized"])');

        if (sentenceBuilderCampaign) {
            sentenceBuilderCampaign.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', sentenceBuilderCampaignJS.init);