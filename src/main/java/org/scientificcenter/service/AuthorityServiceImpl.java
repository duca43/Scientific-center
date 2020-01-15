package org.scientificcenter.service;

import org.scientificcenter.model.Authority;
import org.scientificcenter.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityServiceImpl(final AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public Authority findByName(final String name) {
        Assert.notNull(name, "Authority name can't be null!");
        return this.authorityRepository.findByName(name);
    }
}