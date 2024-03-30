/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.controller;

import static org.example.controller.AbstractController.failure;
import static org.example.controller.AbstractController.success;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.annoation.APIEndpoint;
import org.example.annoation.Controller;
import org.example.annoation.authorization.RolesAllowed;
import org.example.common.AuthorizationRole;
import org.example.common.CustomValidator;
import org.example.common.HTTPMethod;
import org.example.dto.NodeDto;
import org.example.dto.ResultDTO;
import org.example.service.ProviderService;
import org.example.state.NodeState;
import spark.Request;
import spark.Response;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@RolesAllowed(AuthorizationRole.ROLE_PROVIDER)
@Controller
public class ProviderController extends AbstractController {

    @NonNull private CustomValidator validator;

    @NonNull private Gson gson;

    @NonNull private ProviderService providerService;

    // add new node
    @APIEndpoint(method = HTTPMethod.POST, path = "/api/v1/provider/node")
    public ResultDTO<UUID> addNewNode(Request request, Response response) throws SQLException {
        NodeDto nodeDto = gson.fromJson(request.body(), NodeDto.class);

        // validate
        List<String> errors = validator.validate(request, nodeDto);
        if (!errors.isEmpty()) {
            return failure(errors);
        }

        NodeState nodeState = providerService.addNewNode(nodeDto);
        switch (nodeState.getState()) {
            case SUCCESS:
                return success(nodeState.getUuid());
            case ADDRESS_EXISTS:
                return failure(getLocalization().getString(request, "provider.node.address.exists"));
            default:
                throw new IllegalArgumentException();
        }
    }

    // update node
    @APIEndpoint(method = HTTPMethod.PUT, path = "/api/v1/provider/node/:id")
    public ResultDTO<Void> updateNode(Request request, Response response) throws SQLException {
        UUID id = UUID.fromString(request.params(":id"));
        NodeDto nodeDto = gson.fromJson(request.body(), NodeDto.class);

        // validate
        List<String> errors = validator.validate(request, nodeDto);
        if (!errors.isEmpty()) {
            return failure(errors);
        }

        NodeState nodeState = providerService.updateNode(id, nodeDto);
        switch (nodeState.getState()) {
            case SUCCESS:
                return success();
            case ADDRESS_EXISTS:
                return failure(getLocalization().getString(request, "provider.node.address.exists"));
            default:
                throw new IllegalArgumentException();
        }
    }
}
