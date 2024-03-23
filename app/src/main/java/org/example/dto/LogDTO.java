/* (C) 2024 */
package org.example.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import org.example.entity.Log;

@Data
public class LogDTO {

    private UUID id;

    private Log.Type type;

    private Map<String, Object> data;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;
}
