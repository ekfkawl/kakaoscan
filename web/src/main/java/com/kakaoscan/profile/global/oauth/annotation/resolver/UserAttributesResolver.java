package com.kakaoscan.profile.global.oauth.annotation.resolver;

import com.kakaoscan.profile.domain.dto.UserDTO;
import com.kakaoscan.profile.global.oauth.annotation.UserAttributes;
import com.kakaoscan.profile.global.session.instance.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

import static com.kakaoscan.profile.global.session.instance.SessionManager.SESSION_FORMAT;
import static com.kakaoscan.profile.utils.HttpRequestUtils.getCookie;

@RequiredArgsConstructor
@Component
@Log4j2
public class UserAttributesResolver implements HandlerMethodArgumentResolver {

    private final SessionManager sessionManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(UserAttributes.class) != null;
        boolean isUserClass = UserDTO.class.equals(parameter.getParameterType());

        // 파라미터에 @UserAttributes 어노테이션 + dto 클래스 타입 체크
        return isLoginUserAnnotation && isUserClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        try {
            assert request != null;
        } catch (AssertionError e) {
            log.error("request instance null: {}", e.getMessage(), e);
        }
        return getCookie(request)
                .map(cookie -> sessionManager.getValue(String.format(SESSION_FORMAT, cookie.getValue())))
                .orElse(null);
    }
}