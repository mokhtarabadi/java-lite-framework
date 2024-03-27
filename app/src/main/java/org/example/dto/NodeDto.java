package org.example.dto;


import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class NodeDto {

    @NotEmpty(message = "{provider.node.addressEmpty}")
    private String address;

}
