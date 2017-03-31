'use strict';

angular.module('sparkonixWebApp').controller('waDashboardController',
		waDashboardController);

function waDashboardController($scope, $rootScope) {
	$scope.loggedUser = $rootScope.user.name;
}
