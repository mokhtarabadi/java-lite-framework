/* (C) 2023 */
package org.example.di;

import dagger.Component;
import javax.inject.Singleton;
import org.example.App;
import org.example.util.Utility;

@Singleton
@Component(
        modules = {
            AppModule.class,
            DaoModule.class,
        })
public interface AppComponent {
    App getApp();

    // inject to utility class
    void inject(Utility utility);
}
