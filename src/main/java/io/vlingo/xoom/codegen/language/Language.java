// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.language;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.template.TemplateParameters;

import java.util.function.Consumer;

public enum Language {
  C_SHARP("cs", new String[]{}),
  F_SHARP("fs", new String[]{}),
  JAVA("java", new String[]{"src", "main", "java"}),
  KOTLIN("kt", new String[]{"src", "main", "kotlin"},
          KotlinSyntaxConverter::convertFieldTypes, KotlinSyntaxConverter::convertImports);


  private final String extension;
  public final String[] sourceFolder;
  public final Consumer<CodeGenerationParameters> preParametersProcessingResolver;
  public final Consumer<TemplateParameters> postParametersProcessingResolver;

  Language(final String extension,
           final String[] sourceFolder) {
    this(extension, sourceFolder, params -> {}, params -> {});
  }

  Language(final String extension,
           final String[] sourceFolder,
           final Consumer<CodeGenerationParameters> preParametersProcessingResolver,
           final Consumer<TemplateParameters> postParametersProcessingResolver) {
    this.extension = extension;
    this.sourceFolder = sourceFolder;
    this.preParametersProcessingResolver = preParametersProcessingResolver;
    this.postParametersProcessingResolver = postParametersProcessingResolver;
  }

  public static Language findDefault() {
    return JAVA;
  }

  public String formatFilename(final String fileName) {
    if(fileName.contains(".")) {
      return fileName;
    }
    return fileName + "." + extension;
  }

  public String templateFolderName() {
    return name().toLowerCase();
  }

  public boolean isJava() {
    return equals(JAVA);
  }

  public boolean isKotlin() {
    return equals(KOTLIN);
  }

  public void resolvePreParametersProcessing(final CodeGenerationParameters parameters) {
    this.preParametersProcessingResolver.accept(parameters);
  }

  public void resolvePostParametersProcessing(final TemplateParameters parameters) {
    this.postParametersProcessingResolver.accept(parameters);
  }

}