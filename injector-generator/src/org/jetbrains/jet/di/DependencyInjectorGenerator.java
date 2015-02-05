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

package org.jetbrains.jet.di;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.utils.Printer;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

import static org.jetbrains.jet.di.InjectorGeneratorUtil.var;

public class DependencyInjectorGenerator {

    private final Set<Field> fields = Sets.newLinkedHashSet();
    private final Set<Parameter> parameters = Sets.newLinkedHashSet();
    private final Set<Field> backsParameter = Sets.newHashSet();
    private final Set<FactoryMethod> factoryMethods = Sets.newLinkedHashSet();
    private final List<Class<?>> implementsList = Lists.newArrayList();

    private final Dependencies dependencies = new Dependencies();

    private final ImportManager importManager = new ImportManager();

    private String targetSourceRoot;
    private String injectorPackageName;
    private String injectorClassName;
    private String generatedBy;

    public DependencyInjectorGenerator() {
    }

    public String getInjectorClassName() {
        return injectorClassName;
    }

    public DependencyInjectorGenerator configure(String targetSourceRoot, String injectorPackageName, String injectorClassName, String generatedBy) {
        this.targetSourceRoot = targetSourceRoot;
        this.injectorPackageName = injectorPackageName;
        this.injectorClassName = injectorClassName;
        this.generatedBy = generatedBy;
        return this;
    }

    public void generate() throws IOException {
        assert targetSourceRoot != null : "Don't forget to call configure()";

        GeneratorsFileUtil.writeFileIfContentChanged(getOutputFile(), generateText().toString());
    }

    public File getOutputFile() {
        String outputFileName = targetSourceRoot + "/" + injectorPackageName.replace(".", "/") + "/" + injectorClassName + ".java";
        return new File(outputFileName);
    }

    public CharSequence generateText() throws IOException {
        assert generatedBy != null : "Don't forget to call configure()";

        fields.addAll(dependencies.satisfyDependencies());
        reportUnusedParameters(injectorPackageName, injectorClassName);

        StringBuilder preamble = new StringBuilder();
        generatePreamble(injectorPackageName, new Printer(preamble));

        StringBuilder body = new StringBuilder();
        Printer p = new Printer(body);

        p.println();

        p.println("/* This file is generated by " + generatedBy + ". DO NOT EDIT! */");
        p.println("@SuppressWarnings(\"all\")");
        p.print("public class " + injectorClassName);
        generateImplementsList(p);
        p.println(" {");
        p.pushIndent();
        p.println();
        generateFields(p);
        p.println();
        generateConstructor(injectorClassName, p);
        p.println();
        generateDestroy(injectorClassName, p);
        p.println();
        generateGetters(p);
        generateFactoryMethods(p);
        p.popIndent();
        p.println("}"); // class

        importManager.addClass(NotNull.class);
        importManager.addClass(PreDestroy.class);
        StringBuilder imports = new StringBuilder();
        generateImports(new Printer(imports), injectorPackageName);

        StringBuilder text = new StringBuilder(preamble);
        text.append(imports);
        text.append(body);
        return text;
    }

    private void reportUnusedParameters(String injectorPackageName, String injectorClassName) {
        Sets.SetView<Field> unusedParameters = Sets.difference(backsParameter, dependencies.getUsedFields());
        for (Field parameter : unusedParameters) {
            if (!parameter.isPublic()) {
                System.err.println("Unused parameter: " + parameter + " for " + injectorPackageName + "." + injectorClassName);
            }
        }
    }

    private static void generatePreamble(String injectorPackageName, Printer p) throws IOException {
        String copyright = "injector-generator/copyright.txt";
        p.println(FileUtil.loadFile(new File(copyright)));

        p.println("package " + injectorPackageName + ";");
        p.println();
    }

    private void generateImplementsList(Printer out) {
        if (!implementsList.isEmpty()) {
            out.print(" implements ");
            for (Iterator<Class<?>> iterator = implementsList.iterator(); iterator.hasNext(); ) {
                Class<?> superInterface = iterator.next();
                if (!superInterface.isInterface()) {
                    throw new IllegalArgumentException("Only interfaces are supported as supertypes");
                }
                out.print(type(superInterface));
                if (iterator.hasNext()) {
                    out.print(", ");
                }
            }
        }
    }

    public void implementInterface(Class<?> superInterface) {
        implementsList.add(superInterface);
    }

    public void addParameter(boolean reexport, @NotNull DiType type, @Nullable String name, boolean required, boolean useAsContext) {
        Field field = addField(reexport, type, name, null, useAsContext);
        Parameter parameter = new Parameter(type, name, field, required);
        parameters.add(parameter);
        field.setInitialization(new ParameterExpression(parameter));
        backsParameter.add(field);
        dependencies.addSatisfiedField(field);
    }

    public Field addField(boolean isPublic, DiType type, @Nullable String name, @Nullable Expression init, boolean useAsContext) {
        Field field = Field.create(isPublic, type, name == null ? var(type) : name, init);
        addField(field);
        if (useAsContext) {
            for (Field accessibleViaGetter : field.getFieldsAccessibleViaGetters()) {
                addField(accessibleViaGetter);
            }
        }
        return field;
    }

    private void addField(@NotNull Field field) {
        fields.add(field);
        dependencies.addField(field);
    }

    public void addFactoryMethod(@NotNull Class<?> returnType, Class<?>... parameterTypes) {
        List<DiType> types = Lists.newArrayList();
        for (Class<?> type : parameterTypes) {
            types.add(new DiType(type));
        }
        addFactoryMethod(new DiType(returnType), types);
    }

    public void addFactoryMethod(@NotNull DiType returnType, DiType... parameterTypes) {
        addFactoryMethod(returnType, Arrays.asList(parameterTypes));
    }

    public void addFactoryMethod(@NotNull DiType returnType, @NotNull Collection<DiType> parameterTypes) {
        List<Parameter> parameters = Lists.newArrayList();
        for (DiType type : parameterTypes) {
            parameters.add(new Parameter(type, var(type), null, false));
        }
        factoryMethods.add(new FactoryMethod("create" + type(returnType), returnType, parameters));
    }

    private void generateImports(Printer out, String injectorPackageName) {
        for (Class<?> importedClass : importManager.getImportedClasses()) {
            if (importedClass.isPrimitive()) continue;
            String importedPackageName = importedClass.getPackage().getName();
            if ("java.lang".equals(importedPackageName)
                || injectorPackageName.equals(importedPackageName)) {
                continue;
            }
            out.println("import " + importedClass.getCanonicalName() + ";");
        }
    }

    private void generateFields(Printer out) {
        for (Field field : getUsedFields()) {
            out.println("private final " + type(InjectorGeneratorUtil.getEffectiveFieldType(field)) + " " + field.getName() + ";");
        }
    }

    @NotNull
    private List<Field> getUsedFields() {
        return ContainerUtil.filter(fields, new Condition<Field>() {
            @Override
            public boolean value(Field field) {
                return dependencies.getUsedFields().contains(field) || field.isPublic();
            }
        });
    }

    private void generateConstructor(String injectorClassName, Printer p) {
        // Constructor parameters
        if (parameters.isEmpty()) {
            p.println("public ", injectorClassName, "() {");
        }
        else {
            p.print("public ", injectorClassName);
            generateParameterList(p, parameters);
        }

        p.pushIndent();

        InjectionLogicGenerator.generateForFields(p, getUsedFields());

        p.popIndent();
        p.println("}");
    }

    private void generateParameterList(Printer p, Collection<Parameter> parameters) {
        p.printlnWithNoIndent("(");
        p.pushIndent();
        for (Iterator<Parameter> iterator = parameters.iterator(); iterator.hasNext(); ) {
            Parameter parameter = iterator.next();
            p.printIndent();
            if (parameter.isRequired()) {
                p.printWithNoIndent("@NotNull ");
            }
            p.printWithNoIndent(type(parameter.getType()), " ", parameter.getName());
            if (iterator.hasNext()) {
                p.printlnWithNoIndent(",");
            }
        }
        p.printlnWithNoIndent();
        p.popIndent();
        p.println(") {");
    }

    private void generateDestroy(@NotNull String injectorClassName, @NotNull Printer out) {
        out.println("@PreDestroy");
        out.println("public void destroy() {");
        out.pushIndent();
        for (Field field : fields) {
            // TODO: type of field may be different from type of object
            List<Method> preDestroyMethods = InjectorGeneratorUtil
                    .getPreDestroyMethods(InjectorGeneratorUtil.getEffectiveFieldType(field).getClazz());
            for (Method preDestroy : preDestroyMethods) {
                out.println(field.getName() + "." + preDestroy.getName() + "();");
            }
            if (preDestroyMethods.size() > 0) {
                out.println();
            }
        }
        out.popIndent();
        out.println("}");
    }

    private void generateGetters(Printer out) {
        for (Field field : fields) {
            if (!field.isPublic()) continue;
            String visibility = "public";
            out.println(visibility + " " + type(field.getType()) + " " + field.getGetterName() + "() {");
            out.pushIndent();

            out.println("return this." + field.getName() + ";");

            out.popIndent();
            out.println("}");
            out.println();
        }

    }

    private void generateFactoryMethods(Printer p) {
        if (factoryMethods.isEmpty()) return;
        p.println();

        p.pushIndent();
        for (FactoryMethod method : factoryMethods) {
            generateFactoryMethod(p, method);
        }
        p.popIndent();
    }

    private Collection<Field> computeFieldsForFactoryMethod(FactoryMethod method, Field resultField) {
        Dependencies localDependencies = new Dependencies();

        Map<Parameter, Field> parameterToField = Maps.newHashMap();
        for (Parameter parameter : method.getParameters()) {
            Field field = new Field(true, parameter.getType(), parameter.getName());
            localDependencies.addSatisfiedField(field);
            parameterToField.put(parameter, field);
        }

        for (Field storedField : fields) {
            localDependencies.addSatisfiedField(storedField);
        }

        localDependencies.addField(resultField);

        Collection<Field> fields = Lists.newArrayList(localDependencies.satisfyDependencies());
        fields.add(resultField);
        return fields;
    }

    private void generateFactoryMethod(Printer p, FactoryMethod method) {
        Field resultField = new Field(true, method.getReturnType(), "_result");
        Collection<Field> fields = computeFieldsForFactoryMethod(method, resultField);

        p.print("public ", type(method.getReturnType()), " ", method.getName());
        generateParameterList(p, method.getParameters());

        p.pushIndent();

        InjectionLogicGenerator.generateForLocalVariables(importManager, p, fields);

        p.println("return ", resultField.getName(), ";");

        p.popIndent();
        p.println("}");
    }

    private CharSequence type(DiType type) {
        return importManager.render(type);
    }

    private CharSequence type(Class<?> type) {
        return type(DiType.fromReflectionType(type));
    }
}