package ar.edu.itba.paw.webapp.utils;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;


@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class DefaultCacheFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (requestContext.getMethod().equalsIgnoreCase("GET")) {
            // Only apply default if the controller/resource didn't set a Cache-Control header
            if (!responseContext.getHeaders().containsKey(HttpHeaders.CACHE_CONTROL)) {
                CacheControl cacheControl = new CacheControl();
                cacheControl.setNoCache(true);
                cacheControl.setNoStore(true);
                cacheControl.setMustRevalidate(true);
                cacheControl.setPrivate(true); // Directs intermediate proxies to not cache this
                responseContext.getHeaders().add(HttpHeaders.CACHE_CONTROL, cacheControl.toString());
            }
        }
    }
}
