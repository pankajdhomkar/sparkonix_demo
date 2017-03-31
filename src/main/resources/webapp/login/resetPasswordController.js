'use strict';

angular.module('sparkonixWebApp').controller('resetPasswordController', resetPasswordController);

loginController.$inject = [ '$http', '$state', 'restAPIService', '$scope',
		'$rootScope', 'dialogs', '$cookies','$stateParams' ];
function resetPasswordController($http, $state, restAPIService, $scope, $rootScope,
		dialogs, $cookies, $stateParams) {
	$scope.token = $stateParams.token;
	$scope.checkUserAuth = function(){
		var emptyObj = {email : ""};
		var promise = restAPIService.authenticateForgotPassLink($scope.token).save(emptyObj);
		promise.$promise.then(function(response) {
			$scope.retreivedUserObj = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
			$state.go('login');
		});
	}
	$scope.checkUserAuth();
	
	$scope.updatePassword = function() {
		
		if($scope.password !== $scope.confpassword){
			dialogs.error("Error", "New Password and Confirm Password did not match.", {
				'size' : 'sm'
			});
			return;
		}
		var encryptedPassword = CryptoJS.MD5($scope.password).toString();
		$scope.retreivedUserObj.newPassword = encryptedPassword;
		
		var promise = restAPIService.updateUserPassword().update($scope.retreivedUserObj);
		promise.$promise.then(function(response) {
			dialogs.notify("Success", response.success, {
				'size' : 'sm'
			});
			$state.go('login');
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
			$state.go('login');
		});
	}
}