(function() {
    'use strict';

    angular
        .module('jh2App')
        .controller('ScheduleRegController',ScheduleRegController);

    ScheduleRegController.$inject = ['$uibModalInstance', 'entity', 'Schedule'];

    function ScheduleRegController($uibModalInstance, entity, Schedule) {
        var vm = this;

        vm.schedule = entity;
        vm.clear = clear;
        vm.confirmReg = confirmReg;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmReg (id) {
            Schedule.reg({"id": id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
