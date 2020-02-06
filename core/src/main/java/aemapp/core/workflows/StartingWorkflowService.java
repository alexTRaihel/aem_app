package aemapp.core.workflows;

import aemapp.core.utils.JCRAuth;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.Workflow;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;

@Component(immediate = true, service = StartingWorkflowService.class)
public class StartingWorkflowService {

    private static final String MODEL_PATH = "/var/workflow/models/module_8";

    @Reference
    private WorkflowService workflowService;

    @Reference
    private JCRAuth jcrAuth;

    public void startWorkflow(String resourcePath) {

        try {
            WorkflowSession workflowSession = workflowService.getWorkflowSession(jcrAuth.getSession());
            WorkflowModel workflowModel = workflowSession.getModel("module_8");

            WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", resourcePath);

            Workflow workflow = workflowSession.startWorkflow(workflowModel, workflowData);

        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (WorkflowException e) {
            e.printStackTrace();
        }
    }
}
