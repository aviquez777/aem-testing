var ehhSearchResultsJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            infoSection = element.querySelector('.information-container'),
            questionsSection = element.querySelector('.default-questions'),
            questionsTitle = questionsSection.querySelector('.title'),
            answerInfo = element.querySelector('.answer-info'),
            answerSection = answerInfo.querySelector('.answer'),
            questionTitle = answerSection.querySelector('.question-title'),
            questionAnswer = answerSection.querySelector('.answer-content'),
            relatedQuestionsList = answerInfo.querySelector('.related-questions'),
            searchInput = d.querySelector('.c-ehh-search .search-input'),
            infoWelcome = infoSection.querySelector('.welcome'),
            infoNoAnswer = infoSection.querySelector('.no-answers'),
            binaryCta = element.querySelector('.c-binary-cta'),
            serviceURL = element.getAttribute('data-action'),
            readMore = element.getAttribute('data-read-more') ? element.getAttribute('data-read-more') : '',
            loadMore = element.querySelector('.load-more'),
            loadMoreEvent = loadMore && loadMore.getAttribute('data-load-more-event') ? loadMore.getAttribute('data-load-more-event') : '',
            loadMoreText = loadMore && loadMore.getAttribute('data-load-more-english') ? loadMore.getAttribute('data-load-more-english') : '',
            topResults = element.querySelector('.top-results'),
            answerProfile = answerInfo.querySelector('.answer-profile'),
            ehhForm = d.querySelector('.c-ehh-team-form'),
            showResults = element.getAttribute('data-results') ? parseInt(element.getAttribute('data-results'), 10) : 3,
            invalidChars = /\?/g,
            serverAction = 'GET',
            relatedScores = '',
            relatedQuestions = '',
            relatedCountries = '',
            relatedTopics = '',
            confidenceLevel,
            confidenceView,
            country,
            topic,
            questionsData,
            query,
            topResult,
            topScore;

        // Private methods

        // Data Layer
        function _trackEvent(dataLayerObj) {
            EDC.utils.dataLayerTracking(dataLayerObj);
        }

        // Helper function to show the default sections
        function _showHideSections() {
            element.classList.remove('processing');
            answerInfo.classList.add('hide');
            infoSection.classList.remove('hide');
            questionsSection.classList.remove('hide');
        }

        // Helper function to display no answers found section
        function _showNoAnswers() {
            var obj = {
                eventInfo: {
                    eventComponent: (element.dataset.eventComponent).toLowerCase(),
                    eventType: (answerSection.dataset.eventType).toLowerCase(),
                    eventName: (answerSection.dataset.eventType + ' - ' + answerSection.dataset.eventName).toLowerCase(),
                    eventAction: element.dataset.eventAction,
                    eventText: '',
                    eventValue: 'zero',
                    eventValue2: '',
                    eventValue3: '',
                    eventValue4: '',
                    confidenceView: 'no results',
                    relatedResultCountries: '',
                    relatedResultTopics: '',
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    engagementType: element.dataset.eventEngagement,
                    searchQuery: query.toLowerCase(),
                    topResult: '',
                    addtionalResults: ''
                }
            };

            questionsTitle.innerText = questionsTitle.getAttribute('data-no-answers');
            infoWelcome.classList.add('hide');
            infoNoAnswer.classList.remove('hide');
            _showHideSections();

            _trackEvent(obj);
        }

        // Helper function to reset Binary CTA component
        function _resetCTA() {
            var returnText = binaryCta.querySelector('.return');

            if (binaryCta) {
                returnText.classList.remove('show');
                returnText.classList.add('hide');
                binaryCta.querySelector('.cta-wrap').classList.remove('hide');
            }
        }

        // Check if element and value exist if exist so set element with value, if not remove element.
        function _setElemValue(currentElem, text, attr) {
            if (currentElem) {
                if (text) {
                    if (attr) {
                        currentElem.setAttribute(attr, text);
                    } else {
                        currentElem.innerText = text;
                    }
                } else {
                    currentElem.remove();
                }
            }
        }

        // Set author information
        function _setAuthor(author, profile) {
            var link = profile.querySelector('a'),
                image = link ? link.querySelector('img') : null,
                infoLink = profile.querySelector('.info a'),
                name = infoLink ? infoLink.querySelector('.name') : null,
                position = profile.querySelector('.info .position'),
                company = profile.querySelector('.info .company span'),
                linkedIn = profile.querySelector('.info .linkedin');

            // Sets al the author data on Profile component
            if (author) {
                // Check and set author image
                if (link && author.bioURL && image && author.imageUrl) {
                    link.setAttribute('href', author.bioURL);
                    link.setAttribute('title', author.name || '');

                    image.setAttribute('src', author.imageUrl);
                    image.setAttribute('alt', author.imageAlt ? author.imageAlt : author.name || '');
                } else if (link && !author.bioURL) {
                    link.remove();
                }

                // Check and set linkedIn
                if (linkedIn && author.linkedInUrl) {
                    linkedIn.setAttribute('href', author.linkedInUrl);
                    linkedIn.setAttribute('title', 'LinkedIn ' + (author.name ? '- ' + author.name : ''));
                    linkedIn.querySelector('span').innerText = 'LinkedIn ' + (author.name ? '- ' + author.name : '');
                } else if (linkedIn && !author.linkedInUrl) {
                    linkedIn.remove();
                }

                // Check and set info link
                _setElemValue(infoLink, author.bioURL, 'href');

                // Check and set name
                _setElemValue(name, author.name);

                // Check and set position
                _setElemValue(position, author.position);

                // Check and set company
                _setElemValue(company, author.companyName);
            } else {
                profile.classList.add('hide');
            }
        }

        // Helper function to display the selected related question
        function _showRelatedQuestion(e) {
            var questionClicked = e.target.classList.contains('question') ? e.target : e.target.closest('li'),
                questionId = questionClicked.getAttribute('data-id'),
                questionList = questionClicked.closest('ul'),
                relatedQuestion = questionList.querySelector('li[data-id="' + answerSection.getAttribute('data-id') + '"]'),
                obj;

            // Gets the id of the initial question and hides shows it on the related questions lists
            relatedQuestion.classList.remove('hide');
            relatedQuestion.classList.remove('selected-result');
            EDC.utils.setClasses(questionClicked, ['hide', 'selected-result']);
            answerSection.setAttribute('data-id', questionId);

            // Looks for the matching id to display the selected question
            questionsData.questions.forEach(function (question) {
                if (question.id.toString() === questionId) {
                    questionTitle.innerText = question.question;
                    questionAnswer.innerHTML = question.answer;

                    // Updates the question author profile
                    _setAuthor(question.author, answerProfile);

                    // Resets the binary CTA and scrolls back to top
                    _resetCTA();
                    EDC.forms.resetEHHForm(ehhForm);
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y);

                    answerProfile.classList.remove('hide');
                    answerSection.classList.remove('hide');

                    obj = {
                        eventInfo: {
                            eventComponent: (element.dataset.eventComponent).toLowerCase(),
                            eventType: (answerSection.dataset.eventType).toLowerCase(),
                            eventName: (answerSection.dataset.eventType + ' - ' + relatedQuestionsList.dataset.eventName).toLowerCase(),
                            eventAction: element.dataset.eventAction,
                            eventText: question.dataLayerQuestion,
                            eventValue: questionsData.questions.length ? questionsData.questions.length : 'zero',
                            eventValue2: country,
                            eventValue3: topic,
                            eventValue4: relatedQuestions,
                            confidenceView: confidenceView,
                            relatedResultCountries: relatedCountries,
                            relatedResultTopics: relatedTopics,
                            eventPage: EDC.props.pageNameURL,
                            eventPageName: EDC.utils.getPageName(),
                            engagementType: element.dataset.eventEngagement,
                            searchQuery: query.toLowerCase(),
                            topResult: topResult + ':' + topScore,
                            addtionalResults: relatedScores
                        }
                    };

                    _trackEvent(obj);
                }
            });
        }

        // Helper function to display the correct answer after the search from JSON response
        function _displayCorrectQuestion(question) {
            var author = question.author,
                obj;

            // Resets the binary CTA
            _resetCTA();

            // Sets al the author data on Profile component
            _setAuthor(author, answerProfile);

            // Displays the found question and asnwer
            if (question.question && question.answer) {
                topResult = question.id;
                topScore = question.score;
                country = question.country;
                topic = question.topic;
                answerSection.setAttribute('data-id', question.id);
                questionTitle.innerText = question.question;
                questionAnswer.innerHTML = question.answer;

                if (binaryCta) {
                    binaryCta.setAttribute('data-search-query', query);
                    binaryCta.setAttribute('data-top-result', topResult + ':' + topScore);
                }

                // will only display the question based on confidence level
                if (confidenceLevel) {
                    answerProfile.classList.remove('hide');
                    answerSection.classList.remove('hide');
                    confidenceView = 'full view';
                } else {
                    answerProfile.classList.add('hide');
                    answerSection.classList.add('hide');
                    confidenceView = 'related view';
                }

                obj = {
                    eventInfo: {
                        eventComponent: (element.dataset.eventComponent).toLowerCase(),
                        eventType: (answerSection.dataset.eventType).toLowerCase(),
                        eventName: (answerSection.dataset.eventType + ' - ' + answerSection.dataset.eventName).toLowerCase(),
                        eventAction: element.dataset.eventAction,
                        eventText: question.dataLayerQuestion,
                        eventValue: questionsData.questions.length ? questionsData.questions.length : 'zero',
                        eventValue2: country,
                        eventValue3: topic,
                        eventValue4: relatedQuestions,
                        confidenceView: confidenceView,
                        relatedResultCountries: relatedCountries,
                        relatedResultTopics: relatedTopics,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        engagementType: element.dataset.eventEngagement,
                        searchQuery: query.toLowerCase(),
                        topResult: topResult + ':' + topScore,
                        addtionalResults: relatedScores
                    }
                };

                _trackEvent(obj);
            } else {
                answerSection.classList.add('hide');
            }
        }

        // Helper function to load more results
        function _loadMoreResults() {
            var questionList = element.querySelectorAll('.question-list .question.hide:not(.selected-result)'),
                relatedResults = '',
                obj = {
                    eventInfo: {
                        eventComponent: (element.dataset.eventComponent).toLowerCase(),
                        eventType: (answerSection.dataset.eventType).toLowerCase(),
                        eventName: (answerSection.dataset.eventType + ' - ' + loadMoreEvent).toLowerCase(),
                        eventAction: element.dataset.eventAction,
                        eventText: loadMoreText.toLowerCase(),
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        engagementType: element.dataset.eventEngagement,
                        searchQuery: query.toLowerCase(),
                        addtionalResults: ''
                    }
                };

            // loads only 3 results per click
            questionList.forEach(function (question, index) {
                if (index <= (showResults - 1)) {
                    question.classList.remove('hide');
                    relatedResults += question.getAttribute('data-id') + ':' + question.getAttribute('data-score') + '|';
                }
            });

            // hides the load more button
            if (element.querySelector('.question-list .question.hide:not(.selected-result)') <= 1) {
                loadMore.classList.add('hide');
            }

            obj.eventInfo.addtionalResults = relatedResults.slice(0, -1);

            _trackEvent(obj);
        }

        // Helper function to display all related question from found answer on JSON response
        function _displayQuestions(questions) {
            var questionUL = relatedQuestionsList.querySelector('ul'),
                questionCount = 0;

            relatedQuestionsList.classList.remove('hide');

            // Removes all childs of the List to be replaced with new ones
            while (questionUL.firstChild) {
                questionUL.removeChild(questionUL.firstChild);
            }

            relatedScores = '';
            relatedQuestions = '';
            relatedCountries = '';
            relatedTopics = '';

            questions.forEach(function (question, index) {
                var li = d.createElement('li'),
                    h3 = d.createElement('h3'),
                    p = d.createElement('p'),
                    readMoreBtn = d.createElement('button'),
                    questionAttrs = {
                        'data-id': question.id,
                        'data-score': question.score,
                        'data-english-label': question.dataLayerQuestion
                    };

                // checks the confidence level to display the current question on the related question section
                if (questions[0] === question && confidenceLevel) {
                    EDC.utils.setClasses(li, ['hide', 'selected-result']);
                } else {
                    // Sends only top 5 results to data layer
                    relatedScores += questionCount <= 4 ? question.id + ':' + question.score + '|' : '';
                    relatedQuestions += questionCount <= 4 ? question.dataLayerQuestion + '|' : '';
                    relatedCountries += questionCount <= 4 ? question.country + '|' : '';
                    relatedTopics += questionCount <= 4 ? question.topic + '|' : '';
                    questionCount++;
                }

                // hides more results
                if (index > (showResults - 1)) {
                    li.classList.add('hide');
                }

                h3.classList.add('related-title');
                h3.innerText = question.question;

                p.classList.add('related-description');
                p.innerText = question.description;

                EDC.utils.setClasses(readMoreBtn, ['related-link', 'edc-btn-unstyled']);
                readMoreBtn.innerHTML = '<span>' + readMore + '</span>';
                EDC.utils.attachEvents(readMoreBtn, 'click', _showRelatedQuestion);

                EDC.utils.setAttributes(li, questionAttrs);
                li.classList.add('question');

                li.appendChild(h3);
                li.appendChild(p);
                li.appendChild(readMoreBtn);

                questionUL.appendChild(li);
            });

            // removes last char on relatedQuestion var
            relatedScores = relatedScores.slice(0, -1);
            relatedQuestions = relatedQuestions.slice(0, -1);
            relatedCountries = relatedCountries.slice(0, -1);
            relatedTopics = relatedTopics.slice(0, -1);

            // displayes the load more button
            if (questionUL.querySelectorAll('.question').length > 4) {
                loadMore.classList.remove('hide');
            } else {
                loadMore.classList.add('hide');
            }
        }

        // Helper function success callback from JSON response
        function _displayAnswers(data) {
            var questions = data.questions;

            element.classList.remove('processing');

            if (questions && questions.length > 0) {
                // Sets confidence level to display questions
                confidenceLevel = data.confidenceLevel === 'high' || questions.length === 1;
                questionsData = data;
                infoNoAnswer.classList.add('hide');

                // Displays related questions
                if (questions.length > 1) {
                    _displayQuestions(questions);
                } else {
                    relatedQuestionsList.classList.add('hide');
                }

                // Adds htlm of top result
                _displayCorrectQuestion(questions[0]);
                answerInfo.classList.remove('hide');

                if (ehhForm) {
                    ehhForm.classList.remove('hide');
                }
            } else {
                _showNoAnswers();
            }
        }

        // Helper function to set search values
        function _setSearchValue(value) {
            if (searchInput) {
                searchInput.value = '';
                searchInput.setAttribute('placeholder', value);
            }
        }

        // Helper function to initialize the component information
        function _initializeContent() {
            var encodedQuery;

            query = decodeURIComponent(EDC.utils.getURLParams().query); // Gets the query parameter from URL
            encodedQuery = encodeURIComponent(query)
                .replace(/\(/g, '%28')
                .replace(/\)/g, '%29')
                .replace(/\%26/g, '&')
                .replace(/\%24/g, '$')
                .replace(/\%22/g, '”'); // encode and replace parentheses and other symbols to avoid the resquest being blocked by the dispatcher

            // Checks if the URL contains any query parameters
            if (!query || query === 'undefined') {
                questionsTitle.innerText = questionsTitle.getAttribute('data-welcome');
                infoWelcome.classList.remove('hide');
                infoNoAnswer.classList.add('hide');

                if (ehhForm) {
                    ehhForm.classList.add('hide');
                }

                _showHideSections();
            } else {
                _setSearchValue(query);

                if (topResults) {
                    topResults.innerText = query;
                }

                // Show processing screen
                element.classList.add('processing');
                infoSection.classList.add('hide');
                questionsSection.classList.add('hide');
                // Calls JSON file from servlet to get the requested questions
                EDC.utils.fetchJSON(serverAction, serviceURL, encodeURIComponent('query') + '=' + encodedQuery, _displayAnswers, _showNoAnswers);
            }
        }

        function _setHistoryQuery(searchQuery) {
            // Sets the URL value on the navigation bar
            history.pushState(null, null, EDC.props.pageNameURL + '?query=' + searchQuery.replace(invalidChars, ''));
            _initializeContent();
        }

        // Helper function to display the content of any of the default questions
        function _displayDefaultQuestion(e) {
            var questionClicked = e.target.classList.contains('question') ? e.target : e.target.closest('li'),
                questionText = questionClicked ? questionClicked.querySelector('span').innerHTML : '';

            if (questionText) {
                // Changes history to avoid reloading the page
                _setHistoryQuery(questionText);
            }

            _setSearchValue(questionText);
        }

        // Attach events
        function _attachEvents() {
            var defaultQuestions = element.querySelectorAll('.default-questions'),
                loadMoreBtn = element.querySelector('.load-btn');

            EDC.utils.attachEvents(defaultQuestions, 'click', _displayDefaultQuestion);
            EDC.utils.attachEvents(loadMoreBtn, 'click', _loadMoreResults);

            // Loads content on back and next browser action
            EDC.utils.attachEvents(window, 'popstate', function () {
                _initializeContent();
            });

            // Listens for the seach component question submission
            EDC.utils.attachEvents(element, 'searchQuery', function (e) {
                _setHistoryQuery(e.detail.query);
            });
        }

        _attachEvents();
        _initializeContent();
    }

    // Public methods
    function init() {
        var ehhResultPage = document.querySelectorAll('.c-ehh-search-results.init-js:not([data-component-state="initialized"])');

        if (ehhResultPage) {
            ehhResultPage.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', ehhSearchResultsJS.init);