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
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.jackrabbit.vault.packaging.PackageId;
import org.apache.jackrabbit.vault.packaging.PackageProperties;
import org.apache.jackrabbit.vault.validation.spi.PropertiesValidator;
import org.apache.jackrabbit.vault.validation.spi.ValidationMessage;
import org.apache.jackrabbit.vault.validation.spi.ValidationMessageSeverity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PackageIdNamespaceValidator implements PropertiesValidator {

    private final ValidationMessageSeverity severity;
    private final Set<Pattern> allowedGroupPatterns;
    private final Set<Pattern> allowedNamePatterns;

    public PackageIdNamespaceValidator(
            @NotNull ValidationMessageSeverity severity,
            @NotNull Set<Pattern> allowedGroupPatterns,
            @NotNull Set<Pattern> allowedNamePatterns) {
        this.severity = severity;
        this.allowedGroupPatterns = allowedGroupPatterns;
        this.allowedNamePatterns = allowedNamePatterns;
    }

    @Override
    public @Nullable Collection<ValidationMessage> validate(@NotNull PackageProperties properties) {
        PackageId id = properties.getId();
        if (id == null) {
            return Collections.singleton(new ValidationMessage(severity, "Package ID is missing"));
        }
        Collection<ValidationMessage> messages = new LinkedList<>();
        if (allowedGroupPatterns.stream()
                .noneMatch(pattern -> pattern.matcher(id.getGroup()).matches())) {
            messages.add(new ValidationMessage(
                    severity,
                    String.format(
                            "Package group '%s' is not allowed (does not match any of the group patterns [%s])",
                            id.getGroup(),
                            allowedGroupPatterns.stream().map(Pattern::pattern).collect(Collectors.joining(",")))));
        }
        if (allowedNamePatterns.stream()
                .noneMatch(pattern -> pattern.matcher(id.getName()).matches())) {
            messages.add(new ValidationMessage(
                    severity,
                    String.format(
                            "Package name '%s' is not allowed (does not match any of the name patterns [%s])",
                            id.getName(),
                            allowedNamePatterns.stream().map(Pattern::pattern).collect(Collectors.joining(",")))));
        }
        return messages;
    }

    @Override
    public @Nullable Collection<ValidationMessage> done() {
        return null;
    }
}
