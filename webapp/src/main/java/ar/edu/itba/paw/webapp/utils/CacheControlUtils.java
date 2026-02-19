package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

public class CacheControlUtils {

    public static final int MAX_CACHE_TIME = 15724800; // 6 meses
    public static final int MAX_CONDITIONAL_CACHE_TIME = 86400; // 1 dia


    public static Response setMaxTimeToResponse(final Response.ResponseBuilder responseBuilder) {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_CACHE_TIME);
        return responseBuilder.cacheControl(cacheControl).build();
    }

    public static Response setEtagToResponse(final Response.ResponseBuilder responseBuilder, Request request, int resourceHash) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_CONDITIONAL_CACHE_TIME);
        EntityTag etag = new EntityTag(String.valueOf(resourceHash));
        Response.ResponseBuilder resourceChangedResponseBuilder = request.evaluatePreconditions(etag);
        if (resourceChangedResponseBuilder != null) {
            return resourceChangedResponseBuilder.cacheControl(cacheControl).tag(etag).build();
        }
        return responseBuilder.status(Response.Status.NOT_MODIFIED).cacheControl(cacheControl).tag(etag).build();
    }
}
