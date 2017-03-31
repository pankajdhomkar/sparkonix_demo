'use strict';

var sparkonixWebApp = angular.module(
		'sparkonixWebApp',
		[ 'ui.router', 'ui.bootstrap', 'ngResource', 'sparkonixRestAPIs', 'ngSanitize', 
		  'dialogs.main', 'datatables','ui.select','ngCookies','sparkonixParseExcelService','ui.filters'])
		.config(config)
		.controller(appController);

function appController ($scope,$cookies,$rootScope,$state,$http,$location) {	
	
	var user = $cookies.user;
	
	/***Added to resolve the redirection issue, resetPassword screen was redirected to login by default because the user from 
	 * cookie is null or undefined**/
	if($location.path().indexOf("resetpassword")>-1){
		window.location.href = $location.absUrl();
		return false;
	}
	/** till here**/
	
	if(user == "" || user == undefined) {
		window.location.href = "#/login";
	}
	else{
		 user= JSON.parse(user);
		 $rootScope.user = user;
		 $http.defaults.headers.common.Authorization = "Basic "
				+ btoa($rootScope.user.token + ":");
		 if ($rootScope.user.role == "SALESTEAM") {
				$state.go('home.stdashboard');
			}
			if ($rootScope.user.role == "SUPERADMIN") {
				$state.go('home.sadashboard');
			}
			if ($rootScope.user.role == "MANUFACTURERADMIN") {
				$state.go('home.wadashboard');
			}
			if ($rootScope.user.role == "RESELLERADMIN") {
				$state.go('home.wadashboard');
			}
			if ($rootScope.user.role == "TECHNICIAN") {
				$state.go('home.tcdashboard');
			}
	 }
	
	//common fun to create md5 hash
	$scope.md5 = function(value) {
		return CryptoJS.MD5(value).toString();
	}
}

function config($stateProvider, $urlRouterProvider) {
	
	$stateProvider// SET NEW PASSWORD SCREEN
	.state('resetpassword', {
		url : '/resetpassword/:token',
		params : {"token":null},
		templateUrl : 'login/resetpassword.html',
		controller : "resetPasswordController"
	})
	.state('login', {
		url : '/login',
		templateUrl : 'login/login.html',
		controller : "loginController"
	})
	// FORGOT PASSWORD
	.state('forgotpassword', {
		url : '/forgotpassword',
		templateUrl : 'login/forgotPassword.html',
		controller : "loginController"
	})
	
	
	
	.state('home', {
		url : '/home',
		templateUrl : 'home/home.html',
		controller : "homeController"
	})
	
	// SALES TEAM WORKFLOW
	.state('home.stdashboard', {
		templateUrl : 'salesteam/st_dashboard/st_dashboard.html',
		url : '/stdashboard',
		controller : "stDashboardController"
	})
	
	// WEB ADMIN WORKFLOW
	.state('home.wadashboard', {
		templateUrl : 'webadmin/wa_dashboard/wa_dashboard.html',
		url : '/wadashboard',
		controller : "waDashboardController"
	}).state('home.wareports', {
		templateUrl : 'webadmin/wa_reports/wa_reports.html',
		url : '/wareports',
		controller : "waReportsController"
	}).state('home.waprofile', {
		templateUrl : 'webadmin/wa_profile/wa_profile.html',
		url : '/waprofile',
		controller : "waProfileController"
	})
	
	// SUPERADMIN WORKFLOW
	.state('home.sadashboard', {
		templateUrl : 'superadmin/sa_dashboard/sa_dashboard.html',
		url : '/sadashboard',
		controller : "saDashboardController"
	})
	
	// TECHNICIAN WORKFLOW
	.state('home.tcdashboard', {
		templateUrl : 'technician/tc_dashboard/tc_dashboard.html',
		url : '/tcdashboard',
		controller : "tcDashboardController"
	})
	
	// MANAGE CUSTOMERS
	.state('home.managecustomers', {
		templateUrl : 'managecustomers/managecustomers.html',
		url : '/managecustomers',
		controller : "manageCustomersController"
	}).state('home.managecustomers.addcustomer', {
		templateUrl : 'managecustomers/addcustomer/addcustomer.html',
		url : '/addcustomer',
		controller : "addcustomerController"
	})	
	// MANAGE COMPLAINTS
	.state('home.managecomplaints', {
		templateUrl : 'managecomplaints/managecomplaints.html',
		url : '/managecomplaints',
		controller : "manageComplaintsController"
	})
	// MANAGE MANUFATURERS AND RESELLERS
	.state('home.managemanres', {
		templateUrl : 'managemanres/managemanres.html',
		url : '/managemanres',
		controller : "manageManResController"
	}).state('home.managemanres.addmanres', {
		templateUrl : 'managemanres/addmanres/addmanres.html',
		url : '/addmanres',
		controller : "addManResController"
	}).state('home.managemanres.viewmanres', {
		templateUrl : 'managemanres/addmanres/viewmanres.html',
		url : '/viewmanres',
		controller : "viewManResController"
	})
	// MANAGE SALES TEAM
	.state('home.managesalesteam', {
		templateUrl : 'managesalesteam/managesalesteam.html',
		url : '/managesalesteam',
		controller : "manageSalesTeamController"
	}).state('home.managesalesteam.addsalesteammember', {
		templateUrl : 'managesalesteam/addsalesteammember/addsalesteammember.html',
		url : '/addsalesteammember',
		controller : "addSalesTeamMemberController"
	})
	// MANAGE TECHNICIANS
	.state('home.managetechnicians', {
		templateUrl : 'managetechnicians/managetechnicians.html',
		url : '/managetechnicians',
		controller : "manageTechniciansController"
	}).state('home.managetechnicians.addtechnician', {
		templateUrl : 'managetechnicians/addtechnician/addtechnician.html',
		url : '/addtechnician',
		controller : "addTechnicianController"

	})
	// MANAGE QR CODES
	.state('home.manageqrcodes', {
		templateUrl : 'manageqrcodes/manageqrcodes.html',
		url : '/manageqrcodes',
		controller : "manageQRCodesController"
	})
	// MANAGE MACHINE DOCUMENTS
	.state('home.managemachinedocuments', {
		templateUrl : 'managemachinedocuments/managemachinedocuments.html',
		url : '/managemachinedocuments',
		controller : "manageMachineDocumentsController"
	})
	//MANAGE SUBSCRIPTIONS REPORTS
	.state('home.managesubscriptions', {
		templateUrl : 'managesubscriptions/managesubscriptions.html',
		url : '/managesubscriptions',
		controller : "managesubscriptionsController"
	})
	//MANAGE BROADCAST MESSAGES
	.state('home.managebroadcastmessages', {
		templateUrl : 'managebroadcastmessages/managebroadcastmessages.html',
		url : '/managebroadcastmessages',
		controller : "managebroadcastmessagesController"
	});
	$urlRouterProvider.otherwise("/login");
}
