package org.scientificcenter.service;

import org.scientificcenter.model.Authority;

public interface AuthorityService {

    Authority findByName(String name);
}
