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

package uk.q3c.krail.persist.jpa.option

import com.google.inject.Inject
import org.apache.onami.persist.PersistenceService
import org.apache.onami.persist.UnitOfWork
import spock.guice.UseModules
import spock.lang.Specification
import uk.q3c.krail.core.data.DataModule
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.option.OptionKey
import uk.q3c.krail.core.persist.cache.option.OptionCacheKey
import uk.q3c.krail.core.persist.common.option.OptionDao
import uk.q3c.krail.core.user.profile.RankOption
import uk.q3c.krail.core.user.profile.UserHierarchy
import uk.q3c.krail.core.view.component.LocaleContainer
import uk.q3c.krail.persist.jpa.common.Jpa1

@UseModules([TestOptionJpaModule, DataModule])
class DefaultJpaOptionDao__IntegrationTest extends Specification {

    @Inject
    @Jpa1
    PersistenceService persistenceService;

    @Inject
    @Jpa1
    OptionDao dao;


    @Inject
    @Jpa1
    UnitOfWork unitOfWork;


    UserHierarchy userHierarchy = Mock();

    OptionKey<String> optionKey = new OptionKey<String>("x", LocaleContainer.class, LabelKey.Yes);
    OptionKey<String> optionKey1 = new OptionKey<String>("x", LocaleContainer.class, LabelKey.No);
    OptionKey<String> optionKey2 = new OptionKey<String>("x", LocaleContainer.class, LabelKey.Active_Source);

    def setup() {
        userHierarchy.persistenceName() >> "simple"
        userHierarchy.rankName(0) >> "ds"
        userHierarchy.rankName(1) >> "ds"
        persistenceService.start();
        unitOfWork.begin();
    }

    def cleanup() {
        unitOfWork.end();
        persistenceService.stop();
    }


    def "setting up"() {
        expect:
        persistenceService != null
        persistenceService.isRunning() == true

        unitOfWork != null
        unitOfWork.isActive() == true

        dao != null


    }




    def "write with the same cache key multiple times and ensure there is only the latest value present in persistence"() {
        given:
        OptionCacheKey cacheKey = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKey);

        when:
        dao.write(cacheKey, Optional.of("4"));
        dao.write(cacheKey, Optional.of("5"));
        dao.write(cacheKey, Optional.of("6"));
        JpaOptionEntity result = dao.find(cacheKey)

        then:
        dao.count() == 1
        result != null
        result.getValue() == "6"

    }

    def "write multiple keys and delete one"() {
        given:
        OptionCacheKey cacheKey = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKey);
        OptionCacheKey cacheKey1 = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKey1);
        OptionCacheKey cacheKey2 = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKey2);

        when:

        dao.write(cacheKey, Optional.of("3"))
        dao.write(cacheKey1, Optional.of("4"))
        dao.write(cacheKey2, Optional.of("5"))

        then:
        dao.count() == 3

        when:

        dao.deleteValue(cacheKey)
        JpaOptionEntity result1 = dao.find(cacheKey1)
        JpaOptionEntity result2 = dao.find(cacheKey2)

        then:

        dao.count() == 2
        result1.getValue() == "4"
        result2.getValue() == "5"

    }


}