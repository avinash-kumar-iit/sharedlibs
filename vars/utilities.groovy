def setPipelineProperties(List parametersList, config){
    propertiesList=[]
    propertiesList.add(buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '25')))
    
    String disableConcurrent=config["disableConcurrentBuilds"]
    if (disableConcurrent == null || disableConcurrent.toBoolean() ) {
        propertiesList.add(disableConcurrentBuilds())
    }
    //to simplyfy the builds for rebuilding. no custom UI... by default enabled
    String customRegistry=config["customRegistry"]
    if (customRegistry == null || customRegistry.toBoolean() ) {
        
        propertiesList.add(parameters(parametersList))
        //GS : add properties only in case of default pipeline. else this will not allowed any custom properties added 
        // later. this was bad design and limitation at jenkins
        properties(propertiesList)
    }
