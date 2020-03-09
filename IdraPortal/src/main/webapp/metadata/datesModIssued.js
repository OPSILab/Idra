/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
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
angular.module("IdraPlatform").controller('IssuedCtrl',['$scope','$rootScope',function ($scope,$rootScope) {
	
	if($rootScope.previousContext!=undefined){
		$rootScope.issuedEndDate=$rootScope.previousContext.issuedEndDate;
		$rootScope.issuedStartDate=$rootScope.previousContext.issuedStartDate;
		$scope.issuedEndDate=$rootScope.previousContext.issuedEndDate;
		$scope.issuedStartDate=$rootScope.previousContext.issuedStartDate;
	}else{
		$rootScope.issuedEndDate=null;
		$rootScope.issuedStartDate=null;
		$scope.issuedEndDate=null;
		$scope.issuedStartDate=null;
	}
	
	$scope.toggleMin = function() {
		$scope.minDate1 = $scope.minDate1 ? null : new Date(2010,1,1);
	};

	$scope.toggleMin();
	$scope.maxDate1 = new Date();
	$scope.maxDate2 = new Date();

	$rootScope.resetIssued = function(){
		$scope.issuedStartDate = null;
		$scope.issuedEndDate = null;
	}

	$scope.$watch('issuedStartDate', function() {
		if($scope.issuedStartDate != null){
			$scope.minDate2 = $scope.issuedStartDate;
			if($scope.issuedEndDate==null)
				$scope.issuedEndDate = new Date();
		}else{
			$scope.minDate2 = $scope.minDate1; 
		}
		$rootScope.issuedStartDate = $scope.issuedStartDate;

	});

	$scope.$watch('issuedEndDate', function() {
		if($scope.issuedEndDate != null){
			$scope.maxDate1 = $scope.issuedEndDate;
		}else{
			$scope.maxDate1 = $scope.maxDate2; 
		}

		$rootScope.issuedEndDate = $scope.issuedEndDate;

	});

	$scope.clear = function () {
		$scope.issuedEndDate = null;
		$scope.issuedEndDate = null;
	};

	$scope.open1 = function($event) {
		$event.preventDefault();
		$scope.status1.opened = !$scope.status1.opened;
	};

	$scope.open2 = function($event) {
		$event.preventDefault();
		$scope.status2.opened = !$scope.status2.opened;
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

	$scope.getDayClass = function(date, mode) {
		if (mode === 'day') {
			var dayToCheck = new Date(date).setHours(0,0,0,0);

			for (var i=0;i<$scope.events.length;i++){
				var currentDay = new Date($scope.events[i].date).setHours(0,0,0,0);

				if (dayToCheck === currentDay) {
					return $scope.events[i].status;
				}
			}
		}

		return '';
	};
}]);

angular.module("IdraPlatform").controller('ModifiedCtrl',['$scope','$rootScope',function ($scope,$rootScope) {

	if($rootScope.previousContext!=undefined){
		$rootScope.modifiedEndDate=$rootScope.previousContext.modifiedEndDate;
		$rootScope.modifiedStartDate=$rootScope.previousContext.modifiedStartDate;
		$scope.modifiedEndDate=$rootScope.previousContext.modifiedEndDate;
		$scope.modifiedStartDate=$rootScope.previousContext.modifiedStartDate;
	}else{
		$rootScope.modifiedEndDate=null;
		$rootScope.modifiedStartDate=null;
		$scope.modifiedEndDate=null;
		$scope.modifiedStartDate=null;
	}
	

	$scope.toggleMin = function() {
		$scope.minDate1 = $scope.minDate1 ? null : new Date(2010,1,1);
	};

	$scope.toggleMin();
	$scope.maxDate1 = new Date();
	$scope.maxDate2 = new Date();

	$rootScope.resetModified = function(){
		$scope.modifiedStartDate = null;
		$scope.modifiedEndDate = null;
	}

	$scope.$watch('modifiedStartDate', function() {
		if($scope.modifiedStartDate != null){
			$scope.minDate2 = $scope.modifiedStartDate;
			if($scope.issuedEndDate==null)
				$scope.modifiedEndDate = new Date();
			
		}else{
			$scope.minDate2 = $scope.minDate1; 
		}

		$rootScope.modifiedStartDate = $scope.modifiedStartDate;

	});

	$scope.$watch('modifiedEndDate', function() {
		if($scope.modifiedEndDate != null){
			$scope.maxDate1 = $scope.modifiedEndDate;
		}else{
			$scope.maxDate1 = $scope.maxDate2; 
		}

		$rootScope.modifiedEndDate = $scope.modifiedEndDate;

	});


	$scope.clear = function () {
		$scope.modifiedEndDate = null;
		$scope.modifiedEndDate = null;
	};

	$scope.open1 = function($event) {
		$event.preventDefault();
		$scope.status1.opened = !$scope.status1.opened;
	};

	$scope.open2 = function($event) {
		$event.preventDefault();
		$scope.status2.opened = !$scope.status2.opened;
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

	$scope.getDayClass = function(date, mode) {
		if (mode === 'day') {
			var dayToCheck = new Date(date).setHours(0,0,0,0);

			for (var i=0;i<$scope.events.length;i++){
				var currentDay = new Date($scope.events[i].date).setHours(0,0,0,0);

				if (dayToCheck === currentDay) {
					return $scope.events[i].status;
				}
			}
		}

		return '';
	};
}]);
