/* (C) 2023 */
package org.example.dto;

import java.util.List;
import lombok.Data;

@Data
public class ResultDTO<T> {
    private boolean success;
    private T data;
    private List<String> errors;
}
