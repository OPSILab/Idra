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
angular.module("IdraPlatform").controller('DataletAdminCtrl',['$scope','config','$http','$rootScope','$modal','$interval','dialogs','dataletsAPI','_',function($scope,config,$http,$rootScope,$modal,$interval,dialogs,dataletsAPI,_){
	
	if(!$rootScope.dataletEnabled){
		$window.location.assign('#/metadata');
		return;
	}
	
	$scope.itemsByPage=10;
	$scope.datalets=[];
	var alreadySeenMessage = false;
	var isFirst = true;
	function getDatalets(){
		dataletsAPI.getAllDatalets().then(function(value){
			$scope.datalets = value.data;
			
			var updatedDatalets=new Map($scope.datalets.map((i) => [i.id, i]));
			
			if($scope.datalets.length==0 && !alreadySeenMessage){
				$rootScope.showAlert('warning',"No Datalets in the federation!");
				alreadySeenMessage=true;
			}else{
				
				if(isFirst){
					$scope.dataletsSafeSrc = [].concat($scope.datalets);
					$scope.displayedCollection = [].concat($scope.dataletsSafeSrc);
					isFirst=false;
				}else{
					
					safeSrcDataletID = $scope.dataletsSafeSrc.map(a=> a.id);
					currentDataletID = $scope.datalets.map(a=> a.id);
					var newDataletID = _.difference(currentDataletID,safeSrcDataletID);
					var toRemove = [];
					
					for(i=0; i<$scope.dataletsSafeSrc.length; i++){
						if(updatedDatalets.get($scope.dataletsSafeSrc[i].id)!=undefined){
							$scope.dataletsSafeSrc[i] = updatedDatalets.get($scope.dataletsSafeSrc[i].id);
						}else{
							toRemove.push(i);
						}
					};
					
					if(toRemove.length!=0){
						for(i=0; i<toRemove.length; i++)
							$scope.dataletsSafeSrc.splice(toRemove[i],1);
					}
					
					if(newDataletID.length!=0){
						for(i=0; i<newDataletID.length; i++)
							$scope.dataletsSafeSrc.push(updatedDatalets.get(newDataletID[i]));
					};
					
				}
				
			}
		}, function(value){
			console.log("ERROR");
		});
	}
	getDatalets();

	var promise = $interval(function(){
		getDatalets();
	}, 5000);
	
	// Cancel interval on page changes
	$scope.$on('$destroy', function(){
	    if (angular.isDefined(promise)) {
	        $interval.cancel(promise);
	        promise = undefined;
	    }
	});	

	
	$scope.previewDatalet = function(datalet){
		
		var modalInstance = $modal.open({
			animation: true,
			templateUrl: 'ModalDataletAdmin.html',
			controller: 'ModalDataletAdmin',
			size: 'lg',
			resolve: {
				datalet: function(){
					return datalet;
				}
			}
		});

		modalInstance.result.then(function () {
			console.log("MODAL RESULT");
		});

	};
	
	$scope.deleteDatalet = function(datalet){
		var dlg = dialogs.confirm("Delete datalet","Delete datalet "+datalet.title+"?");
		
		dlg.result.then(function(btn){	

			dataletsAPI.deleteDatalet(datalet).then(function(value){
				getDatalets();
				$rootScope.showAlert('success',"Datalet "+datalet.title+" deleted!");
			}, function(value){
				$rootScope.showAlert('danger',"Problem during the deletion of "+datalet.title+"!");
			});

		},function(btn){
			return;
		});
	};
	
}]);
