'use strict';

angular.module('sparkonixWebApp').controller('headernotificationController',
		headernotificationController);

headernotificationController.$inject = [ '$scope', '$http', '$state',
		'$rootScope', 'restAPIService', '$cookies' ];

function headernotificationController($scope, $http, $state, $rootScope,
		restAPIService, $cookies) {

	$scope.loggedUserRole = $rootScope.user.role;

	$scope.logout = function() {
		$cookies.user = "";
		$state.go('login');
	}
}
