package com.digit9.reports.application.security.service;

import com.digit9.commons.core.spi.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("permissionEvaluator")
public class PermissionEvaluator {

    @Autowired
    private AuthService authService;

}
