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

package org.jetbrains.kotlin.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.JetNameReferenceExpression;
import org.jetbrains.kotlin.psi.stubs.KotlinNameReferenceExpressionStub;
import org.jetbrains.kotlin.psi.stubs.elements.JetStubElementTypes;

public class KotlinNameReferenceExpressionStubImpl extends KotlinStubBaseImpl<JetNameReferenceExpression> implements
                                                                                                       KotlinNameReferenceExpressionStub {
    @NotNull
    private final StringRef referencedName;

    public KotlinNameReferenceExpressionStubImpl(StubElement parent, @NotNull StringRef referencedName) {
        super(parent, JetStubElementTypes.REFERENCE_EXPRESSION);
        this.referencedName = referencedName;
    }

    @NotNull
    @Override
    public String getReferencedName() {
        return referencedName.getString();
    }
}
