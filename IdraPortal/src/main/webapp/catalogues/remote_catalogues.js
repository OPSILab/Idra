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
angular.module("IdraPlatform").controller('RemoteCataloguesController',["$scope","$http",'$filter','config','$rootScope','dialogs','$interval','$timeout','$modal','FileSaver','Blob','$window','ODMSNodesAPI','md5',function($scope,$http,$filter,config,$rootScope,dialogs,$interval,$timeout,$modal,FileSaver,Blob,$window,ODMSNodesAPI,md5){

$scope.allRemCat=[];
$scope.sel = "";
$scope.selIsAuth = false;

	//Visualizzazione della lista di cataloghi remoti
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
			$scope.displayedCollection2 = [].concat($scope.allRemCat);
			//if($scope.displayedCollection2.length != 0)
				$scope.selected = $scope.displayedCollection2[0];
			$scope.sel = $scope.selected.URL;
			//$scope.getRemoteNodes();
			
			//$scope.sel = $scope.selected.URL;
			$scope.selID = $scope.selected.id;
			$scope.selIsAuth = ($scope.selected.username!=null)?true:false;
			
			//$scope.getRemoteNodes();
			$scope.selCatalogue();
			
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
	
	
	$scope.selCatalogue = function(){
			$scope.sel = $scope.selected.URL;
			$scope.selID = $scope.selected.id;
			$scope.selIsAuth = ($scope.selected.username!=null)?true:false;

			// 3 CASI
			if( ($scope.selected.username==null) && ($scope.selected.isIdra==false)){
				console.log("Visualizzazione catalogo JSON");
				$scope.getRemoteNodes();
			}
			else if( ($scope.selected.username!=null) && ($scope.selected.clientID==null) && ($scope.selected.isIdra==true)){
				console.log("Visualizzazione catalogo IDRA AUTENTICATO in IDRA");

				var req = {
						method: 'GET',
						url: config.ADMIN_SERVICES_BASE_URL + config.REMOTE_CAT_SERVICE + "/auth/" + $scope.selID,
						dataType: 'json',
						headers: {
							'Content-Type': 'application/json'
						}};	
		$rootScope.startSpin();
		$http(req).then(function(value){
			
			if(value.data.catalogues!=null || value.data.catalogues!=undefined){
				$rootScope.remote_nodes=value.data.catalogues; 
			}
			else{
			$rootScope.remote_nodes=value.data; 
			}
			checkCatalogues();
			$rootScope.stopSpin();
		}, function(e){
			console.log("ERROR");
			$rootScope.stopSpin();
		});

			}
			
			else if($scope.selected.clientID!=null){
				console.log(" Visualizzazione catalogo IDRA AUTENTICATO con IDM");			
				var req = {
						method: 'GET',
						url: config.ADMIN_SERVICES_BASE_URL + config.REMOTE_CAT_SERVICE + "/authIDM/" + $scope.selID,
						headers: {
							'Content-Type': 'application/json'
		
						}};	
						
				$rootScope.startSpin();
				$http(req).then(function(value){
					
					if(value.data.catalogues!=null || value.data.catalogues!=undefined){
						$rootScope.remote_nodes=value.data.catalogues; 
					}
					else{
					$rootScope.remote_nodes=value.data; 
					}
					checkCatalogues();
					$rootScope.stopSpin();
				}, function(e){
					console.log("ERROR");
					$rootScope.stopSpin();
				});
				
			}
			
			else{
				console.log("Visualizzazione catalogo IDRA NON AUTENTICATO");
	
				var strQuery="withImage=true&rows=100&offset=0&orderBy=id&orderType=asc";
				var req = {
						method: 'GET',
						url: $scope.sel + "Idra/api/v1/client" + config.CLIENT_CATALOGUES+"?"+strQuery,
						headers: {
							'Content-Type': 'application/json'
						}};
						
				$rootScope.startSpin();
				$http(req).then(function(value){
					
					if(value.data.catalogues!=null || value.data.catalogues!=undefined){
						$rootScope.remote_nodes=value.data.catalogues; 
					}
					else{
					$rootScope.remote_nodes=value.data; 
					}
					checkCatalogues();
					$rootScope.stopSpin();
				}, function(e){
					console.log("ERROR");
					$rootScope.stopSpin();
				});
		
				
		}
		};


	$scope.updatePeriods=[{text:'1 hour',value:'3600'},{text:'1 day',value:'86400'},{text:'1 week',value:'604800'}];
	$scope.nodeTypes = config.NODE_TYPES.split(',');
	$scope.returnToCatalogues = function(){
		$window.location.assign('#/catalogues');
	}



	$scope.dateFormat="MMM - dd - yyyy";
	
	$scope.getRemoteNodes = function(){
		
			var req = {
					method: 'GET',
					url: $scope.sel,
					headers: {
						'Content-Type': 'application/json'
					}};
		
		
		$rootScope.startSpin();
		$http(req).then(function(value){
			
			if(value.data.catalogues!=null || value.data.catalogues!=undefined){
				$rootScope.remote_nodes=value.data.catalogues; 
			}
			else{
			$rootScope.remote_nodes=value.data; 
			}
			checkCatalogues();
			$rootScope.stopSpin();
		}, function(e){
			console.log("ERROR");
			$rootScope.stopSpin();
		});
		
	}
		
	$scope.itemsByPage = 20;
		
	$scope.toDate = function(value){
		var date = new Date(value);
		return date.getDate() + '/' +( date.getMonth()+1) + '/' +  date.getFullYear()+" "+date.getHour()+":"+date.getMinute();
	}

	$scope.getNumber = function(str){
		return str.split('_')[1];
	};

	$scope.addRemoteNode = function(node) {	
		dialogs.confirm("Confirm","Add this remote catalogue to your local instance?").result.then(function(value){
			node.alreadyLocal = true;
			
			$scope.setLevel(node);
			
			if(node.hasOwnProperty("id"))
				delete node.id;
			if(node.hasOwnProperty("image"))
				if(node.image.hasOwnProperty("imageId"))
					delete node.image.imageId;
			if(node.hasOwnProperty("isActive"))
				delete node.isActive;
			
			if(node.hasOwnProperty("sitemap")){
				delete node.sitemap.id;
				delete node.sitemap.navigationParameter.id;
			}
			
			if(!node.hasOwnProperty("homepage")){
				node.homepage=node.host;
			}
			
			if(node.hasOwnProperty("datasetStart")){
				node.datasetStart=0;
			}
			var fd = new FormData();  
			fd.append("node",JSON.stringify(node));
			fd.append("dump",'');

			$rootScope.startSpin();
			ODMSNodesAPI.addODMSNode(fd).then(function(){
				$rootScope.stopSpin();
				$rootScope.showAlert('success',"Catalogue "+node.name+" added to the local Federation!");
				$rootScope.getNodes();
			}, function(value){
				
				$rootScope.stopSpin();
				
				if(value.status==401){
					$rootScope.token=undefined;
					dialogs.error("Authentication failed","Please login first");
				}

				if(value.status!=502){
					dialogs.error("Registration failed",value.data.userMessage);
					$rootScope.getNodes();
					checkCatalogues();
				}

			});
		},function(){

		});
	};	
	
	$scope.setLevel = function(node) {

		switch(node.nodeType){
			case 'CKAN':
				node.federationLevel='LEVEL_3';
				return "3";
			case 'DKAN':
				node.federationLevel='LEVEL_2';
				return "2";
			case 'SOCRATA':
				node.federationLevel='LEVEL_2';
				return "2";
			case 'SPOD':
				node.federationLevel='LEVEL_2';
				return "2";
			case 'WEB':
				node.federationLevel='LEVEL_2';
				return "2";
			case 'DCATDUMP':
				if(node.dumpURL!=''){
					node.federationLevel='LEVEL_2';
					return "2";
				}
				else{
					node.federationLevel='LEVEL_4';
					return "4";
				}

			case 'ORION':
				node.federationLevel='LEVEL_4';
				return "4";
			case 'SPARQL':
				node.federationLevel='LEVEL_4';
				return "4";
			case 'OPENDATASOFT':
			case 'JUNAR':	
				node.federationLevel='LEVEL_2';
				return "2";
			default:
				break;
			}
		
	}	
	
	$scope.nodeCountries=[]
	var checkCatalogues = function(){
		for(i=0; i<$rootScope.remote_nodes.length; i++){
			$rootScope.remote_nodes[i].alreadyLocal = alreadyLocal($rootScope.remote_nodes[i].name,$rootScope.remote_nodes[i].host); 
		}
		$scope.remote_displayedCollection = [].concat($rootScope.remote_nodes);
		
		$rootScope.remote_nodes.forEach(n=>{
			if($scope.nodeCountries.indexOf(n.country)<0 && n.country!='')
				$scope.nodeCountries.push(n.country);
		});
	}
	
	var currentUrls = angular.copy($rootScope.urls);
	var currentNames = angular.copy($rootScope.names);
	var alreadyLocal = function(name,host){
		var tmpHost="";
		if(host[host.length-1]=='/'){
			tmpHost=host.substring(0,host.length-1);
		}else{
			tmpHost=host+"/";
		}
		
		if(currentUrls==undefined){
			return false;
		}

		if(currentNames==undefined){
			return false;
		}
		
		if(currentUrls.indexOf(host.toLowerCase())>=0 || currentUrls.indexOf(tmpHost.toLowerCase())>=0){
				return true;
		}
		if(currentNames.indexOf(name.toLowerCase())>=0){
			return true;
		}
		return false;
	};
	
	
	if($rootScope.remote_nodes==undefined || $rootScope.remote_nodes.length==0 ){
		$rootScope.remote_nodes=[];	
	}else{
		checkCatalogues();
	}
	

	
	
	
}]);

