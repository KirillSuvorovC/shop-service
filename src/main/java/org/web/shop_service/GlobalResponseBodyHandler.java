package org.web.shop_service;

import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.web.shop_service.utils.VersionedResponse;

@RestControllerAdvice
public class GlobalResponseBodyHandler implements ResponseBodyAdvice<VersionedResponse> {
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        final Class<?> bodyType = ResolvableType.forMethodParameter(returnType)
                .getGeneric(0)
                .toClass();

        return VersionedResponse.class.isAssignableFrom(bodyType);
    }

    @Override
    public VersionedResponse beforeBodyWrite(VersionedResponse body,
                                            MethodParameter returnType,
                                            MediaType selectedContentType,
                                            Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                            ServerHttpRequest request,
                                            ServerHttpResponse response) {
        if(body == null) return null;
        final String eTag = String.valueOf(body.getVersion());
        response.getHeaders().setETag(eTag);
        return body;
    }
}
