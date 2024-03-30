/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.common;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public enum OrderStatus {
    @SerializedName("pending")
    PENDING("pending"),
    @SerializedName("active")
    ACTIVE("active"),
    @SerializedName("reserved")
    RESERVED("reserved"),
    @SerializedName("cancelled")
    CANCELLED("cancelled");

    private String vaule;

    OrderStatus(String value) {
        this.vaule = value;
    }

    public static OrderStatus fromValue(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.vaule.equals(value)) {
                return status;
            }
        }
        return null;
    }
}
