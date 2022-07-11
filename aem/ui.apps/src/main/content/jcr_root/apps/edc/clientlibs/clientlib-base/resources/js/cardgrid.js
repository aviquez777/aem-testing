EDC.components.ArticleResultSet = function () {
    var self = this,
        serviceEndPoint = getArticlesByTagIdEndPoint(),
        db = new Dexie('EDC_' + EDC.props.lang),
        pageSize = 10,
        contentCardGrid = document.querySelector('.content-card-grid');

    self.articlesTable = {};
    self.currentResultSet = {}; //Collection
    self.currentPage = 1;
    self.debug = false;
    self.numPage = 1;

    EDC.utils.debug(false);

    function fetchDataFromServer(fnCallback) {
        var request = new XMLHttpRequest();

        request.open('GET', serviceEndPoint, true);
        request.onload = function () {
            var data;

            if (request.status >= 200 && request.status < 400) {
                data = JSON.parse(request.responseText);
                fnCallback({ success: true, data: data })
            } else {
                fnCallback({ success: false, error: 'server returned an error' });
            }
        };

        request.onerror = function () {
            fnCallback({ success: false, error: 'connection error' });
        };

        request.send();
    }

    function populateDBFromServer(fnCallback) {
        // Note: for tag result page, the tags field will not exist.
        fetchDataFromServer(function (result) {
            var articles,
                pageSize,
                i,
                article;

            if (result.success) {
                articles = result.data.articles;
                pageSize = result.data.pagesize;

                for (i = 0; i < articles.length; i++) {
                    article = articles[i];
                    delete article.id;
                    article.tags = article.tags.join('|');
                    article.filteredSortOrder = '';
                }

                db.open().then(function (db) {
                    db.table('status').clear();
                    db.table('articles').clear();
                    db.table('status').bulkAdd([{ field: 'lastRetrieved', value: new Date() }, { field: 'pageSize', value: pageSize }]).then(function () {
                        db.table('articles').bulkAdd(articles).then(function () {
                            fnCallback(db.table('articles'));
                        });
                    });
                })
            }
        });
    }

    function fetchDataFromDB(fnCallback) {
        db.open().then(function (db) {
            populateDBFromServer(function (data) {
                fnCallback({ success: true, data: data });
            });
        }).catch('NoSuchDatabaseError', function (e) {
            db.version(1).stores({
                articles: '++id, path, teaserimage, articlesynopsis, pageTitle, sortdate, imagealttext, tags, filteredSortOrder, [filteredSortOrder+sortdate]',
                status: 'field, value'
            });
            populateDBFromServer(function (data) {
                fnCallback({ success: true, data: data });
            });

        }).catch(function () {
            fnCallback({ success: false, error: 'IndexedDB error' });
        });
    }

    function _cardClicked(e) {
        EDC.utils.cardGridTrackEvent('card-clicked', contentCardGrid, '', e);
    }

    function filterByTags(tags, fnCallback) {
        var filters = [[]];

        if (tags.a && tags.b && tags.c) {
            //AC15: If user fills in fields “A”, “B” and “C”, results are displayed, content is sorted by most recent and content is displayed in following sorting priority order:
            //AC15.1: Priority 1: All content that has tags "B" and “C”
            //AC15.2: Priority 2: All content that has tags “A” and “C”
            //AC15.3: Priority 3: All content that has tags “B” only
            //AC15.4: Priority 4: All content that has tags “A” only
            //AC15.5: Priority 5: All content that has tags “C” only
            filters = [
                [tags.a, tags.b, tags.c],
                [tags.b, tags.c],
                [tags.a, tags.c],
                [tags.b],
                [tags.a],
                [tags.c]
            ]
        } else if (tags.a && tags.b) {
            //AC14: If user fills in fields “A” and “B”, results are displayed, content is sorted by most recent and content displayed must be in following sorting priority:
            //AC14.1: Priority 1: All content that has tags “B”.
            //AC14.2: Priority 2: All content that contain tag “A”
            filters = [
                [tags.a, tags.b],
                [tags.b],
                [tags.a]
            ]
        } else if (tags.a && tags.c) {
            //AC13: If user fills in fields “A” and “C”, results are displayed, content is sorted by most recent and content displayed must be in following sorting priority:
            //AC13.1: Priority 1: All content that has both tags “A” and “C”.
            //AC13.2: Priority 2: All content that has tag “A” only
            //AC13.3: Priority 3: All content that has tag “C” only
            filters = [
                [tags.a, tags.c],
                [tags.a],
                [tags.c]
            ]
        } else if (tags.a) {
            //AC11: If user fills in only field “A”, results are displayed, content is sorted by most recent, and each content displayed must have tag selected "A".
            filters = [
                [tags.a]
            ]
        } else if (tags.c) {
            //AC12: If user fills in only field “C”, results are displayed, content is sorted by most recent, and each content displayed must have tag selected "C".
            filters = [
                [tags.c]
            ]
        }

        self.articlesTable.where("filteredSortOrder").notEqual("_").modify({ filteredSortOrder: "_" }).then(function () {
            var i = filters.length - 1,
                stop = 0;

            function filter() {
                var changes = { filteredSortOrder: i };

                self.articlesTable.filter(function (article) {
                    var matchesFound = 0,
                        j;

                    for (j = 0; j < filters[i].length; j++) {
                        if (article.tags.indexOf(filters[i][j]) > -1) {
                            matchesFound++;
                        }
                    }
                    return matchesFound == filters[i].length;
                }).modify(changes).then(function () {
                    if (i > stop) {
                        i--;
                        filter();
                    }
                    else {
                        self.articlesTable.where('filteredSortOrder').notEqual('_').toArray(function (articles) {
                            self.currentResultSet = articles;
                            self.numPages = Math.ceil(articles.length / pageSize);
                            fnCallback();
                        });
                    }
                });
            }

            filter();
        });
    }

    function getResultsByPage(pageNumber, fnCallback) {
        var start,
            stop,
            articles = [],
            i;

        self.currentPage = pageNumber;
        start = (pageNumber - 1) * pageSize;
        stop = ((start + pageSize) < self.currentResultSet.length ? start + pageSize : self.currentResultSet.length);

        for (i = start; i < stop; i++) {
            articles.push(self.currentResultSet[i]);
        }

        fnCallback(articles);
    }

    function loadNextPage(fnCallback) {
        var nextPage = self.currentPage + 1;

        if (nextPage <= self.numPages) {
            getResultsByPage(nextPage, function (articles) {
                self.renderContentCards(articles, fnCallback);
                EDC.utils.cardGridTrackEvent('load-next-page', contentCardGrid, nextPage);
            });
        }
    }

    function renderContentCards(articles, fnCallback) {
        var j,
            article,
            contentCard;

        function composeContentCard(article, index) {
            var imagesrc = '',
                dateText = '',
                card = document.createElement('a'),
                tags,
                tagsHTML,
                i;

            card.setAttribute('href', article.url);
            card.setAttribute('data-event-type', 'link');
            card.setAttribute('data-event-name', 'link click - card grid');
            card.setAttribute('data-event-engagement', '1');
            card.setAttribute('data-event-page-name', article.pagename);
            card.setAttribute('data-event-action', index);
            card.setAttribute('data-event-destination-page', article.url);
            card.classList.add('content-card');
            card.classList.add(article.contenttypeclassname);

            if (article.ispremium && contentCardGrid.getAttribute('data-small-cards') === null) {
                card.classList.add('premium');
                imagesrc = article.tabletFileReference;
            } else {
                imagesrc = article.teaserimage;
            }

            if (self.debug) {
                tags = article.tags.split('|');
                tagsHTML = '';
                for (i = 0; i < tags.length; i++) {
                    tagsHTML += '<li>' + tags[i] + '</li>';
                }
                card.innerHTML = '<ul>' + tagsHTML + '</ul>' +
                    '<span>sortdate: ' + article.sortdate + '</span><br/>' +
                    '<span>grouping: ' + article.filteredSortOrder + '</span><br/>' +
                    '<span>url: ' + article.url + '</span>';
                card.style.fontSize = '9px';
            } else {
                if (article.displaydate && article.contenttypeclassname != 'success-story') {
                    dateText = '<span class="date">' + article.displaydate + '</span> ';
                }

                card.innerHTML = '<div class="card-img ' + article.imagealignment + '">' +
                    '<img src="' + imagesrc + '" alt="' + article.imagealttext + '">' +
                    '</div>' +
                    '<div class="card-content">' +
                    '<h4 class="title">' + article.pageTitle + '</h4>' +
                    '<p class="synopsis">' +
                    '<span class="category-tag ' + article.contenttypeclassname + '">' + article.contenttypetitletext + '</span>' +
                    dateText + article.articlesynopsis + '</p></div></a>';
            }

            EDC.utils.attachEvents(card, 'click', _cardClicked);
            return card;
        }

        if (self.currentPage == 1) {
            contentCardGrid.innerHTML = "";
        }

        for (j = 0; j < articles.length; j++) {
            article = articles[j];
            contentCard = composeContentCard(article, (j + 1));
            contentCardGrid.appendChild(contentCard);
        }
    }

    function main() {
        if (location.search.indexOf("debug=true") > -1) {
            self.debug = true;
        }

        if (document.querySelector('.sentence-builder') != null && typeof (document.querySelector('.sentence-builder')) != 'undefined') {
            fetchDataFromDB(function (result) {
                if (result.success) {
                    self.articlesTable = result.data;
                    self.filterByTags({ a: "", b: "", c: "" }, function () { });
                }
            });
        }
    }

    main();

    self.filterByTags = filterByTags;
    self.getResultsByPage = getResultsByPage;
    self.loadNextPage = loadNextPage;
    self.renderContentCards = renderContentCards;
}

EDC.components.articleResultSet = new EDC.components.ArticleResultSet()
