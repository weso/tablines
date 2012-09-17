<html>
    <head>
        <title>Tabels projects</title> <!-- FIXME: i18n -->
        <meta name="layout" content="main" />
    </head>
    <body>
        <h2>Tabels projects</h2> <!-- FIXME: i18n -->
        
    	<g:if test="${!projects}">
    	    <div class="messagebox"><p><g:message code="msg.why.nothing.in.project.list.msg"/></p></div>
    	</g:if>
        
        <div class="createProjectBox" >
        <h2><g:message code="msg.create.project.button"/></h2>
       		   <g:form action="create" method="post" class="projectListLink">
    		        <g:textField name="newProjectId" value="${message(code:'msg.new.project.name')}"/>
    		        <r:script>
	    		        $('#newProjectId').blur(function() {if (this.value == '') {this.value = '${message(code:"msg.new.project.name")}';}});
	    		        $('#newProjectId').focus(function() {if (this.value == '${message(code:"msg.new.project.name")}') {this.value = '';}});
    		        </r:script>
    		        <g:submitButton class="AddProjectButton" name="createProject" value="${message(code:'msg.create.project.button')}" />
    		    </g:form>
        </div>
        <ul class="projectList">
        
            <h2><g:message code="msg.project.list"/></h2>
            <g:each in="${projects}" var="project">
                <li>
                    <g:link action="index" id="${project}" class="projectListLink">${project}</g:link>
                </li>
            </g:each>
        </ul>
        

        <h2>Access your data</h2> <!-- FIMXE: i18 --> 
        
		<p class="sparqlLink"><g:message code="msg.endpoint.sparql.link"/> <g:link mapping="globalSparql"><g:createLink mapping="globalSparql" absolute="true"/></g:link></p>
		
		<p class="pubbyLink"><span class="stars">★★★★★</span> <a href="${resource(dir:'pubby')}"><g:message code="msg.linked.data.link"/></a></p>
		
	</body>
</html>
