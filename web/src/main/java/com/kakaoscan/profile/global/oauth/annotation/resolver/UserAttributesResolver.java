package com.kakaoscan.profile.global.oauth.annotation.resolver;

import com.kakaoscan.profile.global.oauth.OAuthAttributes;
import com.kakaoscan.profile.global.oauth.annotation.UserAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Component
public class UserAttributesResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(UserAttributes.class) != null;
        boolean isUserClass = OAuthAttributes.class.equals(parameter.getParameterType());

        // 파라미터에 @UserAttributes 어노테이션 + OAuthAttributes 클래스 타입 체크
        return isLoginUserAnnotation && isUserClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return httpSession.getAttribute("user"); // 세션에 저장 된 user 파라미터에 전달
    }
}