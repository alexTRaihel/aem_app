package aemapp.core.workflows;

import aemapp.core.utils.JCRAuth;
import com.day.cq.wcm.api.Page;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

@Component(
        service = WorkflowProcess.class,
        property = {
                "process.label" + "=Training module 8 workflow processor"
        }
)
public class TestingWorkflowProcessor implements WorkflowProcess {

    @Reference
    private JCRAuth jcrAuth;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {

        String resourcePath = workItem.getWorkflowData().getPayload().toString();

        try {
            ResourceResolver resourceResolver = jcrAuth.getResourceResolver();
            Resource resource = resourceResolver.getResource(resourcePath);
            String movePath = getMovePath(resource);

            UserManager userManager = resourceResolver.adaptTo(UserManager.class);

            if (movePath.isEmpty() || resourcePath.equals(movePath)) {
                return;
            }

            Workspace workspace = getWorkspace(resourceResolver);
            //workspace.move(resourcePath, getMovePath(resource));

        } catch (LoginException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMovePath(Resource resource) throws RepositoryException {
        Page page = resource.adaptTo(Page.class);
        String path = (String) page.getProperties().get("pathToMove");
        Node node = resource.adaptTo(Node.class);
        return path + "/" + node.getName();
    }

    private Workspace getWorkspace(ResourceResolver resourceResolver) {
        Session session = resourceResolver.adaptTo(Session.class);
        return session.getWorkspace();
    }
}
