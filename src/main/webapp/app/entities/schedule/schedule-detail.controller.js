(function() {
    'use strict';

    angular
        .module('jh2App')
        .controller('ScheduleDetailController', ScheduleDetailController);

    ScheduleDetailController.$inject = ['Room', '$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Schedule', 'Presentation'];

    function ScheduleDetailController(Room, $scope, $rootScope, $stateParams, previousState, entity, Schedule, Presentation) {
        var vm = this;

        vm.schedule = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('jh2App:scheduleUpdate', function(event, result) {
            vm.schedule = result;
        });
        Room.$on('$destroy', unsubscribe);
    }
})();
