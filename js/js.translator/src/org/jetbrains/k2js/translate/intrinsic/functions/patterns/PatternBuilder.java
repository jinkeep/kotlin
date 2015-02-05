/*
 * Copyright 2010-2013 JetBrains s.r.o.
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

package org.jetbrains.k2js.translate.intrinsic.functions.patterns;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.*;
import org.jetbrains.jet.lang.resolve.DescriptorUtils;
import org.jetbrains.jet.lang.resolve.OverrideResolver;
import org.jetbrains.jet.lang.resolve.name.Name;
import org.jetbrains.k2js.translate.context.Namer;
import org.jetbrains.k2js.translate.utils.JsDescriptorUtils;
import org.jetbrains.k2js.translate.utils.TranslationUtils;

import java.util.Arrays;
import java.util.List;

public final class PatternBuilder {

    @NotNull
    private static final NamePredicate KOTLIN_NAME_PREDICATE = new NamePredicate("kotlin");

    @NotNull
    private static final Name KOTLIN_NAME = Name.identifier(Namer.KOTLIN_LOWER_NAME);

    private PatternBuilder() {
    }

    @NotNull
    public static DescriptorPredicate pattern(@NotNull NamePredicate checker, @NotNull String stringWithPattern) {
        List<NamePredicate> checkers = Lists.newArrayList(checker);
        checkers.addAll(parseFqNamesFromString(stringWithPattern));
        return pattern(checkers, parseArgumentsFromString(stringWithPattern));
    }

    @NotNull
    public static DescriptorPredicate pattern(@NotNull String stringWithPattern, @NotNull NamePredicate checker) {
        List<NamePredicate> checkers = Lists.newArrayList(parseFqNamesFromString(stringWithPattern));
        checkers.add(checker);
        return pattern(checkers);
    }

    @NotNull
    public static DescriptorPredicate pattern(@NotNull String stringWithPattern) {
        return pattern(parseFqNamesFromString(stringWithPattern), parseArgumentsFromString(stringWithPattern));
    }

    @NotNull
    private static List<NamePredicate> parseFqNamesFromString(@NotNull String stringWithPattern) {
        stringWithPattern = getNamePatternFromString(stringWithPattern);
        String[] subPatterns = stringWithPattern.split("\\.");
        List<NamePredicate> checkers = Lists.newArrayList();
        for (String subPattern : subPatterns) {
            String[] validNames = subPattern.split("\\|");
            checkers.add(new NamePredicate(validNames));
        }
        return checkers;
    }

    @Nullable
    private static List<NamePredicate> parseArgumentsFromString(@NotNull String stringWithPattern) {
        stringWithPattern = getArgumentsPatternFromString(stringWithPattern);
        if (stringWithPattern == null) return null;

        List<NamePredicate> checkers = Lists.newArrayList();
        if (stringWithPattern.isEmpty()) {
            return checkers;
        }

        String[] subPatterns = stringWithPattern.split("\\,");
        for (String subPattern : subPatterns) {
            String[] validNames = subPattern.split("\\|");
            checkers.add(new NamePredicate(validNames));
        }
        return checkers;
    }

    @NotNull
    private static String getNamePatternFromString(@NotNull String stringWithPattern) {
        int left = stringWithPattern.indexOf("(");
        if (left < 0) {
            return stringWithPattern;
        }
        else {
            return stringWithPattern.substring(0, left);
        }
    }

    @Nullable
    private static String getArgumentsPatternFromString(@NotNull String stringWithPattern) {
        int left = stringWithPattern.indexOf("(");
        if (left < 0) {
            return null;
        }
        else {
            int right = stringWithPattern.indexOf(")");
            assert right == stringWithPattern.length() - 1 : "expected ')' at the end: " + stringWithPattern;
            return stringWithPattern.substring(left + 1, right);
        }
    }

    @NotNull
    private static DescriptorPredicate pattern(@NotNull List<NamePredicate> checkers) {
        return pattern(checkers, null);
    }

    @NotNull
    private static DescriptorPredicate pattern(@NotNull List<NamePredicate> checkers, @Nullable List<NamePredicate> arguments) {
        assert !checkers.isEmpty();
        final List<NamePredicate> checkersWithPrefixChecker = Lists.newArrayList();
        if (!checkers.get(0).apply(KOTLIN_NAME)) {
            checkersWithPrefixChecker.add(KOTLIN_NAME_PREDICATE);
        }

        checkersWithPrefixChecker.addAll(checkers);

        assert checkersWithPrefixChecker.size() > 1;

        final List<NamePredicate> argumentCheckers = arguments != null ? Lists.newArrayList(arguments) : null;

        return new DescriptorPredicate() {
            @Override
            public boolean apply(@Nullable FunctionDescriptor descriptor) {
                assert descriptor != null : "argument for DescriptorPredicate.apply should not be null, checkers=" + checkersWithPrefixChecker;
                //TODO: no need to wrap if we check beforehand
                try {
                    return doApply(descriptor);
                }
                catch (IllegalArgumentException e) {
                    return false;
                }
            }

            private boolean doApply(@NotNull FunctionDescriptor descriptor) {
                List<Name> nameParts = DescriptorUtils.getFqName(descriptor).pathSegments();
                if (nameParts.size() != checkersWithPrefixChecker.size()) return false;

                return allNamePartsValid(nameParts) && checkAllArgumentsValidIfNeeded(descriptor);
            }

            private boolean checkAllArgumentsValidIfNeeded(@NotNull FunctionDescriptor descriptor) {
                if (argumentCheckers != null) {
                    List<ValueParameterDescriptor> valueParameterDescriptors = descriptor.getValueParameters();
                    if (valueParameterDescriptors.size() != argumentCheckers.size()) {
                        return false;
                    }
                    for (int i = 0; i < valueParameterDescriptors.size(); i++) {
                        ValueParameterDescriptor valueParameterDescriptor = valueParameterDescriptors.get(i);
                        Name name = JsDescriptorUtils.getNameIfStandardType(valueParameterDescriptor.getType());
                        NamePredicate namePredicate = argumentCheckers.get(i);
                        if (!namePredicate.apply(name)) return false;
                    }
                }
                return true;
            }

            private boolean allNamePartsValid(@NotNull List<Name> nameParts) {
                for (int i = 0; i < nameParts.size(); ++i) {
                    Name namePart = nameParts.get(i);
                    NamePredicate correspondingPredicate = checkersWithPrefixChecker.get(i);
                    if (!correspondingPredicate.apply(namePart)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    @NotNull
    public static DescriptorPredicate pattern(@NotNull NamePredicate... checkers) {
        return pattern(Arrays.asList(checkers));
    }

    @NotNull
    public static DescriptorPredicateImpl pattern(@NotNull String... names) {
        return new DescriptorPredicateImpl(names);
    }

    public static class DescriptorPredicateImpl implements DescriptorPredicate {
        private final String[] names;

        private String receiverFqName;

        private boolean checkOverridden;

        public DescriptorPredicateImpl(String... names) {
            this.names = names;
        }

        public DescriptorPredicateImpl isExtensionOf(String receiverFqName) {
            this.receiverFqName = receiverFqName;
            return this;
        }

        public DescriptorPredicateImpl checkOverridden() {
            this.checkOverridden = true;
            return this;
        }

        private boolean matches(@NotNull CallableDescriptor callable) {
            DeclarationDescriptor descriptor = callable;
            int nameIndex = names.length - 1;
            while (true) {
                if (nameIndex == -1) {
                    return false;
                }

                if (!descriptor.getName().asString().equals(names[nameIndex])) {
                    return false;
                }

                nameIndex--;
                descriptor = descriptor.getContainingDeclaration();
                if (descriptor instanceof PackageFragmentDescriptor) {
                    return nameIndex == 0 && names[0].equals(((PackageFragmentDescriptor) descriptor).getFqName().asString());
                }
            }
        }

        @Override
        public boolean apply(@Nullable FunctionDescriptor functionDescriptor) {
            assert functionDescriptor != null :
                    "argument for DescriptorPredicate.apply should not be null, receiverFqName=" + receiverFqName + " names=" + Arrays.asList(names);
            ReceiverParameterDescriptor actualReceiver = functionDescriptor.getExtensionReceiverParameter();
            if (actualReceiver != null) {
                if (receiverFqName == null) return false;

                String actualReceiverFqName = TranslationUtils.getJetTypeFqName(actualReceiver.getType(), false);

                if (!actualReceiverFqName.equals(receiverFqName)) return false;
            }

            if (!(functionDescriptor.getContainingDeclaration() instanceof ClassDescriptor)) {
                return matches(functionDescriptor);
            }

            for (CallableMemberDescriptor real : OverrideResolver.getOverriddenDeclarations(functionDescriptor)) {
                if (matches(real)) {
                    return true;
                }
            }

            if (checkOverridden) {
                for (CallableDescriptor overridden : DescriptorUtils.getAllOverriddenDescriptors(functionDescriptor)) {
                    if (matches(overridden)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}