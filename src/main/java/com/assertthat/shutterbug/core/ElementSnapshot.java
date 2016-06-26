/*
 * Copyright 2016 Glib Briia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.assertthat.shutterbug.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class ElementSnapshot extends Snapshot {

    ElementSnapshot(WebDriver driver, WebElement element) {
        this.driver = driver;
    }

    @Override
    protected ElementSnapshot self() {
        return this;
    }
}
