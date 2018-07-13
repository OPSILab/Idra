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
/*SPARQL*/

angular.module("IdraPlatform").controller('SparqlCtrl',['$scope','$http','config','$rootScope','$window',function($scope,$http,config,$rootScope,$window){

	if($rootScope.executedQuery == null || $rootScope.executedQuery ==undefined ){
		$scope.aceDocumentValue = "PREFIX dc:<http://purl.org/dc/elements/1.1/>\nSELECT ?object\nWHERE {\n?subject ?predicate ?object\n}\nLIMIT 50";
	}else{
		$scope.aceDocumentValue = $rootScope.executedQuery;
	}
//	$scope.aceDocumentValue = "SELECT *\n"+
//	"WHERE {\n"+
//	"?subject rdf:type ?object\n"+
//	"}";

	$scope.outputType=['XML','JSON'];
	$scope.outputMode=$scope.outputType[0];
	
	$scope.prefixes = {};
	$scope.disabledButton = false;
	$scope.getAllPrefix = function(){
		
		var req = {
				method: 'GET',
				url: config.ADMIN_SERVICES_BASE_URL+config.PREFIXES_SERVICE,
				headers: {
					'Content-Type': 'application/json'
				},
				
		};
		$rootScope.startSpin();
		$http(req).then(function(value){
			
			if(value.data.length==0){
				$scope.disabledButton = true;
				$rootScope.showAlert('warning',"No RDF in the federation!");
			}
			
			for(i=0; i<value.data.length; i++){
				$scope.prefixes[value.data[i].prefix] = value.data[i].namespace;
			}	
			console.log($scope.prefixes);
			$rootScope.stopSpin();
			
		}, function(){
			$rootScope.stopSpin();
			return null;
		});
	}
	
	$scope.getAllPrefix();
	
	$scope.aceLoaded = function(_editor) {
		$scope.aceEditor=_editor;
		$scope.aceSession = _editor.getSession();
		$scope.aceSession.getDocument().setValue($scope.aceDocumentValue);			
	};

	$scope.aceChanged = function (e) {
		var pos = angular.copy($scope.aceEditor.getCursorPosition());
//		console.log(pos);
		$scope.aceDocumentValue = $scope.aceSession.getDocument().getValue();
		$scope.prefixManagement(pos);
	};

	$scope.insertedPref = [];

	$scope.prefixManagement = function(pos){
		var tmp = $scope.aceDocumentValue;	

//		var reg = new RegExp("prefix +([a-z.0-9]*)",'mgi');
//
//		var tmp1 = tmp.match(reg);
//	
////		console.log($scope.prefixes.keys({}).length);
//		
//		if(tmp1 != null && !isEmpty($scope.prefixes)){
//			for(i=0; i<tmp1.length; i++){
//				var xstr = tmp1[i].split(' ')[1];
//				console.log(xstr);
//				if(!$scope.prefixes.hasOwnProperty(xstr)){
//					var res="";
//					var arr = tmp.split('\n'); //-> tutte le linee
//					for(i=0; i< arr.length; i++){
//						var t = new RegExp("prefix +"+xstr+":",'gi');
//						if(!t.test(arr[i])){
//							if(i!=arr.length-1){
//								res+=arr[i]+"\n";
//							}else{
//								res+=arr[i];
//							}
//						}
//					}
//					$scope.aceDocumentValue = res;
//					$scope.aceSession.getDocument().setValue($scope.aceDocumentValue);
//				}
//			}
//		}
		//console.log($scope.aceSession.getDocument());

		for(key in $scope.prefixes){
			var withPref = new RegExp("prefix +"+key+":",'gi');
			var withoutPref = new RegExp("^(?!.*prefix).*"+key+":",'mi');			

			var resWith = withPref.test(tmp); 
			var resWithout = withoutPref.test(tmp);	

			if(resWith && resWithout){
//				console.log(key+" Completo");
				
				var regNamespace = new RegExp($scope.prefixes[key],'gi').test(tmp);
//				console.log(regNamespace);
				
				if(!regNamespace){
				var res="";
				var arr = tmp.split('\n'); //-> tutte le linee
				for(i=0; i< arr.length; i++){
					var t = new RegExp("prefix +"+key+":",'gi');
					if(!t.test(arr[i])){
						if(i!=arr.length-1){
							res+=arr[i]+"\n";
						}else{
							res+=arr[i];
						}
					}
				}
				
				$scope.aceDocumentValue = res;
				$scope.aceSession.getDocument().setValue($scope.aceDocumentValue);
				pos.row--;
				$scope.aceEditor.moveCursorToPosition(pos);
				}
				
			}else if(!resWith && resWithout){
				//add
				$scope.aceDocumentValue = "PREFIX "+key+":"+$scope.prefixes[key]+"\n"+$scope.aceDocumentValue;
				$scope.aceSession.getDocument().setValue($scope.aceDocumentValue);
				pos.row++;
				$scope.aceEditor.moveCursorToPosition(pos);

			}else if(resWith && !resWithout){
				//remove
				var res="";
				var arr = tmp.split('\n'); //-> tutte le linee
				for(i=0; i< arr.length; i++){
					var t = new RegExp("prefix +"+key+":",'gi');
					if(!t.test(arr[i])){
						if(i!=arr.length-1){
							res+=arr[i]+"\n";
						}else{
							res+=arr[i];
						}
					}
				}
				$scope.aceDocumentValue = res;
				$scope.aceSession.getDocument().setValue($scope.aceDocumentValue);
				pos.row--;
				$scope.aceEditor.moveCursorToPosition(pos);

			}	
		}
	}

	$scope.clearArea = function(){
		$scope.aceDocumentValue='';
		$scope.aceSession.getDocument().setValue($scope.aceDocumentValue);
	}

	$scope.executeSpaqrl = function(){

		$rootScope.startSpin();

		var req = {
				method: 'POST',
				url: config.CLIENT_SERVICES_BASE_URL+config.SPARQL_SEARCH_SERVICE,
				headers: {
					'Content-Type': 'application/json'
				},
				data:{
					'query':$scope.aceDocumentValue,
					'format':$scope.outputMode
				}};
		
		$rootScope.results=[];
		$http(req).then(function(value){
			$rootScope.closeAlert();
			$rootScope.resultSparql=value.data.result;
			$rootScope.mode = value.data.contentType;
			$rootScope.stopSpin();
			$rootScope.executedQuery = $scope.aceDocumentValue;
			$window.location.assign("#/showSparqlResult");
		}, function(value){
			console.log(value);
			$rootScope.executedQuery = null;
			$rootScope.stopSpin();
			$rootScope.showAlert('danger',value.data.technicalMessage);
		});

	}

}]);

angular.module("IdraPlatform").controller('ShowSparqlCtrl',['$scope','$rootScope','$http','$window',function($scope,$rootScope,$http,$window){
	// The modes

	if($rootScope.resultSparql == undefined){
		$rootScope.executedQuery = undefined;
		$window.location.assign('#/sparql');
	}

	$scope.mode = $rootScope.mode;

	$scope.aceDocumentValue = $rootScope.resultSparql;

	$scope.aceLoaded = function(_editor) {
		$scope.aceSession = _editor.getSession();
		$scope.aceSession.setMode("ace/mode/" + $scope.mode.toLowerCase());
		$scope.aceSession.getDocument().setValue($scope.aceDocumentValue);
		_editor.moveCursorToPosition({'row':0,'column':0});
	};

	$scope.returnToEditor = function(){
		$window.location.assign("#/sparql");
	};

	$scope.successCopy = function(){
		console.log("Success copy");
	}

	$scope.errorCopy = function(err){
		console.log(err);
		console.log("Error copy");
	}

	$scope.downloadFile = function(){

		var req = {
				method: 'POST',
				url: "CreateFile",
				headers: {
					'Content-Type': 'application/json',
					"Accept": 'application/json'
				},
				data:{'result':$scope.aceDocumentValue,
					'format':$scope.mode }
		};

		$http(req).then(function(value){
//			console.log("Success");
//			console.log(value);

			var anchor = angular.element('<a/>');
			anchor.css({display: 'none'}); // Make sure it's not visible
			angular.element(document.body).append(anchor); // Attach to document

			anchor.attr({
				href: "./temp/"+value.data,
				target: '_blank',
				download: "sparql."+$scope.mode.toLowerCase()
			})[0].click();

			anchor.remove(); // Clean it up afterwards

		}, function(value){
			console.log("Problem");
		});

	}

}]);
/*END SPARQL*/

function isEmpty(obj) {
    for(var prop in obj) {
        if(obj.hasOwnProperty(prop))
            return false;
    }

    return true;
}
