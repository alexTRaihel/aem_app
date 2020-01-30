package aemapp.core.service.impl;

import aemapp.core.SearchType;
import aemapp.core.service.SearchService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.*;

@Component(immediate = true, service = SearchService.class)
@Designate(ocd = SearchServiceConfiguration.class)
public class SearchServiceImpl implements SearchService {

    @Reference
    private SlingRepository repository;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private QueryBuilder builder;

    private String sortOrder;

    @Activate
    @Modified
    protected synchronized void configure(SearchServiceConfiguration config) {
        if (config != null) {
            this.sortOrder = config.defaultSortOrder();
        }
    }

    @Override
    public Set<String> findByTerm(String text, String path, SearchType type) {
        return type == SearchType.QUERY_BUILDER ? this.fundByQueryBuilder(text, path) : this.findByQueryManager(text, path);
    }

    private Set<String> findByQueryManager(String text, String path) {
        QueryManager queryManager = null;
        Set<String> resultSet = new HashSet();
        Session session = this.getSession();
        if (session == null) {
            return resultSet;
        } else {
            try {
                queryManager = session.getWorkspace().getQueryManager();
                String sqlStatement = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + path + "]) and CONTAINS(s.*, '" + text + "') order by s.[" + this.sortOrder + "]";
                Query query = queryManager.createQuery(sqlStatement, "JCR-SQL2");
                QueryResult queryResult = query.execute();
                NodeIterator nodeIterator = queryResult.getNodes();

                while (nodeIterator.hasNext()) {
                    Node node = nodeIterator.nextNode();
                    resultSet.add(node.getPath());
                }

                session.logout();
            } catch (RepositoryException var11) {
                var11.printStackTrace();
            }

            return resultSet;
        }
    }

    private Set<String> fundByQueryBuilder(String text, String path) {

        Set<String> resultSet = new HashSet();
        Map<String, String> map = new HashMap();

        map.put("path", path);
        map.put("type", "cq:Page");
        map.put("group.p.or", "true");
        map.put("group.1_fulltext", text);
        map.put("group.1_fulltext.relPath", "jcr:content");
        map.put("group.2_fulltext", text);
        map.put("group.2_fulltext.relPath", "jcr:content/@cq:tags");

        ResourceResolver resolver = this.getResourceResolver();
        QueryBuilder builder = (QueryBuilder) resolver.adaptTo(QueryBuilder.class);

        if (builder != null) {
            com.day.cq.search.Query query = builder.createQuery(PredicateGroup.create(map), (Session) resolver.adaptTo(Session.class));
            SearchResult result = query.getResult();
            Iterator var9 = result.getHits().iterator();

            while (var9.hasNext()) {
                Hit hit = (Hit) var9.next();

                try {
                    resultSet.add(hit.getPath());
                } catch (RepositoryException var12) {
                    var12.printStackTrace();
                }
            }
        }

        return resultSet;
    }

    private Session getSession() {

        try {
            return this.repository.loginService("appusercore", (String) null);
        } catch (RepositoryException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private ResourceResolver getResourceResolver() {

        Map<String, Object> param = new HashMap();

        param.put("sling.service.subservice", "appusercore");
        ResourceResolver resourceResolver = null;

        try {
            this.getSession();
            resourceResolver = this.resolverFactory.getServiceResourceResolver(param);
        } catch (LoginException var4) {
            var4.printStackTrace();
        }

        return resourceResolver;
    }
}

@ObjectClassDefinition(
        name = "Search Service Configuration",
        description = "Training task search configuration"
)
@interface SearchServiceConfiguration {
    @AttributeDefinition(
            name = "DefaultSortOrder",
            description = "DefaultSortOrder",
            required = false,
            cardinality = 0
    )
    String defaultSortOrder() default "tittle";
}
