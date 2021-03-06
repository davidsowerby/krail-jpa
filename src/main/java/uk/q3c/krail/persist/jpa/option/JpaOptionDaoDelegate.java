/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.persist.jpa.option;

import org.apache.onami.persist.Transactional;
import uk.q3c.krail.option.persist.OptionCacheKey;
import uk.q3c.krail.option.persist.OptionDaoDelegate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by David Sowerby on 13/04/15.
 */
public interface JpaOptionDaoDelegate extends OptionDaoDelegate {

    @Transactional
    @Nullable
    <V> JpaOptionEntity find(@Nonnull OptionCacheKey<V> cacheKey);
}
