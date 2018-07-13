/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
angular.module("IdraPlatform").controller('LogCtrl',['$scope','$rootScope','$http','config',function($scope,$rootScope,$http,config){
	// The modes

	$scope.aceDocumentValue = "";

	$scope.issuedEndDate=new Date();
	$scope.issuedStartDate=new Date($scope.issuedEndDate.getTime()-86400000);

	$scope.allLevels = [{value:["ERROR","INFO","WARNING","DEBUG","FATAL"],text:"All"},{value:["ERROR"],text:"Error"},{value:["INFO"],text:"Info"},{value:["WARNING"],text:"Warning"},{value:["DEBUG"],text:"Debug"},{value:["FATAL"],text:"Fatal"}];
	$scope.level = $scope.allLevels[0].value;
	
	$scope.getLogs = function(){
	
	var req = {
			method: 'POST',
			url: config.ADMIN_SERVICES_BASE_URL+config.GET_LOGS,
			headers: {
				'Content-Type': 'application/json',
				'Authorization': "Bearer "+$rootScope.token
			},
			data:{
				levelList: $scope.level,
				startDate: $scope.issuedStartDate,
				endDate: $scope.issuedEndDate
			}
	};

	$rootScope.startSpin();
	$http(req).then(function(value){
		var str="";
		for(i=0; i<value.data.length; i++){
			str += "[ "+value.data[i].level+" ] "+value.data[i].timestamp+" "+value.data[i].logger+" "+value.data[i].message+" \n";
		}
		$scope.aceDocumentValue = str;
		$scope.aceSession.getDocument().setValue($scope.aceDocumentValue);
		$scope.aceEditor.moveCursorToPosition({'row':0,'column':0});
		$rootScope.stopSpin();
	}, function(value){

		$rootScope.stopSpin();
		
		if(value.status==401){
			$rootScope.token=undefined;					
		}else{
			$rootScope.showAlert('danger',value.data.userMessage);
		}
	});
	}
	
	$scope.getLogs();
		
	$scope.aceLoaded = function(_editor) {
		$scope.aceEditor = _editor;
		$scope.aceSession = _editor.getSession();
		$scope.aceSession.getDocument().setValue($scope.aceDocumentValue);
	};

	$scope.toggleMin = function() {
		$scope.minDate1 = new Date(2015,01,01);
	};

	$scope.toggleMin();
	$scope.maxDate1 = new Date();
	$scope.maxDate2 = new Date();

	$scope.$watch('issuedStartDate', function() {
		if($scope.issuedStartDate != null){
			$scope.minDate2 = $scope.issuedStartDate;
		}else{
			$scope.minDate2 = $scope.minDate1; 
		}

	});

	$scope.$watch('issuedEndDate', function() {
		if($scope.issuedEndDate != null){
			$scope.maxDate1 = $scope.issuedEndDate;
		}else{
			$scope.maxDate1 = $scope.maxDate2; 
		}

	});

	$scope.clear = function () {
		$scope.issuedStartDate = $scope.minDate1;
		$scope.issuedEndDate = new Date();
	};

	$scope.open1 = function($event) {
		$scope.status1.opened = true;
	};

	$scope.open2 = function($event) {
		$scope.status2.opened = true;
	};

	$scope.dateOptions = {
			formatYear: 'yyyy',
			startingDay: 1
	};

	$scope.format = 'dd-MMMM-yyyy';

	$scope.status1 = {
			opened: false
	};

	$scope.status2 = {
			opened: false
	};

	var tomorrow = new Date();
	tomorrow.setDate(tomorrow.getDate() + 1);
	var afterTomorrow = new Date();
	afterTomorrow.setDate(tomorrow.getDate() + 2);
	$scope.events =
		[
		 {
			 date: tomorrow,
			 status: 'full'
		 },
		 {
			 date: afterTomorrow,
			 status: 'partially'
		 }
		 ];


}]);
