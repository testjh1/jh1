(function() {
    'use strict';

    angular
        .module('jh2App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('schedule', {
            parent: 'entity',
            url: '/schedule',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jh2App.schedule.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/schedule/schedules.html',
                    controller: 'ScheduleController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('schedule');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('schedule-detail', {
            parent: 'entity',
            url: '/schedule/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jh2App.schedule.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/schedule/schedule-detail.html',
                    controller: 'ScheduleDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('schedule');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Schedule', function($stateParams, Schedule) {
                    return Schedule.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'schedule',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('schedule-detail.edit', {
            parent: 'schedule-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/schedule/schedule-dialog.html',
                    controller: 'ScheduleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Schedule', function(Schedule) {
                            return Schedule.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('schedule.new', {
            parent: 'schedule',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/schedule/schedule-dialog.html',
                    controller: 'ScheduleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                beginSchedule: null,
                                endSchedule: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('schedule', null, { reload: 'schedule' });
                }, function() {
                    $state.go('schedule');
                });
            }]
        })
        .state('schedule.edit', {
            parent: 'schedule',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/schedule/schedule-dialog.html',
                    controller: 'ScheduleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Schedule', function(Schedule) {
                            return Schedule.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('schedule', null, { reload: 'schedule' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('schedule.delete', {
            parent: 'schedule',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/schedule/schedule-delete-dialog.html',
                    controller: 'ScheduleDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Schedule', function(Schedule) {
                            return Schedule.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('schedule', null, { reload: 'schedule' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
