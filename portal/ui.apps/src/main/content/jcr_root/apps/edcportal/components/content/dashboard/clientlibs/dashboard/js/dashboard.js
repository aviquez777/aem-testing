var myDashboardJs = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            messageBox = element.querySelector('.message-box'),
            closeMessage = messageBox.querySelector('.message-close'),
            btnTradeInsights = element.querySelectorAll('.edc-primary-btn'),
            loadingScreen = element.querySelector('.tabs .loading-screen'),
            errorMessage = element.querySelector('.tabs .error-message'),
            tabContentSection = element.querySelector('#tab-content');

        // Data Layer
        function _trackEvent(isContentPiece, e) {
            var currentPiece = e.target.closest('.content-piece'),
                tag = currentPiece ?
                    (currentPiece.querySelector('.tag-title') ? currentPiece.querySelector('.tag-title') :
                        currentPiece.querySelector('.tag-text') ? currentPiece.querySelector('.tag-text') : null) : null,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: element.dataset.eventType,
                        eventAction: element.dataset.eventAction,
                        eventPage: EDC.props.pageNameURL,
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: element.dataset.eventLevel,
                        eventPageName: EDC.utils.getPageName()
                    }
                };

            if (isContentPiece) {
                obj.eventInfo.eventName = (element.dataset.eventType + ' ' + element.dataset.eventName + ' - ' + currentPiece.querySelector('.title.article').innerText).toLowerCase();
                obj.eventInfo.eventText = currentPiece.querySelector('.article').innerText.toLowerCase();
                obj.eventInfo.eventValue = tag ? tag.innerText.toLowerCase() : '';
                obj.eventInfo.destinationPage = currentPiece.href;
                obj.eventInfo.eventStage = element.querySelector('.pagination').innerText;
                obj.eventInfo.engagementType = element.dataset.eventEngagement;
            } else {
                obj.eventInfo.eventName = (element.dataset.eventType + ' ' + element.dataset.eventName + ' - ' + e.currentTarget.innerText).toLowerCase();
                obj.eventInfo.eventText = e.currentTarget.innerText.toLowerCase();
                obj.eventInfo.eventValue = element.classList.contains('no-results') ? 'no content' : 'content';
                obj.eventInfo.destinationPage = e.currentTarget.href;
            }
            EDC.utils.dataLayerTracking(obj);
        }

        function _trackTabClick(e) {
            var currentTab = e.target,
                component = currentTab.closest('.tab-set'),
                obj = {
                    eventInfo: {
                        eventComponent: component.dataset.eventComponent,
                        eventValue: component.querySelectorAll('#' + currentTab.getAttribute('aria-controls') + ' .content-piece').length > 0 ? 'content' : 'no content',
                        eventValue2: currentTab.textContent,
                        eventValue3: '',
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: currentTab.id,
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: element.dataset.eventLevel,
                        eventPageName: EDC.utils.getPageName()
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _clickTradeInsights(e) {
            _trackEvent(false, e);
        }

        function _clickContentPiece(e) {
            _trackEvent(true, e);
        }

        function _closeMessage(e) {
            e.preventDefault();
            messageBox.classList.add('hide');
        }

        function _getTitlesSize() {
            var currentPage;

            setTimeout(function () {
                currentPage = element.querySelector('.tab-panel.show .page.show');

                if (currentPage) {
                    currentPage.querySelectorAll('.title.article').forEach(function (title) {
                        var height = parseInt(window.getComputedStyle(title).getPropertyValue('height'), 10);

                        if (height > 80) {
                            title.closest('.tab-panel-content').classList.add('three-rows');
                        }
                    });
                }
            }, 200);
        }

        function _renderCards(data, dataType, container) {
            var parent = container.closest('.tab-panel'),
                discoverMore = parent.querySelector('.discover-more.top'),
                actionsSection = parent.querySelector('.actions-section');

            if (data.pageItems && data.pageItems.length > 0) {
                container.parentNode.querySelector('.actions .pagination span.total-pages').textContent = data.pageItems.length;
                discoverMore.classList.remove('no-keyline');
                data.pageItems.forEach(function (page, i) {
                    var pageDiv = d.createElement('div');

                    pageDiv.setAttribute('data-page', i + 1);
                    pageDiv.classList.add('page');
                    pageDiv.classList.add('page-' + (i + 1));

                    if (i === 0) {
                        pageDiv.classList.add('show');
                    }

                    page.dashboardItems.forEach(function (card) {
                        var wrapperA,
                            tagSectionDiv1,
                            tagTextP1,
                            dateTextP1,
                            tabPanelContentDiv,
                            imgDiv,
                            img,
                            contentDiv,
                            tagSectionDiv2,
                            tagTextDiv,
                            tagTextP2,
                            dateTextP2,
                            tagColorSpan,
                            tagTitleSpan,
                            titleArticleH3,
                            descriptionDiv,
                            descriptionP;

                        wrapperA = d.createElement('a');
                        tabPanelContentDiv = d.createElement('div');
                        imgDiv = d.createElement('div');
                        img = d.createElement('img');
                        contentDiv = d.createElement('div');
                        tagSectionDiv2 = d.createElement('div');
                        tagTextDiv = d.createElement('div');
                        titleArticleH3 = d.createElement('h3');
                        descriptionDiv = d.createElement('div');
                        descriptionP = d.createElement('p');
                        wrapperA.classList.add('content-piece');
                        wrapperA.href = card.url;
                        tabPanelContentDiv.classList.add('tab-panel-content');

                        if (dataType === 'webinar') {
                            tagSectionDiv1 = d.createElement('div');
                            tagTextP1 = d.createElement('p');
                            dateTextP1 = d.createElement('p');
                            tagTextP2 = d.createElement('p');
                            dateTextP2 = d.createElement('p');
                            tagSectionDiv1.classList.add('tag-section');
                            tagTextP1.classList.add('tag-text');
                            tagTextP1.classList.add(card.webinarStatusClass);
                            tagTextP1.textContent = card.webinarStatus;
                            dateTextP1.classList.add('date-text');
                            dateTextP1.textContent = card.webinarDateText;
                            tagSectionDiv1.appendChild(tagTextP1);
                            tagSectionDiv1.appendChild(dateTextP1);
                            wrapperA.appendChild(tagSectionDiv1);
                            tagTextP2.classList.add('tag-text');
                            tagTextP2.classList.add(card.webinarStatusClass);
                            tagTextP2.textContent = card.webinarStatus;
                            dateTextP2.classList.add('date-text');
                            dateTextP2.textContent = card.webinarDateText;
                            tagSectionDiv2.appendChild(tagTextP2);
                            tagSectionDiv2.appendChild(dateTextP2);
                        } else if (dataType === 'resource') {
                            tagColorSpan = d.createElement('span');
                            tagTitleSpan = d.createElement('span');
                            tagColorSpan.classList.add('tag-color');
                            tagColorSpan.classList.add(card.contentType);
                            tagTitleSpan.classList.add('tag-title');
                            tagTitleSpan.textContent = card.contentTypeName;
                            tagTextDiv.classList.add('tag-text');
                            tagTextDiv.appendChild(tagColorSpan);
                            tagTextDiv.appendChild(tagTitleSpan);
                            tagSectionDiv2.appendChild(tagTextDiv);
                        }
                        contentDiv.classList.add(dataType + '-content');
                        imgDiv.classList.add(dataType + '-img');
                        img.setAttribute('alt', card.imageAlt);
                        img.src = card.image;
                        imgDiv.appendChild(img);
                        tabPanelContentDiv.appendChild(imgDiv);
                        tagSectionDiv2.classList.add('tag-section');
                        contentDiv.appendChild(tagSectionDiv2);
                        titleArticleH3.classList.add('title');
                        titleArticleH3.classList.add('article');
                        titleArticleH3.textContent = card.title;
                        contentDiv.appendChild(titleArticleH3);
                        descriptionDiv.classList.add('description');
                        descriptionP.textContent = card.description;
                        descriptionDiv.appendChild(descriptionP);
                        contentDiv.appendChild(descriptionDiv);
                        tabPanelContentDiv.appendChild(contentDiv);
                        wrapperA.appendChild(tabPanelContentDiv);
                        pageDiv.appendChild(wrapperA);
                    });

                    container.appendChild(pageDiv);

                    if (data.pageItems.length > 1) {
                        actionsSection.classList.remove('hide');
                    }
                });
            } else {
                discoverMore.classList.add('no-keyline');
                actionsSection.classList.add('hide');
            }
        }

        function _toggleScreen(screen, toHide) {
            if (!toHide) {
                tabContentSection.classList.add('hide');
                screen.classList.remove('hide');
            } else {
                tabContentSection.classList.remove('hide');
                screen.classList.add('hide');
            }
        }

        function _handleTabChange(e) {
            var el = e.target,
                container = el.closest('.tabs').querySelector('#' + el.getAttribute('aria-controls') + ' .cards-container'),
                jsonUrl = container ? container.getAttribute('data-json-url') : null,
                dataType = container ? container.getAttribute('data-type') : null,
                loadOnce = container ? container.getAttribute('data-load-once') === 'true' : null,
                contentPieces;

            _getTitlesSize();
            _toggleScreen(errorMessage, true);

            if (jsonUrl && !container.getAttribute('data-json-loaded')) {
                _toggleScreen(errorMessage, true);
                _toggleScreen(loadingScreen, false);
                container.innerHTML = '';
                EDC.utils.fetchJSON('GET', jsonUrl, '', function (data) {
                    if (data.errorMessage) {
                        errorMessage.textContent = data.errorMessage;
                        _toggleScreen(loadingScreen, true);
                        _toggleScreen(errorMessage, false);
                    } else {
                        _renderCards(data, dataType, container);
                        contentPieces = container.querySelectorAll('.content-piece');
                        EDC.utils.attachEvents(contentPieces, 'click', _clickContentPiece);
                        _toggleScreen(loadingScreen, true);
                        _trackTabClick(e);
                    }
                }, function (response) {
                    _toggleScreen(loadingScreen, true);
                    _toggleScreen(errorMessage, false);

                    // if no error is because we have a timeout
                    if (response.status !== 404 && response.status !== 500) {
                        window.location.reload(true);
                    } else {
                        container.removeAttribute('data-json-loaded');
                    }
                });

                if (loadOnce) {
                    container.setAttribute('data-json-loaded', 'true');
                }
            }
        }

        function _arrowClicked(e) {
            var el = e.currentTarget,
                btnsParent = el.closest('.actions'),
                side = el.getAttribute('data-go-to'),
                panel = el.closest('.tab-panel'),
                discoverMore = panel.querySelectorAll('.discover-more'),
                pages = panel.querySelectorAll('.page'),
                visiblePage = panel.querySelector('.page.show'),
                pageNum = Number(visiblePage.getAttribute('data-page')),
                paginationSpan = btnsParent.querySelector('.pagination span.incremental-number'),
                newPageNum,
                left = btnsParent ? btnsParent.querySelector('button.arrow.arrow-left') : null,
                right = btnsParent ? btnsParent.querySelector('button.arrow.arrow-right') : null;

            newPageNum = side === 'left' ? pageNum - 1 : pageNum + 1;

            if (newPageNum > 0 && newPageNum <= pages.length) {
                visiblePage.classList.remove('show');
                panel.querySelector('.page[data-page="' + newPageNum + '"]').classList.add('show');
                paginationSpan.innerHTML = newPageNum;

                if (newPageNum === 1) {
                    discoverMore.forEach(function (elem) {
                        if (elem.classList.contains('top')) {
                            elem.classList.remove('hide');
                        } else {
                            elem.classList.add('hide');
                        }
                    });
                } else if (newPageNum === pages.length) {
                    discoverMore.forEach(function (elem) {
                        if (elem.classList.contains('bottom')) {
                            elem.classList.remove('hide');
                        } else {
                            elem.classList.add('hide');
                        }
                    });
                } else {
                    discoverMore.forEach(function (elem) {
                        elem.classList.add('hide');
                    });
                }

                if (left && left.classList.contains('disabled') && left.getAttribute('disabled') !== null) {
                    left.classList.remove('disabled');
                    left.removeAttribute('disabled');
                }

                if (right && right.classList.contains('disabled') && right.getAttribute('disabled') !== null) {
                    right.classList.remove('disabled');
                    right.removeAttribute('disabled');
                }

                if (right && side === 'right' && newPageNum === pages.length) {
                    right.classList.add('disabled');
                    right.setAttribute('disabled', 'disabled');
                } else if (left && side === 'left' && newPageNum === 1) {
                    left.classList.add('disabled');
                    left.setAttribute('disabled', 'disabled');
                }
            }

            _getTitlesSize();
        }

        function _attachEvents() {
            var arrows = element.querySelectorAll('.actions button.arrow'),
                contentPieces = element.querySelectorAll('.content-piece'),
                tabsBtns = element.querySelectorAll('.tabs nav button');

            EDC.utils.attachEvents(closeMessage, 'click', _closeMessage);
            EDC.utils.attachEvents(contentPieces, 'click', _clickContentPiece);
            EDC.utils.attachEvents(btnTradeInsights, 'click', _clickTradeInsights);
            EDC.utils.attachEvents(tabsBtns, 'click', _handleTabChange);

            if (arrows) {
                EDC.utils.attachEvents(arrows, 'click', _arrowClicked);
            }
        }

        _attachEvents();
        setTimeout(function () {
            _getTitlesSize();
        }, 500);
    }

    // Public methods
    function init() {
        var myDashboard = document.querySelectorAll('.c-my-dashboard:not([data-component-state="initialized"])');

        if (myDashboard) {
            myDashboard.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', myDashboardJs.init);