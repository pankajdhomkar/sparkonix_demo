'use strict';

angular.module('sparkonixWebApp').controller('waProfileController',
		waProfileController);

function waProfileController($scope, $rootScope, restAPIService, dialogs,
		$state) {
	$scope.companyDetails = {};
	$scope.resetPasswordDTO = {};

	getCompanyDetails();

	function getCompanyDetails() {
		var promise1;
		if ($rootScope.user.role == "MANUFACTURERADMIN"
				|| $rootScope.user.role == "RESELLERADMIN") {

			// use .get() to fetch single record
			// use .query() to fetch multiple record
			promise1 = restAPIService.companyDetailResource(
					$rootScope.user.companyDetailsId).get();

			promise1.$promise.then(function(response) {
				$scope.companyDetails = response;

			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}
	}

	$scope.onSaveCustomerSupportDetails = function() {

		var companyDetailsId = Number($rootScope.user.companyDetailsId);

		var promise = restAPIService.companyDetailResource(companyDetailsId)
				.update($scope.companyDetails);

		promise.$promise.then(function(response) {
			dialogs.notify("Success", response.success, {
				'size' : 'sm'
			});
			getCompanyDetails();
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
		$state.go("home.waprofile");
	}

	$scope.onUpdateWebAdminPassword = function() {

		var encryptedOldPassword = $scope.md5($scope.webAdmin.oldPassword);
		$scope.webAdmin.oldPassword = encryptedOldPassword;

		var encryptedNewPassword = $scope.md5($scope.webAdmin.newPassword);
		$scope.webAdmin.newPassword = encryptedNewPassword;

		$scope.resetPasswordDTO.userId = Number($rootScope.user.id);
		$scope.resetPasswordDTO.oldPassword = $scope.webAdmin.oldPassword;
		$scope.resetPasswordDTO.newPassword = $scope.webAdmin.newPassword;

		var promise = restAPIService.resetPasswordResource().update(
				$scope.resetPasswordDTO);

		promise.$promise.then(function(response) {
			$('.modal-backdrop').remove();
			$('body').removeClass('modal-open');

			dialogs.notify("Success", response.success, {
				'size' : 'sm'
			});
			getCompanyDetails();
			$state.go("home.waprofile");
			$state.reload();

		}, function(error) {
			$scope.webAdmin = '';
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});

	}

	$scope.onClose = function() {
		$state.go('home.wadashboard');
		// $state.reload();
	};

}
