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
angular.module("IdraPlatform").controller('DataletClientCtrl',['$scope','$rootScope','config','$sce','$http','$window','dataletsAPI',function($scope,$rootScope,config,$sce,$http,$window,dataletsAPI){

	if(!$rootScope.dataletEnabled){
		$window.location.assign('#/metadata');
		return;
	}

	var currentDatalets = dataletsAPI.getCurrentDatalets();
	if(currentDatalets.length==0){
		$window.location.assign('#/metadata');
		return;
	}
	
	$scope.datalets = currentDatalets;
	$scope.selectedDatalet='';
	var req = {
				method: 'GET',
				url: config.ADMIN_SERVICES_BASE_URL+config.TOKEN_VALIDATION,
				headers: {
					'Content-Type': 'application/json',
					'Authorization': "Bearer " +$rootScope.token	
				}
		};

		$http(req).then(function(value){
			//console.log(value);
			$scope.isAdmin = true;
		}, function(value){
			$scope.isAdmin = false;
		});
		
	for(i=0; i<$scope.datalets.length; i++){
		$scope.datalets[i].showHtml=$sce.trustAsHtml($scope.datalets[i].datalet_html);
	}
	
	$scope.selectedDatalet=$scope.datalets[0];
	$scope.alreadyUpdatedID=[];
	updateDataletViews($scope.selectedDatalet);
	
	$scope.showSelectedDatalet = function(datalet){
		$scope.selectedDatalet = datalet;
		updateDataletViews(datalet);		
	};
	
	
	
	function updateDataletViews(datalet){
		if($scope.alreadyUpdatedID.indexOf(datalet.id)>=0){
			return;
		}
		
		$scope.alreadyUpdatedID.push(datalet.id);
		
		if(!$scope.isAdmin){
			dataletsAPI.updateDataletsViews(datalet).then(function(value){
				console.log("OK");
			}, function(value){
				console.log("ERROR");
			});
		}
	};
	
	$scope.deleteDatalet = function(datalet){
		if($scope.isAdmin){
			
			var dlg = dialogs.confirm("Delete datalet","Delete datalet "+datalet.title+"?");
			
			dlg.result.then(function(btn){	

				dataletsAPI.deleteDatalet(datalet).then(function(value){
					console.log("Deleted");
				}, function(value){
					console.log("error while deleting datalet");
				});

			},function(btn){
				return;
			});
		}
	}
	
}]);	
