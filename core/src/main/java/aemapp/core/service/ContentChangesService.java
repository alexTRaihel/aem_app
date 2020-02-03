package aemapp.core.service;

import com.day.cq.wcm.api.Page;

public interface ContentChangesService {

    void createNewPageVersion(Page prevPage);
}
