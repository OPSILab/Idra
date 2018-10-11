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

<title>Idra - Open Data Federation Platform</title>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />

<link rel="stylesheet" href="css/bootstrap-custom.min.css">
<link rel="stylesheet" href="css/label.min.css">
<link rel="stylesheet" href="css/table.css">
<link rel="stylesheet" href="css/spinner.css">
<link rel="stylesheet" href="css/footer.css">
<link rel="stylesheet" href="css/gridlist.css">

<link rel="stylesheet"
	href="bower_components/angular-xeditable/dist/css/xeditable.css">
<link rel="stylesheet"
	href="bower_components/ng-tags-input/ng-tags-input.min.css">
<link rel="stylesheet"
	href="bower_components/ng-tags-input/ng-tags-input.bootstrap.min.css">
<link rel="stylesheet"
	href="bower_components/angular-dialog-service/dist/dialogs.min.css">
<!--    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" rel="stylesheet"> -->
<!--    Include roboto.css to use the Roboto web font, material.css to include the theme and ripples.css to style the ripple effect -->
<!--    Non presenti in bower quindi li lascio per ora -->

<link rel="stylesheet"
	href="bower_components/angular-material/angular-material.min.css">

<link rel="stylesheet" href="material-bootstrap/css/roboto.min.css">
<link rel="stylesheet"
	href="material-bootstrap/css/material-fullpalette.css">
<link rel="stylesheet" href="material-bootstrap/css/ripples.min.css">

<link rel="stylesheet"
	href="bower_components/ng-img-crop/compile/minified/ng-img-crop.css">
<link rel="stylesheet"
	href="bower_components/flag-icon-css/css/flag-icon.min.css">
<link rel="stylesheet"
	href="bower_components/angular-ui-switch/angular-ui-switch.css">

<link rel="stylesheet" href="css/flag.css">
<link rel="stylesheet" href="css/main.css">
<link rel="stylesheet" href="css/navbar.css">


<style type="text/css">
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
			<div class="messaging">
				<img class="img-responsive " src="images/idra_logo.png"
					style="margin: 0 auto; max-width: 20%;">
				<img class="img-responsive " src="images/spinner.gif"
					style="margin: 0 auto; max-width: 2%;">
<!-- 				<p style="margin-top: 1.2em; font-size: 28px;">Loading Idra - -->
<!-- 					Open Data Federation Platform</p> -->
			</div>
			<!--         END: Actual animated container. -->
		</div>
	</div>
	<!--     END: App-Loading Screen. -->


	<div class="wrapper" style="background-color: #FFFFFF;">
		<!-- HEADER -->
		<div id="header" ng-controller="HeaderController" ng-cloak="">
			<div class="us-spinner-wrapper" ng-show="spinneractive">
				<div us-spinner="{radius:50, width:15, length: 25,color:'#44313f'}"
					spinner-key="spinner-1"></div>
			</div>

			<!-- NAVBAR -->
			<div class="navbar navbar-default">
				<div class="container-fluid" >
					<div class="navbar-header">
						<a class="navbar-left" href="#/metadata"
							style="margin-left: 0px">
							<img class="img-responsive" style="height:60px" src="./images/idra_white.png">
<!-- 							<p class="navbar-text" -->
<!-- 								style="margin-top: -4px; margin-bottom: 0px;"> - Open -->
<!-- 								Data Federation Platform</p> -->
						</a>
						<button type="button" class="navbar-toggle" data-toggle="collapse"
							data-target=".navbar-responsive-collapse">
							<span class="icon-bar"></span> <span class="icon-bar"></span> <span
								class="icon-bar"></span>
						</button>
					</div>
					<div class="navbar-collapse collapse navbar-responsive-collapse">
						<ul class="nav navbar-nav navbar-right">
							<li
								ng-class="{ active: isActive('/metadata') || isActive('/showDatasets') || isActive('/showDatasetDetail') || isActive('/createDatalet') }"><a
								href="#/metadata"><strong>Dataset Search</strong></a></li>
							<li
								ng-class="{ active: isActive('/sparql') || isActive('/showSparqlResult') }"><a
								href="#/sparql"><strong>SPARQL Search</strong></a></li>
							<li ng-class="{ active: isActive('/viewCatalogues')}"><a
								href="#/viewCatalogues"><strong>Federated
										Catalogues</strong></a></li>

							<li ng-if="token!=undefined"
								ng-class="{ active: isActive('/catalogues') || isActive('/node') || isActive('/configuration') || isActive('/logs') || isActive('/statistics') || isActive('/dataletsManagement')}"
								dropdown><a href class="dropdown-toggle" dropdown-toggle><strong>Administration</strong><b
									class="caret"></b></a>
								<ul class="dropdown-menu">
									<li
										ng-class="{ active: isActive('/catalogues') || isActive('/addNode') }">
										<a href="#/catalogues"><strong>Manage Catalogues</strong></a>
									</li>
									<!-- 								<li -->
									<!-- 									ng-class="{ active: isActive('/federableNodes') }"><a -->
									<!-- 									href="#federableNodes">Manage Federable Catalogues</a></li> -->
									<li ng-class="{ active: isActive('/configuration')}"><a
										href="#/configuration"><strong>Manage
												Configurations</strong></a></li>
									<li ng-show="dataletEnabled"
										ng-class="{ active: isActive('/dataletsManagement')}"><a
										href="#/dataletsManagement"><strong>Manage
												Datalets</strong></a></li>
									<li ng-class="{ active: isActive('/logs')}"><a
										href="#logs"><strong>View Logs</strong></a></li>
								</ul></li>

							<li class="loginBtns">
								<form ng-if="token==undefined" class="navbar-form"
									ng-controller="LoginCtrl">
									<button type="button" class="btn btn-default"
										ng-click="signIn()">Login</button>
								</form>

								<form ng-if="token!=undefined" class="navbar-form"
									ng-controller="LogoutCtrl">
									<button type="button" class="btn btn-default"
										ng-click="logout()">Logout</button>
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
				<div us-spinner="{radius:50, width:15, length: 25,color:'#44313f'}"
					spinner-key="spinner-1"></div>
			</div>
			<div class="col-md-10 col-md-offset-1" ng-controller="AlertCtrl">
				<div class="col-md-6 col-md-offset-3">
					<alert type='{{alertType}}' style="text-align:center"
						ng-show="alert" close="closeAlert()" dismiss-on-timeout="3000">
					{{textAlert}} </alert>
				</div>
			</div>

			<div class="col-md-10 col-md-offset-1">
				<div ng-view></div>
			</div>
		</div>
		<div class="push"></div>

	</div>
	<!--  END WRAPPER -->
	<!-- FOOTER -->
	<div class="footer" style="display: none">
		<div class="col-md-12 col-lg-12 col-sm-12">
			<div class="col-md-4 col-lg-4 col-sm-4"></div>
			<div class="col-md-4 col-lg-4 col-sm-4">
				<a href="https://www.eng.it/" target="_blank"><img
					class="img-responsive center-block footerImages small-margin"
					ng-src="images/logo_eng-100.jpg" /></a>
			</div>
			<div class="col-md-4 col-lg-4 col-sm-4"></div>
		</div>
		<div class="col-md-12 col-lg-12 col-sm-12 text-center copyright">
			<div class="col-md-4 col-lg-4 col-sm-4"></div>
			<div class="col-md-4 col-lg-4 col-sm-4">
				<p>Copyright &copy;Engineering 2018. - Idra version {{idraVersion}}</p>
			</div>
			<div class="col-md-4 col-lg-4 col-sm-4">
				<a class="pull-right" href="#/credits">Credits</a>
			</div>
		</div>
	</div>
	<!-- END FOOTER -->


	<script type="text/ng-template" id="ModalContent.html">
<div class="modal-header">
	<button type="button" class="close" aria-hidden="true" ng-click="cancel()">x</button>
    <h3 class="modal-title">{{title}}</h3>
</div>
<div class="modal-body row">
	<alert type='{{alertTypeModal}}' style="text-align:center"
		ng-show="alertModal" close="closeAlertModal()" dismiss-on-timeout="3000">
	{{textAlertModal}} </alert>

	<div class="col-md-6">
		<label>Available</label>
		<div class="well">
			<input class="search form-control" placeholder="Search" ng-model="no_selected" />
			<div style="overflow: auto; height: 300px;">
				<ul class="list-group list" >
					<li ng-repeat="item in nonSelectedItems | filter:no_selected as results_no" class='list-group-item' ng-click="addItem(item)" >
							<label class='name h4'>
							{{item}}   
							<button type="button" ng-if="federationLevel[item]!='LEVEL_0'" class="btn btn-success btn-xs" ng-click="addItem(item)">
							<i class="glyphicon glyphicon-arrow-right"></i>
							</button>
							</label>
					</li>
					<li class="list-group-item" ng-if="results_no.length == 0 ">
								<strong ng-if="selectedItems.length != allItems.length">No results found!</strong>
							<strong ng-if="selectedItems.length == allItems.length ">All selected</strong>
					</li>
				</ul>				
			</div>
		</div>
	</div>

	<div class="col-md-6">
		<label>Selected</label>
		<div class="well">
			<input class="search form-control" placeholder="Search" ng-model="selected" />
			<div style="overflow: auto; height: 300px;">
				<ul class="list-group list" >
					<li ng-repeat="item in selectedItems | filter:selected as results" class='list-group-item' ng-click="removeItem(item)">
							<label class='name h4'>
							<button type="button" class="btn btn-danger btn-xs" ng-click="removeItem(item)">
							<i class="glyphicon glyphicon-arrow-left"></i>
							</button>
							<span class="align-right">
							{{item}}
							</label>
					</li>
					<li class="list-group-item" ng-if="results.length == 0">
							<strong ng-if="nonSelectedItems.length != allItems.length" >No results found!</strong>
							<strong ng-if="nonSelectedItems.length == allItems.length" >All available</strong>
					</li>
				</ul>				
			</div>
		</div>		  
	</div>
	<label class='name checklbl'> <input type='checkbox' ng-click="selectAll()" ng-checked=" selectedItems.length == allItems.length " > Select All </label>
</div>

<div class="modal-footer">
    <button class="btn btn-default" type="button" ng-click="cancel()">Cancel</button>
	<button class="btn btn-primary" type="button" ng-click="ok()">OK</button>
</div>
</script>

	<script type="text/ng-template" id="ModalEurovoc.html">
<div class="modal-header">
	<button type="button" class="close" aria-hidden="true" ng-click="cancel()">x</button>
    <h3 class="modal-title">Eurovoc Languages</h3>
</div>
<div class="modal-body row">
	<alert type='{{alertTypeModal}}' style="text-align:center"
		ng-show="alertModal" close="closeAlertModal()" dismiss-on-timeout="3000">
	{{textAlertModal}} </alert>

	<div class="col-md-6">
		<label>Source Language</label>
		<div class="well">
			<input class="search form-control" placeholder="Search" ng-model="inputLanguage" />
			<div style="overflow: auto; height: 300px;">
				<md-radio-group ng-model="inputLan">
					<md-radio-button style="margin-bottom:0px">
							<label class='name h4'>
							None   
							</label>
					</md-radio-button>
					<md-radio-button ng-repeat="option in sourceLanguages | filter:inputLanguage as results_inputLanguage" ng-value="option" style="margin-bottom:0px">
							<label class='name h4'>
							{{option.text}}   
							</label>
					</md-radio-button>
						<label class='name h4' ng-if="results_inputLanguage.length == 0 ">
							<strong >No results found!</strong>
						</label>
				</md-radio-group>				
			</div>
		</div>
	</div>

	<div class="col-md-6">
		<label>Target Language</label>
		<div class="well">
			<input class="search form-control" placeholder="Search" ng-model="outputLanguage" />
				<ul class="list-group list" > 
				 <div style="overflow: auto; height: 300px;">
					<li ng-repeat="option in targetLanguages | filter:outputLanguage as results_outputLanguage" class='list-group-item' style="margin-bottom:-18px">
							<label class='name h4'>
							<md-checkbox ng-checked="exists(option, outputLan)" ng-click="toggle(option, outputLan)">{{option.text}}</md-checkbox>
							</label>
					</li>
						<li class="list-group-item" ng-if="results_outputLanguage.length == 0">
							<label class='name h4'>
								<strong >No results found!</strong>
							</label>
					</li>
					</div>
					<li class="list-group-item" style="margin-bottom:-18px" ng-if="outputLanguage==''">
						<label class='name h4'> <md-checkbox ng-checked="isChecked()"
                         ng-click="toggleAll()" > Select All</md-checkbox></label>
					</li>
				</ul>				
		</div>		  
	</div>
</div>

<div class="modal-footer">
    <button class="btn btn-default" type="button" ng-click="cancel()">Cancel</button>
	<button class="btn btn-primary" type="button" ng-click="ok()">OK</button>
</div>
</script>

	<script type="text/ng-template" id="ModalContentSingle.html">
<div class="modal-header">
	<button type="button" class="close" aria-hidden="true" ng-click="cancel()">x</button>
    <h3 class="modal-title">{{title}}</h3>
</div>
<div class="modal-body row">
	<alert type='{{alertTypeModal}}' style="text-align:center"
		ng-show="alertModal" close="closeAlertModal()" dismiss-on-timeout="3000">
	{{textAlertModal}} </alert>

	<div class="col-md-12 col-sm-12 col-lg-12">
		<div class="well">

	<input class="search form-control" placeholder="Search" ng-model="selected" />
			<div style="overflow: auto; height: 300px;">
				<ul class="list-group list" >
					<li ng-repeat="item in allItems | filter:selected as results" class='list-group-item' style='margin-bottom:-18px'>
							<label class='name h4'>
							<md-checkbox ng-checked="exists(item, selectedItems)" ng-click="toggle(item, selectedItems)">
               {{ item }}
              </md-checkbox>
							</label>
					</li>
					<li class="list-group-item" ng-if="results.length == 0">
						<label class='name h4'>
							<strong >No results found!</strong>
						</label>
					</li>
				</ul>				
			</div>

		</div>
	</div>

	<label class='name checklbl'> <md-checkbox ng-checked="isChecked()"
                         ng-click="toggleAll()" > Select All</md-checkbox> </label>
</div>

<div class="modal-footer">
    <button class="btn btn-default" type="button" ng-click="cancel()">Cancel</button>
	<button class="btn btn-primary" type="button" ng-click="ok()">OK</button>
</div>
</script>

	<script type="text/ng-template" id="ModalDistribution.html">
<div class="modal-header">
	<button type="button" class="close" aria-hidden="true" ng-click="cancel()">x</button>
    <h3 class="modal-title">Distribution Detail</h3>
</div>
<div class="modal-body row">

	<md-card md-theme-watch style="width:100%;margin: 0px 0px 8px 0px"> <md-card-title>
				<md-card-title-text > 
				<h2 style="margin: 0px 0px 0px 0px">{{distribution.title.value}}</h2> 
				</md-card-title-text> 
				</md-card-title> 
				<md-card-content>
				<p style="line-height: 1.75em;">
						<span ng-if="distribution.description.value!=''">{{distribution.description.value}}</span>
						<span ng-if="distribution.description.value==''">No description available for this distribution.</span>
				</p>
				<p>URL: <a href='{{distribution.downloadURL.value}}' target="_blank">{{distribution.downloadURL.value}}</a></p>
				<table class="table">
							<tr ng-show="distribution.format.value!=''">
								<td><span style="font-weight: bold;">Format:</span></td>
								<td>{{distribution.format.value}}</td>
							</tr>
							<tr ng-show="distribution.license.name.value!='' && distribution.license.uri!=''">
								<td><span style="font-weight: bold;">License:</span></td>
								<td>{{distribution.license.name.value}} <a ng-show="distribution.license.license.uri!=''"
									href="{{distribution.license.uri}}"
									class="btn-flat mdi-material-deep-orange" target="_blank"><i
									class="mdi-action-open-in-new" style="vertical-align: middle;"></i></a></td>
							</tr>
							<tr ng-show="distribution.releaseDate.value!=''">
								<td><span style="font-weight: bold;">Release Date:</span></td>
								<td>{{distribution.releaseDate.value | date}}</td>
							</tr>
							<tr ng-show="distribution.updateDate.value!=''">
								<td><span style="font-weight: bold;">Update Date:</span></td>
								<td>{{distribution.updateDate.value | date}}</td>
							</tr>
							<tr ng-show="distribution.language.length>1 || (distribution.language.length==1 && distribution.language[0]!='' ) ">
								<td><span style="font-weight: bold;">Language:</span></td>
								<td>
									<ul class="list-group list" style="margin:0px 0px 0px 0px;"> 
										<li class='list-group-item' ng-repeat="language in distribution.language track by $index">{{language}}</li>
									</ul>
								</td>
							</tr>
							<tr ng-show="distribution.linkedSchemas.length>1 || (distribution.linkedSchemas.length==1 && distribution.linkedSchemas[0]!='' ) ">
								<td><span style="font-weight: bold;">Linked Schemas:</span></td>
								<td>
									<ul class="list-group list" style="margin:0px 0px 0px 0px;"> 
										<li class='list-group-item' ng-repeat="link in distribution.linkedSchemas track by $index">{{link}}</li>
									</ul>
								</td>
							</tr>
							<tr ng-show="distribution.rights.value!=''">
								<td><span style="font-weight: bold;">Rights:</span></td>
								<td>{{distribution.rights.value}}</td>
							</tr>
							<tr ng-show="distribution.status.value!=''">
								<td><span style="font-weight: bold;">Status:</span></td>
								<td>{{distribution.status.value}}</td>
							</tr>
							<tr ng-show="distribution.mediaType.value!=''">
								<td><span style="font-weight: bold;">Media Type:</span></td>
								<td>{{distribution.mediaType.value}}</td>
							</tr>
							<tr ng-show="distribution.byteSize.value!='' && distribution.byteSize.value!='0'">
								<td><span style="font-weight: bold;">Size:</span></td>
								<td>{{distribution.byteSize.value}}</td>
							</tr>
							<tr ng-show="distribution.checksum.value!=''">
								<td><span style="font-weight: bold;">Checksum:</span></td>
								<td>{{distribution.checksum.value}}</td>
							</tr>
							<tr ng-show="distribution.documentation.length>1 || (distribution.documentation.length==1 && distribution.documentation[0]!='' ) ">
								<td><span style="font-weight: bold;">Documentation:</span></td>
								<td>
									<ul class="list-group list" style="margin:0px 0px 0px 0px;"> 
										<li class='list-group-item' ng-repeat="doc in distribution.documentation track by $index">{{doc}}</li>
									</ul>
								</td>
							</tr>
				</table>
				</md-card-content>			
	</md-card>
</div>

<div class="modal-footer">
    <button class="btn btn-default" type="button" ng-click="cancel()">Close</button>
</div>

</script>

	<script type="text/ng-template" id="ModalDataletAdmin.html">
<div class="modal-header">
	<button type="button" class="close" aria-hidden="true" ng-click="cancel()">x</button>
    <h3 class="modal-title">Datalet Detail</h3>
</div>
<div class="modal-body row">
	<div ng-bind-html="datalet.showHtml"></div>
</div>
<div class="modal-footer">
    <button class="btn btn-default" type="button" ng-click="cancel()">Close</button>
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
	<button type="button" class="btn btn-default" ng-click="yes()">{{opt1}}</button>
	<button type="button" class="btn btn-default" ng-click="keep()">{{opt2}}</button>
	<button type="button" class="btn btn-primary" ng-click="no()">No</button>
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
	<button type="button" class="btn btn-default" ng-click="discard()">{{opt1}}</button>
	<button type="button" class="btn btn-default" ng-click="save()">{{opt2}}</button>
	<button type="button" class="btn btn-primary" ng-click="no()">Cancel</button>
</div>
</script>
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
		src="bower_components/angular-ui-ace/ui-ace.js"></script>
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

	<script type="text/javascript" src="app.js"></script>
	<script type="text/javascript" src="catalogues/catalogues.services.js"></script>
	<script type="text/javascript" src="catalogues/catalogues.js"></script>
	<script type="text/javascript" src="catalogues/remote_catalogues.js"></script>
	<script type="text/javascript" src="catalogues/single_catalogue.js"></script>
	<script type="text/javascript" src="catalogues/view_catalogues.js"></script>

	<script type="text/javascript"
		src="catalogues/defaultDatasets.services.js"></script>
	<script type="text/javascript" src="catalogues/editDumpCtrl.js"></script>

	<script type="text/javascript" src="accounts/accounts.js"></script>
	<script type="text/javascript" src="sparql/sparql.js"></script>
	<script type="text/javascript" src="js/mode-sparql.js"></script>
	<script type="text/javascript" src="configuration/configuration.js"></script>

	<script type="text/javascript"
		src="metadata/defaultparameter.services.js"></script>
	<script type="text/javascript" src="metadata/metadata.services.js"></script>
	<script type="text/javascript" src="metadata/metadata.js"></script>
	<script type="text/javascript" src="metadata/datasetResults.js"></script>
	<script type="text/javascript" src="metadata/datasetDetail.js"></script>
	<script type="text/javascript" src="metadata/datesModIssued.js"></script>
	<script type="text/javascript" src="metadata/tagCloudController.js"></script>
	<script type="text/javascript" src="datalets/datalet.services.js"></script>
	<script type="text/javascript" src="datalets/dataletClient.js"></script>
	<script type="text/javascript" src="datalets/dataletAdmin.js"></script>
	<script type="text/javascript" src="logPage/logging.js"></script>
	<script type="text/javascript" src="credits/credits.js"></script>

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
