'use strict';

angular.module('sparkonixWebApp').controller('manageSalesTeamController',
		manageSalesTeamController);

function manageSalesTeamController($scope, restAPIService, dialogs) {
	// ------------- PUBLIC VARIABLES ----------------
	$scope.salesteammembers = [];
	$scope.newMember = {name:"", email:"", altEmail:"", mobile:"", password:""};
	$scope.editMember ={};
	// ------------- PRIVATE VARIABLES ----------------
	
	
	// ------------- CONTROLLER CODE ----------------
	getEntireSalesTeam();
	
	
	// ------------- PRIVATE FUNCTIONS ----------------
	function getEntireSalesTeam(){
		var promise = restAPIService.usersByRoleResource("SALESTEAM").query();
		promise.$promise.then(function(response) {
			$scope.salesteammembers = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, { 'size' : 'sm' });
		});
	}
	
	// ------------- PUBLIC FUNCTIONS ----------------
	$scope.addNewSalesTeamMember = function(){
		$scope.newMember.password = CryptoJS.MD5($scope.newMember.password).toString();
		$scope.newMember.role = "SALESTEAM";
		var promise = restAPIService.usersResource().save($scope.newMember);
		promise.$promise.then(function(response) {
			$scope.salesteammembers.push(response);
			$scope.newMember = {};
			$('#addSTMember').hide();
			$('.modal-backdrop').remove();
			$('body').removeClass('modal-open');
			dialogs.notify("Success", "Added new member successfully", {'size' : 'sm'});
		}, function(error) {
			dialogs.error("Error", error.data.error, { 'size' : 'sm' });
		});
	}
	
	$scope.updateUser = function(){
		var dlg = dialogs.confirm("Are you sure?","Are you sure you wish to update this Sales TEam Member?", {'size' : 'sm'});
		dlg.result.then(function() {
			var promise = restAPIService.userResource($scope.editMember.id).update($scope.editMember);
			promise.$promise.then(function(response) {
				var index = $scope.salesteammembers.indexOf($scope.editMember);
				$scope.salesteammembers.splice(index, 1);
				$scope.salesteammembers.splice(index, 0, response);
				dialogs.notify("Success", "Details updated successfully", {'size' : 'sm'});
			}, function(error) {
				dialogs.error("Error", error.data.error, { 'size' : 'sm' });
			});
		}, function() {
		});
	}
	$scope.onEdit = function(salesteammember){
		$scope.editMember = salesteammember;
		//$scope.editMember.mobile = Number($scope.editMember.mobile);
		$('#editSTMember').modal().show();
	}
	
	$scope.onDelete = function(salesteammember){
		var dlg = dialogs.confirm("Are you sure?","Are you sure you wish to delete this sales team member?", {'size' : 'sm'});
		dlg.result.then(function() {
			var promise = restAPIService.userResource(salesteammember.id).remove();
			promise.$promise.then(function(response) {
				var index = $scope.salesteammembers.indexOf(salesteammember);
				$scope.salesteammembers.splice(index, 1);
				dialogs.notify("Success", response.success, {'size' : 'sm'});
			}, function(error) {
				dialogs.error("Error", error.data.error, { 'size' : 'sm' });
			});
		}, function() {
		});
	}	
}
