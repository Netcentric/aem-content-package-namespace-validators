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
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.jackrabbit.vault.fs.api.PathFilterSet;
import org.apache.jackrabbit.vault.fs.api.WorkspaceFilter;
import org.apache.jackrabbit.vault.validation.spi.FilterValidator;
import org.apache.jackrabbit.vault.validation.spi.ValidationMessage;
import org.apache.jackrabbit.vault.validation.spi.ValidationMessageSeverity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FilterNamespaceValidator implements FilterValidator {

    private final ValidationMessageSeverity severity;
    private final Set<Pattern> allowedRootPatterns;

    public FilterNamespaceValidator(ValidationMessageSeverity severity, Set<Pattern> allowedRootPatterns) {
        this.severity = severity;
        this.allowedRootPatterns = allowedRootPatterns;
    }

    @Override
    public @Nullable Collection<ValidationMessage> done() {
        return null;
    }

    @Override
    public @Nullable Collection<ValidationMessage> validate(@NotNull WorkspaceFilter filter) {
        Collection<ValidationMessage> messages = filter.getFilterSets().stream()
                .map(PathFilterSet::getRoot)
                .filter(root -> allowedRootPatterns.stream()
                        .noneMatch(pattern -> pattern.matcher(root).matches()))
                .map(root -> new ValidationMessage(
                        severity,
                        String.format(
                                "Filter root '%s' is not allowed (does not match any of the allowed patterns [%s])",
                                root,
                                allowedRootPatterns.stream()
                                        .map(Pattern::pattern)
                                        .collect(Collectors.joining(",")))))
                .collect(Collectors.toList());
        // TODO: properties filter
        return messages;
    }
}
