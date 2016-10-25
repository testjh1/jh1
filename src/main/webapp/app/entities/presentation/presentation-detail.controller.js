(function() {
    'use strict';

    angular
        .module('jh2App')
        .controller('PresentationDetailController', PresentationDetailController);

    PresentationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Presentation', 'User'];

    function PresentationDetailController($scope, $rootScope, $stateParams, previousState, entity, Presentation, User) {
        var vm = this;

        vm.presentation = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('jh2App:presentationUpdate', function(event, result) {
            vm.presentation = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
