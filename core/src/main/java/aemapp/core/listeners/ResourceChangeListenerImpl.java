package aemapp.core.listeners;

import aemapp.core.utils.JCRAuth;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Date;
import java.util.List;

@Component(service = ResourceChangeListener.class,
        immediate = true,
        property = {
                ResourceChangeListener.PATHS + "=/content/ht",
                ResourceChangeListener.CHANGES + "=REMOVED"
        })
public class ResourceChangeListenerImpl implements ResourceChangeListener {

    private String logPath = "/var/log/removedProperties";

    @Reference
    private JCRAuth jcrAuth;

    private ResourceResolver resourceResolver;

    @Override
    public void onChange(List<ResourceChange> list) {

        try {
            resourceResolver = jcrAuth.getResourceResolver();
            for (ResourceChange resourceChange : list) {
                saveRemovedProps(resourceChange.getPath());
            }
        } catch (LoginException | RepositoryException e) {
            e.printStackTrace();
        }
    }

    private void saveRemovedProps(String resourcePath) throws RepositoryException {

//        Resource resource = resourceResolver.getResource(resourcePath);
//        Node node = resource.adaptTo(Node.class);
//        String type = node.getProperty("sling:resourceType").toString();
//
//        Resource resource1 = resourceResolver.getResource(logPath);
//        Node node1 = resource1.adaptTo(Node.class);
//
//        Node savedNode = node1.addNode("removed");
//
//        savedNode.setProperty("type", type);
//        savedNode.setProperty("path", resourcePath);

    }
}
