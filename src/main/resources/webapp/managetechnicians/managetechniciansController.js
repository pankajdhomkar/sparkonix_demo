'use strict';

angular.module('sparkonixWebApp').controller('manageTechniciansController',
		manageTechniciansController);

function manageTechniciansController($scope, $rootScope, $state,
		restAPIService, dialogs) {
	// ------------- PUBLIC VARIABLES ----------------
	$scope.technicians = [];
	$scope.newTechnician = {};
	$scope.newTechnician = {
		name : "",
		email : "",
		altEmail : "",
		mobile : "",
		password : ""
	};

	// ------------- PRIVATE VARIABLES ----------------

	// ------------- CONTROLLER CODE ----------------
	getTechnicians();

	// ------------- PRIVATE FUNCTIONS ----------------
	function getTechnicians() {
		var promise;

		if ($rootScope.user.role == "SUPERADMIN") {
			promise = restAPIService.usersByRoleResource(
					"TECHNICIAN").query();
		} else {
			// for Man/Res
			promise = restAPIService.usersByRoleByCompanyResource(
					"TECHNICIAN", $rootScope.user.companyDetailsId).query();
		}

		// var promise = restAPIService.usersByRoleResource("TECHNICIAN").query();

		promise.$promise.then(function(response) {
			$scope.technicians = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	// ------------- PUBLIC FUNCTIONS ----------------
	$scope.addNewTechnician = function() {

		$scope.newTechnician.password = CryptoJS.MD5(
				$scope.newTechnician.password).toString();
		$scope.newTechnician.role = "TECHNICIAN";
		$scope.newTechnician.companyDetailsId = Number($rootScope.user.companyDetailsId);

		var promise = restAPIService.usersResource().save($scope.newTechnician);
		promise.$promise.then(function(response) {
			$scope.technicians.push(response);
			$scope.newTechnician = {};
			$('#addTechnician').hide();
			$('.modal-backdrop').remove();
			$('body').removeClass('modal-open');
			dialogs.notify("Success", "Added new technician successfully", {
				'size' : 'sm'
			});
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	$scope.updateTechnician = function() {
		var dlg = dialogs.confirm("Are you sure?",
				"Are you sure you want to update this technician?", {
					'size' : 'sm'
				});
		dlg.result.then(function() {

			var promise = restAPIService.userResource($scope.editTechnician.id)
					.update($scope.editTechnician);
			promise.$promise.then(function(response) {
				var index = $scope.technicians.indexOf($scope.editTechnician);
				$scope.technicians.splice(index, 1);
				$scope.technicians.splice(index, 0, response);
				dialogs.notify("Success", "Details updated successfully", {
					'size' : 'sm'
				});
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}, function() {
		});
	}
	$scope.onEdit = function(technician) {
		$scope.editTechnician = angular.copy(technician);
		$scope.editTechnician.mobile = Number($scope.editTechnician.mobile);
		$('#editTechnician').modal().show();
	}

	$scope.onDelete = function(technician) {
		var dlg = dialogs.confirm("Are you sure?",
				"Are you sure you want to delete this technician?", {
					'size' : 'sm'
				});
		dlg.result.then(function() {
			var promise = restAPIService.userResource(technician.id).remove();
			promise.$promise.then(function(response) {
				var index = $scope.technicians.indexOf(technician);
				$scope.technicians.splice(index, 1);
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}, function() {
		});
	}
}
