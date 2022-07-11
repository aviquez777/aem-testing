var fileUploaderJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            form = element.closest('form'),
            validExtensions = form ? form.getAttribute('data-file-uploader-valid-extensions') : null,
            validExtensionsArray = validExtensions ? validExtensions.split('|') : ['PDF', 'DOC', 'DOCX', 'TXT', 'JPG', 'JPEG', 'PNG'],
            maxFileSize = element.dataset.fileSizeLimit || 5,
            maxQuantityFiles = element.dataset.fileQuantityLimit || 4,
            addedFilesSection = element.querySelector('ul'),
            error = element.querySelector('span.error'),
            printCountFiles = element.querySelector('.options p span'),
            disableBtn = element.querySelector('button.discard-input'),
            maxSizeReached = false;

        function _addMiniError(el, msg) {
            var span = d.createElement('span');

            span.classList.add('li-error');
            span.setAttribute('data-error-msg', msg);
            span.innerHTML = msg;
            el.appendChild(span);
        }

        function _removeBigFile(li) {
            var fileNumber,
                span,
                inputToRemove;

            li.classList.remove('valid-file');
            li.classList.add('max-file');
            span = li.querySelector('span');
            span.classList.add('error-x');
            span.classList.remove('checkmark');

            _addMiniError(li.querySelector('.file-name'), error.getAttribute('data-big-size-file'));

            fileNumber = li.getAttribute('file-number');
            inputToRemove = element.querySelector('input[data-file-number="' + fileNumber + '"]');
            inputToRemove.classList.add('discard-input');
            inputToRemove.classList.remove('dirty');
            inputToRemove.value = null;
        }

        function _validateSize() {
            var allFilesSizes = 0;

            addedFilesSection.querySelectorAll('li.valid-file').forEach(function (fileFromList) {
                allFilesSizes += parseFloat(fileFromList.getAttribute('file-size'), 10);
                if ((allFilesSizes / 1024 / 1024) > maxFileSize) {
                    _removeBigFile(fileFromList);
                }
            });

            return ((allFilesSizes / 1024 / 1024) > maxFileSize);
        }

        function _removeFile(e) {
            var ul = e.target.closest('ul'),
                li = e.target.closest('li'),
                fileNumber = e.target.closest('li').getAttribute('file-number'),
                countFiles = addedFilesSection.querySelectorAll('li').length,
                inputToRemove = element.querySelector('input[data-file-number="' + fileNumber + '"]');

            inputToRemove.classList.add('discard-input');
            inputToRemove.classList.remove('dirty');
            inputToRemove.value = null;
            error.innerHTML = '';

            if (element.classList.contains('error')) {
                element.classList.remove('error');
            }

            ul.removeChild(li);

            if (ul.querySelectorAll('li').length === 0) {
                ul.classList.remove('show');
            }

            if (li.classList.contains('max-file')) {
                maxSizeReached = false;
            }

            if (ul.querySelectorAll('li').length <= (maxQuantityFiles - 1) && !maxSizeReached) {
                disableBtn.removeAttribute('disabled');
            }

            printCountFiles.innerHTML = (countFiles - 1) + '/' + maxQuantityFiles + ' files max';

        }

        function _checkIfFileExists(list, name, type) {
            var validValue = true;

            if (name !== '') {
                list.forEach(function (item) {
                    var el = item.querySelector('.file-name'),
                        msg = error.getAttribute('data-already-added-message'),
                        errorToRemove = item.querySelector('span[data-error-msg="' + msg + '"]');

                    if (errorToRemove) {
                        errorToRemove.parentNode.removeChild(errorToRemove);
                    }

                    if (item.getAttribute('file-name') === name && item.getAttribute('file-type') === type) {
                        validValue = false;
                        _addMiniError(el, msg);
                    }
                });
            }
            return validValue;
        }

        function _getFileExt(fileExt, validFileExt) {
            var imageExt = '';

            if (fileExt === 'pdf' || fileExt === 'doc' || fileExt === 'txt') {
                imageExt = fileExt;
            } else if (fileExt === 'docx') {
                imageExt = 'doc';
            } else if (fileExt === 'jpg' || fileExt === 'jpeg' || fileExt === 'png' || fileExt === 'xls' || fileExt === 'xlsx' || fileExt === 'tif' || !validFileExt) {
                imageExt = 'unidentified';
            }

            return imageExt;
        }

        function _checkFileName(fileName) {
            var regex = /^[a-zA-Z\u00C0-\u024F\u1E00-\u1EFF0-9](?:[a-zA-Z\u00C0-\u024F\u1E00-\u1EFF0-9 ._-]*[a-zA-Z\u00C0-\u024F\u1E00-\u1EFF0-9])?\.[a-zA-Z\u00C0-\u024F\u1E00-\u1EFF0-9_-]+$/;

            return regex.test(fileName) && fileName.length <= 255;
        }

        function _uploadFile(e) {
            var fileExt,
                fileName,
                fileNameSplit,
                fileSize,
                sizeInMb,
                validFileExt = false,
                validFileName = false,
                addedFiles = addedFilesSection.querySelectorAll('li'),
                buttonImg = addedFilesSection.getAttribute('data-button-image'),
                btn = '<button type="button" class="no-btn"><span class="remove-file"><img src="' + buttonImg + '"/></span></button>',
                target = e.target,
                incomingFiles = target.files,
                fileUploaderInput = element.querySelector('input.discard-input:not(.dirty)'),
                li,
                span,
                img = '',
                countFilesLi = addedFilesSection.querySelectorAll('li').length,
                imageExt,
                alreadyExists = false;

            e.preventDefault();
            if (incomingFiles.length > 0) {
                fileName = incomingFiles[0] ? incomingFiles[0].name : '';
                fileNameSplit = fileName.split('.');
                fileExt = fileNameSplit[fileNameSplit.length - 1].toLowerCase();
                fileSize = incomingFiles[0] ? incomingFiles[0].size : '';
                sizeInMb = (fileSize / (1024 * 1024)).toFixed(2);

                validExtensionsArray.forEach(function (ext) {
                    if (fileExt.toLowerCase() === ext.toLowerCase()) {
                        validFileExt = true;
                    }
                });

                validFileName = _checkFileName(fileName);
                li = d.createElement('li');
                span = d.createElement('span');
                span.classList.add('file-name');
                span.innerHTML = fileName + ' (' + sizeInMb + 'MB)';
                element.classList.remove('error');
                error.innerHTML = '';

                if (_checkIfFileExists(addedFiles, fileName, fileExt)) {
                    imageExt = _getFileExt(fileExt, validFileExt);

                    // check if same file exist on other file uploaders
                    alreadyExists = document.querySelectorAll('li[file-name="' + fileName + '"]').length !== 0;
                    // remmove from imput
                    if (alreadyExists) {
                        fileUploaderInput.value = null;
                    } else {
                        fileUploaderInput.classList.add('dirty');
                    }

                    if (!validFileExt || !validFileName || alreadyExists) {
                        img = '<span class="error-x"></span><img src="/etc.clientlibs/edc/clientlibs/clientlib-base/resources/images/file-upload-' + imageExt + '.svg" alt="file to upload" />';
                        _addMiniError(span, error.getAttribute(!validFileExt ? 'data-file-type' : !validFileName ? 'data-file-name' : 'data-already-added-message'));
                    } else {
                        fileUploaderInput.classList.remove('discard-input');
                        img = '<span class="checkmark"></span><img src="/etc.clientlibs/edc/clientlibs/clientlib-base/resources/images/file-upload-' + imageExt + '.svg" alt="file to upload" />';
                        li.classList.add('valid-file');
                    }

                    printCountFiles.innerHTML = (countFilesLi + 1) + '/' + maxQuantityFiles + ' files max';
                    li.innerHTML += img;
                    li.appendChild(span);
                    li.innerHTML += btn;
                    li.setAttribute('file-type', fileExt);
                    li.setAttribute('file-number', fileUploaderInput.getAttribute('data-file-number'));
                    li.setAttribute('file-name', fileName);
                    li.setAttribute('file-size', fileSize);
                    EDC.utils.attachEvents(li.querySelector('button'), 'click', _removeFile);

                    if (!addedFilesSection.classList.contains('show')) {
                        addedFilesSection.classList.add('show');
                    }

                    addedFilesSection.appendChild(li);

                    if (countFilesLi === (maxQuantityFiles - 1)) {
                        disableBtn.setAttribute('disabled', 'disabled');
                    }

                    if (_validateSize()) {
                        disableBtn.setAttribute('disabled', 'disabled');
                        // this flag will be used to eneble the button, is cleared untill the big file is removed
                        maxSizeReached = true;
                    }
                } else {
                    element.querySelectorAll('input[type="file"]').forEach(function (input, index) {
                        if (index + 1 > element.querySelectorAll('input[type="file"].dirty:not(.discard-input)').length) {
                            input.value = '';
                        }
                    });
                }
            }
        }

        function _triggerFileUploader(e) {
            var fileUploaderInputList = element.querySelectorAll('input:not(.discard-input):not(.dirty)'),
                fileUploaderInput = element.querySelector('input.discard-input:not(.dirty)');

            if (fileUploaderInputList.length < maxFileSize && (e.type === 'click' || (e.type === 'keypress' && e.keyCode === EDC.props.enterKeyCode))) {
                e.preventDefault();
                fileUploaderInputList.forEach(function (input) {
                    input.removeEventListener('input', _uploadFile);
                });
                EDC.utils.attachEvents(fileUploaderInput, 'input', _uploadFile);
                if (fileUploaderInput) {
                    fileUploaderInput.click();
                }
            }
        }

        // Attach events
        function _attachEvents() {
            var fileUploaderBtn = element.querySelector('button');

            EDC.utils.attachEvents(fileUploaderBtn, 'keypress', _triggerFileUploader);
            EDC.utils.attachEvents(fileUploaderBtn, 'click', _triggerFileUploader);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var fileUploader = document.querySelectorAll('.c-file-uploader:not([data-component-state="initialized"])');

        if (fileUploader) {
            fileUploader.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', fileUploaderJS.init);