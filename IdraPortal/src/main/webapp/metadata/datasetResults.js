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
angular.module("IdraPlatform").controller('DataSetCtrl',['$scope','$rootScope','$http','config','$anchorScroll','$location','$window','DefaultParameter',function($scope,$rootScope,$http,config,$anchorScroll,$location,$window,DefaultParameter){

	if($rootScope.foundDatasets == undefined){
		$window.location.assign('#/metadata');
		return;
	}

	$scope.returnToForm = function(){
//		window.location.assign('#metadata');
		$window.location.assign('#/'+$rootScope.previousLocation);
	}

	$rootScope.newPage='';

	$scope.oneAtATime = false;
	$scope.isOpen = false;

	
	
	/*
	 * FACETS MANAGEMENT
	 * */
	
	$scope.facetLimit=10;
	
	$scope.getDatasetByFacet = function(search_parameter,newValue,displayValue,isRemove){
		$rootScope.foundDatasets=undefined;
		$rootScope.reqDataset.url = config.CLIENT_SERVICES_BASE_URL+config.SEARCH_SERVICE;
		
		var varFilters = $rootScope.reqDataset.data.filters;
		var tagsPresent = false;
		var index=null;
		for(i=0; i<varFilters.length; i++){
			if(varFilters[i].field==search_parameter){ 
				tagsPresent = true;
				index=i;
				break;
			}
		}
		
		if(!isRemove){	

			if(!tagsPresent){
				var newFilter= {
						field:search_parameter,
						value:newValue
				}
				$rootScope.reqDataset.data.filters.push(newFilter);
			}else{
				varFilters[index].value += ','+newValue;
				$rootScope.reqDataset.data.filters = varFilters; 
			}

			$scope.selectedFacets.push({"search_parameter":search_parameter,"value":newValue,"display_value":displayValue});
		}else{

			arr=[];
			arr = varFilters[index].value.split(',');				
			arr.splice(arr.indexOf(newValue),1);
			varFilters[index].value = arr.join(',');
			
			if(varFilters[index].value==''){
				varFilters.splice(index,1);
			}
			
			$rootScope.reqDataset.data.filters = varFilters; 
		}
		
		$rootScope.selectedFacetsPrevious = $scope.selectedFacets;
		
		$rootScope.startSpin();
		$http($rootScope.reqDataset).then(function(value){
			$rootScope.foundDatasets=value.data;
			$scope.totalItems = value.data.count;
			$rootScope.datasets=value.data.results;
			displayFacets();
			displayResults();
			$rootScope.stopSpin();
//			$scope.gotoTop();
		},function(value){

		});
		
	}
	
	function arrayObjectIndexOf(arr, obj){
	    for(var i = 0; i < arr.length; i++){
	        if(angular.equals(arr[i], obj)){
	            return i;
	        }
	    };
	    return -1;
	}
	
	$scope.keywordLengthLimit = 25;
	
	function displayFacets(){
		
		$scope.facetsList = angular.copy($rootScope.foundDatasets.facets);
		
		if($rootScope.selectedFacetsPrevious !=undefined){
			$scope.selectedFacets = $rootScope.selectedFacetsPrevious;
		}else{
			$scope.selectedFacets=[];
		}
		
		for(j=0; j<$scope.facetsList.length; j++){
			
			$scope.facetsList[j].limit=$scope.facetLimit;
			
			var facetsTmp=[];
			var searchParameter = $scope.facetsList[j].search_parameter;
			
			var values = $scope.facetsList[j].values;
			for(i=0; i< values.length; i++){

				if(arrayObjectIndexOf($scope.selectedFacets,{"search_parameter":searchParameter,"display_value":values[i].keyword,"value":values[i].search_value})< 0 ){

					var tmp = (values[i].keyword.length <= $scope.keywordLengthLimit) ? values[i].facet : 
						values[i].keyword.substring(0,$scope.keywordLengthLimit).trim()+"... "+
						values[i].facet.substring(values[i].facet.lastIndexOf(" ")+1, values[i].facet.length);

					facetsTmp[i] = { 
							keyword: values[i].keyword,
							facet:tmp,
							search_value:values[i].search_value
					} 
				}
			}
			$scope.facetsList[j].values = [];
			$scope.facetsList[j].values = facetsTmp;
		}
	}

	
	displayFacets();
	
	/*
	 * END FACETS MANAGEMENT
	 * */
	
	
	
	$scope.orderParameter=DefaultParameter.getOrderBy();
	if($rootScope.originalSortParam != undefined){
		$scope.orderBy = $rootScope.originalSortParam;
	}else{
		$scope.orderBy=$scope.orderParameter[4].value;
	}
	
	$scope.orderType=DefaultParameter.getOrderType();
	if($rootScope.originalSortMode!=undefined){
		$scope.orderMode = $rootScope.originalSortMode;
	}else{
		$scope.orderMode=$scope.orderType[0].value;
	}
	
	$scope.numberOfResults= DefaultParameter.getNumberOfResults();
	if($rootScope.originalRows != undefined){
		$scope.rows = $rootScope.originalRows;
	}else{
		$scope.rows = $scope.numberOfResults[1].value;
	}
	
	$scope.itemsPerPageDistribution = 3;
	
	$scope.getRealName=function(id){
		for(i=0; i<$rootScope.nodeForResults.length; i++){
			if($rootScope.nodeForResults[i].id == id){
				return $rootScope.nodeForResults[i].name;
			}
		}
	}

	$scope.totalItems = $rootScope.foundDatasets.count;
	$scope.currentPage = 1;
	if($rootScope.currentPageGlobal!=undefined){
		$scope.currentPage=$rootScope.currentPageGlobal;
	}
	$scope.itemsPerPage = $scope.rows;
	$scope.maxSize=3;
	
	$scope.$watch('orderBy',function(newValue, oldValue){
		if(newValue != oldValue){
			$rootScope.foundDatasets=undefined;
			$rootScope.reqDataset.url = config.CLIENT_SERVICES_BASE_URL+config.SEARCH_SERVICE;
			$rootScope.reqDataset.data.sort.field=$scope.orderBy;
			$rootScope.startSpin();
			$http($rootScope.reqDataset).then(function(value){
				$rootScope.foundDatasets=value.data;
				$rootScope.datasets=value.data.results;
				displayResults();
				$rootScope.stopSpin();
			},function(value){

			});
		}
	});

	$scope.$watch('orderMode',function(newValue, oldValue){
		if(newValue != oldValue){
			$rootScope.foundDatasets=undefined;
			$rootScope.reqDataset.url = config.CLIENT_SERVICES_BASE_URL+config.SEARCH_SERVICE;
			$rootScope.reqDataset.data.sort.mode=$scope.orderMode;
			$rootScope.startSpin();
			$http($rootScope.reqDataset).then(function(value){
				$rootScope.foundDatasets=value.data;
				$rootScope.datasets=value.data.results;
				displayResults();
				$rootScope.stopSpin();
			},function(value){

			});
		}
	});
	
	$scope.$watch('rows',function(newValue, oldValue){
		if(newValue != oldValue){

			$rootScope.foundDatasets=undefined;
			$rootScope.reqDataset.url = config.CLIENT_SERVICES_BASE_URL+config.SEARCH_SERVICE;
			$rootScope.startSpin();
			$rootScope.reqDataset.data.rows = $scope.rows;
			$rootScope.reqDataset.data.start = (($scope.watchPage-1)*$scope.rows).toString();
			
			$http($rootScope.reqDataset).then(function(value){
				$rootScope.foundDatasets=value.data;
				$scope.totalItems = value.data.count;
				$rootScope.datasets=value.data.results;
				displayResults();
				$rootScope.stopSpin();
			},function(value){

			});
		}
	});
	var firstTime=true;
	$scope.$watch('currentPage', function(newPage){		
		if(newPage == 1 && $rootScope.foundDatasets!=undefined && firstTime){
			$scope.watchPage = newPage;
			$rootScope.datasets = $rootScope.foundDatasets.results;
			displayResults();
			firstTime=false;
			return;
		}
			
		$rootScope.currentPageGlobal = newPage;
		$rootScope.reqDataset.url = config.CLIENT_SERVICES_BASE_URL+config.SEARCH_SERVICE;
		$rootScope.reqDataset.data.start = ((newPage-1)*$scope.rows).toString();
		
		$rootScope.startSpin();
		$http($rootScope.reqDataset).then(function(value){
			$rootScope.foundDatasets=value.data;
			$rootScope.datasets=value.data.results;
			$scope.watchPage = newPage;
			displayResults();
			$rootScope.stopSpin();
		},function(value){

		});
//		$scope.gotoTop();
	});

	var displayResults = function(){
		$scope.visualDataset = {dataset:[]};
		var beg=0;
		var end = ($rootScope.datasets.length == $scope.rows)? $scope.rows : $rootScope.datasets.length;
		for(i=beg;i<end; i++){
			
			$scope.visualDataset.dataset[i-beg]=$rootScope.datasets[i];
			$scope.visualDataset.dataset[i-beg].isCollapsed= true;
			$scope.visualDataset.dataset[i-beg].resourcesCollapsed= true;
			$scope.visualDataset.dataset[i-beg].distributionFormats=[];
			var tmp=[];
			for(j=0; j<$scope.visualDataset.dataset[i-beg].distributions.length; j++){
				var tmpFormat="";				
				if($scope.visualDataset.dataset[i-beg].distributions[j].format!='' && $scope.visualDataset.dataset[i-beg].distributions[j].format!='null' 
					&& $scope.visualDataset.dataset[i-beg].distributions[j].format!=null && $scope.visualDataset.dataset[i-beg].distributions[j].format!=undefined){					
					tmpFormat = $scope.visualDataset.dataset[i-beg].distributions[j].format.toUpperCase();
				}else if($scope.visualDataset.dataset[i-beg].distributions[j].mediaType!='' && $scope.visualDataset.dataset[i-beg].distributions[j].mediaType!='null' 
					&& $scope.visualDataset.dataset[i-beg].distributions[j].mediaType!=null && $scope.visualDataset.dataset[i-beg].distributions[j].mediaType!=undefined){
					
					if($scope.visualDataset.dataset[i-beg].distributions[j].mediaType.indexOf("/")>0)
						tmpFormat=$scope.visualDataset.dataset[i-beg].distributions[j].mediaType.split("/")[1].toUpperCase();
					else
						tmpFormat=$scope.visualDataset.dataset[i-beg].distributions[j].mediaType.toUpperCase();
				
				}else{
					tmpFormat = 'UNKNOWN';
				}
				if(tmp.indexOf(tmpFormat)<0){
					tmp.push(tmpFormat);
					$scope.visualDataset.dataset[i-beg].distributionFormats.push({'format':tmpFormat,'count':1});
				}else{
					$scope.visualDataset.dataset[i-beg].distributionFormats[tmp.indexOf(tmpFormat)].count++;
				}
			}
			if($scope.visualDataset.dataset[i-beg].description != undefined && $scope.visualDataset.dataset[i-beg].description!=''){
				
				$scope.visualDataset.dataset[i-beg].tmpDesc='';
				var tmpStr = $scope.visualDataset.dataset[i-beg].description;
				if(tmpStr!='') $scope.visualDataset.dataset[i-beg].tmpDesc = tmpStr.replace(/\*/g,'').replace(/\\n/g,'')
																				.replace(/\(http.*\)/g,'').replace(/##\s*/g,'')
																				.replace(/<.*>(.*)<\/.*>/g,'$1')
																				.replace(/>/g,'').replace(/\[|\]/g,'');
			}
//			else{
//				$scope.visualDataset.dataset[i-beg].tmpDesc ="No description available for this dataset."
//			}
		}
		$scope.gotoTop();
	};

	$scope.modifyId = function(id){
		for(i=0 ; i<$scope.visualDataset.dataset.length; i++){
			if($scope.visualDataset.dataset[i].id === id){
				return "str"+i.toString();
			}
		}
	}
	
	$scope.openDatasetDetails = function(dataset){
		dataset.nodeName = $scope.getRealName(dataset.nodeID);
//		//console.log(JSON.stringify(dataset));
		$rootScope.datasetDetail=dataset;					
//		$window.location.assign('#/showDatasetDetail');
		//$window.location.assign('./#/dataset/'+dataset.seoIdentifier);
		$window.location.assign('./#/dataset/'+dataset.id);
	}

	$scope.gotoTop = function() {
	      // set the location.hash to the id of
	      // the element you wish to scroll to.
	      $location.hash('content');
	      // call $anchorScroll()
	      $anchorScroll();
	    };
	    
	 $scope.goToAnchor = function(anchor) {
		 if ($location.hash() !== anchor) {
		        // set the $location.hash to `newHash` and
		        // $anchorScroll will automatically scroll to it
		        $location.hash(anchor);
		      } else {
		        // call $anchorScroll() explicitly,
		        // since $location.hash hasn't changed
		        $anchorScroll();
		      }
		    };
	
}]);
