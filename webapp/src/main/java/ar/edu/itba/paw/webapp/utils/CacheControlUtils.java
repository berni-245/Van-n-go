package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

public class CacheControlUtils {

    private static final int LONG_MAX_CACHE_TIME = 15724800; // 6 meses
    private static final int SHORT_MAX_CACHE_TIME = 86400; // 1 dia
    private static final int MAX_CONDITIONAL_CACHE_TIME = 3600; // 1 hora

    public static Response setMaxTimeToResponse(final Response.ResponseBuilder responseBuilder, int maxCacheTime) {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(maxCacheTime);
        return responseBuilder.cacheControl(cacheControl).build();
    }

    public static Response setLongMaxTimeToResponse(final Response.ResponseBuilder responseBuilder) {
        return setMaxTimeToResponse(responseBuilder, LONG_MAX_CACHE_TIME);
    }

    public static Response setShortMaxTimeToResponse(final Response.ResponseBuilder responseBuilder) {
        return setMaxTimeToResponse(responseBuilder, SHORT_MAX_CACHE_TIME);
    }

    public static Response setNoCacheResponse(final Response.ResponseBuilder responseBuilder) {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
        cacheControl.setMustRevalidate(true);
        return responseBuilder.cacheControl(cacheControl).build();
    }

    public static Response setEtagToResponse(final Response.ResponseBuilder responseBuilder, Request request, int resourceHash) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_CONDITIONAL_CACHE_TIME);
        EntityTag etag = new EntityTag(String.valueOf(resourceHash));
        Response.ResponseBuilder evaluatedResponseBuilder = request.evaluatePreconditions(etag);
        if (evaluatedResponseBuilder == null) {
            // CASO 1: No hay headers condicionales o el contenido cambió. Devolvemos el contenido con el ETag y Cache-Control.
            return responseBuilder.cacheControl(cacheControl).tag(etag).build();
        }
        // CASO 2: El contenido no cambió (304)
        return evaluatedResponseBuilder.cacheControl(cacheControl).tag(etag).build();
    }

    public static Response setPrivateEtagToResponse(final Response.ResponseBuilder responseBuilder, Request request, int resourceHash) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_CONDITIONAL_CACHE_TIME);
        cacheControl.setPrivate(true);
        EntityTag etag = new EntityTag(String.valueOf(resourceHash));
        Response.ResponseBuilder evaluatedResponseBuilder = request.evaluatePreconditions(etag);
        if (evaluatedResponseBuilder == null) {
            // CASO 1: No hay headers condicionales o el contenido cambió.
            return responseBuilder.cacheControl(cacheControl).tag(etag).build();
        }
        // CASO 2: El contenido no cambió (304)
        return evaluatedResponseBuilder.cacheControl(cacheControl).tag(etag).build();
    }
}
