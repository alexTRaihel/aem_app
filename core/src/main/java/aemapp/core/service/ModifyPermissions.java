package aemapp.core.service;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.security.Privilege;
import java.util.NoSuchElementException;


@Component
public class ModifyPermissions {

    private static final String CONTENT_SITE_HT_FR = "/content/ht/fr";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private SlingRepository slingRepository;

    @Activate
    protected void activate() {
        logger.info("ModifyPermissions activated");
        modifyPermissions();
    }

    private void modifyPermissions() {
        Session adminSession = null;

        try {
            adminSession = slingRepository.loginAdministrative(null);
            UserManager userManager = ((org.apache.jackrabbit.api.JackrabbitSession) adminSession).getUserManager();
            AccessControlManager accessControlManager = adminSession.getAccessControlManager();

            Authorizable denyAccess = userManager.getAuthorizable("deny-access");
            AccessControlPolicyIterator accessControlPolicyIterator = accessControlManager.getApplicablePolicies(CONTENT_SITE_HT_FR);

            AccessControlList acl;

            try {
                acl = (AccessControlList)
                        accessControlPolicyIterator.nextAccessControlPolicy();
            } catch (NoSuchElementException nse) {
                acl = (AccessControlList)
                        accessControlManager.getPolicies(CONTENT_SITE_HT_FR)[0];
            }

            Privilege[] privileges =
                    {accessControlManager.privilegeFromName(Privilege.JCR_READ)};
            acl.addAccessControlEntry(denyAccess.getPrincipal(), privileges);
            accessControlManager.setPolicy(CONTENT_SITE_HT_FR, acl);
            adminSession.save();

        } catch (RepositoryException e) {
            logger.error("ModifyPermissions exception", e);
        } finally {
            if (adminSession != null) {
                adminSession.logout();
            }
        }
    }
}
