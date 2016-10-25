(function() {
    'use strict';

    angular
        .module('jh2App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('presentation', {
            parent: 'entity',
            url: '/presentation',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jh2App.presentation.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/presentation/presentations.html',
                    controller: 'PresentationController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('presentation');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('presentation-detail', {
            parent: 'entity',
            url: '/presentation/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jh2App.presentation.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/presentation/presentation-detail.html',
                    controller: 'PresentationDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('presentation');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Presentation', function($stateParams, Presentation) {
                    return Presentation.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'presentation',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('presentation-detail.edit', {
            parent: 'presentation-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/presentation/presentation-dialog.html',
                    controller: 'PresentationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Presentation', function(Presentation) {
                            return Presentation.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('presentation.new', {
            parent: 'presentation',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/presentation/presentation-dialog.html',
                    controller: 'PresentationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                namePresentation: null,
                                topicPresentation: null,
                                textPresentation: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('presentation', null, { reload: 'presentation' });
                }, function() {
                    $state.go('presentation');
                });
            }]
        })
        .state('presentation.edit', {
            parent: 'presentation',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/presentation/presentation-dialog.html',
                    controller: 'PresentationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Presentation', function(Presentation) {
                            return Presentation.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('presentation', null, { reload: 'presentation' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('presentation.delete', {
            parent: 'presentation',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/presentation/presentation-delete-dialog.html',
                    controller: 'PresentationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Presentation', function(Presentation) {
                            return Presentation.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('presentation', null, { reload: 'presentation' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
