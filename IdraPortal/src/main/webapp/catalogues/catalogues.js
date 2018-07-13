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
angular.module("IdraPlatform").controller('CataloguesController',["$scope","$http",'$filter','config','$rootScope','dialogs','$interval','$timeout','$modal','FileSaver','Blob','$window','ODMSNodesAPI','_',function($scope,$http,$filter,config,$rootScope,dialogs,$interval,$timeout,$modal,FileSaver,Blob,$window,ODMSNodesAPI,_){

	$scope.updatePeriods=[{text:'1 hour',value:'3600'},{text:'1 day',value:'86400'},{text:'1 week',value:'604800'}];
	
	$rootScope.nodeToUpdate = undefined;
	/*ODMSNodesAPI.getNodes().success(function(value){
		console.log(value);
	});*/
	
	var req = {
			method: 'GET',
			url: config.ADMIN_SERVICES_BASE_URL+config.NODES_SERVICE + "?withImage=false",
			headers: {
				'Content-Type': 'application/json'
			}};


	$rootScope.allNodes=[];

	$scope.nodes=[];
	$scope.predicates=config.NODE_FIELDS.split(',');
	$scope.types=config.NODE_TYPES.split(',');
	$scope.grades=config.FEDERATION_LEVEL.split(',');
	$scope.displayedCollection=[];
	$scope.dateFormat="MMM - dd - yyyy";
	
	var isFirst = true;
	$rootScope.getNodes = function(){

		$rootScope.names=[];
		$rootScope.urls=[];
		var updatedNodes=[];
		$http(req).then(function(value){

			$scope.nodes=value.data;
			
			$rootScope.allNodes = $scope.nodes;
			 
			$rootScope.names = $scope.nodes.map(a=>a.name.toLowerCase());
			$rootScope.urls = $scope.nodes.map(a=>a.host.toLowerCase());
			updatedNodes = new Map($scope.nodes.map((i) => [i.id, i]));
			//console.log(updatedNodes);
			//console.log($scope.nodes);
//			for(i=0; i<$scope.nodes.length; i++){				
//				$rootScope.names.push($scope.nodes[i].name.toLowerCase());
//				$rootScope.urls.push($scope.nodes[i].host.toLowerCase());
//				updatedNodes[$scope.nodes[i].id]=$scope.nodes[i];
//			}
			
			if(isFirst){
				$scope.nodesSafeSrc = [].concat($scope.nodes);
				$scope.displayedCollection = [].concat($scope.nodesSafeSrc);
				isFirst=false;
			}else{
				var toRemove = [];
				safeSrcNodesID = $scope.nodesSafeSrc.map(a=> a.id);
				currentNodesID = $scope.nodes.map(a=> a.id);
				var newNodesID = _.difference(currentNodesID,safeSrcNodesID); 
				
				for(i=0; i<$scope.nodesSafeSrc.length; i++){
					if(updatedNodes.get($scope.nodesSafeSrc[i].id)!=undefined){
						$scope.nodesSafeSrc[i] = updatedNodes.get($scope.nodesSafeSrc[i].id);
					}else{
						toRemove.push(i);
					}
				};
				
				if(toRemove.length!=0){
					for(i=0; i<toRemove.length; i++)
						$scope.nodesSafeSrc.splice(toRemove[i],1);
				}
				
				if(newNodesID.length!=0){
					for(i=0; i<newNodesID.length; i++)
						$scope.nodesSafeSrc.push(updatedNodes.get(newNodesID[i]));
				};
					
			};

		}, function(){
			
		});
		
	}
	$rootScope.getNodes();
	var promise = $interval(function(){$rootScope.getNodes();}, 5000);

	// Cancel interval on page changes
	$scope.$on('$destroy', function(){
	    if (angular.isDefined(promise)) {
	        $interval.cancel(promise);
	        promise = undefined;
	    }
	});	
	
	$scope.changed = function(node){
		if(node.isActive){
			node.synchLock = 'PERIODIC';
			ODMSNodesAPI.activateNode(node.id).then(function(value){
				//$rootScope.getNodes();
			},function(value){
				console.log("error");
			})
			
		}else{
			var keepDatasets=true;
			var dlg = dialogs.create('deletecatalogue_dialog.html','confirmDialogCtrlEdit',{'header':"Deactivate catalogue "+node.name+"?",
				'msg':"Chose KEEP DATASETS in order to keep dataset in cache ",'opt1':"Delete ALL",'opt2':"KEEP Datasets"},{key: false,back: 'static'});
			dlg.result.then(function(value){
				node.synchLock = 'PERIODIC';
				if(value==1) keepDatasets=false;

				ODMSNodesAPI.deactivateNode(node.id,keepDatasets).then(function(value){
					//$rootScope.getNodes();
				},function(value){
					console.log("error");
				})
				
			},function(value){
				node.isActive=!node.isActive;
			});	
		}
	}
	
	$scope.openMessageModal=function(name,id){
		
		var req = {
				method: 'GET',
				url: config.ADMIN_SERVICES_BASE_URL+config.NODES_SERVICE+"/"+id.toString()+config.NODE_MESSAGES_SERVICE,
				headers: {
					'Content-Type': 'application/json',
					'Authorization': "Bearer " +$rootScope.token
				}};
		
		
		
		$http(req).then(function(value){
					
			var modalInstance = $modal.open({
				animation: true,
				templateUrl: 'MessagesModal.html',
				controller: 'MessageCtrl',
				size: 'md',
				resolve: {
					messages: function(){
						return value.data;
					},
					name: function(){
						return name;
					},
					nodeID: function(){
						return id;
					}
				}
			});

			modalInstance.result.then(function () {
				console.log("MODAL RESULT");
				$scope.getNodes();
			});

			
		},function(value){
			
		})
		
	}
	
	
	$scope.itemsByPage=10;

	$scope.selectedPredicate = $scope.predicates[0];

	$scope.getHours = function(millisecond){
		for(i=0; i<$scope.updatePeriods.length; i++){
			if($scope.updatePeriods[i].value == millisecond){
				return $scope.updatePeriods[i].text
			}
		}
	};

	$scope.toDate = function(value){
		var date = new Date(value);
		return date.getDate() + '/' +( date.getMonth()+1) + '/' +  date.getFullYear()+" "+date.getHour()+":"+date.getMinute();
	}

	$scope.getters={
			name: function (value) {
				return value.name;
			},
			isActive: function (value) {
				return value.isActive;
			},
			host: function (value) {
				return value.host;
			},
			type: function (value) {
				return value.nodeType;
			},
			federationLevel: function (value) {
				return value.federationLevel;
			},
			status: function (value) {
				return value.nodeState;
			},
			datasetCount: function (value) {
				return value.datasetCount;
			},
			updatePeriod: function (value) {
				return value.refreshPeriod;
			},
			lastUpdate: function (value) {
				return value.lastUpdateDate;
			}
	}

//	$scope.checkName = function(data) {
//		////console.log(data);
//		for(i=0; i<$rootScope.names.length; i++){
//			if(data==$rootScope.names[i]){
//				return "Name already exists";
//			}
//		}
//	};

	$scope.getNumber = function(str){
		return str.split('_')[1];
	};

//	$scope.checkUrl = function(data) {
//		for(i=0; i<$rootScope.urls.length; i++){
//			if(data==$rootScope.urls[i]){
//				return "Host already exists";
//			}
//		}
//
//		var reg = /^(ftp|http|https):\/\/[^ "]+$/;
//
//		if(!reg.test(data)){
//			return "Insert a valid url";
//		}
//
//	};

	$scope.deleteNode = function deleteNode(node) {

		var keepCatalogue=true;
//		var dlg = dialogs.create('deletenode_dialog.html','confirmDialogCtrlEdit',{'header':"Delete catalogue "+node.name+"?",
//			'msg':"Deleting this catalogue you will delete all of the associated resources."},{key: false,back: 'static'});
		var dlg = dialogs.confirm("Delete node "+node.name+"?","Deleting this node you will delete all of the associated resources.");
		dlg.result.then(function(value){
		
			var index = $scope.nodes.indexOf(node);
			$scope.nodes[index].synchLock = "PERIODIC";

			ODMSNodesAPI.deleteODMSNode(node.id).then(function(value){
				console.log("Success");
				$rootScope.showAlert('success',"Catalogue "+node.name+" deleted!");	 
				$rootScope.getNodes();
			}, function(value){
				$rootScope.getNodes();
				if(value.status==401){
					$rootScope.token=undefined;
				}else{
					$rootScope.showAlert('danger',value.data.userMessage);
				}
			});

		},function(){

		});

	};

	$scope.updateNode = function(node) {

		
		// Call the getODMSNode API in order to retrieve also the image
		var getODMSNodeReq = {
				method: 'GET',
				url: config.ADMIN_SERVICES_BASE_URL+config.NODES_SERVICE+"/"+node.id.toString() + "?withImage=true",
				headers: {
					'Authorization': "Bearer "+$rootScope.token
				}
		};
		
		
		$http(getODMSNodeReq).then(function(value){
			
			$rootScope.nodeToUpdate = value.data;
			$rootScope.mode = "update";
			$window.location.assign('#/catalogue');
			
		},function(){});
		
		
		
	};

	$scope.synchroNode = function(node){

		//console.log(node);

		//node.synchLock = 'FIRST';

		//var index = $scope.displayedCollection.indexOf(node);
		//console.log(index);

		//$scope.displayedCollection[index].synchLock = true;

		var req = {
				method: 'POST',
				url: config.ADMIN_SERVICES_BASE_URL+config.NODES_SERVICE+"/"+node.id.toString()+config.NODE_SYNCH_SERVICE,
				headers: {
					'Content-Type': 'application/json',
					'Authorization': "Bearer "+$rootScope.token
				},
				data:{
				}
		};

		$http(req).then(function(value){
			//console.log("Success");
			$timeout(function(){
				$rootScope.getNodes();
				//$scope.displayedCollection[index].synchLock = false;
//				$rootScope.showAlert('success',"Node "+node.name+" synchronized!");
			},1000);
			//$rootScope.stopSpin();
			//$rootScope.showAlert('success',"Node!");
		}, function(value){
			//console.log(value);
			if(value.status==401){
				$rootScope.token=undefined;
			}
			
			if(value.status!=502){
				$rootScope.getNodes();
			//$rootScope.stopSpin();
				$rootScope.showAlert('danger',value.data.userMessage);
			}
		});

		$rootScope.getNodes();
		
	};

	$scope.addNode = function() {	    	
		$rootScope.closeAlert();
		$rootScope.mode = "create";
		$window.location.assign('#/catalogue');
	};
	
	$scope.downloadZip = false;
	
	$scope.downloadDump = function(node){

		var download_url = config.ADMIN_SERVICES_BASE_URL+config.NODES_DUMP_DOWNLOAD;
		var filename = "Federated Catalogues Dump";
		if(node!=undefined){
			download_url+="/"+node.id.toString()+"?zip="+$scope.downloadZip;
			filename = node.name+" Dump";
		}else{
			download_url+="?zip="+$scope.downloadZip;
			//download_url+="?forceDump=true&zip="+$scope.downloadZip;
		}
		
		var req = {
				method: 'GET',
				url: download_url,
				headers: {
					'Content-Type': 'application/json',
					'Authorization': "Bearer "+$rootScope.token
				},
				responseType: 'blob'
		};

		$http(req).then(function(value){
						
			var data = value.data;
	        FileSaver.saveAs(data, filename+($scope.downloadZip?".zip":".rdf"));
	        
		}, function(value){
			if(value.status==401){
				$rootScope.token=undefined;
			}
			console.log("ERROR");
		});
		
	};
	
//	$scope.downloadDumpAllNodes = function(){
//		console.log(nodeID);
//	};

}]).controller('confirmDialogCtrlEdit',['$scope','$uibModalInstance','data',function($scope,$uibModalInstance,data){
	//-- Variables -----//

	$scope.header = (angular.isDefined(data.header)) ? data.header : "Delete Catalogue";
	$scope.msg = (angular.isDefined(data.msg)) ? data.msg : "Delete Current Catalogue?";
	$scope.icon = (angular.isDefined(data.fa) && angular.equals(data.fa,true)) ? 'fa fa-check' : 'glyphicon glyphicon-check';
	$scope.opt1 = (angular.isDefined(data.opt1)) ? data.opt1 : 'Delete All';
	$scope.opt2 = (angular.isDefined(data.opt2)) ? data.opt2 : 'KEEP';
	//-- Methods -----//
	
	$scope.no = function(){
		$uibModalInstance.dismiss(0);
	}; // end close
	
	$scope.yes = function(){
		$uibModalInstance.close(1);
	}; // end yes
	
	$scope.keep = function(){
		$uibModalInstance.close(2);
	};
}]);
