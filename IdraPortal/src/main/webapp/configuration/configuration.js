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





angular.module("IdraPlatform").controller('RemCatCtrl',['$scope','config','$rootScope','$http','$modal',function($scope,config,$rootScope,$http,$modal){

	$scope.itemsByPage = 6;

	$scope.allRemCat=[];

	$scope.getAllRemCat = function(){

		var req = {
				method: 'GET',
				url: config.ADMIN_SERVICES_BASE_URL + config.REMOTE_CAT_SERVICE,
				headers: {
					'Content-Type': 'application/json',
					'Authorization': "Bearer "+$rootScope.token
				}

		};

		$rootScope.startSpin();
		$http(req).then(function(value){
			$rootScope.stopSpin();
			console.log(value.data);
			$scope.allRemCat = value.data;	
			$scope.displayedCollection = [].concat($scope.allRemCat);
		}, function(value){
			console.log(value.status);
			if(value.status==401){
				$rootScope.token=undefined;
			}
			$rootScope.stopSpin();
			return null;
		});
	}


	$scope.getAllRemCat();
	
	$scope.addRemCat = function() {
		$scope.inserted = {
				URL: '',
				catalogueName: ''
		};
		$scope.allRemCat.unshift($scope.inserted);
	};

		$scope.openModal = function (c) {

		var catalogue;
		var title="";
		if(c==''){
			catalogue={
					id: '-1',
					URL: '',
					catalogueName: ''
			}
			mode="create";
		}else{
			catalogue=c;
			mode="update";
		}

		var modalInstance = $modal.open({
			animation: true,
			templateUrl: 'RemCatModalContent.html',
			controller: 'RemoteModalInstanceCtrl',
			size: 'md',
			resolve: {
				catalogue: function () {
					return catalogue;
				},
				mode: function(){
					return mode;
				},
				message: function(){
					return "Please select a node";
				},
				allRemCat: function(){
					return $scope.allRemCat;
				}
			}
		});

		modalInstance.result.then(function () {
			$scope.getAllRemCat();
		});

	};

	$scope.deleteCatalogue = function(c){

		var req = {
				method: 'DELETE',
				url: config.ADMIN_SERVICES_BASE_URL+config.REMOTE_CAT_SERVICE+'/' + c.id,
				headers: {
					'Authorization': "Bearer "+$rootScope.token
				}
		};

		$rootScope.startSpin();
		$http(req).then(function(value){
			console.log(value);
			$scope.getAllRemCat();
			$rootScope.stopSpin();
		}, function(value){

			if(value.status==401){
				$rootScope.token=undefined;
			}

			$scope.getAllRemCat();
			$rootScope.stopSpin();
			$rootScope.showAlert('danger',value.data.userMessage);
		}); 
	}
	
}]);




angular.module('IdraPlatform').controller('RemoteModalInstanceCtrl',["$scope","$modalInstance", "catalogue","mode",'config','$rootScope','$http','allRemCat','md5', function ($scope, $modalInstance, catalogue,mode,config,$rootScope,$http,allRemCat,md5) {

	$scope.tmp = angular.copy(catalogue);
	$scope.oldRemCat = angular.copy(catalogue); 
	
	$scope.checkValue = "";
	$scope.checkValueIDM = "";
	
	$scope.isBasic = ($scope.tmp.clientID==null && $scope.tmp.username!=null)?true:false;
	$scope.isOauth = ($scope.tmp.clientID!=null && $scope.tmp.username!=null)?true:false;
	$scope.noAuth = false;
	
	$scope.isIdra = false;
	$scope.usernameIdra = "";
	$scope.passwordIdra = "";
	$scope.cataloguePassword = "";
	$scope.somePlaceholder = 'Insert URL';
	$scope.deleteCred = false;
	
	$scope.request = function(req){		
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
	
	console.log($scope.oldRemCat.catalogueName == $scope.tmp.catalogueName && $scope.oldRemCat.URL == $scope.tmp.URL);

	console.log($scope.tmp);
	
	$scope.mode=mode;

	if(mode=="create"){
		$scope.title = "Create new Remote Catalogue";
	}else{
		$scope.title = "Update Remote Catalogue";
	}

	$scope.textAlertModal="";
	$scope.alertModal=false;
	
	$scope.closeAlertModal = function(){
		$scope.alertModal=false;
	}

	$scope.showAlertModal = function(){
		$scope.alertModal=true;
	}

	function checkRemcat(tmp,mode){

		if(tmp.URL=='' || tmp.catalogueName==''){
			$scope.alertModal=true;
			$scope.textAlertModal="All fields required";
			return false;
		}
		
		for(i=0; i<allRemCat.length; i++){
			if(allRemCat[i].URL == tmp.URL && tmp.id != allRemCat[i].id){
				$scope.alertModal=true;
				$scope.textAlertModal="Remote Catalogue already exists";
				return false;
			}else if(allRemCat[i].catalogueName == tmp.catalogueName && tmp.id != allRemCat[i].id){
				$scope.alertModal=true;
				$scope.textAlertModal="Remote Catalogue Name already exists";
				return false;
			}
		}
		//var reg = /^<(http|https):\/\/[^ "]+>$/;
		//if(!reg.test(tmp.URL)){
			//$scope.alertModal=true;
			//$scope.textAlertModal="Wrong URL format";
			//return false;
		//}
		return true;
	}

	$scope.addRemCat=function(){	
		//if($scope.checkValue || $scope.checkValueIDM){
			if($scope.isBasic || $scope.isOauth){
					console.log("Aggiunta catalogo CON CREDENZIALI IDRA o IDM");

					if($scope.checkValueIDM)
						$scope.cataloguePassword = $scope.tmp.password;
					else
						$scope.cataloguePassword = md5.createHash($scope.tmp.password);
					
				if(checkRemcat($scope.tmp,$scope.mode) ){
				console.log("Aggiunta di username: "+$scope.tmp.username,+" passw: "+$scope.cataloguePassword);
					
					var req = {
							method: 'POST',
							url: config.ADMIN_SERVICES_BASE_URL + config.REMOTE_CAT_SERVICE,
							headers: {
								'Content-Type': 'application/json',
								'Authorization': "Bearer "+$rootScope.token
							},
							data:{
								'URL': $scope.tmp.URL,
								'catalogueName': $scope.tmp.catalogueName,
								'editable': true,
								'password': $scope.cataloguePassword,
								'username': $scope.tmp.username,
								'isIdra': $scope.isIdra,
								'clientID': $scope.tmp.clientID,
								'clientSecret': $scope.tmp.clientSecret,
								'portal': $scope.tmp.portal
							}
					};
			$scope.request(req);
				}
	}
	else{
		console.log("Aggiunta catalogo SENZA CREDENZIALI");
			if(checkRemcat($scope.tmp,$scope.mode) ){
		
					var req = {
							method: 'POST',
							url: config.ADMIN_SERVICES_BASE_URL + config.REMOTE_CAT_SERVICE,
							headers: {
								'Content-Type': 'application/json',
								'Authorization': "Bearer "+$rootScope.token
							},
							data:{
								'URL': $scope.tmp.URL,
								'catalogueName': $scope.tmp.catalogueName,
								'editable': true,
								'isIdra': $scope.isIdra
							}
					};
				$scope.request(req);
				}	
	}
	}



	$scope.updateRemCat=function(){
		
		if($scope.noAuth){
					console.log("ELIMINAZIONE CREDENZIALI");
					
				if(checkRemcat($scope.tmp,$scope.mode) ){
					var req = {
							method: 'PUT',
							url: config.ADMIN_SERVICES_BASE_URL+config.REMOTE_CAT_SERVICE+"/"+$scope.tmp.id.toString(),
							headers: {
								'Content-Type': 'application/json',
								'Authorization': "Bearer "+$rootScope.token
							},
							data:{
								'URL': $scope.tmp.URL,
								'catalogueName': $scope.tmp.catalogueName,
								'editable': true,
								'password': null,
								'username': null,
								'isIdra': true,
								'clientID': null,
								'clientSecret': null,
								'portal': null
							}
					};
				$scope.request(req);
				}
			}
			
		else if($scope.checkValue){
			console.log("MODIFICA catalogo CON CREDENZIALI IDRA");

				if(checkRemcat($scope.tmp,$scope.mode) ){
					var req = {
							method: 'PUT',
							url: config.ADMIN_SERVICES_BASE_URL+config.REMOTE_CAT_SERVICE+"/"+$scope.tmp.id.toString(),
							headers: {
								'Content-Type': 'application/json',
								'Authorization': "Bearer "+$rootScope.token
							},
							data:{
								'URL': $scope.tmp.URL,
								'catalogueName': $scope.tmp.catalogueName,
								'editable': true,
								'password': md5.createHash($scope.tmp.password),
								'username': $scope.tmp.username,
								'isIdra': true,
						 
							}
					};
				$scope.request(req);
				}
		
			}
		else {
				if(checkRemcat($scope.tmp,$scope.mode) ){
					var req = {
							method: 'PUT',
							url: config.ADMIN_SERVICES_BASE_URL+config.REMOTE_CAT_SERVICE+"/"+$scope.tmp.id.toString(),
							headers: {
								'Content-Type': 'application/json',
								'Authorization': "Bearer "+$rootScope.token
							},
							data:{	
								'URL': $scope.tmp.URL,
								'catalogueName': $scope.tmp.catalogueName,
								'editable': true,
								'password': $scope.tmp.password,
								'username': $scope.tmp.username,
								'isIdra': $scope.tmp.isIdra,
								'clientID': $scope.tmp.clientID,
								'clientSecret': $scope.tmp.clientSecret,
								'portal': $scope.tmp.portal
							}
					};
		
					$scope.request(req);
				}
			}
	}
	$scope.cancel = function () {
		$modalInstance.close();
	};
	
	
	  $scope.checkedValue=function(checked){
		$scope.checkValue = checked;
			if($scope.checkValueIDM){
				$scope.checkValueIDM = false;
			}
		}
		
	$scope.checkedValueIDM=function(checkedIDM){
		$scope.checkValueIDM = checkedIDM;
		if($scope.checkValue){
				$scope.checkValue = false;
			}
		}
		
	$scope.delete_Cred=function(){
		$scope.deleteCred = true;
			
		}
	
	$scope.ckeckIDM=function(){
			return $scope.checkValueIDM;
		}
		
	$scope.ckeck=function(){
			return $scope.checkValue;
		}
		
		$scope.catalogueType=function(selected){
		if(selected=="idra"){
				$scope.isIdra = true;
			}
		else
			$scope.isIdra = false;
		}
		
		$scope.authenticationType=function(authSelected){
	
		 
		if(authSelected=="basic"){
				$scope.isBasic = true;
				$scope.isOauth = false;
				$scope.noAuth = false;
			}
		else if(authSelected=="oauth2"){
				$scope.isOauth = true;
				$scope.isBasic = false;
				$scope.noAuth = false;
		}
		else if (authSelected=="noAuth"){
			$scope.isBasic = false;
			$scope.isOauth = false;
			$scope.noAuth = true;
		}
		}
		
		
		$scope.setSelection=function(){
			if($scope.mode == 'update'){
				return false;
			}
			else
				return true;
		}

		$scope.setPlaceholder=function(){
			if($scope.isIdra)
				$scope.somePlaceholder = 'Insert Idra Base Path';
			else
				$scope.somePlaceholder = 'Insert URL';
			}
		
	  $scope.setCredentials=function(username, password){
		$scope.usernameIdra = username;
		$scope.passwordIdra = password;

		}
		
/*
		$scope.tokenIdra="";
		$scope.loginIdra = function(username, password){
			$scope.usernameIdra = username;
			$scope.passwordIdra = password;
			
			console.log("Login username: " + $scope.usernameIdra);
			console.log("URL: " + $scope.tmp.URL);
		
			var req = {
					method: 'POST',
				
					url: config.ADMIN_SERVICES_BASE_URL + config.REMOTE_CAT_SERVICE + "/login",
					dataType: 'json',
					headers: {
						'Content-Type': 'application/json'
					},
					data:{					
						'username':$scope.usernameIdra,
						'password':md5.createHash($scope.passwordIdra)
					}};			

			$rootScope.startSpin();
			$http(req).then(function(value){
				console.log("Login token: " + value.data);
				console.log("Login passw: " + md5.createHash($scope.passwordIdra));
				$rootScope.stopSpin();
				$scope.tokenIdra=value.data;
				//$rootScope.loggedUsername = $scope.username;
				//$cookies.put('loggedin', value.data,{"path":"/"});
				//$cookies.put('username', $scope.username,{"path":"/"});
				//$window.location.assign('#/metadata');
			}, function(value){
				//console.log(value);
				$rootScope.stopSpin();
				$rootScope.showAlert('danger',value.data.userMessage);
			});			
		}
*/
}]);




angular.module("IdraPlatform").controller('UpdatePasswordCtrl',['$scope','config','$rootScope','$http','md5',function($scope,config,$rootScope,$http,md5){

	$scope.oldPassword="";
	$scope.newPassword="";
	$scope.newPasswordConfirm="";
	
	$scope.orionUrl="";
	
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
	
	
		$scope.updateOrionUrl = function(){
		
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
	
	$rootScope.orionUrlInConf='';
	$rootScope.orionEnabled=false;
	$rootScope.showUrl=false;

	$rootScope.startSpin();
	$http(req).then(function(value){

		$rootScope.stopSpin();		
		console.log(value);
		$scope.refreshPeriod = value.data.refresh_period;
		$scope.rdfMaxDimension=parseInt(value.data.rdf_max_dimension);
		$scope.checkContentLength= (value.data.rdf_undefined_content_length === 'true');
		$scope.checkRdfDimension= (value.data.rdf_undefined_dimension === 'true');
		$rootScope.orionUrlInConf = value.data.orionUrl;
		if($rootScope.orionUrlInConf=='')
			$rootScope.orionEnabled=false;
		else
			$rootScope.orionEnabled=true;
		
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

angular.module("IdraPlatform").controller('OrionManagerCtrl',['$scope','config','$rootScope','$http',function($scope,config,$rootScope,$http){
	
		function validateUrl(url){
			var reg = /^(http|https):\/\/[^ "]+$/;
			if(reg.test(url) && (url.slice(-1) != '/')){
				return true;
			}else{
				return false;
			}
		}
		
		$scope.$watch('orionEnabled', function(newValue, oldValue) {
        if (newValue !== oldValue) {
            
			if(newValue==false){
				$rootScope.showUrl = false;
				$rootScope.orionEnabled=false;

				var req = {
						method: 'POST',
						url: config.ADMIN_SERVICES_BASE_URL+config.CONFIGURATION_SERVICE,
						headers: {
							'Content-Type': 'application/json',
							'Authorization': "Bearer "+$rootScope.token
						},
						data:{
							orionUrl: ""
						}
				};
		
				$rootScope.startSpin();
				$http(req).then(function(value){
		
					$rootScope.stopSpin();
					$rootScope.showAlert('success',"Context Broker successfully disabled!");
				}, function(value){
		
					if(value.status==401){
						$rootScope.token=undefined;
					}
		
					$rootScope.stopSpin();
					$rootScope.showAlert('danger',value.data.userMessage);
				});
			}
			
			
		    if(($rootScope.orionUrlInConf=='') && ($rootScope.orionEnabled==true))
				$rootScope.showUrl = false;
	
			if(($rootScope.orionUrlInConf!='') && ($rootScope.orionEnabled==true))
				$rootScope.showUrl = true;

        }
    });

	if(($rootScope.orionUrlInConf=='') && ($rootScope.orionEnabled==true))
		$rootScope.showUrl = false;
	
	if(($rootScope.orionUrlInConf!='') && ($rootScope.orionEnabled==true))
		$rootScope.showUrl = true;


	$scope.orionUrl="";


	$scope.updateOrionUrl = function(){
		if(validateUrl($scope.orionUrl.toString())){
	
			var req = {
					method: 'POST',
					url: config.ADMIN_SERVICES_BASE_URL+config.CONFIGURATION_SERVICE,
					headers: {
						'Content-Type': 'application/json',
						'Authorization': "Bearer "+$rootScope.token
					},
					data:{
						orionUrl: $scope.orionUrl.toString()
					}
			};
	
			$rootScope.startSpin();
			$http(req).then(function(value){
	
				$rootScope.stopSpin();
				$rootScope.showAlert('success',"Context Broker Configurations successfully updated!");
				
				var req = {
				method: 'GET',
				url: config.ADMIN_SERVICES_BASE_URL+config.CONFIGURATION_SERVICE,
				headers: {
					'Content-Type': 'application/json'
				}
				};
				//$rootScope.orionUrlInConf='';
			
				$rootScope.startSpin();
				$http(req).then(function(value){
			
					$rootScope.stopSpin();		
					$rootScope.orionUrlInConf = value.data.orionUrl;
					$rootScope.showUrl=true;
					
					//if($rootScope.orionUrlInConf=='')
					//	$rootScope.orionEnabled=false;
					//else
					//	$rootScope.orionEnabled=true;
					
				}, function(value){
			
					$rootScope.stopSpin();
					$rootScope.showAlert('danger',"Error in receiving configurations");
				});
				
	
			}, function(value){
	
				if(value.status==401){
					$rootScope.token=undefined;
				}
	
				$rootScope.stopSpin();
				$rootScope.showAlert('danger',"Error in updating configurations");
			});
	
		} else {
			$rootScope.showAlert('danger',"Invalid URL!");
		}
		
		}
	

}]);
