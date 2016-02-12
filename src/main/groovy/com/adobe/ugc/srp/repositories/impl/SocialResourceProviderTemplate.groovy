package com.adobe.ugc.srp.repositories.impl

import com.adobe.cq.social.srp.SocialResourceProvider
import com.adobe.cq.social.ugcbase.SocialUtils
import groovy.transform.CompileStatic
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory

import javax.annotation.Nonnull

@CompileStatic
public abstract class SocialResourceProviderTemplate {
    protected final static String BASE_PATH = "/content/usergenerated/asi/mongo"

    /**
     *
     * @param resourceResolver
     * @return
     */
    protected SocialResourceProvider getSocialResourceProvider(final ResourceResolver resourceResolver) {
        //TODO: Replace the SocialUtils API once non-deprecated available though the Adobe doc suggest deprecated interface
        //Ref Link: https://docs.adobe.com/docs/en/aem/6-1/develop/communities/scf/srp.html#Method to Access UGC
        final SocialUtils socialUtils = resourceResolver.adaptTo(SocialUtils)
        if (socialUtils) {
            final Resource resource = resourceResolver.getResource(BASE_PATH)
            if(resource) {
                final SocialResourceProvider socialResourceProvider = socialUtils.getConfiguredProvider(resource)
                socialResourceProvider.setConfig(socialUtils.getDefaultStorageConfig())
                return socialResourceProvider
            }
        }
    }

    /**
     *
     * @param resourceResolver
     * @param path
     * @return
     */
    protected Resource getResource(final ResourceResolver resourceResolver, String path) {
        final SocialResourceProvider socialResourceProvider = getSocialResourceProvider(resourceResolver)
        return socialResourceProvider.getResource(resourceResolver, path)
    }

    protected ResourceResolver getResourceResolver(final ResourceResolverFactory resolverFactory) {
        final def authInfo = new HashMap<String, Object>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, "omegaAuthService");
        resolverFactory.getServiceResourceResolver(authInfo)
    }
}
