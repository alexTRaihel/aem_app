package aemapp.core.listeners;

import aemapp.core.service.ContentChangesService;
import aemapp.core.utils.JCRAuth;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import java.util.Iterator;

@Component(service = EventHandler.class,
        immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Module 7 AEM courses",
                EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/*",
                EventConstants.EVENT_FILTER + "=(path=/content/ht/en/*/jcr:content)"
        })
public class PageChangesListener implements EventHandler {

    @Reference
    private ContentChangesService contentChangesService;

    @Reference
    private JCRAuth jcrAuth;

    @Reference
    private EventAdmin eventAdmin;

    @Override
    public void handleEvent(Event event) {

        PageEvent pageEvent = PageEvent.fromEvent(event);
        Iterator<PageModification> modifications = pageEvent.getModifications();

        while (modifications.hasNext()){
           PageModification pageModification = modifications.next();
           pageModification.getType();
        }

        try {
            ResourceResolver resourceResolver = jcrAuth.getResourceResolver();
            String path = (String) event.getProperty("path");
            Resource resource = resourceResolver.getResource(path);
            if (resource != null && resource.getParent() != null) {
                Page page = resource.getParent().adaptTo(Page.class);
                if (page != null) {
                    if (page.getProperties().containsKey("jcr:description")) {
                        contentChangesService.createNewPageVersion(page);
                    }
                }
            }
        } catch (LoginException ex) {

        }
    }
}
