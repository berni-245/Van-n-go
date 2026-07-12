package ar.edu.itba.paw.webapp.utils;


import org.glassfish.jersey.server.validation.ValidationConfig;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Locale;

@Provider
public class ValidationContextResolver implements ContextResolver<ValidationConfig> {

    @Context
    private HttpHeaders headers;

    @Override
    public ValidationConfig getContext(Class<?> type) {
        final ValidationConfig config = new ValidationConfig();
        config.messageInterpolator(new LocaleContextHolderMessageInterpolator(headers));
        return config;
    }

    private record LocaleContextHolderMessageInterpolator(HttpHeaders headers) implements MessageInterpolator {

        private static final ValidatorFactory factory = Validation
                .byDefaultProvider()
                .configure()
                .messageInterpolator(
                        new ResourceBundleMessageInterpolator(
                                new PlatformResourceBundleLocator("i18n/ValidationMessages")
                        )
                ).clockProvider(() -> Clock.system(ZoneId.of("America/Argentina/Buenos_Aires")))
                .buildValidatorFactory();

        private static final MessageInterpolator defaultInterpolator = factory.getMessageInterpolator();

        @Override
        public String interpolate(String messageTemplate, Context context) {
            return defaultInterpolator.interpolate(messageTemplate, context, getLocale());
        }


        @Override
        public String interpolate(String messageTemplate, Context context, Locale locale) {
            return defaultInterpolator.interpolate(messageTemplate, context, getLocale());
        }

        private Locale getLocale() {
            Locale toReturn;
            try {
                toReturn = headers.getAcceptableLanguages().getFirst();
            } catch (Exception e) {
                toReturn = Locale.getDefault();
            }
            return toReturn;
        }
    }
}
