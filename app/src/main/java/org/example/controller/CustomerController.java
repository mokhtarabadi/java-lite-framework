package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.annoation.Controller;
import org.example.annoation.authorization.RolesAllowed;
import org.example.common.AuthorizationRole;

import javax.inject.Inject;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@RolesAllowed(AuthorizationRole.ROLE_CUSTOMER)
@Controller
public class CustomerController extends AbstractController {

}
