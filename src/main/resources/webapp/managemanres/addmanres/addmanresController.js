'use strict';

angular.module('sparkonixWebApp').controller('addManResController',
		addManResController);

function addManResController($scope, $state, $rootScope, restAPIService,
		dialogs) {
	// $state.reload();
	// ------------- PUBLIC VARIABLES ----------------
	$scope.companyTypes = [ "MANUFACTURER", "RESELLER" ];
	$scope.curSubscriptionTypes = [ "BASIC", "PREMIUM" ];
	$scope.curSubscriptionStatusData = [ "ACTIVE", "PAYMENT_DUE", "INACTIVE",
			"EXPIRED" ];
	$scope.ManResDTO = {}
	$scope.opened = {};
	// $scope.isCompanyPanExist = false;
	$scope.panValidationMsg = "";
	

	
	// ------------- PRIVATE VARIABLES ----------------

	// ------------- CONTROLLER CODE ------------------
	$scope.open = function($event) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.opened = {};
		$scope.opened[$event.target.id] = true;
	};
	$scope.format = 'dd-MM-yyyy'

	if ($scope.mode == "add") {
		$scope.headerTitleText = "Add New Manufacturer / Reseller";
		$scope.editMode = false;
		$scope.disablePassword = true;
	} else if ($scope.mode == "edit") {
		$scope.headerTitleText = "Edit Manufacturer / Reseller";
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

			if (response.webAdminUser != null) {
				$scope.newUser = response.webAdminUser;

				// $scope.newUser.mobile = Number($scope.newUser.mobile);
				// if user not updated his password then assign his old
				$scope.dbPassword = $scope.newUser.password;
				// do not display password in edit mode
				$scope.newUser.password = null;
			}
			// show/hide web admin details
			$scope.manageWebAdminDetails();

		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
			
		});

	}

	// ------------- PUBLIC FUNCTIONS -------------

	$scope.onSubmit = function() {
		if ($scope.mode == "add") {

			$scope.newManRes.onBoardedBy = $rootScope.user.id;
			$scope.newManRes.companyType = $scope.newManRes.companyType
					.toUpperCase();

			if ($scope.newManRes.curSubscriptionStatus == "INACTIVE") {

				$scope.newManRes.curSubscriptionStartDate = "";
				$scope.newManRes.curSubscriptionEndDate = "";
			}

			$scope.ManResDTO.manResDetail = $scope.newManRes;
			if ($scope.hideFlag != "true") {
				// if web admin details not hidden
				var encryptedPassword = $scope.md5($scope.newUser.password);
				$scope.newUser.password = encryptedPassword;
				$scope.ManResDTO.webAdminUser = $scope.newUser;

			} else {
				$scope.ManResDTO.webAdminUser = null;
			}

			var promise1 = restAPIService.companyDetailsManResResource().save(
					$scope.ManResDTO);
			promise1.$promise.then(function(response) {
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});

				if ($scope.newManRes.companyType == "MANUFACTURER") {
					$rootScope.tabValueManRes = 1;
				} else if ($scope.newManRes.companyType == "RESELLER") {
					$rootScope.tabValueManRes = 2;
				}

				$state.go('home.managemanres');
				$state.reload();

			}, function(error) {
				$scope.newUser.password = $scope.password2;
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		} else if ($scope.mode == "edit") {

			$scope.newManRes.onBoardedBy = $rootScope.user.id;
			$scope.newManRes.companyType = $scope.newManRes.companyType
					.toUpperCase();
			$scope.ManResDTO.manResDetail = $scope.newManRes;
			// if password updated
			/*
			 * if ($scope.disablePassword) { var encryptedPassword2 =
			 * $scope.md5($scope.newUser.password); $scope.newUser.password =
			 * encryptedPassword2; } else { $scope.newUser.password =
			 * $scope.dbPassword; }
			 */

			if ($scope.newManRes.curSubscriptionStatus == "INACTIVE") {
				$scope.newManRes.curSubscriptionStartDate = "";
				$scope.newManRes.curSubscriptionEndDate = "";
			}
			if ($scope.newUser != null) {
				$scope.ManResDTO.webAdminUser = $scope.newUser;
			}

			var promise2 = restAPIService.companyDetailManResResource(
					$scope.manResId).update($scope.ManResDTO);
			promise2.$promise.then(function(response) {
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});

				if ($scope.newManRes.companyType == "MANUFACTURER") {
					$rootScope.tabValueManRes = 1;
				} else if ($scope.newManRes.companyType == "RESELLER") {
					$rootScope.tabValueManRes = 2;
				}
				$state.go('home.managemanres');
				$state.reload();

			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});

		}
	}

	// for back pressed browser button
	// this method for onback pressed on browser without any change in current
	// page
	$scope.$on('$locationChangeSuccess', function() {
		$state.go('home.managemanres');
		$state.reload();
		console.log("Clicked");
	});
	
	$scope.onCancel = function() {
		$state.go('home.managemanres');
		$state.reload();
	}

	$scope.matchPassword = function() {
		if ($scope.newUser.password != $scope.password2) {
			$scope.ngDisabled = true;
		} else if ($scope.newUser.password == $scope.password2) {
			$scope.ngDisabled = false;
		}
	}

	$scope.resetPasswordField = function() {
		if ($scope.disablePassword) {
			$scope.newUser.password = "";
			$scope.password2 = "";
		}
	}

	$scope.manageWebAdminDetails = function() {

		if ($scope.newManRes.curSubscriptionStatus == "INACTIVE") {
			// alert('hide web admin details')
			$scope.hideFlag = "true";
		} else {
			// alert('show web admin details')
			$scope.hideFlag = "false";
		}
	}

	$scope.checkCompanyPan = function() {
		// alert($scope.newManRes.pan);
		if (($scope.newManRes.pan).length > 9) {
			$scope.isCompanyPanAvailable = false;
			if ($scope.newManRes.pan != undefined) {
				$scope.isCompanyPanFieldEmpty = false;
				var promise = restAPIService
						.companyDetailByCompanyTypeAndCompanyPan(
								$scope.newManRes.companyType,
								$scope.newManRes.pan).get();
				promise.$promise.then(function(response) {
					if (response.isCompanyPanExist == true) {
						// alert('not null');
						$scope.isCompanyPanExist = true;
						$scope.isCompanyPanAvailable = false;
					} else {
						// alert('null');
						$scope.isCompanyPanExist = false;
						$scope.isCompanyPanAvailable = true;
					}
				});
			} else if ($scope.newManRes.pan == undefined) {
				$scope.isCompanyPanFieldEmpty = true;

			}
		} else {
			$scope.isCompanyPanFieldEmpty = true;
			$scope.isCompanyPanExist = false;
			$scope.isCompanyPanAvailable = false;
		}
	}

	$scope.checkCompanyPanAvailability = function() {
		$scope.panValidationMsgColor = "red";

		if ($scope.newManRes.pan != undefined) {
			if (($scope.newManRes.pan).length == 10) {
				var promise = restAPIService
						.companyDetailByCompanyTypeAndCompanyPan(
								$scope.newManRes.companyType,
								$scope.newManRes.pan).get();
				promise.$promise
						.then(function(response) {
							if (response.isCompanyPanExist == true) {
								// pan already used by this company type
								$scope.panValidationMsg = "This company PAN is not available";
							} else {
								$scope.panValidationMsgColor = "green";
								// pan not being used by this company type
								$scope.panValidationMsg = "This company PAN is available";
							}
						});
			} else {
				$scope.panValidationMsg = "Enter 10 digit in company PAN ";

			}
		} else if ($scope.newManRes.pan == undefined) {
			$scope.panValidationMsg = "Enter company PAN";

		}

	}

	$scope.setEndDate = function() {

		var curSubStartDate = $scope.newManRes.curSubscriptionStartDate;
		var curSubEndDate = new Date();
		curSubEndDate.setYear(curSubStartDate.getFullYear() + 1);
		curSubEndDate.setMonth(curSubStartDate.getMonth());
		curSubEndDate.setDate(curSubStartDate.getDate());
		$scope.newManRes.curSubscriptionEndDate = "";

		// set end date
		$scope.newManRes.curSubscriptionEndDate = curSubEndDate;
	}

}
