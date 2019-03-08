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
angular.module("IdraPlatform").controller('DatasetDetailCtrl',['$scope','$rootScope','$http','config','$anchorScroll','$location','$modal','$sce','$window','dataletsAPI','dialogs','$routeParams','Papa',function($scope,$rootScope,$http,config,$anchorScroll,$location,$modal,$sce,$window,dataletsAPI,dialogs,$routeParams,Papa){
	
	console.log($routeParams.id);	
	var checkDistributionFormat = function(distribution){
		
		
		var allowed=false;
		var preview=false;
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
			case 'fiware-ngsi':
			case 'kml':
				allowed=true;
				break;
			default:
				allowed=false;
			break;
			}
						
		switch(parameter.toLowerCase()){		
		case 'xml':
		case 'rdf':
		case 'rdf+xml':
		case 'geojson':
		//case 'html':
		case 'text/html':
		case 'text':
		case 'txt':
		case 'text/plain':
		case 'csv':
		case 'text/csv':
		case 'json':
		case 'fiware-ngsi':
		case 'text/json':
		case 'application/json':
		case 'kml':
//		case 'gpx':
//check
		case 'sparql':
		case 'pdf':
		case 'application/pdf':
/*		case 'excel':
		case 'xlsx':
		case 'xls':
		case 'doc':
		case 'docx':
		case 'ppt':
		case 'kml':
		case 'png':
		case 'jpeg':
		case 'jpg':*/
//end check
			preview=true;
			break;
		default:
			preview=false;
		}
			
		
		if(!allowed){
			if(parameter.toLowerCase().includes("csv")){
				allowed=true;
			}
		}
		
		if(!preview){
			if(parameter.toLowerCase().includes("csv")){
				preview=true;
			}
		}
			
		}
		
		if(!$rootScope.dataletEnabled){
			allowed=false;
		}
		
		distribution.dataletShowButtonEnabled=allowed;
		distribution.previewShowButtonEnabled=preview;
	}
		
	
	var manageLicense = function(){
		$scope.dataset.licenses = [];
		var tmpLic=[];
		for(i=0; i<$scope.dataset.distributions.length; i++){
			$scope.dataset.distributions[i].lockFile=false;
			$scope.dataset.distributions[i].collapseDetails=true;
			if(tmpLic.indexOf($scope.dataset.distributions[i].license.name)<0 && $scope.dataset.distributions[i].license.name!=''){
				tmpLic.push($scope.dataset.distributions[i].license.name);
				$scope.dataset.licenses.push({"name":$scope.dataset.distributions[i].license.name, "uri":$scope.dataset.distributions[i].license.uri});
			}
			
			$scope.dataset.distributions[i].dataletShowButtonEnabled = false;	
			$scope.dataset.distributions[i].distributionDonwloadUrlOk = true;
			$scope.dataset.distributions[i].distributionPreviewOk=true;
			$scope.dataset.distributions[i].previewShowButtonEnabled = false;
			
			checkDistributionFormat($scope.dataset.distributions[i]);
		}
	};
	
	//if($rootScope.datasetDetail==undefined || $rootScope.datasetDetail.seoIdentifier != $routeParams.id){
	if($rootScope.datasetDetail==undefined || $rootScope.datasetDetail.id != $routeParams.id){
		var req = {
			method: 'GET',
			url: config.CLIENT_SERVICES_BASE_URL+'/datasets/'+$routeParams.id,
			headers: {
				'Content-Type': 'application/json',
			}
		};
		$rootScope.startSpin();
		$http(req).then(function(value){
			console.log(value);
			$rootScope.datasetDetail = value.data;
			$scope.dataset = value.data;
			manageLicense();
			$rootScope.stopSpin();
		},function(value){
		   $rootScope.stopSpin();
		   $window.location.assign("#/metadata");
		});
	}else{
		$scope.dataset = $rootScope.datasetDetail;
		manageLicense();
	}
	
	$scope.returnToSearch = function(){
		$window.location.assign("#/showDatasets");
	}
	
	$scope.gotoTop = function() {
	      // set the location.hash to the id of
	      // the element you wish to scroll to.
	      $location.hash('top');

	      // call $anchorScroll()
	      $anchorScroll();
	    };
	
//	$scope.gotoTop();
	
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
			case 'sparql':
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
			
			if(str=="file"){
				if(parameter.toLowerCase().includes("csv")){
					str="csv";
				}
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
			if(parameter=='fiware-ngsi') parameter ='json';
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
		
		distribution.lockFile=true;
		$http(reqCheckDownloadUri).then(function(value){
			distribution.lockFile=false;
			$window.open($sce.trustAsResourceUrl(config.DATALET_URL+"?format="+parameter+"&nodeID="+nodeID+"&distributionID="+distribution.id+"&datasetID="+datasetID+"&url="+window.encodeURIComponent(distribution.downloadURL)));
		},function(value){
			distribution.lockFile=false;
			distribution.distributionDonwloadUrlOk = false;
			dialogs.create('idra_error_dialog.html','IdraDialogErrorCTRL',{'header':"Unable to create Datalet",
				'msg':"<p md-truncate>File with url "+distribution.downloadURL+" returned "+value.status+"!</p>"},{key: false,back: 'static'});
		});		
	};
	
	
$scope.showPreview = function(datasetID,nodeID,distribution){
		
		var parameter=undefined;
		
		var reqCheckPreview = {
				method: 'GET',
				url: config.CLIENT_SERVICES_BASE_URL+config.CHECK_DISTRIBUTION_PREVIEW+window.encodeURIComponent(distribution.downloadURL)
		};
		
		distribution.lockPreview=true;
		
		var parameter='';
		if(distribution.format!=undefined && distribution.format!=""){
			parameter=distribution.format.toLowerCase();
		}else if(distribution.mediaType!=undefined && distribution.mediaType!=""){
			if(distribution.mediaType.indexOf("/")>0)
				parameter=distribution.mediaType.split("/")[1].toLowerCase();
			else
				parameter=distribution.mediaType.toLowerCase();
		}
		
		$http(reqCheckPreview).then(function(value){
			if(parameter.includes('csv')){
				
				Papa.parse(config.CLIENT_SERVICES_BASE_URL+'/downloadFromUri?url='+window.encodeURIComponent(distribution.downloadURL)+"&format=csv",{
					download:true,
					 header: true,
					dynamicTyping: true,
					error:function(){
						delete value;
						distribution.lockPreview=false;
						distribution.distributionPreviewOk=false;
						dialogs.create('idra_error_dialog.html','IdraDialogErrorCTRL',{'header':"Unable to show Preview",
							'msg':"<p md-truncate>Error parsing the CSV file, please check the original file!</p>"},{key: false,back: 'static'});					
					},
					complete:function(res){
						var headers = res.meta.fields;
						var data = res.data;
						distribution.lockPreview=false;
						var modalInstance = $modal.open({
							animation: true,
							templateUrl: 'TablePreview.html',
							controller: 'TablePreviewCtrl',
							size: 'lg',
							resolve: {
								title: function(){
									return distribution.title;
								},
								headers: function(){
									return headers;
								},
								data: function(){
									return data;
								}
							}
						});

						modalInstance.result.then(function () {
						},function () {
						     
						      delete value;
					    });
					}})
			}else{
				
				
				var req ={
						method:'GET',
						url: config.CLIENT_SERVICES_BASE_URL+'/downloadFromUri?url='+window.encodeURIComponent(distribution.downloadURL),
					}
				
				if(parameter!='pdf'){
					req.transformResponse = function(response){
						if(parameter=='json' || parameter == 'geojson' || parameter=='fiware-ngsi' ){
							return JSON.parse(response);
						}else{
							return response;
						}
					}
				}else{
					req.responseType= 'arraybuffer';
				}
								
				$http(req).then(function(response){
					//OK
					if(parameter=='json' || parameter=='fiware-ngsi' || parameter == 'xml' 
						|| parameter == 'rdf' || parameter == 'rdf+xml' 
						 || parameter == 'txt' || parameter == 'text' || parameter == 'sparql'){
						
						distribution.lockPreview=false;
						
						var modalInstance = $modal.open({
							animation: true,
							templateUrl: 'DocumentPreview.html',
							controller: 'DocumentPreviewCtrl',
							size: 'lg',
							resolve: {
								title: function(){
									return distribution.title;
								},
								data: function(){
									return response.data;
								},
								format: function(){
									return parameter;
								}
							}
						});

						modalInstance.result.then(function () {
						},function () {
						     
						      delete response;
					    });
					}else if(parameter=='pdf' || parameter=='application/pdf'){
						
						distribution.lockPreview=false;
						
						var modalInstance = $modal.open({
							animation: true,
							templateUrl: 'PDFPreview.html',
							controller: 'PDFPreviewCtrl',
							size: 'lg',
							resolve: {
								title: function(){
									return distribution.title;
								},
								data: function(){
									return response.data;
								}
							}
						});
						
						modalInstance.result.then(function () {
						},function () {
						     
						      delete response;
					    });
					}else if(parameter=='geojson' || parameter=='kml'){
						
						var tmp=response.data;
						
						if(distribution.format.toLowerCase()=='kml'){
							data = toGeoJSON.kml((new DOMParser()).parseFromString(tmp, 'text/xml'));
						}
//						else if(distribution.format.toLowerCase()=='gpx'){
//							data = toGeoJSON.gpx((new DOMParser()).parseFromString(tmp, 'text/xml'));
//						}
						else{
							data = tmp;
						}
						
						distribution.lockPreview=false;
						
//						console.log(response.data.features.length);
						
						if(data.hasOwnProperty('features')){
							//Caso featurecollection
							if(data.features.length > 1000){
								delete response;
								delete data;
								dialogs.create('idra_error_dialog.html','IdraDialogErrorCTRL',{'header':"Unable to show Preview",
									'msg':"Too many features in the geojson!"},{key: false,back: 'static'});
								distribution.distributionPreviewOk=false;
								return;
							}
						}else if(!data.hasOwnProperty('geometry')){
							//Caso singolaFeatures
							delete response;
							delete data;
							dialogs.create('idra_error_dialog.html','IdraDialogErrorCTRL',{'header':"Unable to show Preview",
								'msg':"Please check the original file content!"},{key: false,back: 'static'});
							distribution.distributionPreviewOk=false;
							return;
						}else{
							delete response;
							delete data;
							dialogs.create('idra_error_dialog.html','IdraDialogErrorCTRL',{'header':"Unable to show Preview",
								'msg':"Please check the original file content!"},{key: false,back: 'static'});
							distribution.distributionPreviewOk=false;
							return;
						}
							
								
								
						var modalInstance = $modal.open({
							animation: true,
							templateUrl: 'GEOJSONPreview.html',
							controller: 'GEOJSONPreviewCtrl',
							size: 'lg',
							resolve: {
								title: function(){
									return distribution.title;
								},
								geojson: function(){
									return data;
								}
							}
						});

						modalInstance.result.then(function () {
						},function () {

							delete response;
							delete data;
						});
						
					}
					
				},function(error){
					delete response;
					distribution.lockPreview=false;
					distribution.distributionPreviewOk=false;
					console.log(error);
					dialogs.create('idra_error_dialog.html','IdraDialogErrorCTRL',{'header':"Unable to show Preview",
						'msg':"Please check the original file content!"},{key: false,back: 'static'});

				})
			}
			
		},function(value){
			delete response;
			distribution.lockPreview=false;
			distribution.distributionPreviewOk=false;
			var msg='';
			if(value.status==413){
				if(distribution.title!=undefined && distribution.title!=''){
					msg="File "+distribution.title+" dimension exceeds the limit for a preview!";
				}else{
					msg="File with url "+distribution.downloadURL+" dimension exceeds the limit for a preview!";
				}
			}else{
				if(distribution.title!=undefined && distribution.title!=''){
					msg="File "+distribution.title+" returned "+value.status+"!";
				}else{
					msg="File with url "+distribution.downloadURL+" returned "+value.status+"!";
				}
			}
			
			dialogs.create('idra_error_dialog.html','IdraDialogErrorCTRL',{'header':"Unable to show Preview",
				'msg':"<p md-truncate>"+msg+"</p>"},{key: false,back: 'static'});
		});		
	};
	
	$scope.showDatalets = function(datasetID,nodeID,distribution){
		$rootScope.distributionTitle = distribution.title;
		$rootScope.datasetTitle = $rootScope.datasetDetail.title;
		$rootScope.dataletDatasetID = datasetID;
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
//			dialogs.create('idra_error_dialog.html','IdraDialogErrorCTRL',{'header':"Error retrieving datalets",
//				'msg':"Error retrieving datalets"},{key: false,back: 'static'});
		});

	}
	
	
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
	
}]);
