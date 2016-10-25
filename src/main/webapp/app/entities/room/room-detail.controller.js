(function() {
    'use strict';

    angular
        .module('jh2App')
        .controller('RoomDetailController', RoomDetailController);

    RoomDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Room'];

    function RoomDetailController($scope, $rootScope, $stateParams, previousState, entity, Room) {
        var vm = this;

        vm.room = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('jh2App:roomUpdate', function(event, result) {
            vm.room = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
