var ammapJS = (function () {
    'use strict';

    function _initialize(element) {
        var eventAction = element.getAttribute('data-event-action') || '',
            eventCmp = element.getAttribute('data-event-component') || '',
            map,
            mtype = element.getAttribute('data-type-map') || '',
            muuid = element.getAttribute('data-uuid-map') || '',
            serviceURL = element.getAttribute('data-service-url') || '',
            imagesPath = element.getAttribute('data-images-path') || '',
            payload = {},
            providers = {},
            settings = {},
            trackDefault = {
                'data-event-component': eventCmp,
                'data-event-type': 'map interaction',
                'data-event-name': 'map interaction - country click',
                'data-event-action': eventAction,
                'data-event-text': '',
                'data-event-value': '',
                'data-destination-page': '',
                'data-engagement-type': '1',
                'data-event-stage': '',
                'data-event-level': ''
            },
            trackEvent = {};

        function _track(el) {
            var obj = {};

            obj.eventInfo = {
                eventComponent: el.dataset.eventComponent,
                eventType: el.dataset.eventType,
                eventName: el.dataset.eventName,
                eventAction: el.dataset.eventAction,
                eventText: el.dataset.eventText,
                eventValue: el.dataset.eventValue,
                eventPage: EDC.props.pageNameURL,
                eventPageName: EDC.utils.getPageName(),
                destinationPage: el.dataset.destinationPage,
                engagementType: el.dataset.engagementType,
                eventStage: el.dataset.eventStage,
                eventLevel: el.dataset.eventLevel
            };

            EDC.utils.dataLayerTracking(obj);
        }

        // {type:"clickMapObject", mapObject: MapObject, chart: AmMap, event: MouseEvent}
        function _handleClickMap(args) {
            var ob = args.mapObject,
                trackEl = args.chart.chartDiv;

            trackEvent.extend(trackDefault, {
                'data-event-text': ob.title.toLowerCase()
            });

            EDC.utils.setAttributes(trackEl, trackEvent);
            _track(trackEl);
        }

        function _handleKeys(ev) {
            // close on 'Esc' keypress
            if (EDC.props.escapeKeyCode === ev.keyCode) {
                map.closeAllDescriptions();
            }
        }

        function _listeners() {
            map.addListener('clickMapObject', _handleClickMap);
            EDC.utils.attachEvents(window.document, 'keyup', _handleKeys);
        }

        function _postRender() {
            map.initialZoomLevel = map.zoomLevel();
            map.initialZoomLatitude = map.zoomLatitude();
            map.initialZoomLongitude = map.zoomLongitude();

            _listeners();
        }

        function _buildMap() {
            var dataProvider;

            map = new AmCharts.AmMap();
            map.addClassNames = true;
            map.dragMap = true;
            map.zoomOnDoubleClick = false;
            map.svgIcons = false;
            map.language = settings.language;

            // Path where assets (like images) can be found
            map.pathToImages = imagesPath;

            // Area defaults for all maps
            map.areasSettings = settings.areas;

            dataProvider = providers[mtype];
            dataProvider.mapVar = AmCharts.maps[mtype];
            map.dataProvider = dataProvider;
            map.zoomControl = settings.zoom;

            if (mtype === 'worldHigh') {
                map.legend = settings.legend;
            } else {
                map.objectList = new AmCharts.ObjectList('c-ammap-list-' + muuid);
                map.showImagesInList = true;
            }

            map.listeners = [{
                event: 'rendered',
                method: _postRender
            }];
            map.write('c-ammap-map-' + muuid);

        }

        // Add all the palette colour values to the settings objects
        function _paint() {
            var idx,
                item,
                palette = {};

            // Use the AEM Author-selected group colour values
            // to paint the groups of MapAreas
            // First, build the palette and then iterate Areas
            for (idx = 0; idx < settings.legend.data.length; idx++) {
                item = settings.legend.data[idx];
                palette[item.title] = item.color;
            }

            for (idx = 0; idx < providers.worldHigh.areas.length; idx++) {
                item = providers.worldHigh.areas[idx];
                item.color = palette[item.groupId];
            }
        }

        function _configureData(data) {
            var idx,
                imSett = {};

            payload = data;

            providers = {
                canadaLow: {
                    getAreasFromMap: true,
                    zoomLevel: 2.15,
                    zoomLatitude: 60,
                    zoomLongitude: -100
                },
                continentsLow: {
                    getAreasFromMap: true
                },
                worldHigh: {
                    areas: payload.areas,
                    images: payload.images
                }
            };

            settings.legend.data = data.legend;
            settings.language = data.language;

            _paint();
            // We have to do the image configuration this way, b/c...
            // The library has what could be called a bug, in that
            // It is not passing through *all* supported props to MapImages
            // E.g, 'labelBackgroundColor' is valid, not passed through, and we use it
            for (idx = 0; idx < providers.worldHigh.images.length; idx++) {
                imSett = providers.worldHigh.images[idx];
                imSett.extend(true, imSett, settings.images);
                providers.worldHigh.images[idx] = imSett;
            }

            _buildMap();
        }

        function _configureMap(cfg) {
            var areaSettings = {},
                imageSettings = {};

            payload = cfg;

            if (payload.areaSettings.selectedColor && payload.areaSettings.selectedColor === 'undefined') {
                payload.areaSettings.selectedColor = undefined;
            }

            settings.areas = areaSettings.extend(true, payload.areaSettings, payload.popupSettings);
            settings.images = imageSettings.extend(true, payload.imageSettings, payload.popupSettings);
            settings.legend = payload.legendSettings;
            settings.zoom = payload.zoomSettings;

            EDC.utils.fetchJSON('GET', serviceURL, '', _configureData);
        }

        function _load() {
            var config = {
                areaSettings: {
                    color: '#185fa8',
                    rollOverOutlineColor: '#37769A',
                    selectedColor: 'undefined',
                    selectedBorderColor: '#FFFFFF',
                    outlineThickness: 0.2,
                    rollOverOutlineThickness: 0.4,
                    selectedOutlineColor: '#37769A'
                },
                imageSettings: {
                    balloonText: '',
                    labelBackgroundAlpha: '0.9',
                    labelBackgroundColor: '#2F78C6',
                    labelColor: '#FFFFFF',
                    labelRollOverColor: '#CADDF2',
                    labelFontSize: 12,
                    labelPosition: 'right'
                },
                legendSettings: {
                    bottom: 70,
                    data: '',
                    fontSize: 14,
                    horizontalGap: 20,
                    left: 14,
                    marginBottom: 13,
                    marginTop: 13,
                    markerBorderThickness: 0,
                    markerLabelGap: 10,
                    markerSize: 14,
                    maxColumns: 1,
                    width: 255
                },
                popupSettings: {
                    descriptionWindowHeight: 365,
                    descriptionWindowLeft: 10,
                    descriptionWindowTop: 50,
                    descriptionWindowWidth: 500
                },
                zoomSettings: {
                    buttonBorderAlpha: 0.07,
                    buttonBorderColor: '#333333',
                    buttonBorderThickness: 2,
                    buttonCornerRadius: 6,
                    buttonIconColor: '#2F78C6',
                    buttonSize: 40,
                    homeButtonEnabled: true,
                    homeIconColor: '#2F78C6',
                    iconSize: 16,
                    panControlEnabled: false,
                    roundButtons: false,
                    zoomControlEnabled: true
                }
            };

            _configureMap(config);
        }

        _load();
    }

    // Public methods
    function init() {
        var allMaps = document.querySelectorAll('.c-ammap:not([data-component-state="initialized"])');

        if (allMaps.length) {
            allMaps.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', ammapJS.init);