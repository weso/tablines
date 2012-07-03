class UrlMappings {

	static mappings = {
		"/"(controller: "project", action: "list")
		"/project/create"(controller: "project", action: "create")
		name globalSparql: "/sparql"(controller: "project", action: "sparql")
		name globalTapinos: "/tapinos"(controller: "project", action: "tapinos")
		name projectSpecific: "/project/$id/$action?"(controller: "project")
/*		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}*/
		
		"/upload/$action/$id?"(controller: "upload")

        "/ws/chart"(uri:"/tapinos/chart.dispatch")
        "/ws/dimensions"(uri:"/tapinos/dimensions.dispatch")
        "/ws/mapArea"(uri:"/tapinos/mapArea.dispatch")
        "/ws/sanityChecker"(uri:"/tapinos/sanityChecker.dispatch")

		"500"(view:'/error')
	}
}
