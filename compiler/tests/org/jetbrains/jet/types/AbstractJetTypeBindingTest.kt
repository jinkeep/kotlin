/*
 * Copyright 2010-2014 JetBrains s.r.o.
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

package org.jetbrains.jet.types

import org.jetbrains.jet.JetLiteFixture
import org.jetbrains.jet.ConfigurationKind
import org.jetbrains.jet.JetTestUtils
import java.io.File
import org.junit.Assert.*
import org.jetbrains.jet.lang.resolve.BindingTraceContext
import org.jetbrains.jet.lang.resolve.typeBinding.*
import org.jetbrains.jet.lang.types.JetType
import org.jetbrains.jet.renderer.DescriptorRenderer
import org.jetbrains.jet.lang.descriptors.TypeParameterDescriptor
import org.jetbrains.jet.lang.resolve.lazy.JvmResolveUtil
import org.jetbrains.jet.lang.psi.JetCallableDeclaration
import org.jetbrains.jet.utils.Printer
import org.jetbrains.jet.test.util.trimIndent
import org.jetbrains.jet.lang.psi.JetFile
import org.jetbrains.jet.JetTestUtils.*
import org.jetbrains.jet.lang.psi.JetVariableDeclaration

abstract class AbstractJetTypeBindingTest : JetLiteFixture() {
    override fun createEnvironment() = createEnvironmentWithMockJdk(ConfigurationKind.ALL)

    protected fun doTest(path: String) {
        val testFile = File(path)
        val testKtFile = JetTestUtils.loadJetFile(getProject(), testFile)

        val analyzeResult = JvmResolveUtil.analyzeFilesWithJavaIntegration(getProject(), listOf(testKtFile), { true })

        val testDeclaration = testKtFile.getDeclarations().last!! as JetCallableDeclaration

        val typeBinding = testDeclaration.createTypeBindingForReturnType(analyzeResult.bindingContext)

        JetTestUtils.assertEqualsToFile(
                testFile,
                StringBuilder {
                    append(removeLastComment(testKtFile))
                    append("/*\n")

                    MyPrinter(this).print(typeBinding)

                    append("*/")
                }.toString()
        )
    }

    private fun removeLastComment(file: JetFile): String {
        val fileText = file.getText()
        val lastIndex = fileText.indexOf("/*")
        return if (lastIndex > 0) {
            fileText.substring(0, lastIndex)
        }
        else fileText
    }

    private class MyPrinter(out: StringBuilder) : Printer(out) {
        private fun JetType.render() = DescriptorRenderer.SHORT_NAMES_IN_TYPES.renderType(this)
        private fun TypeParameterDescriptor?.render() = if (this == null) "null" else DescriptorRenderer.SHORT_NAMES_IN_TYPES.render(this)

        fun print(argument: TypeArgumentBinding<*>?): MyPrinter {
            if (argument == null) {
                println("null")
                return this
            }
            println("typeParameter: ${argument.typeParameterDescriptor.render()}")

            val projection = argument.typeProjection.getProjectionKind().label.let {
                if (it.isNotEmpty())
                    "$it "
                else
                    ""
            }

            println("typeProjection: ${projection}${argument.typeProjection.getType().render()}")
            print(argument.typeBinding)
            return this
        }

        fun print(binding: TypeBinding<*>?): MyPrinter {
            if (binding == null) {
                println("null")
                return this
            }

            println("psi: ${binding.psiElement.getText()}")
            println("type: ${binding.jetType.render()}")

            printCollection(binding.getArgumentBindings()) {
                print(it)
            }
            return this
        }

        private fun <T> printCollection(list: Iterable<T>, f: MyPrinter.(T) -> Unit) {
            pushIndent()
            var first = true
            for (element in list) {
                if (first) first = false
                else println()

                f(element)
            }
            popIndent()
        }

        override fun toString(): String = out.toString()
    }
}