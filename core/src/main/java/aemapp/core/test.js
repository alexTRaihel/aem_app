function check(){

     var workflowData = workItem.getWorkflowData();

     if (workflowData.getPayloadType() == "JCR_PATH") {

        var path = workflowData.getPayload().toString();
        var jcrsession = workflowSession.getSession();
        var node = jcrsession.getNode(path);

        return !node.hasProperty("jcr:content");
    }
}