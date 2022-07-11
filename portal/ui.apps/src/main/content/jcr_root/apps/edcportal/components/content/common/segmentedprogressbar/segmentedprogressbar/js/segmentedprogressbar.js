var segmentedProgressBarJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            form = element.closest('form'),
            formBtn = form ? form.querySelector('button.button.next-btn') : null,
            levelsContainer = form ? form.querySelector('.levels') : null,
            levels = levelsContainer ? levelsContainer.querySelectorAll('.level') : null,
            levelsCount = levels ? levels.length : null,
            progressSegment = element.querySelector('.progress-segment'),
            progressItems,
            dataSegments = element.getAttribute('data-segments'),
            dataCurrentStep = element.getAttribute('data-current-step'),
            progressTitle = element.getAttribute('data-progress-title'),
            progressStep = element.getAttribute('data-progress-step'),
            progressOf = element.getAttribute('data-progress-of'),
            titleSection = element.querySelector('.multistep-title'),
            titleStep = titleSection ? titleSection.querySelector('.title-step') : null,
            titleCurrent = titleSection ? titleSection.querySelector('.title-current') : null,
            titleOf = titleSection ? titleSection.querySelector('.title-of') : null,
            titleTotal = titleSection ? titleSection.querySelector('.title-total') : null,
            titleText = titleSection ? titleSection.querySelector('.title-text') : null;

        // Private methods
        function _selectProgressItem() {
            var currentStep = 0,
                i;

            if (dataSegments && dataCurrentStep) {
                progressItems[dataCurrentStep - 1].classList.add('selected');

                if (titleSection) {
                    currentStep = dataCurrentStep;
                    titleCurrent.textContent = dataCurrentStep;
                    titleTotal.textContent = dataSegments;
                    titleText.textContent = progressTitle ? progressTitle : '';
                }
            } else if (progressItems) {
                setTimeout(function () {
                    var thisLevel,
                        selectedIndex = 0;

                    levels.forEach(function (level, index) {
                        if (!level.classList.contains('hide')) {
                            selectedIndex = index;
                            currentStep = index + 1;
                        }
                    });

                    thisLevel = levels[selectedIndex];

                    if (titleSection && thisLevel) {
                        titleCurrent.textContent = (currentStep);
                        titleTotal.textContent = levelsCount;
                        titleText.textContent = thisLevel.getAttribute('data-level-title') ? thisLevel.getAttribute('data-level-title') : '';
                    }
                }, 0);
            }

            setTimeout(function () {
                for (i = 0; i < currentStep; i++) {
                    progressItems[i].classList.add('selected');
                }
            }, 0);

            titleSection.classList.remove('hide');
        }

        function _createStep() {
            var div = d.createElement('div');

            div.classList.add('item');
            return div;
        }

        function _renderSteps(count) {
            var i;

            for (i = 0; i < count; i++) {
                progressSegment.appendChild(_createStep());
            }

            if (titleSection) {
                titleStep.textContent = progressStep;
                titleOf.textContent = progressOf;
            }

            progressItems = progressSegment.querySelectorAll('.item');
            _selectProgressItem();
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(formBtn, 'click', _selectProgressItem);
        }

        if (dataSegments && dataCurrentStep) {
            _renderSteps(dataSegments);
        } else if (formBtn && levelsCount && levelsCount > 0) {
            _attachEvents();
            _renderSteps(levelsCount);
        }
    }

    // Public methods
    function init() {
        var segmentedProgressBars = document.querySelectorAll('.c-segmented-progress-bar:not([data-component-state="initialized"])');

        if (segmentedProgressBars) {
            segmentedProgressBars.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', segmentedProgressBarJS.init);