package aemapp.core.listeners;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import java.util.HashMap;
import java.util.Map;

@Component(service = EventListener.class, immediate = true)
public class PropertyRemoveListenerImpl implements EventListener {

    @Reference
    private ResourceResolverFactory resolverFactory;

    private Session session;

    @Override
    public void onEvent(EventIterator eventIterator) {
        if (eventIterator.hasNext()) {
            eventIterator.nextEvent();
        }
    }

    @Activate
    private void activate(ComponentContext context) {

        try {
            Map<String, Object> params = new HashMap<>();

            params.put(ResourceResolverFactory.SUBSERVICE, "eventingService");
            ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(params);

            session = resourceResolver.adaptTo(Session.class);

            session.getWorkspace().getObservationManager().addEventListener(this,
                    Event.PROPERTY_REMOVED | Event.NODE_REMOVED, "/content/ht", true, null, null, false);

        } catch (Exception ex) {

        }
    }

    @Deactivate
    private void deactivate() {
        if (session != null) {
            session.logout();
        }
    }
}
