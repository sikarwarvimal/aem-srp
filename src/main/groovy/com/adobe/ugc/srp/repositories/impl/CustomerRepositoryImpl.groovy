package com.adobe.ugc.srp.repositories.impl

import com.adobe.cq.social.srp.SocialResourceProvider
import com.adobe.ugc.srp.entities.CustomerEntity
import com.adobe.ugc.srp.exceptions.RepositoryException
import com.adobe.ugc.srp.repositories.CustomerRepository
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.resource.ModifiableValueMap
import org.apache.sling.api.resource.PersistenceException
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.api.resource.ValueMap

@Slf4j
@Service
@CompileStatic
@Component(immediate=true)
public class CustomerRepositoryImpl extends StorageResourceProviderTemplate implements CustomerRepository {

    final String CUSTOMERS_PATH = "${BASE_PATH}/ugc/srp/customers"

    @Reference
    private ResourceResolverFactory resourceResolverFactory

    @Override
    public CustomerEntity find(final String emailId) throws RepositoryException{
        CustomerEntity customerEntity
        ResourceResolver resourceResolver = getResourceResolver(resourceResolverFactory)
        try {
            // Select logic
            customerEntity = fetchCustomerEntity(resourceResolver, "${CUSTOMERS_PATH}/${emailId}")
        } finally {
            resourceResolver.close()
        }
        return customerEntity
    }

    /**
     *
     * @param resourceResolver
     * @param path
     * @return
     */
    private CustomerEntity fetchCustomerEntity(final ResourceResolver resourceResolver, final String path) {
        final Resource resource = getResource(resourceResolver, path)
        CustomerEntity customerEntity = null
        if(resource) {
            final ValueMap valueMap = resource.valueMap
            if (valueMap) {
                customerEntity = new CustomerEntity()
                customerEntity.populate(valueMap)
            } else {
                log.error("Unable to find Customer properties: ${path}")
            }
        } else {
            log.error("Unable to find Customer: ${path}")
        }
        return customerEntity
    }

    @Override
    public void saveOrUpdate(final CustomerEntity customerEntity) throws RepositoryException{
        ResourceResolver resourceResolver = getResourceResolver(resourceResolverFactory)
        try {
            try {
                final String path = "${CUSTOMERS_PATH}/${customerEntity.email}"
                final SocialResourceProvider socialResourceProvider = getSocialResourceProvider(resourceResolver)
                if(socialResourceProvider == null){
                    throw new RepositoryException("Unable to Create Customer, SocialResourceProvider == null: ${path}")
                }
                final Resource resource = socialResourceProvider.getResource(resourceResolver, path)
                if(resource) {
                    // Update Logic
                    final ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap)
                    customerEntity.update(valueMap)
                    resourceResolver.commit()
                } else {
                    // Create Logic
                    socialResourceProvider.create(resourceResolver, path, customerEntity.create())
                    resourceResolver.commit()
                }
            }
            catch (PersistenceException e) {
                throw new RepositoryException("Unable to Save or Update Customer: ${CUSTOMERS_PATH}/${customerEntity.email}", e)
            }
        } finally {
            resourceResolver.close()
        }
    }

    @Override
    public void delete(final String emailId) throws RepositoryException {
        ResourceResolver resourceResolver = getResourceResolver(resourceResolverFactory)
        try {
            try {
                // Delete Logic
                final SocialResourceProvider socialResourceProvider = getSocialResourceProvider(resourceResolver)
                if(socialResourceProvider) {
                    socialResourceProvider.delete(resourceResolver, "${CUSTOMERS_PATH}/${emailId}")
                    resourceResolver.commit()
                } else {
                    throw new RepositoryException("Unable to Delete Customer, SocialResourceProvider == null: ${CUSTOMERS_PATH}/${emailId}")
                }
            }
            catch (PersistenceException e) {
                throw new RepositoryException("Unable to Delete Customer: ${CUSTOMERS_PATH}/${emailId}", e)
            }
        } finally {
            resourceResolver.close()
        }
    }
}
