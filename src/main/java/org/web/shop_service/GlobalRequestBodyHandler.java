package org.web.shop_service;

import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.web.shop_service.utils.VersionedRequest;
import org.web.shop_service.utils.exceptions.HttpHeaderIfMatchRequiredException;

import java.lang.reflect.Type;

@RestControllerAdvice
public class GlobalRequestBodyHandler extends RequestBodyAdviceAdapter {
    @Override
    public boolean supports(MethodParameter methodParameter,
                            Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        final var clazz = ResolvableType.forType(targetType).toClass();
        return VersionedRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public Object afterBodyRead(Object body,
                                HttpInputMessage inputMessage,
                                MethodParameter parameter,
                                Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        final var headers = inputMessage.getHeaders();
        if (body instanceof VersionedRequest request) {
            final String ifMatch = headers.getFirst(HttpHeaders.IF_MATCH);
            if(ifMatch == null || ifMatch.isBlank())
                throw new HttpHeaderIfMatchRequiredException();

            final Long version = Long.parseLong(
                    ifMatch.replace("\"", ""));
            request.setVersion(version);
        }
        return body;
    }
}
