'use strict';

angular.module('sparkonixParseExcelService', []).factory('parseExcelService',
		parseExcelService, '$q');

function parseExcelService($resource, $rootScope, $q) {
	$rootScope.apiUrl = "/api/";
	return {
		excelUploadService : excelUploadService
	}

	function excelUploadService() { 
		this.parseExcel = function(file) {
			var reader = new FileReader();
			var deferred = $q.defer();

			reader.onload = function(e) {
				var data = e.target.result;
				try{
					var workbook = XLSX.read(data, {
						type : 'binary'
					});
				}
				catch(e){
					return deferred.reject("Failed to read excel file.");					 
				}
				var XL_row_object = [];
				workbook.SheetNames
						.forEach(function(sheetName) {
							XL_row_object = XLSX.utils
									.sheet_to_row_object_array(workbook.Sheets["Sheet1"]);
							// console.log(XL_row_object);
						})

				$q.all(XL_row_object).then(function() {
					deferred.resolve({
						finalData : XL_row_object
					});
				});
			};

			reader.onerror = function(e) {
				console.log(e);				 
			};

			reader.readAsBinaryString(file);
			return deferred.promise;
		};
		return this;

	}

}