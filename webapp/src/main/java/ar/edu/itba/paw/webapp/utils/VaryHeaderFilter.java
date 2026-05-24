package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

/**
 * Filter to add the 'Vary' header to all API responses.
 * This ensures that intermediary proxies (caches, CDNs) include Authorization, 
 * Accept, and Accept-Language in their cache keys, preventing them from 
 * serving cached data meant for one user or representation to another.
 */
@Provider
public class VaryHeaderFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        // We only care about GET requests as they are the primary targets for caching.
        if (requestContext.getMethod().equalsIgnoreCase("GET")) {
            responseContext.getHeaders().add(HttpHeaders.VARY, HttpHeaders.AUTHORIZATION);
            responseContext.getHeaders().add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
            responseContext.getHeaders().add(HttpHeaders.VARY, HttpHeaders.ACCEPT_LANGUAGE);
        }
    }
}
