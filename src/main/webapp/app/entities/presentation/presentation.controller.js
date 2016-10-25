(function() {
    'use strict';

    angular
        .module('jh2App')
        .controller('PresentationController', PresentationController);

    PresentationController.$inject = ['$scope', '$state', 'Presentation'];

    function PresentationController ($scope, $state, Presentation) {
        var vm = this;
        
        vm.presentations = [];

        loadAll();

        function loadAll() {
            Presentation.query(function(result) {
                vm.presentations = result;
            });
        }
    }
})();
