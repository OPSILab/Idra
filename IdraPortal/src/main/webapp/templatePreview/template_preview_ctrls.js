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

angular.module("IdraPlatform")
.controller('TablePreviewCtrl',function($scope,$modalInstance,title,headers,data){
	
	$scope.title = title;
	
	$scope.data = data;
	$scope.dataDisplayed = [].concat($scope.data);
	$scope.headers=headers;
//	$scope.colSpan = ($scope.dataDisplayed==undefined || $scope.dataDisplayed==null || $scope.dataDisplayed[0]==undefined)?0:$scope.dataDisplayed[0].length;
	$scope.colSpan = $scope.headers.length;
	

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
	
}).controller('DocumentPreviewCtrl',function($scope,$modalInstance,title,data,format){
	
	$scope.title = title;
	
	if(format=='json' || format=='fiware-ngsi'){
		$scope.previewDocument =JSON.stringify(data,null,4);
	}else
		$scope.previewDocument = data.toString();
	
	var getMode = function(type){
		if(format.toLowerCase() == 'xml' 
			|| format.toLowerCase() == 'rdf' || format.toLowerCase() == 'rdf+xml' ){
			return 'xml';
		}else if(format.toLowerCase() == 'txt' || format.toLowerCase() == 'text'){
			return 'text';
		}
		return 'json';
	} 
	
	$scope.aceLoaded = function(_editor) {
		$scope.aceEditor=_editor;
		$scope.aceSession = _editor.getSession();
		$scope.aceSession.setMode("ace/mode/" + getMode(format));
		$scope.aceEditor.setSession($scope.aceSession);
		$scope.aceEditor.setReadOnly(true);
	};
	
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
	
}).controller('PDFPreviewCtrl',function($scope,$modalInstance,title,data){
	$scope.title = title;
	$scope.pdf = new Uint8Array(data);
	
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
}).controller('GEOJSONPreviewCtrl',function($scope,$modalInstance,leafletData,leafletBoundsHelpers,title,geojson,$timeout){	
	$scope.title = title;
	$scope.center={lat:0,lng:0,zoom:0};
	
	$scope.defaults= {
        scrollWheelZoom: false,
        maxZoom:10,
        reset:true
    }
	
	var centerJSON = function() {
		leafletData.getMap().then(function(map) {
        	leafletData.getGeoJSON().then(function(v){
        		map.fitBounds(v.getBounds());
        		map.invalidateSize();
                map._resetView(map.getCenter(), map.getZoom(), true);  
        	})
        });  
    };

	$scope.geojson = { 
			data:geojson,
//			filter:function(feature){
//				console.log(++qwe);
//				if(feature.hasOwnProperty('geometry')){
//					var g = feature.geometry;
//					if(!g.hasOwnProperty('coordinates')){
//						return false;
//					}else{
//						return checkCoordinates(g.coordinates);	
//					}
//				}else{
//					return false;
//				}
//			},
			onEachFeature: function(feature, layer) {
				var propertiesKey = Object.keys(feature.properties);
				var str="";
				for(i=0; i<propertiesKey.length; i++){
					str+="<span><strong>"+propertiesKey[i]+":</strong>"+feature.properties[propertiesKey[i]]+"</span>"
					if(i!=propertiesKey.length-1){
						str+="</br>";
					}
				}
				if(propertiesKey.length==0){
					str+="<span>No info provided</span>"
				}
                layer.bindPopup(str);
            }
		};
	
	$timeout(function(){ 
		centerJSON();
		},100);

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
	
	function checkCoordinates(tmp){
			
		if(Array.isArray(tmp)){
			
			if(tmp.length==0) return false;
			
			tmp.forEach(f=>{
				if(Array.isArray(f)){
					return checkCoordinates(f);
				}else{
					try{
						var x = parseFloat(f);
//						return !isNaN(x);
						if(isNaN(x)){
							return false;
						}else{
							return true;
						}
					}catch(e){
						console.log(e);
						return false;
					}
				}
			})
		}else{
			return false;
		}
	}
	
	function getLatLon(tmp){
		if(Array.isArray(tmp[0])){
			return getLatLon(tmp[0]);
		}else{
			return {lat:tmp[1],lng:tmp[0]};
		}
	}
	
}).controller('IdraDialogErrorCTRL',['$scope','$uibModalInstance','data',function($scope,$uibModalInstance,data){
	//-- Variables -----//

	$scope.header = (angular.isDefined(data.header)) ? data.header : "Unable to Show Preview";
	$scope.msg = (angular.isDefined(data.msg)) ? data.msg : "";
	$scope.icon = (angular.isDefined(data.fa) && angular.equals(data.fa,true)) ? 'fa fa-check' : 'glyphicon glyphicon-warning-sign';
	//-- Methods -----//
	
	$scope.close = function(){
		$uibModalInstance.dismiss(0);
	}; // end close
}])
.directive('pageSelect', function() {
    return {
      restrict: 'E',
      template: '<input type="text" class="select-page" ng-model="inputPage" ng-change="selectPage(inputPage)">',
      link: function(scope, element, attrs) {
        scope.$watch('currentPage', function(c) {
          scope.inputPage = c;
        });
      }
    }
  });;		