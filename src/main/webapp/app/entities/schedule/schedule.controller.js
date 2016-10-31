(function() {
    'use strict';

    angular
        .module('jh2App')
        .controller('ScheduleController', ScheduleController);

    ScheduleController.$inject = ['$scope', '$state', 'Schedule'];

    function ScheduleController ($scope, $state, Schedule) {
        var vm = this;

        vm.schedules = {};
        loadAll();

        function loadAll() {
            Schedule.query(function(result) {
                angular.forEach(result, function (value, key) {
                    if (!vm.schedules[value.room.numberAudience]){
                        vm.schedules[value.room.numberAudience] = [];
                    }
                    vm.schedules[value.room.numberAudience].push(value);
                });
                console.log(vm.schedules);
            });
        }
    }
})();
