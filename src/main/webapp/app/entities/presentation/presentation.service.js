(function() {
    'use strict';
    angular
        .module('jh2App')
        .factory('Presentation', Presentation);

    Presentation.$inject = ['$resource'];

    function Presentation ($resource) {
        var resourceUrl =  'api/presentations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
