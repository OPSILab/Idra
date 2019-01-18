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
.controller('TablePreviewCtrl',function($scope,$modalInstance,headers,data){
	
	$scope.data = data;
	$scope.dataDisplayed = [].concat($scope.data);
	$scope.colSpan = $scope.dataDisplayed[0].length;
	$scope.headers=headers;

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
	
}).controller('DocumentPreviewCtrl',function($scope,$modalInstance,data,format){
	
	console.log(data);
	if(format=='json'){
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
	
}).controller('PDFPreviewCtrl',function($scope,$modalInstance,data){
	console.log(data);
	$scope.pdf = new Uint8Array(data);
	
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
}).controller('GEOJSONPreviewCtrl',function($scope,$modalInstance,leafletData,leafletBoundsHelpers,geojson){	
	//$scope.center=getLatLon(geojson.features[0].geometry.coordinates);
	$scope.center={lat:0,lng:0,zoom:10};
	
	$scope.centerJSON = function() {
		leafletData.getMap().then(function(map) {
        	leafletData.getGeoJSON().then(function(v){
        		map.fitBounds(v.getBounds());
        	})
        });  
    };

    
    
	$scope.geojson = { 
			data:geojson,
			onEachFeature: function(feature, layer) {
				var propertiesKey = Object.keys(feature.properties);
				var str="";
				for(i=0; i<propertiesKey.length; i++){
					str+="<span><strong>"+propertiesKey[i]+":</strong>"+feature.properties[propertiesKey[i]]+"</span>"
					if(i!=propertiesKey.length-1){
						str+="</br>";
					}
				}
                layer.bindPopup(str);
            }
		};
	$scope.centerJSON();

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
	
	function getLatLon(tmp){
		if(Array.isArray(tmp[0])){
			return getLatLon(tmp[0]);
		}else{
			return {lat:tmp[1],lng:tmp[0]};
		}
	}
	
});		