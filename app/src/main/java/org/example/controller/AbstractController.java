/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.controller;

import java.util.*;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.example.common.Localization;
import org.example.dto.ResultDTO;
import org.jetbrains.annotations.NotNull;
import spark.ModelAndView;

@Slf4j
public abstract class AbstractController {

    @Inject
    @Getter(AccessLevel.PROTECTED)
    protected Localization localization;

    @SafeVarargs
    public final ModelAndView makeView(String viewName, @NotNull Pair<String, Object>... data) {
        Map<String, Object> model = new HashMap<>();
        for (Pair<String, Object> datum : data) {
            model.put(datum.getKey(), datum.getValue());
        }

        return new ModelAndView(model, viewName);
    }

    public static <T> ResultDTO<T> success(T data) {
        ResultDTO<T> resultDTO = new ResultDTO<>();
        resultDTO.setSuccess(true);
        resultDTO.setData(data);
        return resultDTO;
    }

    public static <T> ResultDTO<T> success() {
        ResultDTO<T> resultDTO = new ResultDTO<>();
        resultDTO.setSuccess(true);
        return resultDTO;
    }

    public static <T> ResultDTO<T> failure(List<String> errors) {
        ResultDTO<T> resultDTO = new ResultDTO<>();
        resultDTO.setSuccess(false);
        resultDTO.setErrors(errors);
        return resultDTO;
    }

    public static <T> ResultDTO<T> failure() {
        ResultDTO<T> resultDTO = new ResultDTO<>();
        resultDTO.setSuccess(false);
        return resultDTO;
    }

    public static <T> ResultDTO<T> failure(String error) {
        ResultDTO<T> resultDTO = new ResultDTO<>();
        resultDTO.setSuccess(false);
        resultDTO.setErrors(Collections.singletonList(error));
        return resultDTO;
    }
}
