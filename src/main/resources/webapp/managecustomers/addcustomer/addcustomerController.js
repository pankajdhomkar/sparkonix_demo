'use strict';

angular.module('sparkonixWebApp').controller('addcustomerController',
		addcustomerController);

function addcustomerController($scope, $state, restAPIService, dialogs,
		$rootScope, parseExcelService) {
	// $state.reload();
	// ------------- PUBLIC VARIABLES ----------------
	$scope.activeTabNumber = 1;
	$scope.saveButtonText = "Save and Next";
	$scope.paneltitles = [ "Provide company details",
			"Add the locations of the machines", "Give machine details",
			"Provide the mobile numbers that will be allowed to scan the machine codes." ];
	$scope.panelTitle = $scope.paneltitles[0];
	$scope.factoryLocations = [];
	$scope.machinesData = [];
	$scope.mobilenumbers = [];
	$scope.manufacturersList = [];
	$scope.resellersList = [];
	$scope.companyLocationsDropdown = [];
	$scope.phoneDevicesData = [];
	$scope.curSubscriptionTypes = [ "BASIC", "PREMIUM" ];
	$scope.curSubscriptionStatusData = [ "ACTIVE", "PAYMENT_DUE", "INACTIVE",
			"EXPIRED" ];
	$scope.curAmcTypes = [ "BASIC", "PREMIUM" ];
	$scope.curAmcStatusData = [ "ACTIVE", "PAYMENT_DUE", "INACTIVE", "EXPIRED" ];

	$scope.allMachines = [];
	$scope.machineGroupedByLocation;
	$scope.locationNames = [];
	$scope.qrcode = {};
	$scope.panValidationMsg = "";
	$scope.companyDetail = {};
	$scope.qrCodeValidationMsg = "";
	$scope.assignedQrCodeList = [];
	$scope.finalMachineBulkList = [];
	// $scope.newMachineObj = {};
	$scope.sampleFilePath = "/assets/BulkMachineSample.xlsx";
	$scope.sampleFileName = "BulkMachineSample.xlsx"
	$scope.machine={};	

	$scope.opened = {};
	$scope.open = function($event) {

		$event.preventDefault();
		$event.stopPropagation();

		$scope.opened = {};
		$scope.opened[$event.target.id] = true;
	};
	$scope.format = 'dd-MM-yyyy'

	// ------------- PRIVATE VARIABLES --------------

	// ------------- CONTROLLER CODE ----------------

	// load all qr codes list
	getAssignedQrCodeList();
	// load all man/res list
	getManResList();
	// begin();

	if ($scope.mode == "add") {
		$scope.activeTabNumber = 1;
		$scope.saveButtonText = "Submit";
		$scope.headerTitleText = "Add Customer Details";
		// disable tab links except first tab
		$scope.tabDetails = [ {
			title : 'Company Details',
			click : ''
		}, {
			title : 'Factory Locations',
			click : ''
		}, {
			title : 'Machines',
			click : ''
		}, {
			title : 'Authorized	Operators',
			click : ''
		} ];

	} else if ($scope.mode == "edit") {
		$scope.activeTabNumber = $scope.setTabId;
		$scope.headerTitleText = "Edit Customer Details";
		$scope.tabDetails = [ {
			title : 'Company Details',
			click : $scope.setTab
		}, {
			title : 'Factory Locations',
			click : $scope.setTab
		}, {
			title : 'Machines',
			click : $scope.setTab
		}, {
			title : 'Authorized	Operators',
			click : $scope.setTab
		} ];

		$scope.paneltitles = [ "Edit company details",
				"Edit locations of the machines", "Edit machine details",
				"Edit the mobile numbers that will be allowed to scan the machine codes." ];
		$scope.panelTitle = $scope.paneltitles[$scope.activeTabNumber - 1];

		// fetch & set customer detail data
		$scope.companyDetail = $scope.customer;

		// fetch & set factory location data
		getFactoryLocations();

		// fetch manufacturer dropdown list
		getCompanyDetailListByType("manufacturer");

		// fetch reseller dropdown list
		getCompanyDetailListByType("reseller");

		// fetch location dropdown list by onBoardedBy
		getCompanyLocationsListByOnBoarded();

		// fetch & set machines
		getMachinesListByCustomerIdAndOnBoardedBy();
		// getMobileNumbers();

		// fetch & set phone devices data
		getPhoneDevicesByCustomerIdAndOnboaradedBy();
	}

	// ------------- PRIVATE FUNCTIONS ----------------
	function getFactoryLocations() {
		var promise;
		if ($rootScope.user.role == "SALESTEAM") // let sales team see all
		{ // locations
			promise = restAPIService.companyLocationsByCompanyId(
					$scope.customer.id).query();

		} else if ($rootScope.user.role == "SUPERADMIN") {
			promise = restAPIService.companyLocationsByCompanyId(
					$scope.customer.id).query();
		} else {
			// WEBADMIN
			promise = restAPIService.companyLocationsForCompanyOnboardedBy(
					$scope.customer.id, $rootScope.user.id).query();
		}
		promise.$promise.then(function(response) {
			$scope.factoryLocations = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	function getCompanyDetailListByType(companyType) {
		var promise = restAPIService.companyDetailsByCompanyTypeResource(
				companyType).query();

		// dropdwon should display all Man/Res while adding new machine
		promise.$promise.then(function(response) {
			if (companyType == "manufacturer") {
				$scope.manufacturersList = response;
			}
			if (companyType == "reseller") {
				$scope.resellersList = response;
			}

		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	function getCompanyLocationsListByOnBoarded() {
		var promise;

		if ($rootScope.user.role == "SUPERADMIN") {
			promise = restAPIService.companyLocationsByCompanyId(
					$scope.customer.id).query();
		} else {
			promise = restAPIService.companyLocationsForCompanyOnboardedBy(
					$scope.customer.id, $rootScope.user.id).query();
		}
		promise.$promise.then(function(response) {
			$scope.companyLocationsDropdown = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	function getMachinesListByCustomerIdAndOnBoardedBy() {
		var promise;

		// display all
		if ($rootScope.user.role == "SUPERADMIN") {
			promise = restAPIService.machinesByCustomerId($scope.customer.id)
					.query();
		} else {
			promise = restAPIService.machinesByCustomerIdAndOnBoardedBy(
					$scope.customer.id, $rootScope.user.id).query();
		}
		promise.$promise.then(
				function(response) {
					$scope.machinesData = response;

					$scope.allMachines = response;
					$scope.machineGroupedByLocation = groupBy(
							$scope.allMachines, 'location');
					$scope.locationNames = Object
							.keys($scope.machineGroupedByLocation);

				}, function(error) {
					dialogs.error("Error", error.data.error, {
						'size' : 'sm'
					});
				});
	}

	function groupBy(xs, key) {
		return xs.reduce(function(rv, x) {
			(rv[x[key]] = rv[x[key]] || []).push(x);
			return rv;
		}, {});
	}
	;

	function getPhoneDevicesByCustomerIdAndOnboaradedBy() {
		var promise;
		if ($rootScope.user.role == "SUPERADMIN") {
			promise = restAPIService.phoneDevicesByCustomerId(
					$scope.customer.id).query();
		} else if ($rootScope.user.role == "SALESTEAM"
				|| $rootScope.user.role == "MANUFACTURERADMIN"
				|| $rootScope.user.role == "RESELLERADMIN") {
			// for WWEBADMIN & SALESTEAM
			promise = restAPIService.phoneDevicesByCustomerIdAndOnBoardedBy(
					$scope.customer.id, $rootScope.user.id).query();
		}
		promise.$promise.then(function(response) {
			$scope.phoneDevicesData = response;

		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	// ------------- PUBLIC FUNCTIONS ----------------
	$scope.onBack = function() {
		$scope.setTab($scope.activeTabNumber - 1);
	};

	$scope.onSaveAndNext = function() {
		if ($scope.mode == "add") {
			//disable submit btn
			angular.element(document.getElementById('btnSaveAndNext'))[0].disabled = true;
			
			$scope.companyDetail.companyType = "CUSTOMER";
			$scope.companyDetail.onBoardedBy = Number($rootScope.user.id);

			var customerObj = restAPIService.companyDetailsResource().save(
					$scope.companyDetail);
			customerObj.$promise.then(function(response) {
				$scope.customer = JSON.parse(response.entity);

				$scope.parent = false;
				$scope.mode = "edit";
				$scope.setTabId = 2;

				$scope.companyDetail = $scope.customer;
				// convert string to Date
				if ($scope.companyDetail.curSubscriptionStartDate != null) {
					$scope.companyDetail.curSubscriptionStartDate = new Date(
							$scope.companyDetail.curSubscriptionStartDate);
				}
				if ($scope.companyDetail.curSubscriptionEndDate != null) {
					$scope.companyDetail.curSubscriptionEndDate = new Date(
							$scope.companyDetail.curSubscriptionEndDate);

				}
				
				if($scope.companyDetail.curSubscriptionStatus=="INACTIVE"){					
					$scope.companyDetail.curSubscriptionStartDate="";
					$scope.companyDetail.curSubscriptionEndDate="";
				}
				
				// fetch manufacturer dropdown list
				getCompanyDetailListByType("manufacturer");

				// fetch reseller dropdown list
				getCompanyDetailListByType("reseller");

				// fetch reseller dropdown list
				getCompanyDetailListByType("reseller");
				dialogs.notify("Success", response.message, {
					'size' : 'sm'
				});

				$state.go("home.managecustomers.addcustomer");
				$scope.setTab(2);
				angular.element(document.getElementById('btnSaveAndNext'))[0].disabled = false;

			}, function(error) {
				angular.element(document.getElementById('btnSaveAndNext'))[0].disabled = false;
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
			$scope.panValidationMsg = "";
			// $scope.setTab(2);
		} else if ($scope.mode == "edit") {
			angular.element(document.getElementById('btnSaveAndNext'))[0].disabled = true;
			$scope.bulkUploadDivFlag = false;
			var cuurentTab = $scope.activeTabNumber;
			// update customer detail
			if (cuurentTab == 1) {
				var companyDetailId = Number($scope.customer.id);
				
				if($scope.companyDetail.curSubscriptionStatus=="INACTIVE"){					
					$scope.companyDetail.curSubscriptionStartDate="";
					$scope.companyDetail.curSubscriptionEndDate="";
				}
				var promise = restAPIService.companyDetailResource(
						companyDetailId).update($scope.customer);
				promise.$promise.then(function(response) {
					dialogs.notify("Success", response.success, {
						'size' : 'sm'
					});
					angular.element(document.getElementById('btnSaveAndNext'))[0].disabled = false;
				}, function(error) {
					angular.element(document.getElementById('btnSaveAndNext'))[0].disabled = false;
					dialogs.error("Error", error.data.error, {
						'size' : 'sm'
					});
				});
				$scope.setTab($scope.activeTabNumber + 1);
			}

			if (cuurentTab == 2) {
				$scope.setTab($scope.activeTabNumber + 1);
			}
			if (cuurentTab == 3) {

				$scope.setTab($scope.activeTabNumber + 1);
			} 

		}
	};

	$scope.onCancel = function() {
		$state.go('home.managecustomers');
		$state.reload();
	};

	$scope.addLocationRow = function() {
		$scope.factorylocations.push(' ');
	}

	$scope.removeLocationRow = function(index) {
		if ($scope.factorylocations.length > 1) {
			$scope.factorylocations.splice(index, 1);
			// remove value of that index too from companyLocations obj
			// delete $scope.companyLocations[index];
		}
	}

	$scope.addMobileRow = function() {
		$scope.mobilenumbers.push(' ');
	}

	$scope.removeMobileRow = function(index) {
		if ($scope.mobilenumbers.length > 1) {
			$scope.mobilenumbers.splice(index, 1);
			delete $scope.phoneDevices[index];
		}
	}

	$scope.setTab = function(tabId) {

		$scope.activeTabNumber = tabId;
		$scope.panelTitle = $scope.paneltitles[tabId - 1];
		 
		if (tabId == 4) {
			$scope.saveButtonText = "Submit";
		} else {
			$scope.saveButtonText = "Save and Next";
		}

	};

	$scope.isSet = function(tabId) {
		return $scope.activeTabNumber === tabId;
	};

	/** ******************************************************* */
	// add/edit factory location
	$scope.addNewLocation = function() {
		$scope.locationModalTitle = "Add Location";
		$scope.modalLocationMode = "add";
		$scope.companyLocation = '';

	}
	$scope.editLocation = function(location) {
		$scope.locationModalTitle = "Edit Location";
		$scope.modalLocationMode = "edit";
		$scope.companyLocation = location;
		$scope.locationId = location.id;

		// get all detail by $location id
		if ($scope.locationId != null) {
			var locationObj = restAPIService.companyLocationResource(
					$scope.locationId).get();
			locationObj.$promise.then(function(response) {
				$scope.companyLocation = response;
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}
	}

	$scope.onSaveLocation = function() {
		if ($scope.modalLocationMode == "add") {
			$scope.companyLocation.onBoardedBy = $rootScope.user.id;
			$scope.companyLocation.companyDetailsId = $scope.customer.id;
			var locationObj = restAPIService.companyLocationsResource().save(
					$scope.companyLocation);
			locationObj.$promise.then(function(response) {
				// $scope.factoryLocations.push(response);
				getFactoryLocations();
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		} else if ($scope.modalLocationMode == "edit") {
			$scope.companyLocation.onBoardedBy = $rootScope.user.id;
			var locationId = Number($scope.companyLocation.id);
			var promise = restAPIService.companyLocationResource(locationId)
					.update($scope.companyLocation);
			promise.$promise.then(function(response) {
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});
				getFactoryLocations();
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
			$scope.setTabId = 2;
			$state.go("home.managecustomers.addcustomer");
		}
	}

	/** **************** add/edit machine ************************************* */
	$scope.addNewMachine = function() {
		$scope.machineModalTitle = "Add Machine";
		$scope.modalMachineMode = "add";
		// reset
		$scope.machine = '';
		
		//was initialized as string, needed to be an object. Edit Machine bug solved here
		/*$scope.qrcode = '';*/ 
		$scope.qrcode = {};
		
		$scope.qrCodeValidationMsg = '';

		// fetch location dropdown list by onBoardedBy
		getCompanyLocationsListByOnBoarded();
	}
	$scope.editMachine = function(machine) {
		$scope.machineModalTitle = "Edit Machine";
		$scope.modalMachineMode = "edit";
		$scope.qrCodeValidationMsg = '';
		$scope.machine = '';
		$scope.machineId = machine.machineId;
		$scope.qrcode.part1 = undefined;
		$scope.qrcode.part2 = undefined;
		$scope.qrcode.part3 = undefined;

		// get all detail by machine id
		if ($scope.machineId != null) {
			var machineObj = restAPIService.machineResource($scope.machineId)
					.get();
			machineObj.$promise.then(function(response) {
				$scope.machine = response;

				// split & set qrcode data
				var qrCodeString = $scope.machine.qrCode;
				var qrCodeArr = [];
				if (qrCodeString != undefined || qrCodeString != null) {
					qrCodeArr = qrCodeString.split("-");

					$scope.qrcode.part1 = qrCodeArr[0];
					$scope.qrcode.part2 = qrCodeArr[1];
					$scope.qrcode.part3 = qrCodeArr[2];

				}

				// convert long to Date
				if ($scope.machine.installationDate != null) {
					$scope.machine.installationDate = new Date(
							$scope.machine.installationDate);
				}
				if ($scope.machine.warrantyExpiryDate != null) {
					$scope.machine.warrantyExpiryDate = new Date(
							$scope.machine.warrantyExpiryDate);
				}
				if ($scope.machine.curAmcStartDate != null) {
					$scope.machine.curAmcStartDate = new Date(
							$scope.machine.curAmcStartDate);
				}
				if ($scope.machine.curAmcEndDate != null) {
					$scope.machine.curAmcEndDate = new Date(
							$scope.machine.curAmcEndDate);
				}
				if ($scope.machine.curSubscriptionStartDate != null) {
					$scope.machine.curSubscriptionStartDate = new Date(
							$scope.machine.curSubscriptionStartDate);
				}
				if ($scope.machine.curSubscriptionEndDate != null) {
					$scope.machine.curSubscriptionEndDate = new Date(
							$scope.machine.curSubscriptionEndDate);
				}

			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}
	}

	$scope.onSaveMachine = function() {
		if ($scope.modalMachineMode == "add") {
			//disable submit btn
			angular.element(document.getElementById('btnSaveMachine'))[0].disabled = true;
			
			if ($rootScope.user.role == "MANUFACTURERADMIN"){
				$scope.machine.manufacturerId=$rootScope.user.companyDetailsId;
			}
			if ($rootScope.user.role == "RESELLERADMIN"){
				$scope.machine.resellerId=$rootScope.user.companyDetailsId;
			} 
			 
			$scope.machine.onBoardedBy = $rootScope.user.id;
			$scope.machine.customerId = $scope.customer.id;

			// construct QRcode		 
			
			if (($scope.qrcode.part1 == undefined || $scope.qrcode.part1=='' || $scope.qrcode.part1==null ) 
					&& ($scope.qrcode.part2 == undefined || $scope.qrcode.part2=='' || $scope.qrcode.part2==null)
					&& ($scope.qrcode.part3 == undefined || $scope.qrcode.part3=='' || $scope.qrcode.part3==null)){
				$scope.machine.qrCode = null;
			}else{
				$scope.machine.qrCode = $scope.qrcode.part1 + "-"
				+ $scope.qrcode.part2 + "-" + $scope.qrcode.part3;
			}
			
			if ($scope.machine.curAmcStatus == "INACTIVE") {
				$scope.machine.curAmcStartDate = "";
				$scope.machine.curAmcEndDate = "";
			}
			if ($scope.machine.curSubscriptionStatus == "INACTIVE") {
				$scope.machine.curSubscriptionStartDate = "";
				$scope.machine.curSubscriptionEndDate= "";
			}

			var machineObj = restAPIService.machinesResource().save(
					$scope.machine);
			machineObj.$promise.then(function(response) {
				angular.element(document.getElementById('btnSaveMachine'))[0].disabled = false;
				$("#addMachine").modal('hide');
				$('.modal-backdrop').remove();
				$('body').removeClass('modal-open');
				
				// $scope.machinesData.push(response);
				dialogs.notify("Success", "Machine added successfully.", {
					'size' : 'sm'
				});
				getMachinesListByCustomerIdAndOnBoardedBy();
				

			}, function(error) {
				angular.element(document.getElementById('btnSaveMachine'))[0].disabled = false;
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}
		if ($scope.modalMachineMode == "edit") {
			angular.element(document.getElementById('btnSaveMachine'))[0].disabled = true;
			
			if ($rootScope.user.role == "MANUFACTURERADMIN"){
				$scope.machine.manufacturerId=$rootScope.user.companyDetailsId;
			}
			if ($rootScope.user.role == "RESELLERADMIN"){
				$scope.machine.resellerId=$rootScope.user.companyDetailsId;
			} 
			
			$scope.machine.onBoardedBy = $rootScope.user.id;
			var machineId = Number($scope.machine.id);

			// construct QRcode
			if (($scope.qrcode.part1 == undefined || $scope.qrcode.part1=='' || $scope.qrcode.part1==null ) 
					&& ($scope.qrcode.part2 == undefined || $scope.qrcode.part2=='' || $scope.qrcode.part2==null)
					&& ($scope.qrcode.part3 == undefined || $scope.qrcode.part3=='' || $scope.qrcode.part3==null)){
				$scope.machine.qrCode = null;
			}else{
				$scope.machine.qrCode = $scope.qrcode.part1 + "-"
				+ $scope.qrcode.part2 + "-" + $scope.qrcode.part3;
			}
			
			/*if ($scope.qrcode != null) {
				if (($scope.qrcode.part1 != undefined || ($scope.qrcode.part1).length != 0) 
						|| ($scope.qrcode.part2 != undefined || ($scope.qrcode.part2).length != 0)
						|| ($scope.qrcode.part3 != undefined || ($scope.qrcode.part3).length != 0)) {
					$scope.machine.qrCode = $scope.qrcode.part1 + "-"
							+ $scope.qrcode.part2 + "-" + $scope.qrcode.part3;
				}

			} else {
				$scope.qrcode = null;
			}*/
			
			if ($scope.machine.curAmcStatus == "INACTIVE") {
				$scope.machine.curAmcStartDate = "";
				$scope.machine.curAmcEndDate = "";
			}
			if ($scope.machine.curSubscriptionStatus == "INACTIVE") {
				$scope.machine.curSubscriptionStartDate = "";
				$scope.machine.curSubscriptionEndDate= "";
			}

			var promise = restAPIService.machineResource(machineId).update(
					$scope.machine);
			promise.$promise.then(function(response) {
				angular.element(document.getElementById('btnSaveMachine'))[0].disabled = false;
				$("#addMachine").modal('hide');
				$('.modal-backdrop').remove();
				$('body').removeClass('modal-open');
				
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});
				getMachinesListByCustomerIdAndOnBoardedBy();
				$scope.setTabId = 3;
				$state.go("home.managecustomers.addcustomer");
			}, function(error) {
				angular.element(document.getElementById('btnSaveMachine'))[0].disabled = false;
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});

		}
	}

	/**
	 * **************** add/edit phone devices[OPERATOR]
	 * *************************************
	 */
	$scope.addNewOperator = function() {
		$scope.operatorModalTitle = "Add Operator";
		$scope.operatorModalMode = "add";
		$scope.operator = '';

		// fetch location dropdown list by onBoardedBy
		getCompanyLocationsListByOnBoarded();
	}
	$scope.editOperator = function(phoneDevice) {
		$scope.operatorModalTitle = "Edit Operator";
		$scope.operatorModalMode = "edit";
		$scope.phoneDevice = phoneDevice;
		$scope.phoneDeviceId = Number(phoneDevice.id);

		// get all detail by machine id
		if ($scope.phoneDeviceId != null) {
			var promise = restAPIService.phoneDeviceResource(
					$scope.phoneDeviceId).get();
			promise.$promise
					.then(
							function(response) {
								$scope.operator = response;
								//$scope.operator.phoneNumber = Number($scope.operator.phoneNumber);
							}, function(error) {
								dialogs.error("Error", error.data.error, {
									'size' : 'sm'
								});
							});
		}
	}

	$scope.onSaveOperator = function() {
		if ($scope.operatorModalMode == "add") {
			
			angular.element(document.getElementById('btnSaveOperator'))[0].disabled = true;
			$scope.operator.onBoardedBy = $rootScope.user.id;
			$scope.operator.customerId = $scope.customer.id;
			var promise = restAPIService.phoneDevicesResource().save(
					$scope.operator);
			promise.$promise.then(function(response) {
				angular.element(document.getElementById('btnSaveOperator'))[0].disabled = false;
				$("#addOperator").modal('hide');
				$('.modal-backdrop').remove();
				$('body').removeClass('modal-open');
				
				dialogs.notify("Success", "Operator created successfully", {
					'size' : 'sm'
				});
				getPhoneDevicesByCustomerIdAndOnboaradedBy();
			}, function(error) {
				angular.element(document.getElementById('btnSaveOperator'))[0].disabled = false;
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		} else if ($scope.operatorModalMode == "edit") {
			angular.element(document.getElementById('btnSaveOperator'))[0].disabled = true;
			// $scope.operator.onBoardedBy = $rootScope.user.id;
			var phoneDeviceId = Number($scope.phoneDevice.id);
			var promise = restAPIService.phoneDeviceResource(phoneDeviceId)
					.update($scope.operator);
			promise.$promise.then(function(response) {
				angular.element(document.getElementById('btnSaveOperator'))[0].disabled = false;
				$("#addOperator").modal('hide');
				$('.modal-backdrop').remove();
				$('body').removeClass('modal-open');
				
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});
				getPhoneDevicesByCustomerIdAndOnboaradedBy();
			}, function(error) {
				angular.element(document.getElementById('btnSaveOperator'))[0].disabled = false;
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
			$scope.setTabId = 4;
			$state.go("home.managecustomers.addcustomer");
		}
	}

	$scope.checkCompanyPanAvailability = function() {
		$scope.panValidationMsgColor = "red";

		if ($scope.companyDetail.pan != undefined) {
			if (($scope.companyDetail.pan).length == 10) {
				var promise = restAPIService
						.companyDetailByCompanyTypeAndCompanyPan("CUSTOMER",
								$scope.companyDetail.pan).get();
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
		} else if ($scope.companyDetail.pan == undefined) {
			$scope.panValidationMsg = "Enter company PAN";

		}
	}

	$scope.checkQRCodeAvailability = function() {
		$scope.qrCodeValidationMsgColor = "red";

		if ($scope.qrcode.part1 != undefined
				&& $scope.qrcode.part2 != undefined
				&& $scope.qrcode.part3 != undefined) {
			if (($scope.qrcode.part1).length == 4
					&& ($scope.qrcode.part2).length == 4
					&& ($scope.qrcode.part3).length == 4) {

				// construct QRcode
				var qrCodeString;
				if ($scope.qrcode != null) {
					qrCodeString = $scope.qrcode.part1 + "-"
							+ $scope.qrcode.part2 + "-" + $scope.qrcode.part3;
				} else {
					qrCodeString = "";
				}

				var promise = restAPIService.checkMachineQrCode(qrCodeString)
						.get();
				promise.$promise
						.then(function(response) {
							if (response.isQRcodeAssigned == true) {
								// qr code already used by this machine
								$scope.qrCodeValidationMsg = "This QR code is not available";
							} else {
								$scope.qrCodeValidationMsgColor = "green";
								// this qr code is not being used by this
								// machine
								$scope.qrCodeValidationMsg = "This QR code is available";
							}
						});
			} else {
				$scope.qrCodeValidationMsg = "Enter 4 char of QR code into each box";
			}
		} else {
			$scope.qrCodeValidationMsg = "Enter QR code";

		}
	}

	$scope.setCustomerSubEndDate = function() {
		var curSubStartDate = $scope.companyDetail.curSubscriptionStartDate;
		var curSubEndDate = new Date();
		curSubEndDate.setYear(curSubStartDate.getFullYear() + 1);
		curSubEndDate.setMonth(curSubStartDate.getMonth());
		curSubEndDate.setDate(curSubStartDate.getDate());
		$scope.companyDetail.curSubscriptionEndDate = "";

		// set end date
		$scope.companyDetail.curSubscriptionEndDate = curSubEndDate;
	}

	$scope.setMachineSubEndDate = function() {
		var curSubStartDate = $scope.machine.curSubscriptionStartDate;
		var curSubEndDate = new Date();
		curSubEndDate.setYear(curSubStartDate.getFullYear() + 1);
		curSubEndDate.setMonth(curSubStartDate.getMonth());
		curSubEndDate.setDate(curSubStartDate.getDate());
		$scope.machine.curSubscriptionEndDate = "";

		// set end date
		$scope.machine.curSubscriptionEndDate = curSubEndDate;
	}

	// upload excel sheet
	$scope.showBulkUploadDiv = function() {
		$scope.bulkUploadDivFlag = true;
		$scope.btnUploadBulkMachine="Upload";
		// clear earlier selected file
		if (document.getElementById("uploadFile").value != "") {
			document.getElementById("uploadFile").value = "";
		}

	}

	$scope.hideBulkUploadDiv = function() {
		$scope.bulkUploadDivFlag = false;
	}

	$scope.uploadExcelFile = function() {
		if (document.getElementById("uploadFile").value != "") {		
			uploadConvertExcelFile();
			
		} else {
			dialogs.error("Error", "Please choose file to upload.", {
				'size' : 'sm'
			});
		}

	}

	function uploadConvertExcelFile() {
		
		angular.element(document.getElementById('btnUploadBulkMachine'))[0].disabled = true;
		$scope.btnUploadBulkMachine="Uploading.."; 
		 
		var fileName=document.getElementById("uploadFile");
		var extArray = ['xlsx'];          
        var extension = fileName.value.substring(fileName.value.lastIndexOf('.') + 1).toLowerCase();
          if (extArray.indexOf(extension) <= -1) {
            
	        angular.element(document.getElementById('btnUploadBulkMachine'))[0].disabled = false;
	  		$scope.btnUploadBulkMachine="Upload";
	  			dialogs.error("Error", "Please choose file with .xlsx extention.", {
					'size' : 'sm'
				});
            return false;
          }
		   
        var file = document.getElementById('uploadFile').files[0];   
		var ej = parseExcelService.excelUploadService();
		ej.parseExcel(file).then(function success(data) {			
			parserJsonData(data.finalData);
			
		}, function failure(err) {			
			angular.element(document.getElementById('btnUploadBulkMachine'))[0].disabled = false;
			$scope.btnUploadBulkMachine="Upload";
			 
			dialogs.error("Error", err, {
				'size' : 'sm'
			});
			
		});
	}

	function parserJsonData(jsonData) {
		var errorMessage = "";		 
		var uploadErrorMessage = "";
		var keepGoing = true;

		angular
				.forEach(
						jsonData,
						function(obj, key) {
							if (keepGoing) {
								// skip row in sheet which has comment text
								if (key == 0) {
									// skip it
									// console.log("skiped comment row")
								} else {
									// execute it
									var machineObj = {};
									machineObj.name = obj["MachineName"];
									machineObj.qrCode = obj["QRcode"];
									machineObj.serialNumber = obj["SerialNumber*"];
									machineObj.modelNumber = obj["ModelNumber*"];
									machineObj.description = obj["Description"];
									machineObj.machineYear = obj["MachineYear"];
									machineObj.manufacturerName = obj["ManufacturerName"];
									machineObj.manufacturerPAN = obj["ManufacturerPAN*"];
									machineObj.resellerName = obj["ResellerName"];
									machineObj.resellerPAN = obj["ResellerPAN"];
									machineObj.installationDate = obj["InstallationDate"];
									machineObj.warrantyExpiryDate = obj["WarrantyExpiryDate"];
									machineObj.locationId = obj["Location*"];
									machineObj.supportAssistance = obj["SupportAssistancAttendMe(M/R)*"];
									machineObj.curAmcType = obj["AmcType"];
									machineObj.curAmcStatus = obj["AmcStatus"];
									machineObj.curAmcStartDate = obj["AmcStartDate"];
									machineObj.curAmcEndDate = obj["AmcEndDate"];
									machineObj.curSubscriptionType = obj["AttendMeSubscriptionType*"];
									machineObj.curSubscriptionStatus = obj["AttendMeSubscriptionStatus*"];
									machineObj.curSubscriptionStartDate = obj["AttendMeSubscriptionStartDate"];
									machineObj.curSubscriptionEndDate = obj["AttendMeSubscriptionEndDate"];

									machineObj.customerId = "";
									machineObj.manufacturerId = "";
									machineObj.resellerId = "";

									if (machineObj.serialNumber != undefined
											&& machineObj.modelNumber != undefined
											&& machineObj.serialNumber != undefined
											&& machineObj.manufacturerPAN != undefined
											&& machineObj.locationId != undefined
											&& machineObj.supportAssistance != undefined
											&& machineObj.curSubscriptionType != undefined
											&& machineObj.curSubscriptionStatus != undefined) {

										// find into loaded list
										if (machineObj.qrCode != undefined) {
											$scope.assignedQrCode = checkAssignedQrCode(machineObj.qrCode);
											if ($scope.assignedQrCode == null) {
												keepGoing = false;
												errorMessage = "This is not valid AttendMe QR code.";
											}
											if (keepGoing
													&& $scope.assignedQrCode != null
													&& $scope.assignedQrCode.status == "ASSIGNED") {
												// qr code is not available.
												// already assigned to other
												// machine
												keepGoing = false;
												errorMessage = "This QR code not available since it is already assigned.";
											}
										}
										
										
										// check PAN-manufacturer
										$scope.existingManRes = existingManResListByPAN(machineObj.manufacturerPAN
												, "MANUFACTURER");
										if (keepGoing
												&& $scope.existingManRes == null) {
											// this is not valid PAN
											keepGoing = false;
											errorMessage = "Unable to find manufacturer with this PAN.";
										}
										if (keepGoing
												&& $scope.existingManRes != null
												&& $scope.existingManRes.companyType == "MANUFACTURER") {
											// set manufacturer id
											machineObj.manufacturerId = $scope.existingManRes.id;
										}
										// check PAN-reseller
										if (keepGoing
												&& machineObj.resellerPAN != undefined) {
											$scope.existingManRes2 = existingManResListByPAN(machineObj.resellerPAN
													, "RESELLER");
											if (keepGoing
													&& $scope.existingManRes2 == null) {
												// this is not valid PAN
												keepGoing = false;
												errorMessage = "Unable to find reseller with this PAN.";
											}
											if (keepGoing
													&& $scope.existingManRes2 != null
													&& $scope.existingManRes2.companyType == "RESELLER") {
												// set reseller id
												machineObj.resellerId = $scope.existingManRes2.id;
											}
										}
										// check date
										if (keepGoing) {
											if (machineObj.installationDate != undefined
													&& validateDate(machineObj.installationDate)) {
												machineObj.installationDate = new Date(
														machineObj.installationDate);
											} else {
												keepGoing = false;
												errorMessage = "Instatllation date is not valid.";
											}
										}

										if (keepGoing) {
											if (machineObj.warrantyExpiryDate != undefined
													&& validateDate(machineObj.warrantyExpiryDate)) {
												machineObj.warrantyExpiryDate = new Date(
														machineObj.warrantyExpiryDate);
											} else {
												keepGoing = false;
												errorMessage = "Warranty expiry date is not valid.";
											}
										}

										// check supportAssistance
										if (keepGoing) {
											if ((machineObj.supportAssistance
													.toUpperCase()) == "M") {
												machineObj.supportAssistance = "MANUFACTURER";
											} else if ((machineObj.supportAssistance
													.toUpperCase()) == "R") {
												machineObj.supportAssistance = "RESELLER";
											} else {
												keepGoing = false;
												errorMessage = "Support assistance should be either M or R.";
											}
										}
										// check subscription date
										if (keepGoing) {
											if (machineObj.curSubscriptionStatus != "INACTIVE") {
												// start /end date required
												if (machineObj.curSubscriptionStartDate != undefined
														&& validateDate(machineObj.curSubscriptionStartDate)) {
													machineObj.curSubscriptionStartDate = new Date(
															machineObj.curSubscriptionStartDate);
												} else {
													keepGoing = false;
													errorMessage = "AttendMe subscription start date is not valid.";
												}
											} else {
												machineObj.curSubscriptionStartDate = "";
											}
										}

										if (keepGoing) {
											if (machineObj.curSubscriptionStatus != "INACTIVE") {
												if (machineObj.curSubscriptionEndDate != undefined
														&& validateDate(machineObj.curSubscriptionEndDate)) {
													machineObj.curSubscriptionEndDate = new Date(
															machineObj.curSubscriptionEndDate);
												} else {
													keepGoing = false;
													errorMessage = "AttendMe subscription end date is not valid.";
												}
											} else {
												machineObj.curSubscriptionEndDate = "";
											}
										}

									} else {
										// end of required field validation
										keepGoing = false;
										errorMessage = "Required field is missing.";
									}

									// if error occurred then break & return
									// error message
									if (!keepGoing) {
										uploadErrorMessage = "Error occured in row number "
												+ (key + 2)
												+ ". \n Reason: "
												+ errorMessage;

									} else {
										// if error not occurred then push this
										// object into finalMachineBulkList
										$scope.newMachineObj = {};

										$scope.newMachineObj.name = machineObj.name;
										$scope.newMachineObj.qrCode = machineObj.qrCode;
										$scope.newMachineObj.modelNumber = machineObj.modelNumber;
										$scope.newMachineObj.serialNumber = machineObj.serialNumber;
										$scope.newMachineObj.description = machineObj.description;
										$scope.newMachineObj.machineYear = machineObj.machineYear;
										$scope.newMachineObj.manufacturerId = machineObj.manufacturerId;
										$scope.newMachineObj.resellerId = machineObj.resellerId;
										$scope.newMachineObj.installationDate = machineObj.installationDate;
										$scope.newMachineObj.warrantyExpiryDate = machineObj.warrantyExpiryDate;
										$scope.newMachineObj.locationId = Number(machineObj.locationId);
										$scope.newMachineObj.supportAssistance = machineObj.supportAssistance;
										$scope.newMachineObj.curAmcType = machineObj.curAmcType;
										$scope.newMachineObj.curAmcStatus = machineObj.curAmcStatus;
										$scope.newMachineObj.curAmcStartDate = machineObj.curAmcStartDate;
										$scope.newMachineObj.curAmcEndDate = machineObj.curAmcEndDate;
										$scope.newMachineObj.curSubscriptionType = machineObj.curSubscriptionType;
										$scope.newMachineObj.curSubscriptionStatus = machineObj.curSubscriptionStatus;
										$scope.newMachineObj.curSubscriptionStartDate = machineObj.curSubscriptionStartDate;
										$scope.newMachineObj.curSubscriptionEndDate = machineObj.curSubscriptionEndDate;
										$scope.newMachineObj.customerId = $scope.customer.id;
										$scope.newMachineObj.onBoardedBy = $rootScope.user.id;

										$scope.finalMachineBulkList
												.push($scope.newMachineObj);
									}
								}
							}

						});

		if (!keepGoing) {
			angular.element(document.getElementById('btnUploadBulkMachine'))[0].disabled = false;
			$scope.btnUploadBulkMachine="Upload";
			
			dialogs.error("Error", uploadErrorMessage, {
				'size' : 'sm'
			});
		} else {
			var promise = restAPIService.uploadBulkMachineList().save(
					$scope.finalMachineBulkList);
			promise.$promise.then(function(response) {
				angular.element(document.getElementById('btnUploadBulkMachine'))[0].disabled = false;
				$scope.btnUploadBulkMachine="Upload";
				
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});
				getMachinesListByCustomerIdAndOnBoardedBy();
				// hide bulk upload div
				$scope.bulkUploadDivFlag = false;
			}, function(error) {
				angular.element(document.getElementById('btnUploadBulkMachine'))[0].disabled = false;
				$scope.btnUploadBulkMachine="Upload";
				
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}
	}

	function getAssignedQrCodeList() {
		var promise = restAPIService.qrcodesResource().query();
		promise.$promise.then(function(response) {
			$scope.assignedQrCodeList = response;
		}, function(error) {
			console.log(error.data.error);

		});

	}
	// load list to
	function getManResList() {
		var promise = restAPIService.companyDetailsResource().query();
		promise.$promise.then(function(response) {
			$scope.manResList = response;

		}, function(error) {
			console.log(error.data.error);

		});

	}

	function checkAssignedQrCode(qrCodeString) {
		for (var i = 0; i < ($scope.assignedQrCodeList).length; i++) {
			if ($scope.assignedQrCodeList[i]["qrCode"] == qrCodeString) {
				return ($scope.assignedQrCodeList[i]);
			}
		}
		return null;
	}

	function existingManResListByPAN(manResPAN, manResType) {
		
		for (var i = 0; i < ($scope.manResList).length; i++) {
			if ($scope.manResList[i]["pan"] == manResPAN && $scope.manResList[i]["companyType"] == manResType) {
				return ($scope.manResList[i]);
			}
		}
		return null;
	}

	function validateDate(dateString) {
		var date = new Date(dateString);
		if (isNaN(date.getTime())) {
			// date is not valid
			return false;
		} else {
			// date is valid
			return true;
		}
	}

}