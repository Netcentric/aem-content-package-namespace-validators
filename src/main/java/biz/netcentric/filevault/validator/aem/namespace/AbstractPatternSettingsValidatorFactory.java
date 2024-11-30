/*-
 * #%L
 * AEM FileVault Content Package Namespace Validators
 * %%
 * Copyright (C) 2024 Cognizant Netcentric
 * %%
 * All rights reserved. This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 * SPDX-License-Identifier: EPL-2.0
 * #L%
 */
package biz.netcentric.filevault.validator.aem.namespace;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.apache.jackrabbit.vault.validation.spi.ValidationContext;
import org.apache.jackrabbit.vault.validation.spi.Validator;
import org.apache.jackrabbit.vault.validation.spi.ValidatorFactory;
import org.apache.jackrabbit.vault.validation.spi.ValidatorSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPatternSettingsValidatorFactory implements ValidatorFactory {

    /**
     * Parses a comma-separated string of patterns into a set of compiled patterns.
     *
     * @param stringPatterns
     * @return
     * @throws PatternSyntaxException in case of invalid patterns
     */
    @NotNull
    static Set<Pattern> createPatternsFromCommaSeparatedString(String stringPatterns) {
        if (stringPatterns == null || stringPatterns.isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(stringPatterns.split(","))
                .map(String::trim)
                .map(Pattern::compile)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private final String mainPatternOption;
    private final String id;
    private final boolean allowEmptyMainPatternOption;

    protected AbstractPatternSettingsValidatorFactory(
            @NotNull String id, @NotNull String mainPatternOption, boolean allowEmptyMainPatternOption) {
        this.id = id;
        this.mainPatternOption = mainPatternOption;
        this.allowEmptyMainPatternOption = allowEmptyMainPatternOption;
    }

    @Override
    public boolean shouldValidateSubpackages() {
        return true;
    }

    @Override
    public int getServiceRanking() {
        return 0;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @Nullable Validator createValidator(
            @NotNull ValidationContext context, @NotNull ValidatorSettings settings) {
        Set<Pattern> patterns =
                createPatternsFromCommaSeparatedString(settings.getOptions().get(mainPatternOption));
        if (patterns.isEmpty() && !allowEmptyMainPatternOption) {
            return null;
        } else {
            return createValidator(patterns, context, settings);
        }
    }

    protected abstract Validator createValidator(
            @NotNull Set<Pattern> patterns, @NotNull ValidationContext context, @NotNull ValidatorSettings settings);
}
