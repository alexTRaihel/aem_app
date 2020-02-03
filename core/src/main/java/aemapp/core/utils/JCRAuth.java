package aemapp.core.utils;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true, service = JCRAuth.class)
public class JCRAuth {

    private static final String SERVICE_USER_NAME = "appusercore";

    @Reference
    private SlingRepository repository;

    @Reference
    private ResourceResolverFactory resolverFactory;

    public Session getSession() throws RepositoryException{
        return this.repository.loginService(SERVICE_USER_NAME, null);
    }

    public ResourceResolver getResourceResolver() throws LoginException{

        Map<String, Object> param = new HashMap();
        param.put("sling.service.subservice", SERVICE_USER_NAME);
        return this.resolverFactory.getServiceResourceResolver(param);
    }
}
