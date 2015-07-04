/*
 * Copyright 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.roodie.model.network;

import retrofit.RetrofitError;
import retrofit.client.Response;

public enum NetworkError {

    NOT_FOUND_TMDB, NETWORK_ERROR, UNKNOWN;


    public static NetworkError from(final RetrofitError error) {
        if (error == null) {
            return UNKNOWN;
        }

        final Response response = error.getResponse();

        if (response == null) {
            return UNKNOWN;
        }

        if (error.isNetworkError()) {
            return NETWORK_ERROR;
        }

        final int statusCode = response.getStatus();

         if (statusCode == 404) {
                    return NOT_FOUND_TMDB;

        }

        return UNKNOWN;
    }
}