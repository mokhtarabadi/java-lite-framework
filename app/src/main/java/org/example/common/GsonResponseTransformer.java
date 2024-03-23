/* (C) 2023 */
package org.example.common;

import com.google.gson.Gson;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spark.ResponseTransformer;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class GsonResponseTransformer implements ResponseTransformer {

    @NonNull private Gson gson;

    @Override
    public String render(Object model) throws Exception {
        return gson.toJson(model);
    }
}
