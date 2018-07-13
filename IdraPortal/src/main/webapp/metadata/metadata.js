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
angular.module("IdraPlatform").controller('MetadataCtrl',['$scope','$http','config','$rootScope','dialogs','$modal','$window','ODMSNodesAPI','DefaultParameter','searchService','$location','$anchorScroll',function($scope,$http,config,$rootScope,dialogs,$modal,$window,ODMSNodesAPI,DefaultParameter,searchService,$location,$anchorScroll){ 
	
	$rootScope.previousLocation='metadata';
	
	$scope.flag=false;

	$scope.numberOfCatalogues="";
	if($rootScope.numberOfDatasets==undefined)
		$rootScope.numberOfDatasets="";
	
	var parameters=DefaultParameter.getAllParameters();
	
	$scope.dcat=parameters.dcatFields;
	var allFilters=parameters.allFilters;
	$scope.orderType=parameters.orderType;
	$scope.orderMode=$scope.orderType[0].value;
	$scope.orderParameter = parameters.orderBy;
	$scope.orderBy=$scope.orderParameter[4].value;
	$scope.euroVocEnabled = false;
	$scope.sourceLanguages = parameters.eurovocLanguages;
	$scope.targetLanguages = parameters.eurovocLanguages;
	$scope.selSourceLanguage = "";
	$scope.selTargetLanguages = [];
	$scope.searchOn = parameters.searchOn;
	$scope.live=$scope.searchOn[0].value;
	$scope.optionItems=parameters.optionItems;
	$scope.numberOfResults = parameters.numberOfResults;
	$scope.rows = $scope.numberOfResults[2].value;

	$scope.dcatThemes = parameters.dcatThemes;
	
	$scope.messageError = "This filter cannot be empty!";
	
	$rootScope.selectedFacetsPrevious=undefined;
	$rootScope.currentPageGlobal = undefined;
	
	$scope.advancedSearch=false;
	$scope.showButton = true;
	$scope.allSelectedOption=[];
	$scope.maxNumberOfFilters = allFilters.length; 	
	
	$scope.$watch('advancedSearch', function(newValue, oldValue){
		if (oldValue && !newValue) {
			$rootScope.previousContext.advancedSearch = newValue;
			$rootScope.$broadcast('nodeIdCreated');
		}
	});
	
	if($rootScope.previousContext!=undefined){
		
		$scope.advancedSearch = $rootScope.previousContext.advancedSearch;
		$scope.items = $rootScope.previousContext.items;
		$scope.selSourceLanguage = $rootScope.previousContext.selSourceLanguage;
		$scope.selTargetLanguages = $rootScope.previousContext.selTargetLanguages;
		$scope.rows = $rootScope.previousContext.rows;
		$scope.live = $rootScope.previousContext.live;
		$rootScope.issuedStartDate = $rootScope.previousContext.issuedStartDate;
		$rootScope.issuedEndDate = $rootScope.previousContext.issuedEndDate;
		$rootScope.modifiedStartDate = $rootScope.previousContext.modifiedStartDate;
		$rootScope.modifiedEndDate = $rootScope.previousContext.modifiedEndDate;
		$scope.euroVocEnabled = $rootScope.previousContext.euroVoc;
		$scope.orderMode = $rootScope.previousContext.orderMode;
		$scope.orderBy = $rootScope.previousContext.orderBy;
		$scope.showButton = $rootScope.previousContext.showButton;
		$scope.allSelectedOption=$rootScope.previousContext.allSelectedOption;
		
	}else{
		$scope.items = [{
			options: $scope.optionItems,
			selectedOption:$scope.optionItems[0].value,
			text: "",
			tags:[],
			last:true,
			invalidField:false
		}];
	}	
	
	/* ***************
	 * ADD FILTER
	 * ***************/
	
	$scope.addFilter = function () {

		$scope.allSelectedOption.push($scope.items[$scope.items.length-1].selectedOption);

		$scope.items[$scope.items.length-1].last = false;  

		$scope.temp=[];
		for(i=0; i<$scope.optionItems.length; i++){
			if( $scope.items[$scope.items.length-1].selectedOption != $scope.optionItems[i].value ){
				$scope.temp.push($scope.optionItems[i]);
			}
		}

		$scope.optionItems = $scope.temp;

		var itemTmp = {
				options: $scope.optionItems,
				selectedOption: $scope.optionItems[0].value,
				text: "",
				last:true,
				tags:[],
				invalidField:false
		};

		$scope.items.push(itemTmp);

		if($scope.items.length == $scope.maxNumberOfFilters){
			$scope.showButton = false;
		}	

	};
	
	/*END ADD FILTER*/


	/* ***************
	 * REMOVE FILTER
	 * ***************/
	
	$scope.removeFilter = function(id){

		var arrLength = $scope.items.length-1;

		var itemToRemove={selectedOption : ""};

		if(id == arrLength){
			$scope.items.splice(id,1);
			$scope.items[arrLength-1].last=true;
			var index = $scope.allSelectedOption.indexOf($scope.items[arrLength-1].selectedOption);
			$scope.allSelectedOption.splice(index,1);

			itemToRemove.selectedOption = $scope.items[arrLength-1].selectedOption;

		}else{

			itemToRemove = $scope.items[id];

			$scope.items.splice(id,1);
			var index = $scope.allSelectedOption.indexOf(itemToRemove.selectedOption);
			$scope.allSelectedOption.splice(index,1);

		}

		var removed_option="";
		for(i=0; i<allFilters.length; i++){
			if(allFilters[i].value ==  itemToRemove.selectedOption ){
				removed_option = allFilters[i];
				break;
			}
		}

		$scope.optionItems.push(removed_option);
		$scope.optionItems.sort(function(a, b) {
			return (a.text > b.text) - (a.text < b.text);
		});

		$scope.items[arrLength-1].options = $scope.optionItems;
		$scope.showButton = true;
	};
	
	/*END REMOVE FILTER*/

	
	
	$scope.tagAdded = function($tag,item){
		if(item.tags.length==1){
			item.text += $tag.text;
		}else{
			item.text += ","+$tag.text;
		}
	};

	$scope.tagRemoved = function($tag,item){
		var array = item.text.split(",");
		array.splice(array.indexOf($tag.text),1);
		item.text = array.join(",");
	};

	/* ***************
	 * BUILD SEARCH QUERY
	 * ***************/ 
	
	$scope.executeQuery = function(){

		var err=false;

		var filters=[];
		var filter;
		var issued = undefined;
		var modified = undefined;
		
		
	/* *********************
	 * Build Search Request as excepted by Federation Manager
	 *******************/ 
		
		// Handle filters 
		
		for(i=0; i<$scope.items.length; i++){


			if($scope.items[i].selectedOption != "ALL"){

				if($scope.items[i].text==""){

					$scope.items[i].invalidField=true;
					err=true;

				} else {

					$scope.items[i].invalidField=false;
					filter={
							'field':$scope.items[i].selectedOption,
							'value':$scope.items[i].text
					};
					
					if($scope.items[i].selectedOption == "tags"){
						$rootScope.originalTagsFilter = filter;
					}

				}
				
			} else {

				filter={
						'field':$scope.items[i].selectedOption,
						'value':$scope.items[i].text
				};
			}


			filters.push(filter);
		}

		if(err) return;

		// Build issued and modified strings
		
		if($rootScope.issuedStartDate!=null && $rootScope.issuedEndDate!=null){
			
			var start = $rootScope.issuedStartDate.getFullYear() 
							+ '-' + ("0"+($rootScope.issuedStartDate.getMonth() + 1)).slice(-2) 
							+  '-' + ("0"+$rootScope.issuedStartDate.getDate()).slice(-2) 
							+ 'T00:00:00Z';
			
			var end = $rootScope.issuedEndDate.getFullYear() 
							+ '-' + ("0"+($rootScope.issuedEndDate.getMonth() + 1)).slice(-2) 
							+  '-' + ("0"+$rootScope.issuedEndDate.getDate()).slice(-2) 
							+ 'T00:00:00Z';
			
			issued = {
				'start': start,
				'end': end
			}
		}

		if($rootScope.modifiedStartDate!=null && $rootScope.modifiedEndDate!=null){
			var start = $rootScope.modifiedStartDate.getFullYear() 
							+ '-' + ("0"+($rootScope.modifiedStartDate.getMonth() + 1)).slice(-2) 
							+  '-' + ("0"+$rootScope.modifiedStartDate.getDate()).slice(-2) 
							+ 'T00:00:00Z';
			var end = $rootScope.modifiedEndDate.getFullYear() 
							+ '-' + ("0"+($rootScope.modifiedEndDate.getMonth() + 1)).slice(-2) 
							+  '-' + ("0"+$rootScope.modifiedEndDate.getDate()).slice(-2) + 'T00:00:00Z';
			modified = {
				'start': start,
				'end': end
			}
		}

		
		if($scope.euroVocEnabled){
			console.log($scope.selSourceLanguage);
			if($scope.selSourceLanguage=='' || $scope.selSourceLanguage=='None'){
				dialogs.notify("Warning" , "Please select a source language");
				return;
			}
			if($scope.selTargetLanguages.length==0){
				dialogs.notify("Warning" , "Please select at least one target language");
				return;
			}
		}
		
		
		// Handle selected nodes according to selected search type

		var discardedNodes=[];
		var idTmp=[];
		var warningMsg="";
		
		if($scope.live){
			
			for(i=0; i<$scope.selectedNodeID.length; i++){
				if($scope.federatedLevels[$scope.selectedNodeID[i]]=="LEVEL_2"){
					discardedNodes.push($scope.selectedNodeName[i]);
					idTmp.push($scope.selectedNodeID[i]);
				}
			}
			if(discardedNodes.length!=0){
				warningMsg="Live search not allowed for node:  " + discardedNodes.join(", ");
//				dialogs.notify("Warning","Live search not allowed for node:  "+arrTmp.join(", ")+" ");
			}
			
		}else{
			for(i=0; i<$scope.selectedNodeID.length; i++){
				if($scope.federatedLevels[$scope.selectedNodeID[i]]=="LEVEL_1"){
					discardedNodes.push($scope.selectedNodeName[i]);
					idTmp.push($scope.selectedNodeID[i]);
				}
			}
			if(discardedNodes.length!=0){
				warningMsg="Cache search not allowed for node:  " + discardedNodes.join(", ");
//				dialogs.notify("Warning","Cache search not allowed for node:  "+arrTmp.join(", "));
			}
		}

		if(warningMsg != ""){
			var dlg = dialogs.notify("Warning" , warningMsg);
			dlg.result.then(function(btn){	
				////console.log($scope.selectedNodeID);
				for(i=0; i<idTmp.length; i++){
					$scope.selectedNodeID.splice($scope.selectedNodeID.indexOf(idTmp[i]),1);
				}
				////console.log($scope.selectedNodeID);
				$scope.executeQueryRequest(filters);
			},function(btn){ 
				return;
			});
		}else{
			
//			$scope.getFacets(filters, issued, modified);
			$scope.executeQueryRequest(filters, issued, modified);
		}


	};

	
	/* ***************
	 * EXECUTE SEARCH QUERY
	 * ***************/ 	
	$scope.hitEnter = function(evt){
	    if(angular.equals(evt.keyCode,13))
	    	$scope.executeQuery();
	  }; // end hitEnter
	
	$scope.executeQueryRequest = function(filters, issued, modified){
		$rootScope.startSpin();

		$rootScope.reqDataset = {
				method: 'POST',
				url: config.CLIENT_SERVICES_BASE_URL+config.SEARCH_SERVICE,
				headers: {
					'Content-Type': 'application/json'
				},
				data:{
					'filters':filters,
					'releaseDate': issued,
					'updateDate': modified,
					'live':$scope.live,
					'sort':{'field':$scope.orderBy,'mode':$scope.orderMode},
					'rows': $scope.rows, 
					'start': '0',
					'nodes':$scope.selectedNodeID,
					'euroVocFilter': {
						"euroVoc": $scope.euroVocEnabled,
						"sourceLanguage": $scope.selSourceLanguage,
						"targetLanguages": $scope.selTargetLanguages
					}
				}};

		$rootScope.previousContext = {
				
				advancedSearch: $scope.advancedSearch,
				items: $scope.items,
				modifiedStartDate: $rootScope.modifiedStartDate,
				modifiedEndDate: $rootScope.modifiedEndDate,
				issuedStartDate: $rootScope.issuedStartDate,
				issuedEndDate: $rootScope.issuedEndDate,
				orderBy: $scope.orderBy,
				orderMode: $scope.orderMode,
				euroVoc: $scope.euroVocEnabled,
				selSourceLanguage: $scope.selSourceLanguage,
				selTargetLanguages: $scope.selTargetLanguages,
				live: $scope.live,
				rows: $scope.rows,
				showButton:$scope.showButton,
				selectedNodeID:$scope.selectedNodeID,
				allSelectedOption: $scope.allSelectedOption
				
		}
		
		$rootScope.datasets=[];
		$http($rootScope.reqDataset).then(function(value){

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
					$rootScope.selectedFacetsPrevious=[];
					$rootScope.showAlert('warning',"No result found!");
				}
			}else{
				$rootScope.selectedFacetsPrevious=[];
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

	}

	/* ***************
	 * END Execute query
	 * ****************/

	/* ***************
	 * Reset Form
	 * *****************/
	$scope.resetForm = function(){
		
		$rootScope.previousContext=undefined;
		
		$scope.optionItems = allFilters;

		$scope.orderBy=$scope.orderParameter[4].value;
		$scope.orderMode=$scope.orderType[0].value;
		$scope.rows = $scope.numberOfResults[2].value;
		$rootScope.resetIssued();
		$rootScope.resetModified();
		$scope.live=false;
		$scope.euroVocEnabled = false;
		$scope.selSourceLanguage = "";
		$scope.selTargetLanguages = [];
		
		for(i=0; i<$scope.nodes.length; i++){
			if($scope.selectedNodeID.indexOf($scope.nodes[i].id)<0){
				$scope.selectedNodeID.push($scope.nodes[i].id);
				$scope.selectedNodeName.push($scope.nodes[i].name);
			}
		}
		
		enableLiveSearch();
		
		$scope.items = [{
			options: $scope.optionItems,
			selectedOption:$scope.optionItems[0].value,
			text: "",
			last:true,
			tags:[],
			invalidField:false
		}];

		$scope.showButton = true;
		$scope.allSelectedOption=[];
	};
	
	/*End Reset Form*/

	

	/* **********************************
	 * HANDLE NODES SELECTION
	 **********************************/
	var previouslySelected = undefined;
	if($rootScope.previousContext!=undefined){
		previouslySelected = $rootScope.previousContext.selectedNodeID;
	}
	
	var req = {
			method: 'GET',
			url: config.ADMIN_SERVICES_BASE_URL+config.NODES_SERVICE,
			headers: {
				'Content-Type': 'application/json'
			}
	};

	$scope.nodes=[];
	$scope.selectedNodeID=[];
	$scope.selectedNodeName=[];
	$scope.federatedLevels=[];
	var allNodesIDs=[];

	$rootScope.startSpin();
	ODMSNodesAPI.getODMSNodesAPI(false).then(function(value){

		manageNodes(value.data);
		$rootScope.stopSpin();

	}, function(value){

		$rootScope.stopSpin();
		$rootScope.showAlert('danger',value.data.userMessage);

	});	
	
	$scope.liveSearchEnabled=false;
	
	function enableLiveSearch(){
		var tmpLive=true;
		for(i=0; i<$scope.selectedNodeID.length;i++){
			if($scope.federatedLevels[$scope.selectedNodeID[i]] == 'LEVEL_0' || $scope.federatedLevels[$scope.selectedNodeID[i]] == 'LEVEL_2'){
				tmpLive=false;
				$scope.live=$scope.searchOn[0].value;
				break;
			}
		}
		$scope.liveSearchEnabled=tmpLive;
	};
	
	
	function manageNodes(nodes){
		$scope.numberOfCatalogues=nodes.length;
		for(i=0; i<nodes.length; i++){
			if(nodes[i].synchLock != 'FIRST' && nodes[i].federationLevel!="LEVEL_0" && nodes[i].isActive!=false){
				$scope.nodes.push({id: nodes[i].id, name: nodes[i].name, federationLevel:nodes[i].federationLevel});
				$scope.federatedLevels[nodes[i].id]= nodes[i].federationLevel;
				allNodesIDs.push(nodes[i].id);
				if(previouslySelected!=undefined){
					if(previouslySelected.indexOf(nodes[i].id)>=0){
						$scope.selectedNodeID.push(nodes[i].id);
						$scope.selectedNodeName.push(nodes[i].name);
					}
				}else{
					$scope.selectedNodeID.push(nodes[i].id);
					$scope.selectedNodeName.push(nodes[i].name);
				}
			}
		}

		searchService.storeNodeIDs(allNodesIDs);
		
		if($scope.nodes.length==0){
			$rootScope.showAlert('warning',"No Catalogues in the federation!");
		}else{
			enableLiveSearch();
		}
		
		$rootScope.nodeForResults = angular.copy($scope.nodes);

		modifyNameString($scope.flag);
	}
	
	function modifyNameString(flag){
		$scope.stringSelectedNames = "";
		if($scope.selectedNodeName.length > 3 && !flag){
			for(i=0; i<3; i++ ){
				$scope.stringSelectedNames +=$scope.selectedNodeName[i];
				if(i!=2) $scope.stringSelectedNames +=", ";
				else $scope.stringSelectedNames +="...";
			}
		}else{
			$scope.stringSelectedNames=$scope.selectedNodeName.join(', ');
		}

	}

	$scope.showAllNodeNames = function(){
		if($scope.flag){
			$scope.flag=false;
		}else{
			$scope.flag=true;
		}

		modifyNameString($scope.flag);
	}

	$scope.open = function () {

		var modalInstance = $modal.open({
			animation: true,
			templateUrl: 'ModalContentSingle.html',
			controller: 'ModalInstanceCtrlSingle',
			size: 'md',
			resolve: {
				type: function(){
					return "search";
				},
				items: function () {
					var names =[];
					for(i=0; i<$scope.nodes.length; i++){
						names.push($scope.nodes[i].name);
					}
					return names;
				},
				federationLevel: function(){
					var level = {};
					for(i=0; i<$scope.nodes.length; i++){
						level[$scope.nodes[i].name] = $scope.nodes[i].federationLevel;
					}
					return level;
				},
				selected: function(){
					$scope.selectedNodeName =[];
					for(i=0; i<$scope.nodes.length; i++){
						if($scope.selectedNodeID.indexOf($scope.nodes[i].id) >= 0 ){
							$scope.selectedNodeName.push($scope.nodes[i].name);
						}
					}
					return $scope.selectedNodeName;
				},
				title: function(){
					return "Catalogues";
				},
				message: function(){
					return "Please select at least one Catalogue";
				}
			}
		});

		modalInstance.result.then(function (selectedItem) {

			$scope.selectedNodeID = [];
			$scope.selectedNodeName=[];
			for(i = 0; i<$scope.nodes.length; i++){
				if(selectedItem.indexOf($scope.nodes[i].name) >= 0 ){
					$scope.selectedNodeID.push($scope.nodes[i].id);
					$scope.selectedNodeName.push($scope.nodes[i].name);
				}
			}
			
			enableLiveSearch();
			
			$scope.flag=false;
			modifyNameString($scope.flag);
		});

	};
	
	
	$rootScope.defaultFacetsSearchRequestData = {
			
			"filters":[{"field":"tags","value":""},{"field":"ALL","value":""}],
					"live":false,
					"sort":{"field":"title","mode":"asc"},
					"rows":"25","start":"0",
					"nodes":allNodesIDs,
					"euroVocFilter":{"euroVoc":false,"sourceLanguage":"","targetLanguages":[]}
					
	}; 
	
	$rootScope.selectedFacetsPrevious=[];
	
	$rootScope.cloudWordClicked = function(word){
		var filters = [{"field":"tags","value":word},{"field":"ALL","value":""}];
		$rootScope.selectedFacetsPrevious.push({"search_parameter":"tags","value":word,"display_value":word})
		$scope.executeQueryRequest(filters);
	}
	
	$scope.searchByCategory = function(theme){
		var filters = [{"field":"datasetThemes","value":theme.value.toUpperCase()},{"field":"ALL","value":""}];
		$rootScope.selectedFacetsPrevious.push({"search_parameter":"datasetThemes","value":theme.value.toUpperCase(),"display_value":theme.text})
		$scope.executeQueryRequest(filters);
	}
	
	$scope.isCategories=false;
	
	$scope.scrollTo = function(anchor) {
		 if ($location.hash() !== anchor) {
		        // set the $location.hash to `newHash` and
		        // $anchorScroll will automatically scroll to it
			 	if(anchor=="top"){
		        	$scope.isCategories=false;
		        }else{
		        	$scope.isCategories=true;
		        }
			 	
		        $location.hash(anchor);
		      } else {
		        // call $anchorScroll() explicitly,
		        // since $location.hash hasn't changed
		        $anchorScroll();
		      }
		    };
	
    $scope.getCssClass = function(theme){
    	var arr=['intr','just','regi','soci','tech','tran'];
    	if(arr.indexOf(theme.value)>=0){
    		
    		return "customTile";
    	}
    }
		    
	/*
	 * LAST UPDATED DATASET SECTION
	 * */
	
//	$scope.lastUpdatedDataset=[];
//	
//	function getLastUpdatedDatasets(){
//		var reqLastUpdatedDataset = {
//				method: 'POST',
//				url: config.CLIENT_SERVICES_BASE_URL+config.SEARCH_SERVICE,
//				headers: {
//					'Content-Type': 'application/json'
//				},
//				data:{
//					'filters':[{"field":"ALL","value":""}],
//					'live':false,
//					'sort':{'field':'updateDate','mode':'desc'},
//					'rows': '10', 
//					'start': '0',
//					'nodes':allNodesIDs,
//					'euroVocFilter': {
//						"euroVoc": false,
//						"sourceLanguage": '',
//						"targetLanguages": []
//					}
//				}};
//		
//		$http(reqLastUpdatedDataset).then(function(value){
//			console.log(value.data);
//			$scope.lastUpdatedDataset=value.data.results;
//			displayLastResults();
//		}, function(value){
//		});
//
//	};
//	
//	var displayLastResults = function(){
//		$scope.visualLastChangedDataset = {dataset:[]};
//		for(i=0;i<$scope.lastUpdatedDataset.length; i++){
//			$scope.visualLastChangedDataset.dataset[i]=$scope.lastUpdatedDataset[i];
//			$scope.visualLastChangedDataset.dataset[i].isCollapsed= true;
//			$scope.visualLastChangedDataset.dataset[i].resourcesCollapsed= true;
//			$scope.visualLastChangedDataset.dataset[i].distributionFormats=[];
//			var tmp=[];
//			for(j=0; j<$scope.visualLastChangedDataset.dataset[i].distributions.length; j++){
//				var tmpFormat="";				
//				if($scope.visualLastChangedDataset.dataset[i].distributions[j].format!='' && $scope.visualLastChangedDataset.dataset[i].distributions[j].format!='null' 
//					&& $scope.visualLastChangedDataset.dataset[i].distributions[j].format!=null && $scope.visualLastChangedDataset.dataset[i].distributions[j].format!=undefined){					
//					tmpFormat = $scope.visualLastChangedDataset.dataset[i].distributions[j].format.toUpperCase();
//				}else{
//					tmpFormat = 'UNKNOWN';
//				}
//				if(tmp.indexOf(tmpFormat)<0){
//					tmp.push(tmpFormat);
//					$scope.visualLastChangedDataset.dataset[i].distributionFormats.push({'format':tmpFormat,'count':1});
//				}else{
//					$scope.visualLastChangedDataset.dataset[i].distributionFormats[tmp.indexOf(tmpFormat)].count++;
//				}
//			}
//			if($scope.visualLastChangedDataset.dataset[i].description != undefined && $scope.visualLastChangedDataset.dataset[i].description!=''){
//				
//				$scope.visualLastChangedDataset.dataset[i].tmpDesc='';
//				var tmpStr = $scope.visualLastChangedDataset.dataset[i].description;
//				if(tmpStr!='') $scope.visualLastChangedDataset.dataset[i].tmpDesc = tmpStr.replace(/\*/g,'').replace(/\\n/g,'')
//																				.replace(/\(http.*\)/g,'').replace(/##\s*/g,'').
//																				replace(/>/g,'').replace(/\[|\]/g,'');
//			}
//		}
//	};
//	
//	$scope.openDatasetDetails = function(dataset){
//		dataset.nodeName = $scope.getRealName(dataset.nodeID);
////		//console.log(JSON.stringify(dataset));
//		$rootScope.datasetDetail=dataset;					
//		$window.location.assign('#/showDatasetDetail');
//	}
//	
//	$scope.getRealName=function(id){
//		for(i=0; i<$rootScope.nodeForResults.length; i++){
//			if($rootScope.nodeForResults[i].id == id){
//				return $rootScope.nodeForResults[i].name;
//			}
//		}
//	}
	
}]);
