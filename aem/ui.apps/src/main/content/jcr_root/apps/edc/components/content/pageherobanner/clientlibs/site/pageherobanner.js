var PageHeroBannerYT = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            isNewWebinar = element.hasAttribute('data-webinar-id'),
            isWebinarPage = element.getAttribute('data-is-webinar-page') === 'true',
            webinarStatus,
            webinarStatusUrl = element.getAttribute('data-webinar-status-url') || '',
            webinarRegisterUrl = element.getAttribute('data-webinar-register-url') || '',
            webinarStatusDataFromService = {},
            webinarRegisterDataFromService = {},
            thisWebinarCtas = element.querySelector('.c-webinar-ctas'),
            webinarCtasSection = d.querySelectorAll('.c-webinar-ctas'),
            webinarCtas = [],
            webinarOverride = element.getAttribute('data-webinar-override'),
            userLogged = false,
            statusText = element.querySelector('.status-text'),
            btnContainer = element.querySelector('.btn-container'),
            videoBtn = element.querySelector('.edc-video-btn') ? element.querySelector('.edc-video-btn') : element.querySelector('.edc-primary-btn-video'),
            imageBtn = element.querySelector('.edc-primary-btn'),
            playBtn = element.querySelector('.start-video'),
            videoModal = element.querySelector('.video-modal'),
            videoClose = element.querySelector('.video-close button'),
            video = element.querySelector('.video'),
            buttonText,
            buttonAnchor,
            btnsToTrack = element.querySelectorAll('.c-webinar-ctas .webinar-cta:not(.not-logged-in)'),
            arkadinModal = element.querySelector('.arkadin-modal'),
            arkadinModalText = arkadinModal ? arkadinModal.querySelector('.modal-text') : null,
            arkadinModalCloseBtn = arkadinModal ? arkadinModal.querySelector('.modal-close') : null,
            arkadinModalAgreeBtn = arkadinModal ? arkadinModal.querySelector('button.edc-primary-btn') : null,
            arkadinModalCancelBtn = arkadinModal ? arkadinModal.querySelector('button.no-btn') : null,
            arkadinModalAnchor = arkadinModal ? arkadinModal.querySelector('.anchor-container') : null,
            mainErrorMsg = d.querySelector('.c-message-banner'),
            ctasErrorMsg = thisWebinarCtas ? thisWebinarCtas.querySelector('.webinar-ctas-error') : null,
            firstLoad = true,
            disclaimer = d.querySelector('.disclaimer'),
            userJustRegistered = false,
            modalArkadinText = arkadinModalText ? arkadinModalText.querySelector('.text-section .arkadin-text') : null,
            modalSpecialText = arkadinModalText ? arkadinModalText.querySelector('.text-section .special-text') : null,
            modalSpecialTextExists = modalSpecialText ? modalSpecialText.getAttribute('show-special-text') === 'true' : null,
            modalTotalText = arkadinModalText ? arkadinModalText.querySelector('.text-section .total-text') : null,
            modalTotalTextExists = modalTotalText ? modalTotalText.getAttribute('show-total-text') === 'true' : null,
            witSection = element.querySelector('.wit-section'),
            witRadios = witSection ? witSection.querySelectorAll('input[type="radio"]') : null,
            radioClicked = false,
            radioChanged = false,
            containerHeight;

        // Data Layer
        function _trackEvent(e, data) {
            var obj = {
                eventInfo: {
                    eventComponent: data.comp,
                    eventType: data.type,
                    eventName: data.name,
                    eventText: data.text,
                    eventPageName: EDC.utils.getPageName(),
                    eventAction: element.dataset.eventAction,
                    eventPage: EDC.props.pageNameURL,
                    engagementType: data.engagement ? data.engagement : '',
                    eventLevel: data.level ? data.level : ''
                }
            };

            if (data.destinationPage) {
                obj.eventInfo.destinationPage = data.destinationPage;
            }

            EDC.utils.dataLayerTracking(obj);
        }

        // General PageHeroBanner functions
        // If video player is active share bar has to be hide
        function _shareBarHideShow(action) {
            var sharebars = document.querySelectorAll('.addthis-smartlayers');

            sharebars.forEach(function (share) {
                if (action === 'hide') {
                    share.classList.add('video-active');
                } else if (action === 'show') {
                    share.classList.remove('video-active');
                }
            });
        }

        function _closeVideo(e) {
            e.preventDefault();
            PubSub.publish('stop-video', true);

            if (videoModal) {
                if (videoModal.classList.contains('show')) {
                    videoModal.classList.remove('show');
                }
            }

            if (video) {
                if (video.classList.contains('show')) {
                    video.classList.remove('show');
                }
            }

            _shareBarHideShow('show');
        }

        // Private methods
        function _playVideo(e) {
            e.preventDefault();

            if (videoModal) {
                if (!videoModal.classList.contains('touched')) {
                    videoModal.classList.add('touched');
                    playBtn.click();
                } else {
                    setTimeout(function () {
                        playBtn.click();
                    }, 300);
                }

                if (!videoModal.classList.contains('show')) {
                    videoModal.classList.add('show');
                }

                _shareBarHideShow('hide');
            }
        }

        function _imageBtnClicked(e) {
            var anchorTarget;

            _trackEvent(e, {
                comp: 'hero banner',
                type: 'button',
                name: 'button click - ' + buttonText,
                text: buttonText,
                destinationPage: '#',
                engagement: element.dataset.eventEngagement,
                level: element.dataset.eventLevel
            });

            if (buttonAnchor) {
                anchorTarget = document.querySelector('a.anchor[data-target="' + buttonAnchor + '"]');

                if (anchorTarget) {
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(anchorTarget).y, 1000);
                }
            }
        }

        // Set buttons based on component configuration
        function _setElementData() {
            if (videoBtn) {
                buttonText = videoBtn.querySelector('span').textContent;
            }

            if (imageBtn) {
                buttonText = imageBtn.getAttribute('aria-label') ? imageBtn.getAttribute('aria-label') : imageBtn.querySelector('span').textContent;
                buttonAnchor = imageBtn.getAttribute('data-target');
            }
        }

        function _detectEsc(e) {
            if (e.keyCode === EDC.props.escapeKeyCode) {
                _closeVideo(e);
            }
        }

        // Webinar functions
        function _webinarBtnClicked(e) {
            var target = e.target.tagName.toLowerCase() === 'span' ? e.target.parentNode : e.target,
                ctas = target.closest('.c-webinar-ctas'),
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent.toLowerCase(),
                        eventType: ctas.dataset.eventType.toLowerCase(),
                        eventName: ctas.dataset.eventName.toLowerCase(),
                        eventAction: element.dataset.eventAction.toLowerCase(),
                        eventText: target.querySelector('span').textContent.toLowerCase(),
                        eventValue: element.dataset.eventValue.toLowerCase(),
                        eventPage: EDC.utils.getPageName(),
                        destinationPage: target.href || '',
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: ctas.dataset.eventLevel.toLowerCase(),
                        eventPageName: EDC.props.pageNameURL
                    }
                };

            EDC.utils.dataLayerTracking(obj);

            // post webinar traffic src bug 95588 only if registered
            if (EDC.edctransactions && webinarRegisterDataFromService.userRegistered) {
                EDC.edctransactions.postEdcTransaction();
            }
        }

        function _trackWebinarRegistrationError() {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent.toLowerCase(),
                    eventType: ctasErrorMsg ? ctasErrorMsg.dataset.eventType.toLowerCase() : '',
                    eventName: ctasErrorMsg ? ctasErrorMsg.dataset.eventName.toLowerCase() : '',
                    eventAction: element.dataset.eventAction.toLowerCase(),
                    eventText: ctasErrorMsg ? ctasErrorMsg.textContent.toLowerCase() : '',
                    eventPage: EDC.utils.getPageName(),
                    eventPageName: EDC.props.pageNameURL
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _userIsLogged(userStatus) {
            if (userStatus) {
                element.classList.add('user-logged');
                element.setAttribute('data-external-id', EDC.props.userData.externalID);
            }
        }

        function _showPageLoadErrorMsg(type, msg) {
            var loadErr = mainErrorMsg ? mainErrorMsg.querySelector('.content-info p.load-error') : null,
                gdprErr = mainErrorMsg ? mainErrorMsg.querySelector('.content-info p.gdpr-error') : null;

            if (mainErrorMsg) {
                mainErrorMsg.classList.remove('hide');

                if (!type || type === 'error') {
                    webinarCtasSection.forEach(function (el) {
                        el.classList.add('hide');
                    });

                    if (loadErr) {
                        loadErr.classList.remove('hide');
                    }

                    if (gdprErr) {
                        gdprErr.classList.add('hide');
                    }

                    if (msg && loadErr) {
                        loadErr.textContent = msg;
                    }
                } else if (type === 'gdpr') {
                    if (loadErr) {
                        loadErr.classList.add('hide');
                    }

                    if (gdprErr) {
                        gdprErr.classList.remove('hide');
                    }

                    PubSub.subscribe('user-status', function (name, userStatus) {
                        userLogged = userStatus;
                        _userIsLogged(userStatus);

                        if (userLogged) {
                            webinarCtasSection.forEach(function (el) {
                                el.querySelectorAll('.webinar-cta').forEach(function (subEl) {
                                    if (subEl.classList.contains('logged-in-not-registered')) {
                                        subEl.querySelector('button span').textContent = subEl.querySelector('button').getAttribute('data-upcoming-text');
                                        subEl.classList.remove('hide');
                                    } else {
                                        subEl.classList.add('hide');
                                    }
                                });
                            });
                        }

                        webinarCtasSection.forEach(function (el) {
                            el.querySelectorAll('.webinar-cta').forEach(function (subEl) {
                                var btn;

                                if (!subEl.classList.contains('hide')) {
                                    btn = subEl.querySelector('a.button') ? subEl.querySelector('a.button') : subEl.querySelector('button');
                                    btn.setAttribute('disabled', 'disabled');
                                }
                            });
                        });
                    });
                }
            }
            _trackWebinarRegistrationError();
        }

        function _changeTxt(el, status) {
            if (el.length) {
                el.forEach(function (subEl) {
                    if (subEl.querySelector('span')) {
                        subEl.querySelector('span').textContent = subEl.getAttribute('data-' + status + '-text');
                    } else {
                        subEl.textContent = subEl.getAttribute('data-' + status + '-text');
                    }
                });
            } else if (el.querySelector('span')) {
                el.querySelector('span').textContent = el.getAttribute('data-' + status + '-text');
            } else {
                el.textContent = el.getAttribute('data-' + status + '-text');
            }
        }

        function _replaceStatusClassLists(el, finalStatus) {
            var statusList = ['live', 'ondemand', 'upcoming'];

            statusList.forEach(function (status) {
                el.classList.remove(status);
            });

            element.classList.add(finalStatus);
        }

        function _showTagTxt() {
            statusText.classList.remove('hidden');
        }

        function _handleTagTxtChanges(status) {
            _replaceStatusClassLists(element, status);
            _changeTxt(statusText, status);
        }

        function _handleCtasTxtChanges(status) {
            webinarCtas.forEach(function (item) {
                _changeTxt(item, status);
            });
        }

        function _handleUserRegistration(webinarUrl) {
            element.classList.add('user-registered');
            webinarCtasSection.forEach(function (el) {
                el.querySelectorAll('.webinar-cta').forEach(function (subEl) {
                    if (subEl.classList.contains('logged-in-registered')) {
                        if (webinarUrl) {
                            subEl.querySelector('a').href = webinarUrl;
                        }
                        subEl.classList.remove('hide');
                    } else {
                        subEl.classList.add('hide');
                    }
                });
            });
        }

        function _toggleWebinarCtasError(action) {
            webinarCtasSection.forEach(function (el) {
                if (el.querySelector('.webinar-ctas-error')) {
                    if (action === 'hide') {
                        el.querySelector('.webinar-ctas-error').classList.add('hide');
                    } else {
                        el.querySelector('.webinar-ctas-error').classList.remove('hide');
                    }
                }
            });
        }

        function _hideWebinarCtasError() {
            _toggleWebinarCtasError('hide');
        }

        function _showWebinarCtasError() {
            _toggleWebinarCtasError('show');
        }

        function _arkadinModalScrolled(e) {
            var target = e.target,
                scrollSize = target.scrollHeight - target.offsetHeight,
                newScrollTop = Math.round(target.scrollTop);

            if (newScrollTop + 1 < scrollSize) {
                arkadinModalAnchor.classList.remove('hide');
            } else {
                arkadinModalAnchor.classList.add('hide');
                arkadinModalAgreeBtn.classList.remove('disabled');
                arkadinModalAgreeBtn.removeAttribute('disabled');
            }

            if (newScrollTop > 0 && newScrollTop < scrollSize) {
                arkadinModalText.classList.add('shadow-after');
            } else if (newScrollTop === 0) {
                arkadinModalText.classList.add('shadow-after');
            } else if (newScrollTop === scrollSize) {
                arkadinModalText.classList.remove('shadow-after');
            }
        }

        function _innerScrollToBottom(el) {
            var start = el.scrollTop ? el.scrollTop : 0,
                change = el.querySelector('.full-section') ? parseInt(window.getComputedStyle(el.querySelector('.full-section')).getPropertyValue('height'), 10) - start : 0,
                currentTime = 0,
                increment = 20,
                duration = 600;

            function _animateScroll() {
                var val;

                currentTime += increment;
                val = Math.easeInOutQuad(currentTime, start, change, duration);
                el.scrollTop = val;

                if (currentTime < duration) {
                    setTimeout(_animateScroll, increment);
                }
            }

            Math.easeInOutQuad = function (t, b, c, g) {
                t /= g / 2;
                if (t < 1) {
                    return c / 2 * t * t + b;
                }

                t--;
                return -c / 2 * (t * (t - 2) - 1) + b;
            };

            _animateScroll();
        }

        function _isIE() {
            var ua = window.navigator.userAgent,
                msie = ua.indexOf('MSIE ');

            return msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./);
        }

        function _ifResize() {
            var modalCtas = arkadinModal.querySelector('.modal-ctas'),
                textSection = arkadinModalText.querySelector('.text-section'),
                modalTitle = arkadinModal.querySelector('.modal-title'),
                ctasHeight = parseInt(window.getComputedStyle(modalCtas).getPropertyValue('height'), 10),
                titleHeight = parseInt(window.getComputedStyle(modalTitle).getPropertyValue('height'), 10),
                screenHeight = window.innerHeight,
                isIE = _isIE(),
                maxHeight = screenHeight - ctasHeight - titleHeight - (isIE ? 200 : 0);

            if (containerHeight >= screenHeight) {
                arkadinModalAnchor.classList.remove('hide');
                arkadinModalText.classList.add('section-fixed');
                EDC.utils.attachEvents(textSection, 'scroll', _arkadinModalScrolled);
                EDC.utils.attachEvents(arkadinModalAnchor.querySelector('.circle-button'), 'click', function () {
                    _innerScrollToBottom(arkadinModalText.querySelector('.text-section'));
                });
                textSection.style.maxHeight = maxHeight + 'px';
                textSection.style.overflowY = 'auto';
                arkadinModalAgreeBtn.classList.add('disabled');
                arkadinModalAgreeBtn.setAttribute('disabled', 'disabled');
            } else {
                textSection.style.maxHeight = '';
                textSection.style.overflowY = 'none';
                arkadinModalAnchor.classList.add('hide');
                arkadinModalText.classList.remove('section-fixed');
                EDC.utils.unAttachEvents(textSection, 'scroll', _arkadinModalScrolled);
                EDC.utils.unAttachEvents(arkadinModalAnchor.querySelector('.circle-button'), 'click', function () {
                    textSection.scrollTop = textSection.scrollHeight;
                });
            }
        }

        function _showArkadinModal() {
            var accountProvisioned = webinarRegisterDataFromService.accountProvisioned,
                userRegistered = webinarRegisterDataFromService.userRegistered,
                caslSection = element.querySelector('.casl-section');

            if (webinarStatus === 'ondemand' ||
                (!accountProvisioned && !userRegistered && !modalTotalTextExists) ||
                (accountProvisioned && userRegistered && modalTotalTextExists)) {
                modalArkadinText.classList.remove('hide');
                modalTotalText.classList.add('hide');
                if (modalSpecialTextExists) {
                    modalSpecialText.classList.add('hide');
                }
                if (caslSection) {
                    caslSection.parentNode.removeChild(caslSection);
                }
            } else if (!accountProvisioned && !userRegistered && modalTotalTextExists) {
                modalArkadinText.classList.add('hide');
                modalTotalText.classList.remove('hide');
                if (modalSpecialTextExists) {
                    modalSpecialText.classList.add('hide');
                }
            } else if (accountProvisioned && !userRegistered && modalSpecialTextExists) {
                modalArkadinText.classList.add('hide');
                modalSpecialText.classList.remove('hide');
                if (modalTotalTextExists) {
                    modalTotalText.classList.add('hide');
                }
            }

            _hideWebinarCtasError();

            if (arkadinModal) {
                arkadinModal.classList.remove('hide');
                document.querySelector('body').classList.add('no-scroll');
                containerHeight = parseInt(window.getComputedStyle(arkadinModal.querySelector('.modal-container')).getPropertyValue('height'), 10);

                if (firstLoad) {
                    _ifResize();
                    firstLoad = false;
                    EDC.utils.attachEvents(window, 'resize', _ifResize);
                }
            } else {
                _showWebinarCtasError();
            }
        }

        function _closeArkadinModal() {
            arkadinModal.classList.add('hide');
            document.querySelector('body').classList.remove('no-scroll');
        }

        function _registerUser() {
            var form = element.querySelector('form[name="webinar-modal-form"]');

            _hideWebinarCtasError();
            EDC.utils.fetchJSON('POST', webinarRegisterUrl, EDC.forms.getFormData(form), function (webinarData) {
                if (!userJustRegistered) {
                    if (webinarData && Object.keys(webinarData).length > 0 && webinarData.userRegistered && webinarData.errorMessage === null) {
                        userJustRegistered = true;
                        _handleUserRegistration(webinarData.webinarUrl);
                        webinarRegisterDataFromService = webinarData;
                        PubSub.publish('webinar-register-data', true);
                    } else {
                        _trackWebinarRegistrationError();
                        _showWebinarCtasError();
                        PubSub.publish('webinar-register-data', false);
                    }
                    _closeArkadinModal();
                }
            }, function () {
                _trackWebinarRegistrationError();
                _showWebinarCtasError();
            });
        }

        function _handleWebinarStatusData(webinarStatusData) {
            webinarStatus = webinarStatusData.timeStatus;

            if (webinarStatus) {
                element.setAttribute('data-event-value', webinarStatus);
                _handleTagTxtChanges(webinarStatus);
                _handleCtasTxtChanges(webinarStatus);
            }
        }

        function _handleWebinarRegisterData(webinarRegisterData) {
            var isUserRegistered = webinarRegisterData.userRegistered,
                accountProvisioned = webinarRegisterData.accountProvisioned,
                webinarUrl = webinarRegisterData.webinarUrl,
                sessionCompleted = webinarRegisterData.sessionId,
                webinarTimeStatus = webinarRegisterData.timeStatus || webinarStatusDataFromService.timeStatus,
                ctasForRegistered = [];

            webinarCtasSection.forEach(function (el) {
                ctasForRegistered.push(el.querySelectorAll('.webinar-cta.logged-in-registered'));
            });

            if (webinarTimeStatus) {
                _handleCtasTxtChanges(webinarTimeStatus);
                _handleTagTxtChanges(webinarTimeStatus);
                _showTagTxt();

                if (webinarTimeStatus === 'upcoming') {
                    webinarCtasSection.forEach(function (el) {
                        el.querySelectorAll('.webinar-cta button').forEach(function (subEl) {
                            subEl.classList.remove('edc-primary-btn-video');
                            subEl.classList.add('edc-primary-btn');
                        });
                    });

                    if (isUserRegistered) {
                        ctasForRegistered.forEach(function (el) {
                            el.forEach(function (subEl) {
                                subEl.querySelector('a').classList.add('hide');
                                subEl.querySelector('p').classList.remove('hide');
                            });
                        });
                    }
                }
            }

            if (userLogged) {
                if (!sessionCompleted || !accountProvisioned || !isUserRegistered || !webinarUrl) {
                    // User not ready to watch webinar -> Proceed to find out why
                    element.classList.remove('user-registered');

                    if (!sessionCompleted) {
                        // User hasn't completed it's MyEDC profile -> Set button for profile complation
                        webinarCtasSection.forEach(function (el) {
                            el.querySelectorAll('.webinar-cta').forEach(function (subEl) {
                                if (subEl.classList.contains('logged-in-not-registered')) {
                                    el.querySelector('.not-logged-in').classList.remove('hide');
                                    el.querySelector('.not-logged-in > div').classList.add('hide');
                                    el.querySelector('.not-logged-in a').href += '_!__!__!_webinarregpending';
                                    el.querySelector('.not-logged-in a span').textContent = subEl.textContent;
                                } else {
                                    subEl.classList.add('hide');
                                }
                            });
                        });
                    } else if (!accountProvisioned || !isUserRegistered) {
                        webinarCtasSection.forEach(function (el) {
                            el.querySelectorAll('.webinar-cta').forEach(function (subEl) {
                                if (subEl.classList.contains('logged-in-not-registered')) {
                                    if (!accountProvisioned || (!isUserRegistered && modalSpecialTextExists && webinarTimeStatus !== 'ondemand')) {
                                        // User not registered to arkadin -> Set button for modal functionality
                                        EDC.utils.attachEvents(subEl.querySelector('button'), 'click', _showArkadinModal);
                                    } else if (!isUserRegistered) {
                                        // User not registered to the webinar -> Set button for register functionality
                                        EDC.utils.attachEvents(subEl.querySelector('button'), 'click', _registerUser);
                                    }
                                    subEl.classList.remove('hide');
                                } else {
                                    subEl.classList.add('hide');
                                }
                            });
                        });
                    } else if (webinarTimeStatus === 'upcoming') {
                        // No webinar url provided, but it's an upcoming event -> Hide buttons and show registered message
                        _handleUserRegistration(webinarUrl);
                    } else {
                        // Webinar url not provided -> Show load error message
                        _handleUserRegistration(webinarUrl);
                        _showPageLoadErrorMsg();
                    }
                } else {
                    // User ready to watch webinar -> Set button to show Webinar
                    _handleUserRegistration(webinarUrl);

                    if (userJustRegistered && webinarTimeStatus !== 'upcoming') {
                        element.querySelector('.webinar-cta.logged-in-registered a').click();
                    }
                }
            }
        }

        function _radioClicked(e) {
            radioClicked = true;

            setTimeout(function () {
                if (radioClicked && !radioChanged) {
                    e.target.checked = false;
                }
                radioClicked = false;
            }, 100);
        }

        function _radioChanged() {
            radioChanged = true;

            setTimeout(function () {
                radioChanged = false;
            }, 150);
        }

        function _attachEvents() {
            EDC.utils.attachEvents(videoBtn, 'click', _playVideo);
            EDC.utils.attachEvents(imageBtn, 'click', _imageBtnClicked);
            EDC.utils.attachEvents(videoClose, 'click', _closeVideo);
            EDC.utils.attachEvents(document, 'keyup', _detectEsc);
            EDC.utils.attachEvents(arkadinModalCloseBtn, 'click', _closeArkadinModal);
            EDC.utils.attachEvents(arkadinModalCancelBtn, 'click', _closeArkadinModal);
            EDC.utils.attachEvents(arkadinModalAgreeBtn, 'click', _registerUser);
            if (witRadios) {
                witRadios.forEach(function (radio) {
                    EDC.utils.attachEvents(radio, 'click', _radioClicked);
                    EDC.utils.attachEvents(radio, 'change', _radioChanged);
                });
            }
        }

        function _isWebinarPage() {
            if (isWebinarPage) {
                if (disclaimer) {
                    disclaimer.classList.add('webinar-spacing');
                }
                // When user status is retrieved proceed with the webinar status service call
                EDC.utils.fetchJSON('GET', webinarStatusUrl, '', function (webinarStatusData) {
                    if (webinarStatusData && Object.keys(webinarStatusData).length > 0 && webinarStatusData.errorMessage === null) {
                        webinarStatusDataFromService = webinarStatusData;
                        PubSub.publish('webinar-status-data', true);
                    } else {
                        PubSub.publish('webinar-status-data', false);
                        _showPageLoadErrorMsg('error', webinarStatusData.errorMessage);
                    }
                }, function () {
                    _showPageLoadErrorMsg();
                });

                PubSub.subscribe('webinar-status-data', function (statusReady) {
                    var status = webinarStatusDataFromService.timeStatus;

                    if (!webinarStatus) {
                        if (statusReady && Object.keys(webinarStatusDataFromService).length > 0) {
                            if (status === 'ondemand' || status === 'live' || status === 'upcoming') {
                                _handleWebinarStatusData(webinarStatusDataFromService);
                            } else {
                                _showPageLoadErrorMsg();
                            }
                        }
                    }
                });
            }
        }

        function _getCookie(name) {
            var v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');

            return v ? v[2] : null;
        }

        function _isNewWebinar() {
            var optanonConsentCookie = decodeURIComponent(_getCookie('OptanonConsent')),
                gdprCookieName = 'groups=',
                gdprGroups = optanonConsentCookie ? optanonConsentCookie.substring(optanonConsentCookie.indexOf(gdprCookieName) + gdprCookieName.length, optanonConsentCookie.length) : '',
                gdprBuckets = optanonConsentCookie && gdprGroups ? decodeURIComponent(gdprGroups.substring(0, gdprGroups.indexOf('&'))) : '',
                gdprValuesArray = gdprBuckets.split(','),
                isEUUserCookieApproved = 0,
                i;

            if (isNewWebinar) {
                if (optanonConsentCookie && gdprValuesArray.length >= 3) {
                    for (i = 0; i < 4; i++) {
                        if (gdprValuesArray[i] && gdprValuesArray[i].substring(gdprValuesArray[i].indexOf(':') + 1, gdprValuesArray[i].length) === '1') {
                            isEUUserCookieApproved++;
                        }
                    }
                }

                if (!EDC.nonEU && isEUUserCookieApproved < 4) {
                    _showPageLoadErrorMsg('gdpr');
                } else if (webinarOverride === 'true') {
                    if (btnContainer) {
                        btnContainer.classList.add('show');
                    }
                    webinarCtasSection.forEach(function (el) {
                        el.parentNode.removeChild(el);
                    });
                } else {
                    // Getting user status and data
                    PubSub.subscribe('user-status', function (name, userStatus) {
                        userLogged = userStatus;
                        _userIsLogged(userStatus);

                        if (userLogged) {
                            EDC.utils.fetchJSON('GET', webinarRegisterUrl, '', function (webinarRegisterData) {
                                if (webinarRegisterData && Object.keys(webinarRegisterData).length > 0 && webinarRegisterData.errorMessage === null) {
                                    webinarRegisterDataFromService = webinarRegisterData;
                                    PubSub.publish('webinar-register-data', true);
                                } else {
                                    PubSub.publish('webinar-register-data', false);
                                    _showPageLoadErrorMsg('error', webinarRegisterData.errorMessage);
                                }
                            }, function () {
                                _showPageLoadErrorMsg();
                            });
                        } else {
                            _showTagTxt();
                            webinarCtasSection.forEach(function (el) {
                                el.classList.remove('hide');
                            });
                        }
                    });

                    // Getting webinar register data from service
                    PubSub.subscribe('webinar-register-data', function (registerReady) {
                        if (registerReady && Object.keys(webinarRegisterDataFromService).length > 0) {
                            _handleWebinarRegisterData(webinarRegisterDataFromService);

                            EDC.utils.attachEvents(btnsToTrack, 'click', _webinarBtnClicked);

                            webinarCtasSection.forEach(function (el) {
                                el.classList.remove('hide');
                            });
                        }
                    });

                    webinarCtasSection.forEach(function (el) {
                        webinarCtas.push(el.querySelectorAll('.webinar-cta > button, .webinar-cta > a'));
                    });

                    if (btnContainer) {
                        btnContainer.parentNode.removeChild(btnContainer);
                    }
                }
            } else {
                if (thisWebinarCtas) {
                    thisWebinarCtas.parentNode.removeChild(thisWebinarCtas);
                }

                if (arkadinModal) {
                    arkadinModal.parentNode.removeChild(arkadinModal);
                }

                if (mainErrorMsg) {
                    mainErrorMsg.parentNode.removeChild(mainErrorMsg);
                }
            }
        }

        // Initial code
        _isWebinarPage();
        _isNewWebinar();
        _attachEvents();
        _setElementData();
    }

    // Public methods
    function init() {
        var pageHeroBanners = document.querySelectorAll('.c-page-hero-banner:not([data-component-state="initialized"])');

        if (pageHeroBanners) {
            pageHeroBanners.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
            if (Object.keys(EDC.props.userData).length > 1) {
                PubSub.publish('user-status', true);
            } else {
                EDC.utils.userStatus();
            }
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', PageHeroBannerYT.init);