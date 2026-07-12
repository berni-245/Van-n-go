package ar.edu.itba.paw.webapp.utils;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;


@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class VaryHeaderFilter implements ContainerResponseFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {

        if (!requestContext.getMethod().equalsIgnoreCase("GET")) {
            return;
        }

        Method method = resourceInfo.getResourceMethod();
        Class<?> resourceClass = resourceInfo.getResourceClass();

        Vary vary = null;
        if (method != null && method.isAnnotationPresent(Vary.class)) {
            vary = method.getAnnotation(Vary.class);
        } else if (resourceClass != null && resourceClass.isAnnotationPresent(Vary.class)) {
            vary = resourceClass.getAnnotation(Vary.class);
        }

        if (vary != null) {
            for (String header : vary.value()) {
                responseContext.getHeaders().add(HttpHeaders.VARY, header);
            }
        }
    }
}
