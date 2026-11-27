package ar.edu.itba.paw.webapp.utils;

import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

public class UnconditionalCacheFilter extends OncePerRequestFilter {

    public static final int MAX_CACHE_TIME = 15724800; // 6 meses

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);
        // Como esta configurado en el web.xml, este filtro solo se aplica a los archivos del front. Para el cache de recursos de la api se maneja en los controllers correspondientes
        //Recordar de Protos, los navegadores solo cachean respuestas a peticiones GET, por lo que no tiene sentido agregar el header a otros métodos HTTP
        if (HttpMethod.GET.matches(request.getMethod())) {
            response.setHeader(HttpHeaders.CACHE_CONTROL, String.format("public, max-age=%d, immutable", MAX_CACHE_TIME));
        }

    }
}
