/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.language.nativeplatform.internal;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.specs.Spec;
import org.gradle.language.base.LanguageSourceSet;
import org.gradle.language.base.internal.LanguageSourceSetInternal;
import org.gradle.language.base.internal.registry.LanguageTransform;
import org.gradle.language.nativeplatform.DependentSourceSet;
import org.gradle.language.nativeplatform.tasks.AbstractNativeCompileTask;
import org.gradle.nativeplatform.ObjectFile;
import org.gradle.nativeplatform.internal.NativeBinarySpecInternal;
import org.gradle.nativeplatform.tasks.PrefixHeaderFileGenerateTask;

import java.io.File;
import java.util.Set;
import java.util.concurrent.Callable;

public class PCHCompileTaskConfig extends CompileTaskConfig {
    public PCHCompileTaskConfig(LanguageTransform<? extends LanguageSourceSet, ObjectFile> languageTransform, Class<? extends DefaultTask> taskType) {
        super(languageTransform, taskType);
    }

    @Override
    protected void configureCompileTask(AbstractNativeCompileTask task, final NativeBinarySpecInternal binary, final LanguageSourceSetInternal languageSourceSet) {
        // Note that the sourceSet is the sourceSet this pre-compiled header will be used with - it's not an
        // input sourceSet to the compile task.
        final DependentSourceSet sourceSet = (DependentSourceSet) languageSourceSet;

        task.setDescription(String.format("Compiles a pre-compiled header for the %s of %s", sourceSet, binary));

        // Add the source of the source set to the include paths to resolve any headers that may be in source directories
        task.includes(new Callable<Set<File>>() {
            public Set<File> call() throws Exception {
                return sourceSet.getSource().getSrcDirs();
            }
        });

        final Project project = task.getProject();
        task.setPrefixHeaderFile(sourceSet.getPrefixHeaderFile());
        task.setPreCompiledHeader(sourceSet.getPreCompiledHeader());
        task.source(sourceSet.getPrefixHeaderFile());

        task.setObjectFileDir(project.file(String.valueOf(project.getBuildDir()) + "/objs/" + binary.getNamingScheme().getOutputDirectoryBase() + "/" + languageSourceSet.getFullName() + "PreCompiledHeader"));

        task.dependsOn(project.getTasks().withType(PrefixHeaderFileGenerateTask.class).matching(new Spec<PrefixHeaderFileGenerateTask>() {
            @Override
            public boolean isSatisfiedBy(PrefixHeaderFileGenerateTask prefixHeaderFileGenerateTask) {
                return prefixHeaderFileGenerateTask.getPrefixHeaderFile().equals(sourceSet.getPrefixHeaderFile());
            }
        }));
    }
}
