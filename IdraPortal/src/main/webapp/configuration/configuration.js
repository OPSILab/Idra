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
angular.module("IdraPlatform").controller('PrefixCtrl',['$scope','config','$rootScope','$http','$modal',function($scope,config,$rootScope,$http,$modal){

	$scope.itemsByPage = 6;

	$scope.allPrefix=[];

	$scope.getAllPrefix = function(){

		var req = {
				method: 'GET',
				url: config.ADMIN_SERVICES_BASE_URL + config.PREFIXES_SERVICE,
				headers: {
					'Content-Type': 'application/json',
					'Authorization': "Bearer "+$rootScope.token
				}

		};

		$rootScope.startSpin();
		$http(req).then(function(value){
			$rootScope.stopSpin();
			console.log(value.data);
			$scope.allPrefix = value.data;	
			$scope.displayedCollection = [].concat($scope.allPrefix);
		}, function(value){
			console.log(value.status);
			if(value.status==401){
				$rootScope.token=undefined;
			}
			$rootScope.stopSpin();
			return null;
		});
	}



	$scope.getAllPrefix();

	$scope.addPrefix = function() {
		$scope.inserted = {
				prefix: '',
				namespace: ''
		};
		$scope.allPrefix.unshift($scope.inserted);
	};

	$scope.openModal = function (p) {

		var prefix;
		var title="";
		if(p==''){
			prefix={
					id: '-1',
					prefix: '',
					namespace: ''
			}
			mode="create";
		}else{
			prefix=p;
			mode="update";
		}

		var modalInstance = $modal.open({
			animation: true,
			templateUrl: 'PrefixModalContent.html',
			controller: 'PrefixModalInstanceCtrl',
			size: 'md',
			resolve: {
				prefix: function () {
					return prefix;
				},
				mode: function(){
					return mode;
				},
				message: function(){
					return "Please select a node";
				},
				allPrefix: function(){
					return $scope.allPrefix;
				}
			}
		});

		modalInstance.result.then(function () {
			$scope.getAllPrefix();
		});

	};

	$scope.deletePrefix = function(p){

		var req = {
				method: 'DELETE',
				url: config.ADMIN_SERVICES_BASE_URL+config.PREFIXES_SERVICE+'/' + p.id,
				headers: {
					'Authorization': "Bearer "+$rootScope.token
				}
		};

		$rootScope.startSpin();
		$http(req).then(function(value){
			console.log(value);
			$scope.getAllPrefix();
			$rootScope.stopSpin();
		}, function(value){

			if(value.status==401){
				$rootScope.token=undefined;
			}

			$scope.getAllPrefix();
			$rootScope.stopSpin();
			$rootScope.showAlert('danger',value.data.userMessage);
		}); 
	}

}]);

angular.module("IdraPlatform").controller('UpdatePasswordCtrl',['$scope','config','$rootScope','$http','md5',function($scope,config,$rootScope,$http,md5){

	$scope.oldPassword="";
	$scope.newPassword="";
	$scope.newPasswordConfirm="";
	
	var username = $rootScope.loggedUsername;
	console.log(username);
	
	$scope.updatePassword = function(){
		
		var req = {
				method: 'PUT',
				url: config.ADMIN_SERVICES_BASE_URL + config.UPDATE_PASSWORD_SERVICE,
				headers: {
					'Content-Type': 'application/json',
					'Authorization': "Bearer "+$rootScope.token
				},
				data:{
					oldPassword: md5.createHash($scope.oldPassword),
					newPassword: $scope.newPassword,
					newPasswordConfirm: $scope.newPasswordConfirm,
					username:username
				}
		};
		
		$rootScope.startSpin();
		$http(req).then(function(value){
			
			console.log(value);
			$rootScope.showAlert('success',value.data.message);
			$rootScope.stopSpin();
			
		}, function(value){

			if(value.status==401){
				$rootScope.token=undefined;
			}
			
			var message=value.data.userMessage;
			if(message==undefined || message==""){
				message=value.data.message;
			}
			
			$rootScope.stopSpin();
			$rootScope.showAlert('danger',message);
			
		}); 		
		
	};
	

}]);

angular.module("IdraPlatform").controller('ConfigurationCtrl',['$scope','config','$rootScope','$http',function($scope,config,$rootScope,$http){

	var req = {
			method: 'GET',
			url: config.ADMIN_SERVICES_BASE_URL+config.CONFIGURATION_SERVICE,
			headers: {
				'Content-Type': 'application/json'
			}
	};

	$scope.values = [{text:'1 hour',value:'3600'},{text:'1 day',value:'86400'},{text:'1 week',value:'604800'}];

	$scope.refreshPeriod ='';
	$scope.checkContentLength="";
	$scope.rdfMaxDimension="";
	$scope.checkRdfDimension="";

	$rootScope.startSpin();
	$http(req).then(function(value){

		$rootScope.stopSpin();		
		console.log(value);
		$scope.refreshPeriod = value.data.refresh_period;
		$scope.rdfMaxDimension=parseInt(value.data.rdf_max_dimension);
		$scope.checkContentLength= (value.data.rdf_undefined_content_length === 'true');
		$scope.checkRdfDimension= (value.data.rdf_undefined_dimension === 'true');
		
	}, function(value){

		$rootScope.stopSpin();
		$rootScope.showAlert('danger',value.data.userMessage);
	});

	$scope.updateConfiguration = function(){
		console.log($scope.refreshPeriod);

		var req = {
				method: 'POST',
				url: config.ADMIN_SERVICES_BASE_URL+config.CONFIGURATION_SERVICE,
				headers: {
					'Content-Type': 'application/json',
					'Authorization': "Bearer "+$rootScope.token
				},
				data:{
					refresh_period: $scope.refreshPeriod.toString(),
					rdf_max_dimension: $scope.rdfMaxDimension.toString(),
					rdf_undefined_content_length:$scope.checkContentLength.toString(),
					rdf_undefined_dimension:$scope.checkRdfDimension.toString() 
				}
		};

		$rootScope.startSpin();
		$http(req).then(function(value){

			$rootScope.stopSpin();
			$rootScope.showAlert('success',"Configuration successfully updated!");
		}, function(value){

			if(value.status==401){
				$rootScope.token=undefined;
			}

			$rootScope.stopSpin();
			$rootScope.showAlert('danger',value.data.userMessage);
		});

	}


}]);

angular.module('IdraPlatform').controller('PrefixModalInstanceCtrl',["$scope","$modalInstance", "prefix","mode",'config','$rootScope','$http','allPrefix', function ($scope, $modalInstance, prefix,mode,config,$rootScope,$http,allPrefix) {

	$scope.tmp = angular.copy(prefix);
	$scope.oldPrefix = angular.copy(prefix);
	
	console.log($scope.oldPrefix.namespace == $scope.tmp.namespace && $scope.oldPrefix.prefix == $scope.tmp.prefix);

	console.log($scope.tmp);
	
	$scope.mode=mode;

	if(mode=="create"){
		$scope.title = "Create new Prefix";
	}else{
		$scope.title = "Update Prefix";
	}

	$scope.textAlertModal="";
	$scope.alertModal=false;
	
	$scope.closeAlertModal = function(){
		$scope.alertModal=false;
	}

	$scope.showAlertModal = function(){
		$scope.alertModal=true;
	}

	function checkPrefix(tmp,mode){

		if(tmp.prefix=='' || tmp.namespace==''){
			$scope.alertModal=true;
			$scope.textAlertModal="All fields required";
			return false;
		}
		
		for(i=0; i<allPrefix.length; i++){
			if(allPrefix[i].prefix == tmp.prefix && tmp.id != allPrefix[i].id){
				$scope.alertModal=true;
				$scope.textAlertModal="Prefix already exists";
				return false;
			}else if(allPrefix[i].namespace == tmp.namespace && tmp.id != allPrefix[i].id){
				$scope.alertModal=true;
				$scope.textAlertModal="Namespace already exists";
				return false;
			}
		}
		
		var reg = /^<(http|https):\/\/[^ "]+>$/;
		if(!reg.test(tmp.namespace)){
			$scope.alertModal=true;
			$scope.textAlertModal="Wrong namespace format";
			return false;
		}

		
		return true;
	}

	//crea nuovo
	$scope.addPrefix=function(){

		if(checkPrefix($scope.tmp,$scope.mode)){

			var req = {
					method: 'POST',
					url: config.ADMIN_SERVICES_BASE_URL + config.PREFIXES_SERVICE,
					headers: {
						'Content-Type': 'application/json',
						'Authorization': "Bearer "+$rootScope.token
					},
					data:{
						'prefix': $scope.tmp.prefix,
						'namespace': $scope.tmp.namespace
					}
			};

			$rootScope.startSpin();
			$http(req).then(function(value){
				$rootScope.stopSpin();
				$scope.cancel();
			}, function(value){
				$rootScope.stopSpin();
				$scope.cancel();
				$rootScope.showAlert('danger',value.data.userMessage);
			});
		}

	}

	$scope.updatePrefix=function(){

		if(checkPrefix($scope.tmp,$scope.mode)){

			var req = {
					method: 'PUT',
					url: config.ADMIN_SERVICES_BASE_URL+config.PREFIXES_SERVICE+"/"+$scope.tmp.id.toString(),
					headers: {
						'Content-Type': 'application/json',
						'Authorization': "Bearer "+$rootScope.token
					},
					data:{
						'prefix': $scope.tmp.prefix,
						'namespace': $scope.tmp.namespace
					}
			};

			$rootScope.startSpin();
			$http(req).then(function(value){
				$rootScope.stopSpin();
				$scope.cancel();
			}, function(value){
				$rootScope.stopSpin();
				$scope.cancel();
				$rootScope.showAlert('danger',value.data.userMessage);
			});
		}
	}

	$scope.cancel = function () {
		$modalInstance.close();
	};




}]);
