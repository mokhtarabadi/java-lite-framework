/* (C) 2023 */
package org.example.filter.authorization;

import javax.inject.Inject;
import lombok.NoArgsConstructor;
import org.example.exception.AccessDenied;
import spark.Filter;
import spark.Request;
import spark.Response;

@NoArgsConstructor(onConstructor = @__(@Inject))
public class DenyAllFilter implements Filter {
    @Override
    public void handle(Request request, Response response) throws Exception {
        throw new AccessDenied();
    }
}
