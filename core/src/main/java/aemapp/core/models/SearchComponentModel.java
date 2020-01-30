package aemapp.core.models;

import aemapp.core.SearchType;
import aemapp.core.service.SearchService;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.Set;

@Model(adaptables = Resource.class)
public class SearchComponentModel {

    @Inject
    private SearchService searchService;

    @ValueMapValue(
            name = "text",
            injectionStrategy = InjectionStrategy.OPTIONAL
    )
    @Default(
            values = {"Text"}
    )
    private String text;

    @ValueMapValue(
            name = "path",
            injectionStrategy = InjectionStrategy.OPTIONAL
    )
    @Default(
            values = {"Path"}
    )
    private String path;

    @ValueMapValue(
            name = "type",
            injectionStrategy = InjectionStrategy.OPTIONAL
    )
    @Default(
            values = {"QUERY_BUILDER"}
    )
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
