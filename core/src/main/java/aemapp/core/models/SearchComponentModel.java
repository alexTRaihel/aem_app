package aemapp.core.models;

import aemapp.core.SearchType;
import aemapp.core.service.SearchService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Servlet;
import java.util.List;
import java.util.Set;

@Model(adaptables = Resource.class)
public class SearchComponentModel {

    @Inject
    private SearchService searchService;

    @Inject
    private String text;

    @Inject
    @Default(values = "\\")
    @Optional
    @Named("path")
    private String path;

    @Inject
    @Default(intValues={1,2,3,4})
    @Optional
    private int[] integers;

    @Inject
    @Optional
    private List<Resource> addresses;

    @Inject
    @Optional
    private List<Servlet> servlets;

    @Inject
    @Default(values = "QUERY_BUILDER")
    private String type;

    private Set<String> paths;

    @PostConstruct
    private void init() {
        this.paths = this.searchService.findByTerm(this.text, this.path, SearchType.valueOf(this.type));
    }

    public Set<String> getPaths() {
        return this.paths;
    }
}
