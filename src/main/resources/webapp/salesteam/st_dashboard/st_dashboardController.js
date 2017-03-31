'use strict';

angular.module('sparkonixWebApp').controller('stDashboardController',
		stDashboardController);

function stDashboardController($scope, $rootScope) {
	$scope.loggedUser = $rootScope.user.name;
}
