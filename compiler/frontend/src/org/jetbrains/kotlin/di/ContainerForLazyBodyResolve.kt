/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.frontend.di

import com.intellij.openapi.project.Project
import org.jetbrains.container.StorageComponentContainer
import org.jetbrains.kotlin.context.GlobalContext
import org.jetbrains.kotlin.context.ModuleContext
import org.jetbrains.kotlin.context.withModule
import org.jetbrains.kotlin.context.withProject
import org.jetbrains.kotlin.di.createContainer
import org.jetbrains.kotlin.di.useImpl
import org.jetbrains.kotlin.di.useInstance
import org.jetbrains.kotlin.resolve.AdditionalCheckerProvider
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.LazyTopDownAnalyzerForTopLevel
import org.jetbrains.kotlin.resolve.lazy.KotlinCodeAnalyzer
import org.jetbrains.kotlin.types.DynamicTypesSettings

public fun createContainerForLazyBodyResolve(
        moduleContext: ModuleContext, kotlinCodeAnalyzer: KotlinCodeAnalyzer,
        bindingTrace: BindingTrace, additionalCheckerProvider: AdditionalCheckerProvider,
        dynamicTypesSettings: DynamicTypesSettings
): StorageComponentContainer = createContainer("BodyResolve") { //TODO: name
    configureModule(moduleContext, bindingTrace, additionalCheckerProvider)

    useInstance(kotlinCodeAnalyzer)
    useInstance(kotlinCodeAnalyzer.getScopeProvider())
    useInstance(dynamicTypesSettings)
    useImpl<LazyTopDownAnalyzerForTopLevel>()
}