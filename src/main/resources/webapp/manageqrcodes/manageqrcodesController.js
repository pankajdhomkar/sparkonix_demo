'use strict';

angular.module('sparkonixWebApp')
	.controller('manageQRCodesController', manageQRCodesController);

function manageQRCodesController($scope, $timeout, restAPIService, dialogs, $rootScope, $http) {
	// ------------- PUBLIC VARIABLES ----------------
	$scope.numQRCodes = 0;
	$scope.allQRCodes = [];
	$scope.codesGroupedByBatch;
	$scope.batchNames = [];
	$scope.batchDownloadLink=$rootScope.apiUrl + "qrcodes/download/zip/";
	
	// ------------- PRIVATE VARIABLES ----------------
	
	
	// ------------- CONTROLLER CODE ----------------
	$timeout(removePagination, 1000);
	getAllQRCodes();
	
	// ------------- PRIVATE FUNCTIONS ----------------
	function getAllQRCodes(){
		var promise = restAPIService.qrcodesResource().query();
		promise.$promise.then(function(response) {
			
			$scope.allQRCodes = response;
			$scope.codesGroupedByBatch = groupBy($scope.allQRCodes, 'batchName');
			$scope.batchNames = Object.keys($scope.codesGroupedByBatch);
			removePagination();
		}, function(error) {
			dialogs.error("Error", error.data.error, { 'size' : 'sm' });
		});
	}
	
	function groupBy(xs, key) {
	  return xs.reduce(function(rv, x) {
	    (rv[x[key]] = rv[x[key]] || []).push(x);
	    return rv;
	  }, {});
	};
	
	function removePagination (){
		/*for (var i=0; i<$scope.batchNames.length; i++){
			if($scope.codesGroupedByBatch[$scope.batchNames[i]].length < 3) {
				//$('#qrCodesTable_paginate').attr("style","display:none;");
				debugger
				var tableID = "qrCodesTable" + i;
				var table = document.getElementById(tableID);
				$(tableID).attr("paging", false);
			}
			else{
			}
		}*/
	}
	
	// ------------- PUBLIC FUNCTIONS ----------------
	$scope.createNewBatchOfQRCodes = function(){
		var promise = restAPIService.generateqrcodesResource($scope.numQRCodes).save();
		$scope.IsVisible = true;
		promise.$promise.then(function() {
			$scope.IsVisible = false;
			getAllQRCodes(); // reload
			dialogs.notify("Success", $scope.numQRCodes + " QR Codes generated", {'size' : 'sm'});
		}, function(error) {
			dialogs.error("Error", error.data.error, { 'size' : 'sm' });
		});
	}
	
	$scope.download = function(filename){ 	
		
	    var pom = document.createElement('a');
	    pom.setAttribute('href', 'attachment;charset=utf-8,');
	    pom.setAttribute('download', filename);

	    if (document.createEvent) {
	        var event = document.createEvent('MouseEvents');
	        event.initEvent('click', true, true);
	        pom.dispatchEvent(event);
	    }
	    else {
	        pom.click();
	    }
	}
	
$scope.downloadBatch = function(batchName){ 		 
		 
		var promise = restAPIService.downloadQrcodesBatch(batchName).get();		 
		promise.$promise.then(function(response) {
			var a = document.createElement('a');
			var blob = new Blob([response], {'type':"application/zip"});
			a.href = URL.createObjectURL(blob);
			a.download = batchName+".zip";
			a.click();
			//dialogs.notify("Success", "downloaded", {'size' : 'sm'});
		}, function(error) {
			dialogs.error("Error", "errro", { 'size' : 'sm' });
		});
	  
	}

/*$scope.downloadBatch =function(batchName){
	var request = $http({
		method: 'GET',		 
		url:$rootScope.apiUrl + "qrcode/download/zip/"+batchName,
		responseType: 'arraybuffer', 
		headers: {
			 'Content-Type': 'application/zip; charset=utf-8',
    	 
		}  
	});
 alert('ssss');
	request.success(function(data, status, headers, config) {
		  var anchor = angular.element('<a/>');
		  anchor.css({display: 'none'}); // Make sure it's not visible
		  angular.element(document.body).append(anchor); // Attach to document

		  anchor.attr({
		      href: 'data:attachment/zip;charset=utf-8,' + encodeURI(data),
		      target: '_blank',
		      download: batchName+'.zip'
		  })[0].click();

		  anchor.remove(); // Clean it up afterwards
	  }).
	  error(function(data, status, headers, config) {
		  dialogs.error("Error", "Error", { 'size' : 'sm' });
	  });
}*/
}
