modules = {
    application {
        resource url:'js/application.js'
	}
	codemirror {
	    dependsOn 'jquery'
		resource url:'js/codemirror/codemirror.js'
		resource url:'js/codemirror/sparql.js'
		resource url:'js/codemirror/tabels.js'
		resource url:'js/codemirror/javahint.js'
		resource url:'js/codemirror/simplehint.js'
	}
	trace {
	   dependsOn 'jquery'
	   dependsOn 'modernizr'
	   dependsOn 'jquery-ui'
	   resource url:'http://imakewebthings.com/deck.js/core/deck.core.js'
       resource url:'http://imakewebthings.com/deck.js/extensions/status/deck.status.js'
       resource url:'http://imakewebthings.com/deck.js/extensions/navigation/deck.navigation.js'
	}
	dynatree {
		dependsOn 'jquery'
		dependsOn 'jquery-ui'
		resource url:'http://wwwendt.de/tech/dynatree/src/skin/ui.dynatree.css'
		resource url:'http://wwwendt.de/tech/dynatree/src/jquery.dynatree.js'
		resource url:'http://wwwendt.de/tech/dynatree/jquery/jquery.cookie.js'
		resource url:'css/dynatree.custom.css'
	}

	googlemaps {
		resource url:'http://maps.google.com/maps/api/js?sensor=true', attrs:[type:'js']
	}
	
	infobubble{
		resource url:'http://google-maps-utility-library-v3.googlecode.com/svn/trunk/infobubble/src/infobubble.js'
		resource url:'css/infobubble.custom.css'
	}

    'tapinos-js' {
        dependsOn 'jquery,fancybox,protovis,jquery-ui,jquery-svg,jquery-tipsy,jquery-datatables,jquery-geturlparam,jquery-tooltip'
//        defaultBundle 'ui'
        resource url:'js/tapinos/jquery.tapinosCommon.js'
        resource url:'js/tapinos/jquery.tapinosChart.js'
        resource url:'js/tapinos/jquery.tapinosCombos.js'
        resource url:'js/tapinos/jquery.tapinosMap.js'
        resource url:'js/tapinos/jquery.undermaps.tree.js'
        resource url:'js/tapinos/jquery.undermaps.gmaps.js'
        resource url:'js/tapinos/jquery.tapinosPermLinkBuilder.js'
        resource url:'js/tapinos/jquery.tapinosSelectSearch.js'
        resource url:'js/tapinos/jquery.tapinosTable.js'
        resource url:'css/tapinos.css'
        resource url:'css/undermaps.css'
    }
    'contactable'{
    	 dependsOn 'jquery'
    	 resource url:'js/jquery.contactable.min.js'
    }

}