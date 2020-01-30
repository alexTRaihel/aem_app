package aemapp.core.service;

import aemapp.core.SearchType;

import java.util.Set;

public interface SearchService {
    Set<String> findByTerm(String text, String path, SearchType type);
}
