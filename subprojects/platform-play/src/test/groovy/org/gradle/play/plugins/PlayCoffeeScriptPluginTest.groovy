/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.play.plugins

import org.gradle.api.Action
import org.gradle.api.file.SourceDirectorySet
import org.gradle.language.base.FunctionalSourceSet
import org.gradle.language.coffeescript.CoffeeScriptSourceSet
import org.gradle.model.collection.CollectionBuilder
import org.gradle.platform.base.internal.ComponentSpecInternal
import org.gradle.play.PlayApplicationSpec
import spock.lang.Specification

class PlayCoffeeScriptPluginTest extends Specification {
    def "adds coffeescript source sets to play components" () {
        def plugin = new PlayCoffeeScriptPlugin()
        def components = Mock(CollectionBuilder)
        def sources = Mock(FunctionalSourceSet)
        def sourceSet = Mock(CoffeeScriptSourceSet)
        def sourceDirSet = Mock(SourceDirectorySet)

        when:
        def playApp = Stub(PlayAppInternal) {
            getName() >> "play"
            getSources() >> sources
        }
        _ * components.beforeEach(_) >> { Action a -> a.execute(playApp) }

        and:
        plugin.createCoffeeScriptSourceSets(components)

        then:
        1 * sources.create("coffeeScriptAssets", CoffeeScriptSourceSet) >> sourceSet
        2 * sourceSet.getSource() >> sourceDirSet
        1 * sourceDirSet.srcDir("app/assets")
        1 * sourceDirSet.include("**/*.coffee")
    }

    interface PlayAppInternal extends PlayApplicationSpec, ComponentSpecInternal {}
}
