'use strict';

angular.module('sparkonixWebApp').controller('manageComplaintsController',
		manageComplaintsController);

function manageComplaintsController($scope, $rootScope, restAPIService,
		dialogs, $state, $http) {
	// ------------- PUBLIC VARIABLES ----------------
	$scope.activeTabNumber = 1;
	$scope.complaints = [];
	$scope.services = [];
	$scope.machinesList = [];
	$scope.viewComplaint = {};
	$scope.technicianList = {};
	$scope.newServiceRequest = {};
	$scope.searchForm = {};
	$scope.displayFlag = true;
	// date-picker format
	$scope.format = 'dd-MM-yyyy'

	// ------------- PRIVATE VARIABLES ----------------

	// ------------- CONTROLLER CODE ----------------
	getComplaintsData();
	getServiceHistoryList();

	// ------------- PRIVATE FUNCTIONS ----------------
	function getComplaintsData() {
		var promise1;
		// if man/res web admin logged in
		// get complaint by man/res id & support_assistance
		if ($rootScope.user.role == "MANUFACTURERADMIN"
				|| $rootScope.user.role == "RESELLERADMIN") {
			// companyDetailsId of logged in web admin
			var manResId = Number($rootScope.user.companyDetailsId);

			promise1 = restAPIService.complaintsByManResRoleAndCompanyId(
					$rootScope.user.role, manResId).query();
		}
		if ($rootScope.user.role == "TECHNICIAN") {
			// if technician logged in
			var technicianId = Number($rootScope.user.id);
			promise1 = restAPIService.complaintsByTechnicianId(technicianId)
					.query();
		}
		if ($rootScope.user.role == "SUPERADMIN") {
			// if superadmin logged in
			promise1 = restAPIService.issuesResource().query();
		}

		promise1.$promise.then(function(response) {
			$scope.complaints = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	function getMachinesList() {
		var promise2;
		var manResId = Number($rootScope.user.companyDetailsId);
		promise2 = restAPIService.getAllMachinesForCompany(manResId).query();
		promise2.$promise.then(function(response) {
			$scope.machineList = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	function getTechniciansByCompanyId() {
		// for Man/Res
		var promise1 = restAPIService.usersByRoleByCompanyResource(
				"TECHNICIAN", $rootScope.user.companyDetailsId).query();
		promise1.$promise.then(function(response) {
			$scope.technicianList = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	$scope.viewDetails = function(complaintObj) {
		$scope.viewComplaint = complaintObj;

		$('#viewIssueDetails').modal().show();
	}

	$scope.setTab = function(tabId) {
		$scope.activeTabNumber = tabId;
	};

	$scope.isSet = function(tabId) {
		return $scope.activeTabNumber === tabId;
	};

	$scope.onEditComplaint = function(complaintObj) {
		$scope.updateIssueRecord = {};		// added this line to initialize a default state to the variable.
		
		// Shallow copy was the Issue, angular.copy() solved the bug of changing the status even if it was not updated by clicking the update button.
		$scope.updateIssueRecord = angular.copy(complaintObj); 
		if ($rootScope.user.role == "TECHNICIAN") {
			$('#actionTechnician').modal().show();
		} else {
			// load technnician dropdown
			getTechniciansByCompanyId();
			$('#actionWebAdmin').modal().show();
		}
	}

	$scope.assignComplaintToTechnician = function() {
		var dlg = dialogs.confirm("Are you sure?",
				"Are you sure you want to update this record?", {
					'size' : 'sm'
				});
		dlg.result
				.then(
						function() {
							$scope.updateIssueRecord.assignedTo = Number($scope.updateIssueRecord.assignedTo);
							$scope.updateIssueRecord.status = "ASSIGNED";

							var promise = restAPIService.IssueResource(
									$scope.updateIssueRecord.id).update(
									$scope.updateIssueRecord);
							promise.$promise.then(function(response) {
								getComplaintsData();
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

	$scope.updateComplaintByTechnician = function() {
		var dlg = dialogs.confirm("Are you sure?",
				"Are you sure you want to update this record?", {
					'size' : 'sm'
				});
		dlg.result.then(function() {
			var promise = restAPIService.IssueResource(
					$scope.updateIssueRecord.id).update(
					$scope.updateIssueRecord);
			promise.$promise.then(function(response) {
				getComplaintsData();
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

	$scope.newServiceReq = function() {
		$scope.newServiceRequest = '';
		// load machine drop-down
		getMachinesList();
		// load technician drop-down
		getTechniciansByCompanyId();
		$('#addServiceRequest').modal().show();
	}

	$scope.onSaveServiceRequest = function() {
		$scope.newServiceRequest.companyId = Number($rootScope.user.companyDetailsId);
		var promise = restAPIService.MachineAmcServiceHistoriesResource().save(
				$scope.newServiceRequest);
		promise.$promise.then(function(response) {
			$scope.newTechnician = {};
			// $scope.services.push(response);
			getServiceHistoryList();

			$('.modal-backdrop').remove();
			$('body').removeClass('modal-open');
			dialogs.notify("Success", "New servie request added successfully",
					{
						'size' : 'sm'
					});
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	function getServiceHistoryList() {
		var category;
		var parameter;
		var promise2;

		if ($rootScope.user.role == "MANUFACTURERADMIN"
				|| $rootScope.user.role == "RESELLERADMIN") {
			category = "company";
			parameter = $rootScope.user.companyDetailsId;
		}
		if ($rootScope.user.role == "TECHNICIAN") {
			category = "technician";
			parameter = $rootScope.user.id;
		}

		if ($rootScope.user.role == "SUPERADMIN") {
			promise2 = restAPIService.MachineAmcServiceHistoriesResource()
					.query();
		} else {
			promise2 = restAPIService.serviceHistoryByCategoryResource(
					category, parameter).query();
		}

		promise2.$promise.then(function(response) {
			$scope.services = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	$scope.onEditServiceRequest = function(service) {
		$scope.updatedRecord = angular.copy(service);
		if ($rootScope.user.role == "TECHNICIAN") {
			$('#editServiceRequestByTechnician').modal().show();
		} else if ($rootScope.user.role == "MANUFACTURERADMIN"
				|| $rootScope.user.role == "RESELLERADMIN") {
			// load machine drop-down
			getMachinesList();
			// load technician drop-down
			getTechniciansByCompanyId();

			$('#editServiceRequest').modal().show();
		}

	}

	$scope.assignServiceRequestToTechnician = function() {
		var dlg = dialogs.confirm("Are you sure?",
				"Are you sure you want to update this record?", {
					'size' : 'sm'
				});
		dlg.result.then(function() {
			var promise = restAPIService.MachineAmcServiceHistoryResource(
					$scope.updatedRecord.id).update($scope.updatedRecord);
			promise.$promise.then(function(response) {
				$scope.setTab(2);
				getServiceHistoryList();
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

	$scope.onCancelRegularService = function() {
		$scope.setTab(2);
		getServiceHistoryList();
	};

	$scope.onResetSearchForm = function() {
		$scope.searchForm = '';
		$state.reload();
	};
	$scope.onSubmitSearchForm = function() {

		var promise1;
		if ($rootScope.user.role == "MANUFACTURERADMIN"
				|| $rootScope.user.role == "RESELLERADMIN") {
			// companyDetailsId of logged in web admin
			$scope.complaintSearchFilter = {};
			$scope.complaintSearchFilter.customerId = Number($scope.searchForm.customer);
			$scope.complaintSearchFilter.startDate = $scope.searchForm.startDate;
			$scope.complaintSearchFilter.endDate = $scope.searchForm.endDate;
			$scope.complaintSearchFilter.manResId = Number($rootScope.user.companyDetailsId);
			$scope.complaintSearchFilter.manResRole = $rootScope.user.role;

			promise1 = restAPIService.complaintListBySearchFilter().save(
					$scope.complaintSearchFilter);
		}
		if ($rootScope.user.role == "SUPERADMIN") {
			$scope.complaintSearchFilter = {};
			$scope.complaintSearchFilter.customerId = Number($scope.searchForm.customer);
			$scope.complaintSearchFilter.startDate = $scope.searchForm.startDate;
			$scope.complaintSearchFilter.endDate = $scope.searchForm.endDate;
			$scope.complaintSearchFilter.manResRole = $rootScope.user.role;
			promise1 = restAPIService.complaintListBySearchFilter().save(
					$scope.complaintSearchFilter);
		}

		promise1.$promise.then(function(response) {
			$scope.complaints = response;

		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});

	};

	// download as excel using $http post
	$scope.onDownloadAsExcel = function() {
		angular.element(document.getElementById('btnDownloadAsExcel'))[0].disabled = true;

		if ($rootScope.user.role == "MANUFACTURERADMIN"
				|| $rootScope.user.role == "RESELLERADMIN") {
			// companyDetailsId of logged in web admin
			$scope.complaintSearchFilter = {};
			$scope.complaintSearchFilter.customerId = Number($scope.searchForm.customer);
			$scope.complaintSearchFilter.startDate = $scope.searchForm.startDate;
			$scope.complaintSearchFilter.endDate = $scope.searchForm.endDate;
			$scope.complaintSearchFilter.manResId = Number($rootScope.user.companyDetailsId);
			$scope.complaintSearchFilter.manResRole = $rootScope.user.role;
		}
		if ($rootScope.user.role == "SUPERADMIN") {
			// companyDetailsId of logged in web admin
			$scope.complaintSearchFilter = {};
			$scope.complaintSearchFilter.customerId = Number($scope.searchForm.customer);
			$scope.complaintSearchFilter.startDate = $scope.searchForm.startDate;
			$scope.complaintSearchFilter.endDate = $scope.searchForm.endDate;
			// $scope.complaintSearchFilter.manResId =
			// Number($rootScope.user.companyDetailsId);
			$scope.complaintSearchFilter.manResRole = $rootScope.user.role;
		}

		var fileName = "complaint_list.xlsx";
		var url = $rootScope.apiUrl + "issues/listbyfilter/excel";
		$scope.Authorization = "Basic " + btoa($rootScope.token + "1:");

		$http
				.post(url, $scope.complaintSearchFilter, {
					responseType : 'arraybuffer',
					'Authorization' : $scope.Authorization
				})
				.success(
						function(data) {
							angular.element(document
									.getElementById('btnDownloadAsExcel'))[0].disabled = false;
							var file = new Blob([ data ], {
								type : 'application/octet-stream'
							});
							saveAs(file, fileName);
						})
				.error(
						function(data, status) {
							angular.element(document
									.getElementById('btnDownloadAsExcel'))[0].disabled = false;
							dialogs.error("Error", error.data.error, {
								'size' : 'sm'
							});
						});
	};
}
