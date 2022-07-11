EDC.components.InsightsSearch = function () {
    'use strict';

    var db = new Dexie('EDCInsights_' + EDC.props.lang),
        self = this,
        pageSize = 10;

    // Gets the Service URL from AEM
    self.serviceEndPoint = getArticlesByTagIdEndPoint();

    // Helper function to load Nex Page of initial content
    function _getFilteredData(pageNum, selectedFilters, fnCallBack) {
        if (selectedFilters.length) {
            db.table('articles')
                .toCollection()
                .and(function (article) {
                    var filterCount = 0;

                    // Gets the selected filters and compares them with the article's tags array.
                    // If the selected filter matches any of the article tags it will be added to the result of the query
                    selectedFilters.forEach(function (filter) {
                        var subFilterCount = 0;

                        filter.forEach(function (subFilter) {

                            // OR functionaliy, checks the subFilters on each filter category and returns > 0 if any of the article's tags matches
                            if (article.tags.indexOf(subFilter) !== -1) {
                                subFilterCount++;
                            }
                        });

                        // AND functionaliy, if the article tags matches all the given filters it will filterCount++
                        if (subFilterCount > 0) {
                            filterCount++;
                        }
                    });

                    // If the filter count is === to the number of filter categories it will return the article on the query
                    return filterCount === selectedFilters.length;
                })
                .offset(pageNum * pageSize) // Offsets the amount of rendered articles
                .limit(pageSize) // Only returns the 1st (pageSize) articles after the offset
                .toArray(fnCallBack);
        } else {
            // Brings all the articles of the DB and returns pageSize by pageSize
            db.table('articles')
                .offset(pageNum * pageSize) // Offsets the amount of rendered articles
                .limit(pageSize) // Only returns the 1st (pageSize) articles after the offset
                .toArray(fnCallBack);
        }
    }

    // Helper function to populate DB from service URL
    function _populateDB() {
        // Calls JSON file from servlet to get the requested questions
        EDC.utils.fetchJSON('GET', self.serviceEndPoint, '', function (data) {
            // Overrides default page size
            pageSize = data.pagesize || 10;

            if (data.articles.length) {
                data.articles.forEach(function (article) {
                    // Removes the ids to create new id on DB
                    delete article.id;
                    article.tags = article.tags.join('|');
                });

                // Clears the DBs and adds the values to the tables
                db.table('status').clear();
                db.table('articles').clear();
                db.table('status').bulkAdd([{ field: 'lastRetrieved', value: new Date() }, { field: 'pageSize', value: pageSize }]);
                db.table('articles').bulkAdd(data.articles);
            }
        });
    }

    // Helper function to initalize the DB
    function _initialize() {

        // Checks if the DB is created and opened
        if (db.isOpen()) {
            _populateDB();
        } else {
            db.version(1).stores({
                articles: '++id, *tags',
                status: 'field, value'
            });

            _populateDB();
        }
    }

    self.initialize = _initialize;
    self.getFilteredData = _getFilteredData;
};

EDC.components.InsightsSearch = new EDC.components.InsightsSearch();