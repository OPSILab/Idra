<!--
  Idra - Open Data Federation Platform
   Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
   
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  at your option) any later version.
   
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.
   
  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->

<%@page import="it.eng.idraportal.idm.configuration.IDMProperty"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" import="java.util.*"%>
<%@ page import="it.eng.idraportal.utils.PropertyManager"%>

<!doctype html>
<html lang="en">
<head>

<link rel="shortcut icon" type="image/png" href="images/icons/favicon-32x32.png"/>
<title>Idra - Open Data Federation Platform</title>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />

<!--<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">-->
<link rel="stylesheet" href="css/bootstrap-custom.min.css">
<link rel="stylesheet" href="css/label.min.css">
<link rel="stylesheet" href="css/table.css">
<link rel="stylesheet" href="css/spinner.css">
<link rel="stylesheet" href="css/footer.css">
<link rel="stylesheet" href="css/gridlist.css">

<link rel="stylesheet" href="bower_components/angular-xeditable/dist/css/xeditable.css">
<link rel="stylesheet" href="bower_components/ng-tags-input/ng-tags-input.min.css">
<link rel="stylesheet" href="bower_components/ng-tags-input/ng-tags-input.bootstrap.min.css">
<link rel="stylesheet" href="bower_components/angular-dialog-service/dist/dialogs.min.css">
<!--    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" rel="stylesheet"> -->
<!--    Include roboto.css to use the Roboto web font, material.css to include the theme and ripples.css to style the ripple effect -->
<!--    Non presenti in bower quindi li lascio per ora -->

<link rel="stylesheet" href="bower_components/angular-material/angular-material.min.css">

<link rel="stylesheet" href="material-bootstrap/css/roboto.min.css">
<!--<link rel="stylesheet" href="material-bootstrap/css/material-fullpalette.css">-->
<link href="material-bootstrap/css/material_original.css" rel="stylesheet">
<link rel="stylesheet" href="material-bootstrap/css/ripples.min.css">

<link rel="stylesheet" href="bower_components/ng-img-crop/compile/minified/ng-img-crop.css">
<link rel="stylesheet" href="bower_components/flag-icon-css/css/flag-icon.min.css">
<link rel="stylesheet" href="bower_components/angular-ui-switch/angular-ui-switch.css">
<link rel="stylesheet" href="bower_components/pdf.js-viewer/viewer.css">
<link rel="stylesheet" href="bower_components/leaflet/dist/leaflet.css">

<link rel="stylesheet" href="css/flag.css">
<link rel="stylesheet" href="css/main.css">
<!--<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">-->
<link rel="stylesheet" href="css/navbar.css">


<style type="text/css">

.modal .form-control {
    color: rgb(85,85,85);
}

.select-page {
  width: 50px;
  text-align: center;
}
.pagination li a input {
  padding: 0;
  margin: -5px 0;
}

#toolbarViewerRight{
	visibility: hidden;
}

.breadcrumb {
	background-color: #ffffff;
}

.switch {
	background: rgb(225, 44, 32);
	border-color: rgb(225, 44, 32);
}

.table td.text {
	max-width: 177px;
}

.table td.text span {
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	display: block;
	max-width: 100%;
}

.md-chips {
	font-size: inherit;
}

.ace_editor {
	height: 250px;
}

.btn-ace-toolbar {
	background-color: #ebebeb;
	margin: 0px 0px 0px 0px;
}

.md-chips {
	box-shadow: 0 0px !important;
}

a.disabled {
	color: #AAAAAA;
	cursor: default;
	pointer-events: none;
	text-decoration: none;
}

.cropArea {
	background: #E4E4E4;
	overflow: hidden;
	width: 500px;
	height: 350px;
}

.btn-file {
	position: relative;
	overflow: hidden;
}

.btn-file input[type=file] {
	position: absolute;
	top: 0;
	right: 0;
	min-width: 100%;
	min-height: 100%;
	font-size: 100px;
	text-align: right;
	filter: alpha(opacity = 0);
	opacity: 0;
	outline: none;
	background: white;
	cursor: inherit;
	display: block;
}

#cover {
	position: absolute;
	height: 100%;
	width: 100%;
	z-index: 1; /* make sure logout_box has a z-index of 2 */
	background-color: #ddd;
	opacity: 0.3;
}

.nav, .pagination, .carousel, .panel-title a {
	cursor: pointer;
}

.pagination {
	margin: 0px;
}

.full button span {
	background-color: limegreen;
	border-radius: 32px;
	color: black;
}

.partially button span {
	background-color: orange;
	border-radius: 32px;
	color: black;
}

.icon {
	height: 40px;
	width: 40px;
	margin-right: 10px;
}

.app-modal-window .modal-dialog {
	width: 95%;
	height: 95%;
	/*   background-color:#ffffff; */
}

.app-modal-window .modal-content {
	width: 100%;
	height: 100%;
	background-color: rgba(255, 255, 255, 1);
}

.app-modal-window .modal-body {
	width: 100%;
	height: 100%;
	/*   background-color:#ffffff; */
}

.st-sort-ascent:before {
	content: '\25B2';
}

.st-sort-descent:before {
	content: '\25BC';
}

[ng\:cloak], [ng-cloak], .ng-cloak {
	display: none !important;
}

.dialog-header-idra {
	background-color: #03a9f4;
}

</style>

<style type="text/css">
div.m-app-loading {
	position: fixed;
}

div.m-app-loading div.animated-container {
	background-color: #FFFFFF;
	bottom: 0px;
	left: 0px;
	opacity: 1.0;
	position: fixed;
	right: 0px;
	top: 0px;
	z-index: 999999;
}

div.m-app-loading div.animated-container.ng-leave {
	opacity: 1.0;
	transition: all linear 200ms;
	-webkit-transition: all linear 200ms;
}

div.m-app-loading div.animated-container.ng-leave-active {
	opacity: 0;
}

div.m-app-loading div.messaging {
	color: #333333;
	font-family: monospace;
	left: 0px;
	position: absolute;
	right: 0px;
	text-align: center;
	top: 30%;
}

div.m-app-loading h1 {
	font-size: 26px;
	line-height: 35px;
	margin: 0px 0px 20px 0px;
}

div.m-app-loading p {
	font-size: 18px;
	line-height: 14px;
	margin: 0px 0px 0px 0px;
}
</style>

</head>
<body>

	<div class="m-app-loading">
		<!--         BEGIN: Actual animated container. -->
		<div class="animated-container">
			<div class="messaging hide-xs show-gt-xs">
				<img class="img-responsive " src="images/idra_D_v6_black.svg"
					style="margin: 0 auto; max-width: 20%;">
				<img class="img-responsive " src="images/spinner_synchro.gif"
					style="margin: 0 auto; max-width: 2%;">
			</div>
			<div class="messaging hide-gt-xs">
				<img class="img-responsive " src="images/idra_D_v6_black.svg"
					style="margin: 0 auto; max-width: 50%;">
				<img class="img-responsive " src="images/spinner_synchro.gif"
					style="margin: 0 auto; max-width: 10%;">
			</div>
			<!--         END: Actual animated container. -->
		</div>
	</div>
	<!--     END: App-Loading Screen. -->


	<div class="wrapper" style="background-color: #FFFFFF;">
		<!-- HEADER -->
		<div id="header" ng-controller="HeaderController" ng-cloak="">
			<!-- NAVBAR -->
			<div class="navbar navbar-default">
				<div class="container-fluid" style="margin: 10px 0px 0px 0px;">
					<div class="navbar-header">
						<a class="navbar-brand" href="#/metadata"
							style="margin-left: 5px;">
						</a>
<!-- 						<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" -->
<!-- 							data-target=".navbar-responsive-collapse"> -->
						<button type="button" class="navbar-toggle collapsed" ng-click="toggleDropdown()">
							<span class="icon-bar"></span> 
							<span class="icon-bar"></span> 
							<span class="icon-bar"></span>
						</button>
					</div>
					<div class="navbar-collapse collapse navbar-responsive-collapse" ng-class="{ 'in': isOpen }">
						<ul class="nav navbar-nav navbar-right">
							<li
								ng-class="{ active: isActive('/metadata') || isActive('/showDatasets') || isActive('/showDatasetDetail') || isActive('/createDatalet') }"><a
								href="#/metadata"><strong>{{'datasetSearch' | translate }}</strong></a></li>
							<li
								ng-class="{ active: isActive('/sparql') || isActive('/showSparqlResult') }"><a
								href="#/sparql"><strong>{{'SPARQLSearch' | translate }}</strong></a></li>
							<li ng-class="{ active: isActive('/viewCatalogues')}"><a
								href="#/viewCatalogues"><strong>{{'DataSources' | translate }}</strong></a></li>
							<li ng-class="{ active: isActive('/statistics')}"><a
								href="#/statistics"><strong>{{'statistics' | translate }}</strong></a></li>
							<li dropdown><a href class="dropdown-toggle" dropdown-toggle><strong>Help</strong><b
									class="caret"></b></a>
								<ul class="dropdown-menu">
									<li>
										<a href="#/about" ng-click="isOpen=!isOpen"><strong>About</strong></a>
									</li>
									<li>
										<a href="https://idraopendata.docs.apiary.io" target="_blank" ng-click="isOpen=!isOpen"><strong>API</strong></a>
									</li>
									<li>
										<a href="https://github.com/OPSILab/Idra" target="_blank" ng-click="isOpen=!isOpen"><strong>GitHub</strong></a>
									</li>
									<li>
										<a href="https://idra.readthedocs.io" target="_blank" ng-click="isOpen=!isOpen"><strong>{{'helpMenuManual' | translate }}</strong></a>
									</li>
								</ul></li>
							<li ng-if="token!=undefined"
								ng-class="{ active: isActive('/catalogues') || isActive('/node') || isActive('/configuration') || isActive('/logs') || isActive('/statistics') || isActive('/dataletsManagement')}"
								dropdown><a href class="dropdown-toggle" dropdown-toggle><strong>{{'administration' | translate }}</strong><b
									class="caret"></b></a>
								<ul class="dropdown-menu">
									<li
										ng-class="{ active: isActive('/catalogues') || isActive('/addNode') }">
										<a href="#/catalogues"><strong>{{'manageData' | translate }}</strong></a>
									</li>
									<li ng-class="{ active: isActive('/configuration')}"><a
										href="#/configuration"><strong>{{'manageConf' | translate }}</strong></a></li>
									<li ng-show="dataletEnabled"
										ng-class="{ active: isActive('/dataletsManagement')}"><a
										href="#/dataletsManagement"><strong>{{'manageDatalet' | translate }}</strong></a></li>
									<li ng-class="{ active: isActive('/logs')}"><a
										href="#logs"><strong>{{'viewLogs' | translate }}</strong></a></li>
								</ul></li>
							<li dropdown><a href class="dropdown-toggle" dropdown-toggle><span class="flag-icon" ng-class="(activeLanguage=='it')?'flag-icon-it':'flag-icon-gb'"></span><b
									class="caret"></b></a>
								<ul class="dropdown-menu">
									<li>
										<a href="javascript:void(0)" ng-click="changeLanguage('en')">
										<span class="flag-icon" ng-class="'flag-icon-gb'"></span>&nbsp<strong>English</strong></a>
									</li>
									<li>
										<a href="javascript:void(0)" ng-click="changeLanguage('it')" >
										<span class="flag-icon" ng-class="'flag-icon-it'"></span>&nbsp<strong>Italiano</strong></a>
									</li>
								</ul></li>
							<li class="loginBtns">
								<form ng-if="token==undefined" class="navbar-form"
									ng-controller="LoginCtrl">
									<button type="button" class="btn btn-primary btn-raised"
										ng-click="signIn()">{{'login' | translate }}</button>
								</form>

								<form ng-if="token!=undefined" class="navbar-form"
									ng-controller="LogoutCtrl">
									<button type="button" class="btn btn-primary btn-raised btn-icon"
										ng-click="logout()">{{'logout' | translate }}</button>
								</form>

							</li>
						</ul>
					</div>
					<form style="display:none" id="loginform" method="GET" ng-if="token==undefined"
						class="navbar-form"
						action="<%=PropertyManager.getProperty(IDMProperty.IDM_PROTOCOL) + "://"
					+ PropertyManager.getProperty(IDMProperty.IDM_HOST) + "/oauth2/authorize"%>">
						<input type="hidden" name="response_type" value="code" /> <input
							type="hidden" name="client_id"
							value="<%=PropertyManager.getProperty(IDMProperty.IDM_CLIENT_ID)%>" />
						<input type="hidden" id="loginstate" name="state" value="" /> <input
							type="hidden" name="redirect_uri"
							value="<%=PropertyManager.getProperty(IDMProperty.IDM_REDIRECT_URI)%>" />
					</form>

				</div>
			</div>
			<!-- END NAVBAR -->

		</div>
		<!-- END HEADER -->

		<div id="content" class="container-fluid" ng-cloak=""
			ng-controller="ContentCTRL">

			<div class="us-spinner-wrapper" ng-show="spinneractive">
				<div us-spinner="{radius:25, width:10, length: 20,color:'#00b4ff'}"
					spinner-key="spinner-1"></div>
			</div>
			<div class="col-md-10 col-md-offset-1" ng-controller="AlertCtrl">
				<div class="col-md-6 col-md-offset-3">
					<alert type='{{alertType}}' style="text-align:center"
						ng-show="alert" close="closeAlert()" dismiss-on-timeout="3000">
					{{textAlert}} </alert>
				</div>
			</div>

			<div class="col-md-10 col-sm-10 col-xs-12 col-md-offset-1 col-sm-offset-1">
				<div ng-view autoscroll="true"></div>
			</div>
		</div>
		<div class="push"></div>

	</div>
	<!--  END WRAPPER -->
	<!-- FOOTER -->
	<div class="footer" style="display: none">
		<div class="col-md-12 col-lg-12 col-sm-12 text-center hide-xs show-gt-xs" >
			<div class="col-md-4 col-lg-4 col-sm-4">
			<a href="https://www.eng.it/" target="_blank" class="pull-left"><img
					class="img-responsive footerImages small-margin"
					ng-src="images/logo_eng-100.jpg" style="width:25%"/></a></div>
			<div class="col-md-4 col-lg-4 col-sm-4">
				<p style="margin-bottom:0px;">Copyright &copy;<a href="https://www.eng.it/" target="_blank">Engineering</a> 2018. - Idra v. {{idraVersion}}</p>
			</div>
			<div class="col-md-4 col-lg-4 col-sm-4">
				<a class="pull-right" href="#/credits">{{'credits' | translate}}</a>
			</div>
		</div>
		<div class="col-md-12 col-lg-12 col-sm-12 text-center hide-gt-xs" >
			<div class="col-md-12 col-lg-12 col-sm-12">
				<p style="margin-bottom:0px;">Copyright &copy;<a href="https://www.eng.it/" target="_blank">Engineering</a></p>
			</div>
			<div class="col-md-12 col-lg-12 col-sm-12">
				<p style="margin-bottom:0px;">Idra v. {{idraVersion}}</p>
			</div>
			<div class="col-md-12 col-lg-12 col-sm-12">
				<a href="#/credits">{{'credits' | translate}}</a>
			</div>
			<div class="col-md-12 col-lg-12 col-sm-12">
			<a href="https://www.eng.it/" target="_blank"><img
					class="img-responsive footerImages center-block"
					ng-src="images/logo_eng-100.jpg" style="width:40%; margin-bottom:10px"/></a>
			</div>
		</div>
	</div>
	<!-- END FOOTER -->
	
	<script type="text/ng-template" id="ModalContentSingle.html">
<div class="modal-header">
	<button type="button" class="close" aria-hidden="true" ng-click="cancel()">x</button>
    <h3 class="modal-title">{{'DataSources' | translate}}</h3>
</div>
<div class="modal-body row">
	<alert type='{{alertTypeModal}}' style="text-align:center"
		ng-show="alertModal" close="closeAlertModal()" dismiss-on-timeout="3000">
	{{textAlertModal}} </alert>

	<div class="col-md-12 col-sm-12 col-lg-12">
		<div class="well">

	<input class="search form-control" placeholder="{{'search' | translate}}" ng-model="selected" />
			<div style="overflow: auto; height: 300px;">
				<ul class="list-group list" >
					<li ng-repeat="item in allItems | filter:selected as results" class='list-group-item' style='margin-bottom:-18px'>
							<label class='name h4'>
							<md-checkbox ng-checked="exists(item, selectedItems)" ng-click="toggle(item, selectedItems)" class="blue">
               {{ item }}
              </md-checkbox>
							</label>
					</li>
					<li class="list-group-item" ng-if="results.length == 0">
						<label class='name h4'>
							<strong >{{'noresults' | translate}}!</strong>
						</label>
					</li>
				</ul>				
			</div>

		</div>
	</div>

	<label class='name checklbl'> <md-checkbox class="blue" ng-checked="isChecked()"
                         ng-click="toggleAll()" > {{'selectAll' | translate}}</md-checkbox> </label>
</div>

<div class="modal-footer">
    <button class="btn btn-default btn-raised" type="button" ng-click="cancel()">{{'cancel' | translate}}</button>
	<button class="btn btn-primary btn-raised" type="button" ng-click="ok()">{{'ok' | translate}}</button>
</div>
</script>

<script type="text/ng-template" id="ModalDataletAdmin.html">
<div class="modal-header">
	<button type="button" class="close" aria-hidden="true" ng-click="cancel()">x</button>
    <h3 class="modal-title">{{'dataletDetail' | translate}}</h3>
</div>
<div class="modal-body row">
	<div ng-bind-html="datalet.showHtml"></div>
</div>
<div class="modal-footer">
    <button class="btn btn-default btn-raised" type="button" ng-click="cancel()">{{'dialog_close' | translate}}</button>
</div>
</script>

	<script type="text/ng-template" id="ModalDatalet.html">
	<iframe width="100%" height="100%" ng-src="{{iframeURL}}"></iframe>
</script>

	<script type="text/ng-template" id="deletecatalogue_dialog.html">
<div class="modal-header dialog-header-confirm">
	<button type="button" class="close" ng-click="no()">&times;</button>
	<h4 class="modal-title">
		<span class="{{icon}}"></span>
		{{header}}
	</h4>
</div>
<div class="modal-body" ng-bind-html="msg"></div>
<div class="modal-footer">
	<button type="button" class="btn btn-default btn-raised" ng-click="yes()">{{opt1}}</button>
	<button type="button" class="btn btn-default btn-raised" ng-click="keep()">{{opt2}}</button>
	<button type="button" class="btn btn-primary btn-raised" ng-click="no()">No</button>
</div>
</script>

	<script type="text/ng-template" id="dumpSave_dialog.html">
<div class="modal-header dialog-header-confirm">
	<button type="button" class="close" ng-click="no()">&times;</button>
	<h4 class="modal-title">
		<span class="{{icon}}"></span>
		{{header}}
	</h4>
</div>
<div class="modal-body" ng-bind-html="msg"></div>
<div class="modal-footer">
	<button type="button" class="btn btn-default btn-raised" ng-click="discard()">{{opt1}}</button>
	<button type="button" class="btn btn-default btn-raised" ng-click="save()">{{opt2}}</button>
	<button type="button" class="btn btn-primary btn-raised" ng-click="no()">Cancel</button>
</div>
</script>

<script type="text/ng-template" id="idra_error_dialog.html">
<div class="modal-header dialog-header-idra">
	<button type="button" class="close" ng-click="close()">&times;</button>
	<h4 class="modal-title">
		<span class="{{icon}}"></span>
		{{header}}
	</h4>
</div>
<div class="modal-body" ng-bind-html="msg"></div>
<div class="modal-footer">
	<button type="button" class="btn btn-primary btn-raised" ng-click="close()">CLOSE</button>
</div>
</script>

<!-- PREVIEW TEMPLATES -->

<script type="text/ng-template" id="TablePreview.html">
<div class="modal-header">
	<button type="button" class="close" aria-hidden="true" ng-click="cancel()">x</button>
    <h3 class="modal-title" md-truncate>{{'modalTitlePreview' | translate}}:&nbsp{{title}}</h3>
</div>
<div class="modal-body row">
	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12" style="overflow-x: scroll;">
	<table st-table="dataDisplayed" st-safe-src="data" class="table table-striped">
						<thead>
							<tr>
								<th ng-repeat="header in headers track by $index"><p md-truncate>{{header}}</p></th>
							</tr>
			</thead>
						<tbody>
							<tr ng-repeat="row in dataDisplayed track by $index">
								<td ng-repeat="column in row track by $index">
      								<p md-truncate>{{ column }}</p>
    							</td>
							</tr>
						</tbody>
					<tfoot>
				<tr>
					<td colspan="{{colSpan}}">
						<div st-pagination="" st-items-by-page="10"
							st-template="CustomPagination.html"></div>
					</td>
				</tr>
			</tfoot>
		</table>
	</div>
</div>
</script>

<script type="text/ng-template" id="DocumentPreview.html">
<div class="modal-header">
	<button type="button" class="close" aria-hidden="true" ng-click="cancel()">x</button>
    <h3 class="modal-title" md-truncate>{{'modalTitlePreview' | translate}}:&nbsp{{title}}</h3>
</div>
<div class="modal-body row">
	<div style="height: 500px" ng-model="previewDocument"
	ui-ace="{
					  		useWrapMode : true, 
					 		showPrintMargin: false, 
 					   		showGutter: true, 
 					  		theme:'chrome', 
 					  		firstLineNumber: 1, 
 					  		onLoad: aceLoaded, 
 					  		rendererOptions: { 
 					     		 maxLinks: Infinity 
 					  		} 
 						}">
	</div>
</div>
</script>

<script type="text/ng-template" id="PDFPreview.html">
<div class="modal-header">
	<button type="button" class="close" aria-hidden="true" ng-click="cancel()">x</button>
    <h3 class="modal-title" md-truncate>{{'modalTitlePreview' | translate}}:&nbsp{{title}}</h3>
</div>
<div class="modal-body row">
	<div style="height:500px">
		<pdfjs-viewer data="pdf"></pdfjs-viewer>
	</div>
</div>
</script>

<script type="text/ng-template" id="GEOJSONPreview.html">
<div class="modal-header">
	<button type="button" class="close" aria-hidden="true" ng-click="cancel()">x</button>
    <h3 class="modal-title" md-truncate>{{'modalTitlePreview' | translate}}:&nbsp{{title}}</h3>
</div>
<div class="modal-body row">
	<leaflet style="height:500px;width:100%" lf-center="center" geojson="geojson" default="default"></leaflet>
</div>
</script>

<script type="text/ng-template" id="CustomPagination.html">
<nav ng-if="pages.length >= 2">
  <ul class="pagination">
    <li><a ng-click="selectPage(1)">&lt;&lt;</a>
    </li><li><a ng-click="selectPage(currentPage - 1)">&lt;</a>
    </li><li><a><page-select></page-select> of {{numPages}}</a>
    </li><li><a ng-click="selectPage(currentPage + 1)">&gt;</a>
    </li><li><a ng-click="selectPage(numPages)">&gt;&gt;</a></li>
  </ul>
</nav>
</script>

<!-- END PREVIEW TEMPLATES -->

	<script type="text/javascript" src="bower_components/jquery/jquery.js"></script>
	<script type="text/javascript"
		src="bower_components/angular/angular.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-aria/angular-aria.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-material/angular-material.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-animate/angular-animate.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-route/angular-route.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-cookies/angular-cookies.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-sanitize/angular-sanitize.js"></script>
	<script type="text/javascript"
		src="bower_components/ace-builds/src-noconflict/ace.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-ui-ace/src/ui-ace.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-bootstrap/ui-bootstrap.js"></script>
	<script type="text/javascript"
		src="bower_components/ace-builds/src-noconflict/ext-language_tools.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-bootstrap/ui-bootstrap-tpls.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-smart-table/dist/smart-table.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-xeditable/dist/js/xeditable.js"></script>
	<script type="text/javascript" src="bower_components/spin.js/spin.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-spinner/angular-spinner.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-dialog-service/dist/dialogs.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-md5/angular-md5.js"></script>
	<script type="text/javascript"
		src="bower_components/zeroclipboard/dist/ZeroClipboard.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-zeroclipboard/dist/angular-zeroclipboard.min.js"></script>
	<script type="text/javascript"
		src="bower_components/ng-tags-input/ng-tags-input.js"></script>
	<script type="text/javascript"
		src="bower_components/ng-img-crop/compile/minified/ng-img-crop.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-utils-pagination/dirPagination.js"></script>
	<script type="text/javascript"
		src="bower_components/marked/lib/marked.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-marked/dist/angular-marked.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-file-saver/dist/angular-file-saver.bundle.js"></script>
	<script type="text/javascript"
		src="bower_components/ng-country-select/dist/ng-country-select.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-ui-switch/angular-ui-switch.min.js"></script>
	<script type="text/javascript"
		src="bower_components/underscore/underscore.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-underscore-module/angular-underscore-module.js"></script>

	<!-- 	<script src='bower_components/angular-tag-cloud/src/ng-tag-cloud.js'></script> -->
	<script type="text/javascript" src="bower_components/d3/d3.min.js"></script>
	<script type="text/javascript"
		src="bower_components/d3-cloud/build/d3.layout.cloud.js"></script>
	<script type="text/javascript"
		src="bower_components/angular-d3-word-cloud/dist/angular-word-cloud.min.js"></script>

	<script type="text/javascript" src="bower_components/angular-translate/angular-translate.min.js"></script>
	<script type="text/javascript" src="bower_components/angular-translate-storage-local/angular-translate-storage-local.min.js"></script>
	<script type="text/javascript" src="bower_components/angular-translate-storage-cookie/angular-translate-storage-cookie.min.js"></script>
	<script type="text/javascript" src="bower_components/angular-translate-loader-static-files/angular-translate-loader-static-files.min.js"></script>

	<script type="text/javascript" src="bower_components/chart.js/dist/Chart.js"></script>
	<script type="text/javascript" src="bower_components/angular-chart.js/dist/angular-chart.js"></script>
	
	<script type="text/javascript" src="bower_components/papaparse/papaparse.min.js"></script>
	<script type="text/javascript" src="bower_components/angular-papaparse/dist/js/angular-PapaParse.js"></script>
	
	<script  type="text/javascript" src="bower_components/pdf.js-viewer/pdf.js"></script>
    <script  type="text/javascript" src="bower_components/angular-pdfjs-viewer/dist/angular-pdfjs-viewer.js"></script>
    
    <script  type="text/javascript" src="bower_components/leaflet/dist/leaflet.js"></script>
	<script  type="text/javascript" src="bower_components/angular-leaflet-directive/dist/angular-leaflet-directive.min.js"></script>

	<script  type="text/javascript" src="bower_components/togeojson/togeojson.js"></script>

	<script type="text/javascript" src="app.js"></script>
	<script type="text/javascript" src="catalogues/catalogues.services.js"></script>
	<script type="text/javascript" src="catalogues/catalogues.js"></script>
	<script type="text/javascript" src="catalogues/remote_catalogues.js"></script>
	<script type="text/javascript" src="catalogues/single_catalogue.js"></script>
	<script type="text/javascript" src="catalogues/view_catalogues.js"></script>

	<script type="text/javascript"
		src="catalogues/defaultDatasets.services.js"></script>
	<script type="text/javascript" src="catalogues/editDumpCtrl.js"></script>

	<script type="text/javascript" src="about/about.js"></script>
	<script type="text/javascript" src="sparql/sparql.js"></script>
	<script type="text/javascript" src="js/mode-sparql.js"></script>
	<script type="text/javascript" src="configuration/configuration.js"></script>

	<script type="text/javascript"
		src="metadata/defaultparameter.services.js"></script>
	<script type="text/javascript" src="metadata/metadata.services.js"></script>
	<script type="text/javascript" src="metadata/metadata.js"></script>
	<script type="text/javascript" src="metadata/datasetResults.js"></script>
	
	<script type="text/javascript" src="templatePreview/template_preview_ctrls.js"></script>
	
	<script type="text/javascript" src="metadata/datasetDetail.js"></script>
	<script type="text/javascript" src="metadata/datesModIssued.js"></script>
	<script type="text/javascript" src="metadata/tagCloudController.js"></script>
	<script type="text/javascript" src="datalets/datalet.services.js"></script>
	<script type="text/javascript" src="datalets/dataletClient.js"></script>
	<script type="text/javascript" src="datalets/dataletAdmin.js"></script>
	<script type="text/javascript" src="logPage/logging.js"></script>
	<script type="text/javascript" src="credits/credits.js"></script>
	
	<script type="text/javascript" src="statistics/statistics.services.js"></script>
	<script type="text/javascript" src="statistics/statistics.js"></script>

	<script type="text/javascript"
		src="bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
	<script type="text/javascript"
		src="material-bootstrap/js/material.min.js"></script>
	<script type="text/javascript"
		src="material-bootstrap/js/ripples.min.js"></script>

	<!-- INIT -->
	<script>
	
		$(document).ready(function() {
			console.log("Welcome.");
			
<%-- 			var sessiontoken = '<%=request.getSession().getAttribute("loggedin")%>'; --%>
<%-- 			var sessionrefreshtoken = '<%=request.getSession().getAttribute("refresh_token")%>'; --%>
<%-- 			var sessionusername = '<%=request.getSession().getAttribute("username")%>'; --%>
// 			if (sessiontoken != "null")
// 				document.cookie = "loggedin="+sessiontoken+";path=/";
// 			if (sessionrefreshtoken != "null")
// 				document.cookie = "refresh_token="+sessionrefreshtoken+";path=/";
// 			if (sessionusername != "null")
// 				document.cookie = "username="+sessionusername+";path=/";
			
			// This command is used to initialize some elements and make them work properly
			$.material.init();
			
			var loc = window.document.location;
<%-- 			var postloginuri = loc.protocol+"//"+loc.host+"/"+loc.pathname.split("/")[1]+"<%=PropertyManager.getProperty("idm.postlogin")%>"; --%>
			
			// The URI of the PostLogin must be the RedirectUri, that is the Idra Login service
			var postloginuri = "<%=PropertyManager.getProperty(IDMProperty.IDM_REDIRECT_URI)%>";
// 			console.log("postloginuri" + postloginuri);
			$('#loginstate').val(btoa(postloginuri));
			$('#loginstate').parent().find('button').prop('inactive', false);
			console.log("$location.path(): "+ window.location.href);
			console.log((window.location.href).split('http://').pop().split('.').shift())
		});
	</script>
	<!-- END INIT -->

</body>
</html>
