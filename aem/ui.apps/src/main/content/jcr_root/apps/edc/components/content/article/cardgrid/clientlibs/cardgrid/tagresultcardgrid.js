var loadArticles = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var contentCardGrid = element.querySelector('.content-card-grid'),
            cards = contentCardGrid.querySelectorAll('.content-card'),
            nextCardsInitialCount = cards.length,
            loadMoreBtn = element.querySelector('.view-more-cta'),
            sentenceBuilder = document.querySelector('.sentence-builder'),
            serviceEndPoint = getArticlesByTagIdEndPoint(),
            pageSize = Number(contentCardGrid.dataset.maxItems) || 10,
            articlesObj = {},
            currentPage = 1,
            infiniteScroll = contentCardGrid.dataset.infiniteScroll || 'true',
            lastKnownScrollPosition = 0,
            ticking = false;

        // Private methods
        function _fetchDataFromServer(fnCallback) {
            var request = new XMLHttpRequest(),
                data;

            request.open('GET', serviceEndPoint, true);
            request.onload = function () {
                if (request.status >= 200 && request.status < 400) {
                    data = JSON.parse(request.responseText);
                    fnCallback({ success: true, data: data });
                } else {
                    fnCallback({ success: false, error: 'server returned an error' });
                }
            };

            request.onerror = function () {
                fnCallback({ success: false, error: 'connection error' });
            };

            request.send();
        }

        function _loadArticles() {
            _fetchDataFromServer(function (result) {
                if (result.success) {
                    // pageSize = result.data.pagesize;
                    articlesObj = result.data.articles;
                    self.numPagesToLoad = (articlesObj.length > 0) ? Math.floor(articlesObj.length / pageSize) + 1 : 0;
                }
            });
        }

        function _getResultsByPage(pageNumber, fnCallback) {
            var start = (pageNumber - 1) * pageSize,
                stop = ((start + pageSize) < articlesObj.length ? start + pageSize : articlesObj.length),
                articles = [],
                i;

            for (i = start; i < stop; i++) {
                articles.push(articlesObj[i]);
            }
            fnCallback(articles);
        }

        function _cardClicked(e) {
            EDC.utils.cardGridTrackEvent('card-clicked', contentCardGrid, '', e);
        }

        function _renderContentCards(articles) {
            var i,
                contentCard,
                article;

            function composeContentCard() {
                var imagesrc = '',
                    dateText = '',
                    card = document.createElement('a');

                card.setAttribute('href', article.url);
                card.setAttribute('data-event-type', 'link');
                card.setAttribute('data-event-name', 'link click - card grid');
                card.setAttribute('data-event-engagement', '1');
                card.setAttribute('data-event-action', nextCardsInitialCount);
                card.setAttribute('data-event-destination-page', article.url);

                card.classList.add('content-card');
                card.classList.add(article.contenttypeclassname);

                if (article.ispremium && element.getAttribute('data-small-cards') === null) {
                    card.classList.add('premium');
                    imagesrc = article.tabletFileReference;
                } else {
                    imagesrc = article.teaserimage;
                }

                if (article.displaydate && article.contenttypeclassname !== 'success-story') {
                    dateText = '<span class="date">' + article.displaydate + '</span> ';
                }

                card.innerHTML = '<div class="card-img ' + article.imagealignment + '">' +
                    '<img src="' + imagesrc + '" alt="' + article.imagealttext + '"></div>' +
                    '<div class="card-content">' +
                    '<h4 class="title">' + article.pageTitle + '</h4>' +
                    '<p class="synopsis">' +
                    '<span class="category-tag ' + article.contenttypeclassname + '">' + article.contenttypetitletext + '</span>' +
                    dateText + article.articlesynopsis + '</p></div></a>';

                EDC.utils.attachEvents(card, 'click', _cardClicked);
                return card;
            }

            for (i = 0; i < articles.length; i++) {
                article = articles[i];
                nextCardsInitialCount++;
                contentCard = composeContentCard();
                contentCardGrid.appendChild(contentCard);
            }
        }

        function _loadNextPage() {
            var nextPage = currentPage + 1,
                scrollVar;

            if (nextPage <= self.numPagesToLoad) {

                _getResultsByPage(nextPage, function (articles) {
                    currentPage = nextPage;
                    _renderContentCards(articles);
                    EDC.utils.cardGridTrackEvent('load-next-page', contentCardGrid, nextPage);
                });


                scrollVar = _satellite.getVar('scrolled');
                if (scrollVar === undefined) {
                    _satellite.setVar('scrolled', 1);
                } else {
                    _satellite.setVar('scrolled', scrollVar + 1);
                }
                _satellite.track('infinite scroll');

                if (infiniteScroll === 'false' && nextPage === self.numPagesToLoad && loadMoreBtn) {
                    loadMoreBtn.classList.add('hide');
                }
            }

            scrollVar = _satellite.getVar('scrolled');
            if (scrollVar === undefined) {
                _satellite.setVar('scrolled', 1);
            } else {
                _satellite.setVar('scrolled', scrollVar + 1);
            }
            _satellite.track('infinite scroll');
        }

        function _checkScrollPosition() {
            var ccgpos = contentCardGrid.clientHeight - contentCardGrid.offsetTop - 300,
                scrollpos = lastKnownScrollPosition;

            if (ccgpos < scrollpos) {
                _loadNextPage();
            }
        }

        function _infiniteScroll() {
            lastKnownScrollPosition = window.pageYOffset;
            if (!ticking) {
                window.requestAnimationFrame(function () {
                    _checkScrollPosition();
                    ticking = false;
                });
                ticking = true;
            }
        }

        function _main() {
            if (sentenceBuilder === null || typeof (sentenceBuilder) === 'undefined') {
                _loadArticles();
            }
        }

        function _attachEvents() {
            // Position tracking event
            EDC.utils.attachEvents(cards, 'click', _cardClicked);

            if (loadMoreBtn && infiniteScroll === 'false') {
                loadMoreBtn.classList.remove('hide');
                EDC.utils.attachEvents(loadMoreBtn.querySelector('p'), 'click', _loadNextPage);
            } else if (infiniteScroll === 'true') {
                EDC.utils.attachEvents(window, 'scroll', _infiniteScroll);
                if (loadMoreBtn) {
                    loadMoreBtn.classList.add('hide');
                }
            }
        }

        _main();
        _attachEvents();
    }

    // Public methods
    function init() {
        var contentCardGrids = document.querySelectorAll('.wrapper-card-grid:not([data-component-state="initialized"])');

        if (contentCardGrids) {
            contentCardGrids.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', loadArticles.init);
