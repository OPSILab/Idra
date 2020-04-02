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
angular.module("IdraPlatform").controller('DumpCtrl',["$scope",'dump','type','DefaultDatasets','$modalInstance','dialogs','$rootScope','$translate',function($scope,dump,type,DefaultDatasets,$modalInstance,dialogs,$rootScope,$translate){
	
	
	
	$scope.editorDumpMessageTitle ="";
	$scope.editorDumpMessage="";
	$scope.editorDumpMessageOPT1="";
	$scope.editorDumpMessageOPT2="";
	$scope.wrongFile="";
	$scope.wrongFileMexJSON="";
	$scope.wrongFileMexRDF="";
	var getTranlsatedValueDialogs = function(){
			$translate('editorDumpMessageTitle')
				.then(function (translatedValue) {
					$scope.editorDumpMessageTitle = translatedValue;
			 });
			$translate('editorDumpMessage')
			.then(function (translatedValue) {
				$scope.editorDumpMessage = translatedValue;
			});
			$translate('editorDumpMessageOPT1')
			.then(function (translatedValue) {
				$scope.editorDumpMessageOPT1 = translatedValue;
			});
			$translate('editorDumpMessageOPT2')
			.then(function (translatedValue) {
				$scope.editorDumpMessageOPT2 = translatedValue;
			});
			$translate('wrongFile')
			.then(function (translatedValue) {
				$scope.wrongFile = translatedValue;
			});
			$translate('wrongFileMexJSON')
			.then(function (translatedValue) {
				$scope.wrongFileMexJSON = translatedValue;
			});
			$translate('wrongFileMexRDF')
			.then(function (translatedValue) {
				$scope.wrongFileMexRDF = translatedValue;
			});
	}
	
	$rootScope.$on('$translateChangeSuccess', function(event, current, previous) {
		getTranlsatedValueDialogs();
    });
	
	getTranlsatedValueDialogs();
	
	var getMode = function(type){
		if(type.toLowerCase()=='dcatdump'){
			return 'xml';
		}
		return 'json';
	} 
	
	if(dump!='' && dump!=undefined){
		if(getMode(type)=='json'){
			try{
				dump = JSON.stringify(JSON.parse(dump),null,4);
			}catch(error){
				console.log(error);
				dump=dump;
			}
		}
	}else{
		dump='';
	}
	
	var savedDocument=dump;
	
	$scope.nodeType=type;
	$scope.aceDocumentValue = dump;
	$scope.disableSave = true;
	$scope.disableRedo = true;
	$scope.disableUndo = true;
	
	$scope.ok = function(){
		if(!$scope.disableSave){
			var dlg = dialogs.create('dumpSave_dialog.html','dumpDialogCtrlEdit',{'header':$scope.editorDumpMessageTitle,
				'msg':$scope.editorDumpMessage,'opt1':$scope.editorDumpMessageOPT1,'opt2':$scope.editorDumpMessageOPT2},{key: false,back: 'static'});
			dlg.result.then(function(value){
				if(value==1){
					//Discard Changes
					$modalInstance.close(savedDocument);
				}
				else if(value==2){ 
					//Save Changes
					$modalInstance.close($scope.aceSession.getDocument().getValue());
				}
				
			},function(value){
				
			});	
		}else{
			$modalInstance.close(savedDocument);
		}
	};
	
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
	
	$scope.aceLoaded = function(_editor) {
		$scope.aceEditor=_editor;
		$scope.aceSession = _editor.getSession();
		$scope.aceSession.setMode("ace/mode/" + getMode(type));
		$scope.aceEditor.setSession($scope.aceSession);
		$scope.aceSession.setUndoManager(new ace.UndoManager());			
	};

	var updateToolbar = function(){
		$scope.disableSave = $scope.aceSession.getUndoManager().isClean();
		$scope.disableRedo=!($scope.aceSession.getUndoManager().hasRedo());
		$scope.disableUndo=!($scope.aceSession.getUndoManager().hasUndo());
	} 
	
	$scope.aceChanged = function (_editor) {
		updateToolbar();
	};
	
	$scope.redo=function(){
		$scope.aceSession.getUndoManager().redo();
		updateToolbar();
	};
	
	$scope.undo=function(){
		$scope.aceSession.getUndoManager().undo();
		updateToolbar();
	};
	
	$scope.save=function(){
		$scope.aceSession.getUndoManager().markClean();
		updateToolbar();
		savedDocument = $scope.aceSession.getDocument().getValue();
		$scope.disableSave = $scope.aceSession.getUndoManager().isClean();
	};
	
	$scope.addNewDataset=function(fullDataset){
		DefaultDatasets.getDefaultDatasetDump(type,fullDataset).success(function(dataset) {
			if($scope.aceDocumentValue=='')
				$scope.aceDocumentValue='[]';
			var arr = JSON.parse($scope.aceDocumentValue);
			arr.push(dataset);
			$scope.aceDocumentValue=JSON.stringify(arr,null,4);
			updateToolbar();
		});
	};
	
	$scope.fileUploadJSON = function(fileEl){
		var files = fileEl.files;
		  var file = files[0];
		  //console.log(file);
		  if(file.name.endsWith(".json")){
			  var reader = new FileReader();

			  reader.onloadend = function(evt) {
				  if (evt.target.readyState === FileReader.DONE) {
					  $scope.$apply(function () {
						  
						  var arr = JSON.parse(evt.target.result);
						  savedDocument=JSON.stringify(arr,null,4);
						  $scope.aceDocumentValue=JSON.stringify(arr,null,4);
					  });
				  }
			  };

			  reader.readAsText(file);
		  }else{
			  dialogs.error($scope.wrongFile,$scope.wrongFileMexJSON);
			  return;
		  }
	};
		
	$scope.fileUploadDUMP = function(fileEl){
		var files = fileEl.files;
		var file = files[0];
		if(file.name.endsWith(".rdf") || file.name.endsWith(".xml")){
			var reader = new FileReader();

			  reader.onloadend = function(evt) {
				  if (evt.target.readyState === FileReader.DONE) {
					  $scope.$apply(function () {
						  savedDocument=evt.target.result;
						  $scope.aceDocumentValue=evt.target.result;
					  });
				  }
			  };

			  reader.readAsText(file);
		}else{
			dialogs.error($scope.wrongFile,$scope.wrongFileMexRDF);
			return;
		}
	};
	
}]).controller('dumpDialogCtrlEdit',['$scope','$uibModalInstance','data',function($scope,$uibModalInstance,data){
	//-- Variables -----//

	$scope.header = (angular.isDefined(data.header)) ? data.header : "Warning";
	$scope.msg = (angular.isDefined(data.msg)) ? data.msg : "There are unsaved changes";
	$scope.icon = (angular.isDefined(data.fa) && angular.equals(data.fa,true)) ? 'fa fa-check' : 'glyphicon glyphicon-check';
	$scope.opt1 = (angular.isDefined(data.opt1)) ? data.opt1 : 'Discard';
	$scope.opt2 = (angular.isDefined(data.opt2)) ? data.opt2 : 'Save';
	//-- Methods -----//
	
	$scope.no = function(){
		$uibModalInstance.dismiss(0);
	}; // end close
	
	$scope.discard = function(){
		$uibModalInstance.close(1);
	}; // end yes
	
	$scope.save = function(){
		$uibModalInstance.close(2);
	};
}]);
;