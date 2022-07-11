var youTubePlayer = new function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var videoId = element.getAttribute('data-video-id'),
            playBtn = element.querySelector('button.start-video'),
            videoDataPercentage,
            player,
            checkVideoPercentage,
            videoDuration,
            videoName,
            videoCurrentPercentage,
            dirty = false;

        // Data Layer
        function _trackEvent(state) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventText: videoName,
                    eventPageName: EDC.utils.getPageName(),
                    eventPage: EDC.props.pageNameURL
                }
            };

            if (state === 'percentage') {
                obj.eventInfo.eventType = 'video';
                obj.eventInfo.engagementType = '';
                obj.eventInfo.eventLevel = '';
                obj.eventInfo.eventName = element.dataset.eventName + ' - play ' + videoDataPercentage + '% played';
                obj.eventInfo.eventAction = 'progress';
            } else {
                obj.eventInfo.eventType = 'button';
                obj.eventInfo.engagementType = element.dataset.engagementType;
                obj.eventInfo.eventLevel = element.dataset.eventLevel;
                obj.eventInfo.eventName = element.dataset.eventName + ' - ' + state;
                obj.eventInfo.eventAction = state;
            }

            EDC.utils.dataLayerTracking(obj);
        }

        function _videoPercentage() {
            var numRounded,
                currentNumber = 0;

            checkVideoPercentage = setInterval(function () {
                if (player) {
                    videoCurrentPercentage = player.getCurrentTime() / videoDuration * 100;
                    numRounded = Math.round(videoCurrentPercentage);

                    if (numRounded > 0 && numRounded % 10 === 0 && currentNumber !== numRounded && videoCurrentPercentage >= numRounded) {
                        currentNumber = numRounded;
                        videoDataPercentage = numRounded;
                        _trackEvent('percentage');
                    }
                }
            }, 100);
        }

        function _onPlayerStateChange(stateData) {
            if (stateData) {
                switch (stateData.data) {
                    case 1:
                        if (dirty) {
                            _trackEvent('replay');
                        }

                        _videoPercentage();
                        break;
                    case 2:
                        dirty = true;
                        clearInterval(checkVideoPercentage);
                        _trackEvent('pause');
                        break;
                    case 3:
                        if (!dirty) {
                            _trackEvent('play');
                        }
                        break;
                }
            }
        }

        function _playBtnClicked() {
            var timer;

            element.className += ' show';
            timer = setInterval(function () {
                if (player) {
                    videoName = player.playerInfo.videoData.title;
                    player.playVideo();
                    videoDuration = player.getDuration();
                    clearInterval(timer);
                }
            }, 200);
        }

        function _videoPlayerConfig() {
            if (typeof (YT) !== 'undefined' && typeof (YT.Player) !== 'undefined') {
                player = new YT.Player('player-' + videoId, {
                    height: '100%',
                    width: '100%',
                    videoId: videoId,
                    host: 'https://www.youtube-nocookie.com',
                    playerVars: {
                        rel: 0,
                        enablejsapi: 1,
                        showinfo: 0
                    },
                    events: {
                        onStateChange: _onPlayerStateChange
                    }
                });
            }
        }

        function _pubSubs() {
            PubSub.subscribe('stop-video', function (data) {
                var timer;

                if (data) {
                    timer = setInterval(function () {
                        if (player) {
                            player.pauseVideo();
                            clearInterval(timer);
                        }
                    }, 200);
                }
            });
        }

        function _attachEvents() {
            if (playBtn) {
                EDC.utils.attachEvents(playBtn, 'click', _playBtnClicked);
            }
        }

        _videoPlayerConfig();
        _pubSubs();
        _attachEvents();
    }

    function init() {
        var youTubeVideoPlayers = document.querySelectorAll('.video-container .video:not([data-component-state="initialized"])');

        youTubeVideoPlayers.forEach(function (elem) {
            if (youTubeVideoPlayers) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            }
        });
    }

    return {
        init: init
    };
};

window.onYouTubeIframeAPIReady = function () {
    'use strict';

    youTubePlayer.init();
};

function videoPlayerLoad() {
    'use strict';

    var tag,
        firstScriptTag;

    if (document.querySelectorAll('.video-container')) {
        tag = document.createElement('script');
        tag.src = '//www.youtube.com/iframe_api';
        firstScriptTag = document.getElementsByTagName('script')[0];
        firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
    }
}

document.addEventListener('DOMContentLoaded', videoPlayerLoad);