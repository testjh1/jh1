(function() {
    'use strict';

    angular
        .module('jh2App')
        .controller('RoomController', RoomController);

    RoomController.$inject = ['$scope', '$state', 'Room'];

    function RoomController ($scope, $state, Room) {
        var vm = this;
        
        vm.rooms = [];

        loadAll();

        function loadAll() {
            Room.query(function(result) {
                vm.rooms = result;
            });
        }
    }
})();
