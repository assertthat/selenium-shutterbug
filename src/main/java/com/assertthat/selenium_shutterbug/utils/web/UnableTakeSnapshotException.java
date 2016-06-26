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

package com.assertthat.selenium_shutterbug.utils.web;

import org.openqa.selenium.WebDriverException;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class UnableTakeSnapshotException extends WebDriverException {

    public UnableTakeSnapshotException() {
        super();
    }

    public UnableTakeSnapshotException(String message) {
        super(message);
    }

    public UnableTakeSnapshotException(Throwable cause) {
        super(cause);
    }

    public UnableTakeSnapshotException(String message, Throwable cause) {
        super(message, cause);
    }
}
