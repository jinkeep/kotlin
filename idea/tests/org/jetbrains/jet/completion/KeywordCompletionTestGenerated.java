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

package org.jetbrains.jet.completion;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.jet.JUnit3RunnerWithInners;
import org.jetbrains.jet.JetTestUtils;
import org.jetbrains.jet.test.InnerTestClasses;
import org.jetbrains.jet.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.jet.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("idea/testData/completion/keywords")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class KeywordCompletionTestGenerated extends AbstractKeywordCompletionTest {
    @TestMetadata("AfterClassKeyword.kt")
    public void testAfterClassKeyword() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/AfterClassKeyword.kt");
        doTest(fileName);
    }

    @TestMetadata("AfterClassProperty.kt")
    public void testAfterClassProperty() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/AfterClassProperty.kt");
        doTest(fileName);
    }

    @TestMetadata("AfterClasses.kt")
    public void testAfterClasses() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/AfterClasses.kt");
        doTest(fileName);
    }

    @TestMetadata("AfterDot.kt")
    public void testAfterDot() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/AfterDot.kt");
        doTest(fileName);
    }

    @TestMetadata("AfterFuns.kt")
    public void testAfterFuns() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/AfterFuns.kt");
        doTest(fileName);
    }

    @TestMetadata("AfterSafeDot.kt")
    public void testAfterSafeDot() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/AfterSafeDot.kt");
        doTest(fileName);
    }

    @TestMetadata("AfterSpaceAndDot.kt")
    public void testAfterSpaceAndDot() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/AfterSpaceAndDot.kt");
        doTest(fileName);
    }

    public void testAllFilesPresentInKeywords() throws Exception {
        JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/completion/keywords"), Pattern.compile("^(.+)\\.kt$"), false);
    }

    @TestMetadata("CommaExpected.kt")
    public void testCommaExpected() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/CommaExpected.kt");
        doTest(fileName);
    }

    @TestMetadata("GlobalPropertyAccessors.kt")
    public void testGlobalPropertyAccessors() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/GlobalPropertyAccessors.kt");
        doTest(fileName);
    }

    @TestMetadata("InArgumentList.kt")
    public void testInArgumentList() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InArgumentList.kt");
        doTest(fileName);
    }

    @TestMetadata("InBlockComment.kt")
    public void testInBlockComment() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InBlockComment.kt");
        doTest(fileName);
    }

    @TestMetadata("InChar.kt")
    public void testInChar() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InChar.kt");
        doTest(fileName);
    }

    @TestMetadata("InClassBeforeFun.kt")
    public void testInClassBeforeFun() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InClassBeforeFun.kt");
        doTest(fileName);
    }

    @TestMetadata("InClassNoCompletionInValName.kt")
    public void testInClassNoCompletionInValName() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InClassNoCompletionInValName.kt");
        doTest(fileName);
    }

    @TestMetadata("InClassProperty.kt")
    public void testInClassProperty() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InClassProperty.kt");
        doTest(fileName);
    }

    @TestMetadata("InClassScope.kt")
    public void testInClassScope() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InClassScope.kt");
        doTest(fileName);
    }

    @TestMetadata("InClassTypeParameters.kt")
    public void testInClassTypeParameters() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InClassTypeParameters.kt");
        doTest(fileName);
    }

    @TestMetadata("InCodeBlock.kt")
    public void testInCodeBlock() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InCodeBlock.kt");
        doTest(fileName);
    }

    @TestMetadata("InFunctionExpressionBody.kt")
    public void testInFunctionExpressionBody() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InFunctionExpressionBody.kt");
        doTest(fileName);
    }

    @TestMetadata("InFunctionName.kt")
    public void testInFunctionName() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InFunctionName.kt");
        doTest(fileName);
    }

    @TestMetadata("InFunctionRecieverType.kt")
    public void testInFunctionRecieverType() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InFunctionRecieverType.kt");
        doTest(fileName);
    }

    @TestMetadata("InFunctionTypePosition.kt")
    public void testInFunctionTypePosition() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InFunctionTypePosition.kt");
        doTest(fileName);
    }

    @TestMetadata("InGetterExpressionBody.kt")
    public void testInGetterExpressionBody() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InGetterExpressionBody.kt");
        doTest(fileName);
    }

    @TestMetadata("InMemberFunParametersList.kt")
    public void testInMemberFunParametersList() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InMemberFunParametersList.kt");
        doTest(fileName);
    }

    @TestMetadata("InModifierListInsideClass.kt")
    public void testInModifierListInsideClass() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InModifierListInsideClass.kt");
        doTest(fileName);
    }

    @TestMetadata("InNotFinishedGenericWithFunAfter.kt")
    public void testInNotFinishedGenericWithFunAfter() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InNotFinishedGenericWithFunAfter.kt");
        doTest(fileName);
    }

    @TestMetadata("InParameterDefaultValue.kt")
    public void testInParameterDefaultValue() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InParameterDefaultValue.kt");
        doTest(fileName);
    }

    @TestMetadata("InPropertyInitializer.kt")
    public void testInPropertyInitializer() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InPropertyInitializer.kt");
        doTest(fileName);
    }

    @TestMetadata("InPropertyTypeReference.kt")
    public void testInPropertyTypeReference() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InPropertyTypeReference.kt");
        doTest(fileName);
    }

    @TestMetadata("InString.kt")
    public void testInString() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InString.kt");
        doTest(fileName);
    }

    @TestMetadata("InTopFunParametersList.kt")
    public void testInTopFunParametersList() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InTopFunParametersList.kt");
        doTest(fileName);
    }

    @TestMetadata("InTopScopeAfterPackage.kt")
    public void testInTopScopeAfterPackage() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/InTopScopeAfterPackage.kt");
        doTest(fileName);
    }

    @TestMetadata("LineComment.kt")
    public void testLineComment() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/LineComment.kt");
        doTest(fileName);
    }

    @TestMetadata("NoCompletionForCapitalPrefix.kt")
    public void testNoCompletionForCapitalPrefix() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/NoCompletionForCapitalPrefix.kt");
        doTest(fileName);
    }

    @TestMetadata("NoFinalInParameterList.kt")
    public void testNoFinalInParameterList() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/NoFinalInParameterList.kt");
        doTest(fileName);
    }

    @TestMetadata("NotInNotIs.kt")
    public void testNotInNotIs() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/NotInNotIs.kt");
        doTest(fileName);
    }

    @TestMetadata("PrefixMatcher.kt")
    public void testPrefixMatcher() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/PrefixMatcher.kt");
        doTest(fileName);
    }

    @TestMetadata("PropertyAccessors.kt")
    public void testPropertyAccessors() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/PropertyAccessors.kt");
        doTest(fileName);
    }

    @TestMetadata("PropertyAccessors2.kt")
    public void testPropertyAccessors2() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/PropertyAccessors2.kt");
        doTest(fileName);
    }

    @TestMetadata("PropertySetter.kt")
    public void testPropertySetter() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/PropertySetter.kt");
        doTest(fileName);
    }

    @TestMetadata("TopScope.kt")
    public void testTopScope() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/keywords/TopScope.kt");
        doTest(fileName);
    }
}