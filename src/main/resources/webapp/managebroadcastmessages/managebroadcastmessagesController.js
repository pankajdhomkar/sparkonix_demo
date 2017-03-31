'use strict';

angular.module('sparkonixWebApp').controller(
		'managebroadcastmessagesController', managebroadcastmessagesController);

function managebroadcastmessagesController($scope, restAPIService, dialogs, $state) {
	// ------------- PUBLIC VARIABLES ----------------
	$scope.broadcastMessage = {};

	// ------------- PRIVATE VARIABLES ----------------

	// ------------- CONTROLLER CODE ----------------

	// ------------- PRIVATE FUNCTIONS ----------------

	// ------------- PUBLIC FUNCTIONS ----------------
	$scope.onSubmitBroadcastMessageForm = function() {
		angular.element(document.getElementById('btnSubmit'))[0].disabled = true;
		var dlg = dialogs.confirm("Are you sure?",
				"Are you sure you want to send this notification to all AttendMe operators?", {
					'size' : 'sm'
				});
		dlg.result.then(function() {
			var promise = restAPIService.broadcastMessagesResource().save($scope.broadcastMessage);
		promise.$promise.then(function(response) {
			angular.element(document.getElementById('btnSubmit'))[0].disabled = false;  
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});
				
				$state.reload();
			
			}, function(error) {
				angular.element(document.getElementById('btnSubmit'))[0].disabled = false;
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}, function() {
		});
	};

	$scope.onResetBroadcastMessageForm = function() {
		$scope.broadcastMessage = '';
	};

}
