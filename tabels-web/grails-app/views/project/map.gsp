<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <!--<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">-->
    <title>Tabels Map</title>
    <r:require modules="dynatree, maplab"/>

    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"></script>
    <meta name="layout" content="main" />	   
</head>

<body>
	<h2><g:message code="msg.map.view.title"/></h2>
	<p class="backLink"><g:link action="index" id="${params.id}"><g:message code="msg.back.to.project.link"/></g:link></p>

    <div id="container">
        <div id="leftContainer">
            <div id="visualize">
                <div class="centerButton"><a id="visulize-button">Visualize</a></div>
            </div>
            
            <div id="layerMap">
                 <h3>Maps Layers</h3>
                <div id="tree" name="selNodes">
                </div>
            </div>
            
            <!-- <div id="layerLegend">
                <div class="introLegend">   
                    <a href="javascript:void(0);" onclick="javascript:mapLabLayers($('#legend'))">Legend</a>
                </div>
                <div id="legend"><ul><li>No hay nada seleccionado</li></ul></div>
            </div>
            -->

        </div>
        <div id="rightContainer">
            <div id="mapGoogle">
            </div>
        </div>
    </div>
    <!-- Generating -->
	<div id="generating-dialog" title="Thank you for using Tabels">
		<p>Tabels is generating the map</p> 
	</div> 
    <r:script>
        //Tree must be defined before map
        $("#tree").mapLabTree({
            treeWs: "<g:resource dir='ws' file='tree'/>",
            mapRef: "mapGoogle",
            legendRef: "legend",
            endpoint: "${endpoint}",
            namedgraph: "${namedgraph}"
        });
        
        $("#mapGoogle").mapLabMap({
            mapAreaWs: "<g:resource dir='ws' file='mapArea'/>",
            mapZoom : 2,
            treeRef: "tree",
            endpoint: "${endpoint}",
            namedgraph: "${namedgraph}"
        });
        
        window.onload = mapLabLayers($('#tree'));
        
        $('#generating-dialog').dialog({
	        autoOpen: false,
			modal: true,
			resizable: false,
			draggable: false,
		    show: {
		        complete: function() { // callback function
		            mapLabReload($('#tree'), $('#mapGoogle')); 
		            $('#generating-dialog').dialog('close'); // FIXME
		            return true;	            
		        }
		    }
		});
		
        $(function() {
			$( "#visulize-button" ).button();
			$( "#visulize-button" ).click(function() { 
		        $('#generating-dialog').dialog('open');
				return true; 
			});

		});
		

    </r:script>
</body>
</html>