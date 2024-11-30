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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.jackrabbit.vault.validation.spi.OsgiConfigurationValidator;
import org.apache.jackrabbit.vault.validation.spi.ValidationMessage;
import org.apache.jackrabbit.vault.validation.spi.ValidationMessageSeverity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OsgiConfigurationNamespaceValidator implements OsgiConfigurationValidator {

    private final ValidationMessageSeverity severity;
    private final Set<Pattern> allowedPidPatterns;
    private final Set<Pattern> allowedFactoryPidNames;
    private final boolean restrictFactoryConfigurationsToAllowedPidPatterns;

    public OsgiConfigurationNamespaceValidator(
            @NotNull ValidationMessageSeverity severity,
            @NotNull Set<Pattern> allowedPidPatterns,
            @NotNull Set<Pattern> allowedFactoryPidNames,
            boolean restrictFactoryConfigurationsToAllowedPidPatterns) {
        super();
        this.severity = severity;
        this.allowedPidPatterns = allowedPidPatterns;
        this.allowedFactoryPidNames =
                allowedFactoryPidNames; // this is not really technically restricting something, but at least could be
        // used as pointer
        this.restrictFactoryConfigurationsToAllowedPidPatterns = restrictFactoryConfigurationsToAllowedPidPatterns;
    }

    @Override
    public @Nullable Collection<ValidationMessage> done() {
        return null;
    }

    @Override
    public @Nullable Collection<ValidationMessage> validateConfig(
            @NotNull Map<String, Object> config,
            @NotNull String pid,
            @Nullable String subname,
            @NotNull String nodePath) {
        // is it a factory configuration?
        boolean isFactoryConfig = subname != null;
        Collection<ValidationMessage> messages = new LinkedList<>();
        if (!isFactoryConfig || restrictFactoryConfigurationsToAllowedPidPatterns) {
            if (allowedPidPatterns.stream()
                    .noneMatch(pattern -> pattern.matcher(pid).matches())) {
                messages.add(new ValidationMessage(
                        severity,
                        String.format(
                                "OSGi configuration PID '%s' is not allowed to be configured (does not match any of the allowed patterns [%s])",
                                pid,
                                allowedPidPatterns.stream()
                                        .map(Pattern::pattern)
                                        .collect(Collectors.joining(",")))));
            }
        }
        if (isFactoryConfig && !allowedFactoryPidNames.isEmpty()) {
            if (allowedFactoryPidNames.stream()
                    .noneMatch(pattern -> pattern.matcher(subname).matches())) {
                messages.add(new ValidationMessage(
                        severity,
                        String.format(
                                "OSGi factory configuration PID '%s' is not allowed with the given subname '%s' (does not match any of the allowed patterns [%s])",
                                pid,
                                subname,
                                allowedFactoryPidNames.stream()
                                        .map(Pattern::pattern)
                                        .collect(Collectors.joining(",")))));
            }
        }
        return messages;
    }
}
