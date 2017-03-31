'use strict';

angular.module('sparkonixWebApp').controller('managesubscriptionsController',
		managesubscriptionsController);

function managesubscriptionsController($scope, $rootScope, restAPIService,
		dialogs, $state, $http) {
	// ------------- PUBLIC VARIABLES ---------------
	$scope.subscriptionData = [];
	$scope.searchForm = {};
	$scope.searchResultMsg="Nothing to display, Please use search filter.";

	// date-picker format
	$scope.format = 'dd-MM-yyyy'

	// ------------- PRIVATE VARIABLES --------------

	// ------------- CONTROLLER CODE ----------------
	// get months & years drop-down 
	$scope.years = getYears();
	$scope.months = getMonths();

	// ------------- PRIVATE FUNCTIONS --------------

	function getYears() {
		var currentYear = new Date().getFullYear();
		var maxYear = new Date().getFullYear()+10;
		var years = [];
		
		for (var i = currentYear; i <= maxYear ; i++) { 
			 years.push(i); 
		}		 
		return years;
	}
	function getMonths() {
		var months = [];
	        months.push({value:1, text:'January'});
	        months.push({value:2, text:'February'});
	        months.push({value:3, text:'March'});
	        months.push({value:4, text:'April'});
	        months.push({value:5, text:'May'});
	        months.push({value:6, text:'June'});
	        months.push({value:7, text:'July'});
	        months.push({value:8, text:'August'});
	        months.push({value:9, text:'September'});
	        months.push({value:10, text:'October'});
	        months.push({value:11, text:'November'});
	        months.push({value:12, text:'December'});
        return months;
	}
	// ------------- PUBLIC FUNCTIONS ---------------

	$scope.onResetSearchForm = function() {
		$scope.searchForm = '';
		$state.reload();
	};

	$scope.onSubmitSearchForm = function() {		
		
		var attendmeSubEndMonth = $scope.searchForm.attendmeSubEndDateMonth;
		var attendmeSubEndYear = $scope.searchForm.attendmeSubEndDateYear;

		var warrantyExpEndMonth = $scope.searchForm.warrantyExpiryDateMonth;
		var warrantyExpEndYear = $scope.searchForm.warrantyExpiryDateYear;

		var amcSubEndMonth = $scope.searchForm.amcSubEndDateMonth;
		var amcSubEndYear = $scope.searchForm.amcSubEndDateYear;
		
		if(attendmeSubEndMonth==undefined){
			attendmeSubEndMonth=0;
		}
		if(attendmeSubEndYear==undefined){
			attendmeSubEndYear=0;
		}
		if(warrantyExpEndMonth==undefined){
			warrantyExpEndMonth=0;
		}
		if(warrantyExpEndYear==undefined){
			warrantyExpEndYear=0;
		}
		if(amcSubEndMonth==undefined){
			amcSubEndMonth=0;
		}
		if(amcSubEndYear==undefined){
			amcSubEndYear=0;
		}
		
		var onBoardedBy;	
		var promise1;
		if ($rootScope.user.role == "SUPERADMIN") {
			// get all active records
			onBoardedBy=0;
			promise1 = restAPIService.subscriptionReportResource(
					attendmeSubEndMonth, attendmeSubEndYear,
					warrantyExpEndMonth, warrantyExpEndYear, amcSubEndMonth,
					amcSubEndYear,onBoardedBy).query();
		}
		if ($rootScope.user.role == "MANUFACTURERADMIN"
				|| $rootScope.user.role == "RESELLERADMIN") {
			onBoardedBy= Number($rootScope.user.id);

			promise1 = restAPIService.subscriptionReportResource(
					attendmeSubEndMonth, attendmeSubEndYear,
					warrantyExpEndMonth, warrantyExpEndYear, amcSubEndMonth,
					amcSubEndYear,onBoardedBy).query();
		}

		promise1.$promise.then(function(response) {
			$scope.subscriptionData = response;
			
			if(!$scope.subscriptionData.length>0){
				$scope.searchResultMsg="No records found.";	
			}
			
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});

	};
	
	/*$scope.onDownloadAsExcel = function() {

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
		
		var fileName = "complaint_list.xlsx"; 
		var url = $rootScope.apiUrl + "issues/listbyfilter/excel";
		$scope.Authorization="Basic "+ btoa($rootScope.token + ":");
		
		$http.post(url, $scope.complaintSearchFilter, { 
			responseType: 'arraybuffer',
			'Authorization' : $scope.Authorization
			}).success(function(data) { 
				var file = new Blob([data], { type: 'application/octet-stream' }); 
			saveAs(file, fileName); });

	};*/
	// download as excel using $http post	
	$scope.onDownloadAsExcel = function() {
		
		angular.element(document.getElementById('btnDownloadAsExcel'))[0].disabled = true;
		
		var attendmeSubEndMonth = $scope.searchForm.attendmeSubEndDateMonth;
		var attendmeSubEndYear = $scope.searchForm.attendmeSubEndDateYear;

		var warrantyExpEndMonth = $scope.searchForm.warrantyExpiryDateMonth;
		var warrantyExpEndYear = $scope.searchForm.warrantyExpiryDateYear;

		var amcSubEndMonth = $scope.searchForm.amcSubEndDateMonth;
		var amcSubEndYear = $scope.searchForm.amcSubEndDateYear;
		
		if(attendmeSubEndMonth==undefined){
			attendmeSubEndMonth=0;
		}
		if(attendmeSubEndYear==undefined){
			attendmeSubEndYear=0;
		}
		if(warrantyExpEndMonth==undefined){
			warrantyExpEndMonth=0;
		}
		if(warrantyExpEndYear==undefined){
			warrantyExpEndYear=0;
		}
		if(amcSubEndMonth==undefined){
			amcSubEndMonth=0;
		}
		if(amcSubEndYear==undefined){
			amcSubEndYear=0;
		}
		
		var onBoardedBy;	
		var promise1;
		if ($rootScope.user.role == "SUPERADMIN") {
			// get all active records
			onBoardedBy=0; 
		}
		if ($rootScope.user.role == "MANUFACTURERADMIN"
				|| $rootScope.user.role == "RESELLERADMIN") {
			onBoardedBy= Number($rootScope.user.id); 
		}
		 
					
					
		var fileName = "SubscriptionReport.xlsx"; 
		var url = $rootScope.apiUrl + "subscriptionreport/download/excel?" +
					"attendmeSubEndMonth="+attendmeSubEndMonth+
					"&attendmeSubEndYear="+attendmeSubEndYear+
					"&warrantyExpEndMonth="+warrantyExpEndMonth+
					"&warrantyExpEndYear="+warrantyExpEndYear+
					"&amcSubEndMonth="+amcSubEndMonth+
					"&amcSubEndYear="+amcSubEndYear+
					"&onBoardedBy="+onBoardedBy;
		$scope.Authorization="Basic "+ btoa($rootScope.token + ":");
		 
		$http.get(url, { 
			responseType: 'arraybuffer',
			'Authorization' : $scope.Authorization
			}).success(function(data) {
				angular.element(document.getElementById('btnDownloadAsExcel'))[0].disabled = false;
				var file = new Blob([data], { type: 'application/octet-stream' }); 
			saveAs(file, fileName); 
			}).error(function(data, status) {
				angular.element(document.getElementById('btnDownloadAsExcel'))[0].disabled = false;
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});				
			});

	};
}
