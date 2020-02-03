package aemapp.core.service.impl;

import aemapp.core.service.ContentChangesService;
import aemapp.core.utils.JCRAuth;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.*;
import java.util.Date;

@Component(immediate = true, service = ContentChangesService.class)
public class ContentChangesServiceImpl implements ContentChangesService {

    @Reference
    private JCRAuth jcrAuth;

    @Override
    public void createNewPageVersion(Page prevPage) {
        try {
            ResourceResolver resourceResolver = jcrAuth.getResourceResolver();
            Session session = resourceResolver.adaptTo(Session.class);

            if (session != null && prevPage != null) {
                Page newPage = createPage(resourceResolver, prevPage);
                Node pageNode = newPage.adaptTo(Node.class);
                copyContentNode(pageNode, prevPage, resourceResolver);
                session.save();
                session.refresh(true);
                session.logout();
            }

        } catch (LoginException ex) {

        } catch (WCMException ex) {

        } catch (Exception ex) {

        }
    }

    private Page createPage(ResourceResolver resourceResolver, Page prevPage) throws WCMException {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        return pageManager.create(prevPage.getPath(), getPageName(prevPage), prevPage.getTemplate().getPath(), prevPage.getTitle());
    }

    private String getPageName(Page prevPage) {
        return prevPage.getName() + "_" + new Date().getTime();
    }

    private void copyContentNode(Node pageNode, Page prevPage, ResourceResolver resourceResolver) throws RepositoryException {
        Node node = prevPage.getContentResource().adaptTo(Node.class);

        Resource resource = resourceResolver.getResource(prevPage.getPath() + "/jcr:content/root/responsivegrid");
        Session session = resourceResolver.adaptTo(Session.class);
        Workspace workspace = session.getWorkspace();

        NodeIterator nodeIterator = resource.adaptTo(Node.class).getNodes();

        while (nodeIterator.hasNext()) {
            Node n = nodeIterator.nextNode();
            workspace.copy(n.getPath(), pageNode.getPath() + "/jcr:content/root/responsivegrid/" + n.getName());
        }
    }
}
