var sentenceBuilderInit = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        // Private methods

        var selectArray = element.querySelectorAll('.sentence-builder .select'),
            clearBtn = element.querySelector('.clear-btn'),
            tags,
            lastKnownScrollPosition = 0,
            ticking = false,
            isDevice;

        try {
            if (element.dataset.categoryJson) {
                EDC.props.SentenceBuilderTags = JSON.parse(element.dataset.categoryJson);
            } else {
                EDC.props.SentenceBuilderTags = {};
            }
        } catch (error) {
            EDC.props.SentenceBuilderTags = {};
        }

        tags = EDC.props.SentenceBuilderTags;

        // Remove the "category/" from the tag for analitycs
        function cleanTag(tag) {
            return tag.substring(tag.lastIndexOf('/') + 1);
        }

        // Data Layer
        function _trackEvent(values, target) {
            var eventComponent = element.dataset.eventComponent,
                eventType = element.dataset.eventType,
                eventName = element.dataset.eventName,
                valueSelection = 'objective:' + (values.a !== '' ? cleanTag(values.a) : null) +
                    ';topic:' + (values.b !== '' ? cleanTag(values.b) : null) +
                    ';region:' + (values.c !== '' ? cleanTag(values.c) : null),
                obj = {
                    eventInfo: {
                        eventComponent: eventComponent,
                        eventType: eventType,
                        eventName: eventType + ' ' + eventName + ' ' + target,
                        eventAction: element.dataset.eventAction,
                        eventText: '',
                        eventValue: valueSelection,
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: '',
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: ''
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function convertCase(str) {
            var words = str.split(' '),
                i;

            for (i = 0; i < words.length; i++) {
                if (words[i].match(/[A-Z]{2,}/g) === null) {
                    words[i] = words[i].toLowerCase();
                }
            }

            return words.join(' ');
        }

        function initSelectA() {
            var selectA = selectArray[0].querySelector('select'),
                opt = document.createElement('option'),
                tag;

            selectA.innerHTML = '';
            opt.value = '';
            opt.text = '';
            selectA.appendChild(opt);

            for (tag in tags) {
                if (tags.hasOwnProperty(tag)) {
                    opt = document.createElement('option');
                    opt.value = tag;
                    opt.text = convertCase(tags[tag].tagLabel);
                    selectA.appendChild(opt);
                }
            }
        }

        function handleSelectAClick(e) {
            var selectA = selectArray[0].querySelector('select'),
                selectB,
                selectC,
                filters,
                labelB,
                selectedOption,
                opt,
                tagsForSelectB,
                tag,
                target = e ? e.target.parentElement.getAttribute('data-event-selection') : '';

            if (e.type === 'click' || e.type === 'change' || e.keyCode === EDC.props.enterKeyCode || e.keyCode === EDC.props.tabKeyCode) {
                if (clearBtn && clearBtn.className.indexOf('show') === -1) {
                    clearBtn.classList.add('show');
                }

                /* Assumption: there are 3 selects on the component [selectA, selectB, selectC],
                SelectB:
                - should be disabled if selectA is empty
                - should be enabled if selectA has a value
                - shoule be emptied && disabled if select A = ''*/

                if (selectArray.length > 2) {
                    selectB = selectArray[1].querySelector('select');
                    labelB = selectB.previousElementSibling;
                } else {
                    return false;
                }

                selectC = selectArray[2].querySelector('select');
                filters = { a: selectA.value, b: '', c: selectC.value };

                if (EDC.components.articleResultSet) {
                    EDC.components.articleResultSet.filterByTags(filters, function () {
                        EDC.components.articleResultSet.getResultsByPage(1, function (articles) {
                            EDC.components.articleResultSet.renderContentCards(articles);
                        });
                    });
                }

                // if selectA is empty
                if (selectA.value === '') {
                    // disable and empty selectB
                    selectB.value = '';
                    selectB.disabled = true;

                    // handle label B
                    if (labelB.className.indexOf('disabled') === -1) {
                        labelB.classList.add('disabled');
                        labelB.innerHTML = '';
                        labelB.appendChild(document.createElement('span'));
                    }

                    if (selectC.value === '' && clearBtn.classList.contains('show')) {
                        clearBtn.classList.remove('show');
                    }
                } else {
                    // Enable selectB
                    selectB.removeAttribute('disabled');
                    labelB.classList.remove('disabled');

                    selectedOption = selectA.options[selectA.options.selectedIndex].value;
                    selectB.options.length = 0;
                    opt = document.createElement('option');
                    opt.value = '';
                    opt.text = '';
                    selectB.appendChild(opt);

                    tagsForSelectB = tags[selectedOption].childTags;

                    for (tag in tagsForSelectB) {
                        if (tagsForSelectB.hasOwnProperty(tag)) {
                            opt = document.createElement('option');
                            opt.value = tag;
                            opt.text = convertCase(tagsForSelectB[tag]);
                            selectB.appendChild(opt);
                        }
                    }

                    selectB.value = '';
                    labelB.appendChild(document.createElement('span'));
                }

                if (target) {
                    _trackEvent(filters, target);
                }
            }
            return true;
        }

        function handleSelectBClick(e) {
            var selectA = selectArray[0].querySelector('select'),
                selectB = selectArray[1].querySelector('select'),
                selectC = selectArray[2].querySelector('select'),
                filters = { a: selectA.value, b: selectB.value, c: selectC.value },
                target = e ? e.target.getAttribute('data-event-selection') : '';

            if (clearBtn && clearBtn.className.indexOf('show') === -1) {
                clearBtn.classList.add('show');
            }

            if (EDC.components.articleResultSet) {
                EDC.components.articleResultSet.filterByTags(filters, function () {
                    EDC.components.articleResultSet.getResultsByPage(1, function (articles) {
                        EDC.components.articleResultSet.renderContentCards(articles);
                    });
                });
            }

            if (target) {
                _trackEvent(filters, target);
            }
        }

        function hideSelect(select) {
            var selectA = selectArray[0].querySelector('select'),
                selectB = selectArray[1].querySelector('select'),
                selectC = selectArray[2].querySelector('select');

            if (!selectA.value && !selectB.value && !selectC.value) {
                if (clearBtn && clearBtn.className.indexOf('show') > -1) {
                    setTimeout(function () {
                        clearBtn.classList.remove('show');
                    }, 50);
                }
            }
            select.parentNode.classList.remove('open');
        }

        function showSelect(select) {
            var chevron = select.parentNode.querySelector('.label').lastChild,
                options = select.querySelectorAll('option'),
                offsetLeft;

            // Mark parent node as open
            select.parentNode.classList.add('open');

            if (!isDevice) {
                offsetLeft = chevron.offsetLeft - select.offsetWidth;
                select.style.cssText = 'left:' + (offsetLeft > 0 ? offsetLeft + 25 : 25) + 'px; top: ' + (chevron.offsetTop > 0 ? 30 + chevron.offsetTop : 44) + 'px;';
            }

            select.size = options.length;
            select.focus();
        }

        function handleSelectFocus(e) {
            if (e.currentTarget.parentNode.classList.contains('focused') || e.currentTarget.parentNode.classList.contains('open')) {
                // if the element has already been focused, quit
                // used to prevent endless loop
                return false;
            }

            // mark the select as focused
            e.currentTarget.parentNode.classList.add('focused');

            // Trigger LABEL click
            e.currentTarget.parentNode.querySelector('.label').click();

            // mark the select as not-focused
            e.currentTarget.parentNode.classList.remove('focused');

            return true;
        }

        function closeOpenedSelect() {
            var openSelect = element.querySelector('.select.open');

            // Choose the open select
            // If any element, close it
            // if (e.target.classList.contains('label') || e.target.parentNode.classList.contains('label')) {
            if (typeof (openSelect) !== 'undefined' && openSelect !== null) {
                hideSelect(openSelect.querySelector('select'));
            }
            // }
        }

        function handleLabelClick(e) {
            var select = e.currentTarget.nextElementSibling;

            // Set the select associated with this label
            // If current element is disabled do nothing
            if (e.currentTarget.classList.contains('disabled')) {
                return false;
            }

            // If this element is already open, simply close it
            if (e.currentTarget.parentNode.classList.contains('open')) {
                // Close the Select
                hideSelect(select);
                return false;
            }

            // Show Select
            showSelect(select);
            return true;
        }

        function handleSelectClick(e) {
            var selectedOption = e.currentTarget.options[e.currentTarget.options.selectedIndex],
                label = e.currentTarget.previousElementSibling,
                span = document.createElement('span');

            // Close the Select
            if (e.type === 'click' || e.type === 'change' || e.keyCode === EDC.props.enterKeyCode || e.keyCode === EDC.props.tabKeyCode) {
                hideSelect(e.currentTarget);

                // Add a space after the selection option for styling purposes (border case)
                label.innerHTML = selectedOption.text;
                label.appendChild(span);
            }
        }

        function loadNextPage() {
            var ccg = document.querySelector('.content-card-grid');

            if (ccg) {
                if (ccg.clientHeight - 300 < lastKnownScrollPosition) {
                    if (EDC.components.articleResultSet && EDC.components.articleResultSet.numPages > EDC.components.articleResultSet.currentPage) {
                        EDC.components.articleResultSet.loadNextPage();
                    }
                }
            }
        }

        function infiniteScroll() {
            window.addEventListener('scroll', function () {
                lastKnownScrollPosition = window.pageYOffset;

                if (!ticking) {
                    window.requestAnimationFrame(function () {
                        loadNextPage();
                        ticking = false;
                    });

                    ticking = true;
                }
            });
        }

        function setTime(e) {
            var selectA = selectArray[0].querySelector('select'),
                labelA = element.querySelector('#sentenceBuilderA-label'),
                labelB = element.querySelector('#sentenceBuilderB-label'),
                selectC = selectArray[2].querySelector('select'),
                labelC = element.querySelector('#sentenceBuilderC-label');

            e.preventDefault();

            if (selectA.value !== '') {
                selectA.value = '';
                labelA.innerHTML = '';
                labelA.appendChild(document.createElement('span'));
                labelB.innerHTML = '';
                labelB.appendChild(document.createElement('span'));
            }

            if (selectC.value !== '') {
                selectC.value = '';
                labelC.innerHTML = '';
                labelC.appendChild(document.createElement('span'));
            }

            handleSelectAClick(e);

            if (clearBtn && clearBtn.className.indexOf('show') > -1) {
                clearBtn.classList.remove('show');
            }
        }

        function handleKeyboard(e) {
            switch (e.keyCode) {
                case EDC.props.enterKeyCode:
                    handleLabelClick(e);
                    break;

                default:
                    return;
            }
        }

        // Attach events
        function _attachEvents() {
            var html = document.querySelector('html'),
                i;

            if (clearBtn) {
                EDC.utils.attachEvents(clearBtn, 'click', setTime);
            }

            // BROWSER DETECTION HACK
            if ((navigator.userAgent.match(/(iPad|iPhone|iPod)/i)) || (navigator.userAgent.match(/android/i))) {
                isDevice = true;
                html.classList.add('device');

                if (navigator.userAgent.match(/android/i)) {
                    html.classList.add('device-android');
                }
            }

            for (i = 0; i < selectArray.length; i++) {
                // Set Defaul State to all the LABELS (width)

                // Add a Listener for enter keyup (Accessibility)
                EDC.utils.attachEvents(selectArray[i].querySelector('.label'), 'keyup', handleKeyboard);

                // Add a Listener to the Select (it's hidden by default, shown only when the label is clicked)
                selectArray[i].querySelector('.label').addEventListener('click', handleLabelClick, false);

                // Add a Listener to the Select (it's hidden by default, shown only when the label is clicked)

                if (isDevice) {
                    selectArray[i].querySelector('select').addEventListener('change', handleSelectClick, false);
                } else {
                    EDC.utils.attachEvents(selectArray[i].querySelector('select'), 'click', handleSelectClick, false);
                    EDC.utils.attachEvents(selectArray[i].querySelector('select'), 'keydown', handleSelectClick, false);
                }

                // Add a Listener to the Select (it's hidden by default, shown only when the label is clicked)
                selectArray[i].querySelector('select').addEventListener('blur', closeOpenedSelect, false);

                // Add a Listener to the Select (it's hidden by default, shown only when the label is clicked)
                selectArray[i].querySelector('select').addEventListener('focus', handleSelectFocus, false);

                if (i === 0) {
                    // Toggle SelectB on SelectA change
                    if (isDevice) {
                        selectArray[i].querySelector('select').addEventListener('change', handleSelectAClick, false);
                    } else {
                        EDC.utils.attachEvents(selectArray[i].querySelector('select'), 'click', handleSelectAClick, false);
                        EDC.utils.attachEvents(selectArray[i].querySelector('select'), 'keydown', handleSelectAClick, false);
                    }
                } else if (i === 1) {
                    selectArray[i].querySelector('select').addEventListener('change', handleSelectBClick, false);
                } else if (i === 2) {
                    selectArray[i].querySelector('select').addEventListener('change', handleSelectBClick, false);
                }
            }
        }

        // INIT DROPDOWNS
        initSelectA();

        // INIT INFINITE SCROLL LISTENER
        infiniteScroll();

        _attachEvents();
    }

    // Public methods
    function init() {
        var sentenceBuilder = document.querySelectorAll('.sentence-builder:not([data-component-state="initialized"])');

        if (sentenceBuilder) {
            sentenceBuilder.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', sentenceBuilderInit.init);