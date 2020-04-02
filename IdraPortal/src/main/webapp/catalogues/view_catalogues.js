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
angular.module("IdraPlatform").controller('ViewCataloguesController',["$scope","$http",'$filter','config','$rootScope','$window','ODMSNodesAPI','marked','$sce',function($scope,$http,$filter,config,$rootScope,$window,ODMSNodesAPI,marked,$sce){
		
	$rootScope.previousLocation='viewCatalogues';
	$rootScope.selectedFacetsPrevious=undefined;
    $rootScope.currentPageGlobal = undefined;
	
    $scope.enableCardVisualization=true;
    
	$scope.selected ="";
	$scope.nodes=[];
	$scope.displayedCollection=[];
	$scope.nodesLeft=[];
	$scope.nodesRight=[];
//	$scope.nodeNames=[];
	$scope.nodesForResult=[];
	//$rootScope.startSpin();
	
	$rootScope.nodeForResults = angular.copy($scope.nodesForResult);
	//$rootScope.stopSpin();
	
	$scope.categories = [{text:'Municipality',value:'Municipality'},{text:'Province',value:'Province'},{text:'Private Institution',value:'Private Institution'},{text:'Public Body',value:'Public Body'},{text:'Region',value:'Region'}];
	
	/*
	  ODMSNodesAPI.getODMSNodesAPI(true).then(function(value){
		var count=0;
		for(i=0; i<value.data.length; i++){
			if(value.data[i].synchLock!='FIRST' && value.data[i].isActive){
				var node=value.data[i];
				$scope.nodesForResult.push({id: value.data[i].id, name: value.data[i].name, federationLevel:value.data[i].federationLevel});
				$scope.nodeNames.push(node.name);
				
				node.descriptionIsCollapsible=false;
				if(node.description.length>250){
					spaceIndex = node.description.substring(247,node.description.length).indexOf(' ');
					node.tmpDesc=node.description.substring(0,spaceIndex+247);
					node.descriptionIsCollapsible=true;
					node.descCollapse=true;
				}
				
				if(count%2==0){
					$scope.nodesLeft.push(node);
				}else{
					$scope.nodesRight.push(node);
				}
				$scope.nodes.push(node);
				count++;
			}
		}
		$scope.displayedCollection = [].concat($scope.nodes);
		$rootScope.nodeForResults = angular.copy($scope.nodesForResult);
		$rootScope.stopSpin();
	}, function(){

	});	
	 * 
	 * */
	$scope.rows=10;
	$scope.offset=0;
	$scope.totalItems = 0;
	$scope.currentPage = 1;
	$scope.orderBy="name";
	$scope.orderType="asc";
	$scope.name="";
	$scope.country="";
	$scope.nodeCountries=[]
	$rootScope.startSpin();
	
	var defaultSearchRequestData = {
			method: 'POST',
			url: config.CLIENT_SERVICES_BASE_URL+config.SEARCH_SERVICE,
			headers: {
				'Content-Type': 'application/json'
			},
			data:{
				"filters":[{"field":"ALL","value":""}],
				"live":false,
				"sort":{"field":"title","mode":"asc"},
				"rows":"25","start":"0",
				"nodes":[],
				"euroVocFilter":{"euroVoc":false,"sourceLanguage":"","targetLanguages":[]}
			}
	}; 

	$scope.showDatasets = function(node){
		$rootScope.startSpin();
		defaultSearchRequestData.data.nodes.push(node.id);
		$rootScope.reqDataset = defaultSearchRequestData; 
		$rootScope.datasets=[];
		$http(defaultSearchRequestData).then(function(value){

			$rootScope.closeAlert();			
			$rootScope.foundDatasets=value.data;
			$rootScope.stopSpin();
			if($rootScope.foundDatasets.count!=null){
				if($rootScope.foundDatasets.count !=0 ){
					$rootScope.facets = $rootScope.foundDatasets.facets;
					$rootScope.originalSortParam="title";
					$rootScope.originalSortMode = "asc";
					$rootScope.originalRows="25";
					$window.location.assign('#/showDatasets');
				}else{
					$rootScope.showAlert('warning',"No result found!");
				}
			}else{
				$rootScope.showAlert('warning',"No result found!");
			}
		}, function(value){

			$rootScope.stopSpin();
			var messageLevel = 'danger';
			if(value.data.statusCode=='400'){
				messageLevel = 'warning';
			}
			$rootScope.showAlert(messageLevel,value.data.userMessage);

		});
	};

	$scope.$watch('currentPage', function(newPage){
		
		$scope.nodesLeft=[];
		$scope.nodesRight=[];
		$scope.nodes=[];
		$rootScope.startSpin();
		ODMSNodesAPI.clientCataloguesAPI(true,$scope.rows,$scope.offset+$scope.rows*(newPage-1),$scope.orderBy,$scope.orderType,$scope.name,$scope.country).then(function(value){
			var count=0;
			$scope.totalItems = value.data.count; 
			for(i=0; i<value.data.catalogues.length; i++){
				var node=value.data.catalogues[i];
				
				if($scope.nodesForResult.filter(x=> x.id == node.id).length == 0){
					$scope.nodesForResult.push({id: node.id, name: node.name, federationLevel:node.federationLevel});
				}
//				$scope.nodeNames.push(node.name);
					
				node.descriptionIsCollapsible=false;
				if(node.description.length>250){
					spaceIndex = node.description.substring(247,node.description.length).indexOf(' ');
					node.tmpDesc=node.description.substring(0,spaceIndex+247);
					node.descriptionIsCollapsible=true;
					node.descCollapse=true;
				}
					
				if(count%2==0){
					$scope.nodesLeft.push(node);
				}else{
					$scope.nodesRight.push(node);
				}
				$scope.nodes.push(node);
				count++;
			}
			
			$scope.nodes.forEach(n=>{
				if($scope.nodeCountries.indexOf(n.country)<0 && n.country!='')
					$scope.nodeCountries.push(n.country);
			});
			
			$scope.displayedCollection = [].concat($scope.nodes);
			$rootScope.nodeForResults = angular.copy($scope.nodesForResult);
			$rootScope.stopSpin();
		}, function(){

		});	
//		if(newPage == 1 && $rootScope.foundDatasets!=undefined && firstTime){
//			$scope.watchPage = newPage;
//			$rootScope.datasets = $rootScope.foundDatasets.results;
//			displayResults();
//			firstTime=false;
//			return;
//		}
//			
//		$rootScope.currentPageGlobal = newPage;
//		$rootScope.reqDataset.url = config.CLIENT_SERVICES_BASE_URL+config.SEARCH_SERVICE;
//		$rootScope.reqDataset.data.start = ((newPage-1)*$scope.rows).toString();
//		
//		$rootScope.startSpin();
//		$http($rootScope.reqDataset).then(function(value){
//			$rootScope.foundDatasets=value.data;
//			$rootScope.datasets=value.data.results;
//			$scope.watchPage = newPage;
//			displayResults();
//			$rootScope.stopSpin();
//		},function(value){
//
//		});
////		$scope.gotoTop();
	});

}]);
