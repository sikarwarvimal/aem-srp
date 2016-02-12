package com.adobe.ugc.srp.repositories

import com.adobe.ugc.srp.entities.CustomerEntity
import com.adobe.ugc.srp.exceptions.RepositoryException

interface CustomerRepository {
    public CustomerEntity find(final String emailId) throws RepositoryException
    public void saveOrUpdate(final CustomerEntity customerEntity) throws RepositoryException
    public void delete(final String emailId) throws RepositoryException
}
