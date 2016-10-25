'use strict';

describe('Controller Tests', function() {

    describe('Schedule Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockSchedule, MockPresentation, MockRoom;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockSchedule = jasmine.createSpy('MockSchedule');
            MockPresentation = jasmine.createSpy('MockPresentation');
            MockRoom = jasmine.createSpy('MockRoom');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Schedule': MockSchedule,
                'Presentation': MockPresentation,
                'Room': MockRoom
            };
            createController = function() {
                $injector.get('$controller')("ScheduleDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'jh2App:scheduleUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
