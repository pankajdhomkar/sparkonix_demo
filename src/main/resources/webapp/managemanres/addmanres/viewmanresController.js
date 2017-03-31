'use strict';

angular.module('sparkonixWebApp').controller('viewManResController',
		viewManResController);

function viewManResController($scope, $state, $rootScope, restAPIService,
		dialogs) {
	// ------------- PUBLIC VARIABLES ----------------

	$scope.ManResDTO = {}

	// ------------- PRIVATE VARIABLES ----------------

	// ------------- CONTROLLER CODE ------------------

	if ($scope.mode == "view") {

		$scope.headerTitleText = "View Details";
		$scope.editMode = true;
		$scope.disablePassword = false;

		var companyDetailId = Number($scope.manResId);
		// get ManResDTO from db by manResId
		var promise1 = restAPIService.companyDetailManResResource(
				$scope.manResId).get();
		promise1.$promise.then(function(response) {
			// populate value of ManRes for edit form
			$scope.newManRes = response.manResDetail;

			if ($scope.newManRes.curSubscriptionStartDate != null) {
				$scope.newManRes.curSubscriptionStartDate = new Date(
						$scope.newManRes.curSubscriptionStartDate);
			}
			if ($scope.newManRes.curSubscriptionEndDate != null) {
				$scope.newManRes.curSubscriptionEndDate = new Date(
						$scope.newManRes.curSubscriptionEndDate);
			}
			$scope.newUser = response.webAdminUser;			
			 
			if ($scope.newManRes.companyType == "MANUFACTURER") {
				$rootScope.tabValueManRes = 1;
			} else if ($scope.newManRes.companyType == "RESELLER") {
				$rootScope.tabValueManRes = 2;
			}

		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});

		});

	}

	// ------------- PUBLIC FUNCTIONS -------------

	$scope.onCancel = function() {
		$state.go('home.managemanres');
		$state.reload();
	}

}
