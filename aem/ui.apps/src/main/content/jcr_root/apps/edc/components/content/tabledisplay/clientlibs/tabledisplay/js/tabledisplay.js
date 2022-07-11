var tableDisplay = (function () {
    'use strict';

    function _sortTable(n, table) {
        var tbl,
            rows,
            headers,
            switching,
            i,
            x,
            y,
            shouldSwitch,
            dir,
            switchcount = 0;

        tbl = table;
        switching = true;
        dir = 'asc';

        headers = tbl.querySelectorAll('thead th span');

        if (headers[n].classList.contains('double-arrow')) {
            headers[n].classList.remove('double-arrow');
            headers[n].classList.add('arrow-up');

        } else if (headers[n].classList.contains('arrow-up')) {
            headers[n].classList.remove('arrow-up');
            headers[n].classList.add('arrow-down');

        } else if (headers[n].classList.contains('arrow-down')) {
            headers[n].classList.remove('arrow-down');
            headers[n].classList.add('arrow-up');
        }

        while (switching) {
            switching = false;
            rows = tbl.querySelectorAll('tbody tr');

            for (i = 0; i < rows.length; i++) {
                shouldSwitch = false;
                if (rows[i + 1]) {
                    x = rows[i].getElementsByTagName('TD')[n];
                    y = rows[i + 1].getElementsByTagName('TD')[n];

                    if (dir === 'asc') {
                        if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                            shouldSwitch = true;
                            break;
                        }
                    } else if (dir === 'desc') {
                        if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
                            shouldSwitch = true;
                            break;
                        }
                    }
                }
            }

            if (shouldSwitch) {
                rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
                switching = true;
                switchcount++;
            } else if (switchcount === 0 && dir === 'asc') {
                dir = 'desc';
                switching = true;
            }
        }
    }

    function _initialize(elem) {
        var tableType = elem.querySelector('div:first-child'),
            tableContent = elem.querySelector('.c-table-display .c-table-display__content'),
            table = elem.querySelector('.c-table-display table'),
            rowsArrObj = table.querySelectorAll('tr'),
            rowsQuantity = tableContent.getAttribute('data-rows-to-render'),
            congruentHeightBounds = 0,
            height;

        function _renderRows(row, col) {
            if (window.innerWidth < EDC.props.media.md) {
                col.width = '285px';
            } else if (row.children.length === 1) {
                col.width = '100%';
            } else if (row.children.length === 2) {
                col.width = '50%';
            } else if (row.children.length === 3) {
                col.width = '33.333%';
            } else if (row.children.length === 4) {
                col.width = '25%';
            } else if (row.children.length > 4) {
                col.width = '285px';
            }
        }

        function _resizeCols(item, index, arr) {
            arr.forEach(function (row) {
                row.children.forEach(function (col) {
                    _renderRows(row, col);
                });
            });
        }

        function _setRowsToRender(item, index, arr) {
            arr.forEach(function () {
                if (tableType.classList.contains('c-table-display-basic__condensed')) {
                    height = 34;

                    if (rowsQuantity) {
                        congruentHeightBounds = rowsQuantity * height;
                        tableContent.style.height = (congruentHeightBounds + 18) - 10 + 'px';
                    }

                } else {
                    height = 50;

                    if (rowsQuantity) {
                        congruentHeightBounds = rowsQuantity * height;
                        tableContent.style.height = (congruentHeightBounds + 7) + 'px';
                    }
                }
            });
        }

        function _refreshTableContainer() {
            rowsArrObj.forEach(_setRowsToRender);
        }

        function _refreshColumns() {
            rowsArrObj.forEach(_resizeCols);
        }

        function _activateScrollsIfRequired() {
            tableContent.classList.add('activate-scrolls');
        }

        function _attachSortableEvent() {
            var headers;

            if (table.getAttribute('data-is-sortable') === 'sortable') {
                headers = table.querySelectorAll('thead tr th span:last-child');

                headers.forEach(function (item, index) {
                    item.addEventListener('click', function () {
                        _sortTable(index, table);
                    });
                });
            }
        }

        window.addEventListener('resize', _refreshColumns, false);

        window.addEventListener('load', function () {
            _activateScrollsIfRequired();
            _refreshTableContainer();
            _refreshColumns();
            _attachSortableEvent();
        }, false);
    }

    function init() {
        var tablesDisplay = document.querySelectorAll('.c-table-display-wrapper:not([data-component-state="initialized"])');

        if (tablesDisplay) {
            tablesDisplay.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', tableDisplay.init);