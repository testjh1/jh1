(function() {
    'use strict';
    angular
        .module('jh2App')
        .factory('Schedule', Schedule);

    Schedule.$inject = ['$resource', 'DateUtils'];

    function Schedule ($resource, DateUtils) {
        var resourceUrl =  'api/schedules/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.beginSchedule = DateUtils.convertDateTimeFromServer(data.beginSchedule);
                        data.endSchedule = DateUtils.convertDateTimeFromServer(data.endSchedule);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
