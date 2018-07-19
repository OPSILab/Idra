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
angular.module("IdraPlatform").controller('DatasetDetailCtrl',['$scope','$rootScope','$http','config','$anchorScroll','$location','$modal','$sce','$window','dataletsAPI','dialogs',function($scope,$rootScope,$http,config,$anchorScroll,$location,$modal,$sce,$window,dataletsAPI,dialogs){
	
	if($rootScope.datasetDetail==undefined){
		$window.location.assign('#/showDatasets');
		return;
	}
	
	$scope.returnToSearch = function(){
		$window.location.assign("#/showDatasets");
	}
	
	$scope.gotoTop = function() {
	      // set the location.hash to the id of
	      // the element you wish to scroll to.
	      $location.hash('scrollHere');

	      // call $anchorScroll()
	      $anchorScroll();
	    };
	
	$scope.gotoTop();
	
	var checkDistributionFormat = function(distribution){
		
		if(!$rootScope.dataletEnabled){
			distribution.dataletShowButtonEnabled=false;
			return;
		}
			
		var allowed=false;
		var parameter=undefined;
		
		if(distribution.format!=undefined && distribution.format!=""){
			parameter=distribution.format;
		}else if(distribution.mediaType!=undefined && distribution.mediaType!=""){
			if(distribution.mediaType.indexOf("/")>0)
				parameter=distribution.mediaType.split("/")[1];
			else
				parameter=distribution.mediaType;
		}

		if(parameter!=undefined){
			switch(parameter.toLowerCase()){
			case 'xml':
			case 'csv':
			case 'json':
			case 'application/json':
			case 'text/json':
			case 'text/csv':
			case 'geojson':
			case 'kml':
				allowed=true;
				break;
			default:
				allowed=false;
			break;
			}
		}
		distribution.dataletShowButtonEnabled=allowed;
	}
	
	$scope.dataset = $rootScope.datasetDetail;
		
	$scope.dataset.licenses = [];
	var tmpLic=[];
	for(i=0; i<$scope.dataset.distributions.length; i++){
		$scope.dataset.distributions[i].collapseDetails=true;
		if(tmpLic.indexOf($scope.dataset.distributions[i].license.name)<0 && $scope.dataset.distributions[i].license.name!=''){
			tmpLic.push($scope.dataset.distributions[i].license.name);
			$scope.dataset.licenses.push({"name":$scope.dataset.distributions[i].license.name, "uri":$scope.dataset.distributions[i].license.uri});
		}
		
		$scope.dataset.distributions[i].dataletShowButtonEnabled = false;	
		$scope.dataset.distributions[i].distributionDonwloadUrlOk = true;
		
		checkDistributionFormat($scope.dataset.distributions[i]);
	}
	
	$scope.getIcon = function(format,mediaType){
		var str = "";
		var parameter=undefined;
		
		if(format!=undefined && format!=""){
			parameter=format;
		}else if(mediaType!=undefined && mediaType!=""){
			if(mediaType.indexOf("/")>0)
				parameter=mediaType.split("/")[1];
			else
				parameter=mediaType;
		}

		if(parameter!=undefined){
			switch(parameter.toLowerCase()){
			case 'xml':
				str='xml';
				break;
			case 'rdf':
				str='rdf';
				break;
			case 'csv':
			case 'text/csv':
				str='csv';
				break;
			case 'dbf':
				str='dbf';
				break;
			case 'zip':
			case 'application/zip':
				str='zip';
				break;
			case 'xlsx':
			case 'xls':
				str='xlsx';
				break;
			case 'doc':
			case 'docx':
				str='word';
				break;
			case 'pdf':
				str='pdf';
				break;
			case 'mp3':
				str='mp3';
				break;
			case 'txt':
				str='txt';
				break;
			case 'rar':
				str='rar';
				break;
			case 'vlc':
				str='vlc';
				break;
			case 'ppt':
				str='ppt';
				break;
			case 'webgis':
				str='webgis';
				break;
			case 'waw':
				str='waw';
				break;
			case 'json':
			case 'text/json':
			case 'application/json':
				str='json';
				break;
			case 'html':
			case 'text/html':
				str='html';
				break;
			case 'kml':
				str='kml';
				break;
			case 'geojson':
				str='geojson';
				break;
			default:
				str='file'
			}
		}else{
			str='file';
		}

		return 'images/'+str+'.png';

	};
	
	
	
	$scope.searchFacet = function(item){
			
		$rootScope.defaultFacetsSearchRequestData.filters=[];
		
		$rootScope.defaultFacetsSearchRequestData.filters.push({"field":"tags","value":item});
		$rootScope.defaultFacetsSearchRequestData.filters.push({"field":"ALL","value":""});
		
		$rootScope.selectedFacetsPrevious=[];
		$rootScope.selectedFacetsPrevious.push({"search_parameter":"tags","value":item,"display_value":item});
	
		//$rootScope.defaultFacetsSearchRequestData.filters[0].value=item;
		
		$rootScope.reqDataset.data = $rootScope.defaultFacetsSearchRequestData;
		
		$rootScope.startSpin();
		$http($rootScope.reqDataset).then(function(value){
			
			//Al momento lo mettiamo undefined
			$rootScope.previousContext=undefined;
			
			$rootScope.closeAlert();			
			$rootScope.foundDatasets=value.data;
			$rootScope.stopSpin();
			if($rootScope.foundDatasets.count!=null){
				if($rootScope.foundDatasets.count !=0 ){
					$rootScope.facets = $rootScope.foundDatasets.facets;
					$rootScope.originalSortParam=$scope.orderBy;
					$rootScope.originalSortMode = $scope.orderMode;
					$rootScope.originalRows=$scope.rows;
					$window.location.assign('#/showDatasets');
				}else{
					$rootScope.showAlert('warning',"No result found!");
				}
			}else{
				$rootScope.showAlert('warning',"No result found!");
			}
		}, function(value){

			$rootScope.stopSpin();
			//We can change the message to value.data.technicalMessage
			//in order to show the EurovocTranslationNotFound message
			var messageLevel = 'danger';
			if(value.data.statusCode=='400'){
				messageLevel = 'warning';
			}
			$rootScope.showAlert(messageLevel,value.data.userMessage);

		});
		
	};
	
	$scope.showDate = function(date){
		if(date=='1970-01-01T00:00:00Z') return false;
		return true;
	}
//	var promise=undefined;
	$scope.createDatalet = function(datasetID,nodeID,distribution){
		
		var parameter=undefined;
		
		if(distribution.format!=undefined && distribution.format!=""){
			parameter=distribution.format;
		}else if(distribution.mediaType!=undefined && distribution.mediaType!=""){
			if(distribution.mediaType.indexOf("/")>0)
				parameter=distribution.mediaType.split("/")[1];
			else
				parameter=distribution.mediaType;
		}
		
		var reqCheckDownloadUri = {
				method: 'GET',
				url: config.CLIENT_SERVICES_BASE_URL+config.CHECK_DISTRIBUTION_URL+window.encodeURIComponent(distribution.downloadURL)
		};
			
		$http(reqCheckDownloadUri).then(function(value){
			$window.open($sce.trustAsResourceUrl(config.DATALET_URL+"?format="+parameter+"&nodeID="+nodeID+"&distributionID="+distribution.id+"&datasetID="+datasetID+"&url="+window.encodeURIComponent(distribution.downloadURL)));
		},function(value){
			distribution.distributionDonwloadUrlOk = false;
			dialogs.error("Unable to create Datalet","File with url <br/> "+distribution.downloadURL+" <br/> returned "+value.status+"!");
		});		
	};
	
	// Cancel interval on page changes
//	$scope.$on('$destroy', function(){
//	    if (angular.isDefined(promise)) {
//	        $interval.cancel(promise);
//	        promise = undefined;
//	    }
//	});		
//	
//	$scope.getDataset = function(){
//		console.log("GET DATASET");
//		var req = {
//				method: 'GET',
//				url: config.CLIENT_SERVICES_BASE_URL+"/catalogues/"+$scope.dataset.nodeID+"/dataset/"+$scope.dataset.id,
//				headers: {
//					'Content-Type': 'application/json'
//				}
//		};
//		
//		$http(req).then(function(value){
//			var distributionUpdated=value.data.distributions;
//				
//				for(i=0; i<$scope.dataset.distributions.length; i++){
//					if($scope.dataset.distributions[i].id==distributionUpdated[i].id){
//						$scope.dataset.distributions.hasDatalets =distributionUpdated[i].hasDatalets;
////						break;
//					}
//				}
//			
//		},function(value){
//			console.log("Error retrieving dataset")
//		});
//	};
	
	$scope.showDatalets = function(datasetID,nodeID,distribution){
		$rootScope.distributionTitle = distribution.title;
		$rootScope.datasetTitle = $rootScope.datasetDetail.title;
		$rootScope.datalets=[];
		
		var req = {
				method: 'GET',
				url: config.CLIENT_SERVICES_BASE_URL+"/catalogues/"+nodeID+"/dataset/"+datasetID+"/distribution/"+distribution.id+"/datalets",
				headers: {
					'Content-Type': 'application/json'
				}
		};
		
		dataletsAPI.getDataletsByDistribution(nodeID,datasetID,distribution.id).then(function(value){
			dataletsAPI.setCurrentDatalets(value.data);
			$window.location.assign("#/datalets");
		},function(value){
			console.log("Error retrieving datalets")
		});

	}
	
}]);	
