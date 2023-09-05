//package com.pettalk.argumentresolver;
//
//import org.springframework.core.MethodParameter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//@Component
//public class LoginUserIdArgumentResolver implements HandlerMethodArgumentResolver {
//    private final MemberService memberService;
//
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//        parameter.getParameterAnnotations();
//        return true;
//    }
//
//    @Override
//    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 사용자 인증 정보
//        // 익명이면 -1L 리턴
//        if(principal == "anonymousUser"){
//            return -1L;
//        }
//        Member member = memberService.findMemberByEmail(principal.toString());
//        return member.getMemberId();
//    }
//}