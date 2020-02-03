package aemapp.core.listeners;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(service = EventHandler.class,
        immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Module 7 AEM courses",
                EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/*",
                EventConstants.EVENT_FILTER + "=(path=/content/ht)"
        })
public class PropertyRemoveListenerImpl implements EventHandler {

    @Override
    public void handleEvent(Event event) {

    }
}
