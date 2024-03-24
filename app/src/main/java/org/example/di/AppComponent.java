/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
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
            CacheModule.class,
        })
public interface AppComponent {
    App getApp();

    // inject to utility class
    void inject(Utility utility);
}
