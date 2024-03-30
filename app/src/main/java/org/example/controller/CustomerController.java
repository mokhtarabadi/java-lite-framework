/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.controller;

import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.annoation.Controller;
import org.example.annoation.authorization.RolesAllowed;
import org.example.common.AuthorizationRole;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@RolesAllowed(AuthorizationRole.ROLE_CUSTOMER)
@Controller
public class CustomerController extends AbstractController {}
