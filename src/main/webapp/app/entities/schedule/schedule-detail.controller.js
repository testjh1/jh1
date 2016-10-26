(function() {
    'use strict';

    angular
        .module('jh2App')
        .controller('ScheduleDetailController', ScheduleDetailController);

    ScheduleDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Schedule', 'Presentation', 'Room'];

    function ScheduleDetailController($scope, $rootScope, $stateParams, previousState, entity, Schedule, Presentation, Room) {
        var vm = this;

        vm.schedule = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('jh2App:scheduleUpdate', function(event, result) {
            vm.schedule = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
