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

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.vault.util.DocViewNode2;
import org.apache.jackrabbit.vault.validation.spi.DocumentViewXmlValidator;
import org.apache.jackrabbit.vault.validation.spi.NodeContext;
import org.apache.jackrabbit.vault.validation.spi.ValidationMessage;
import org.apache.jackrabbit.vault.validation.spi.ValidationMessageSeverity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientLibraryNamespaceValidator implements DocumentViewXmlValidator {

    private static final Name PROPERTY_NAME_CATEGORIES =
            NameFactoryImpl.getInstance().create(Name.NS_DEFAULT_URI, "categories");

    private final ValidationMessageSeverity severity;
    private final Set<Pattern> allowedCategoryPatterns;

    public ClientLibraryNamespaceValidator(ValidationMessageSeverity severity, Set<Pattern> allowedCategoryPatterns) {
        super();
        this.severity = severity;
        this.allowedCategoryPatterns = allowedCategoryPatterns;
    }

    @Override
    public @Nullable Collection<ValidationMessage> validate(
            @NotNull DocViewNode2 node, @NotNull NodeContext nodeContext, boolean isRoot) {
        if (isClientLibrary(node)) {
            return node.getPropertyValues(PROPERTY_NAME_CATEGORIES).stream()
                    .filter(category -> allowedCategoryPatterns.stream()
                            .noneMatch(pattern -> pattern.matcher(category).matches()))
                    .map(category -> new ValidationMessage(
                            severity,
                            String.format(
                                    "Client Library's categories contains '%s' which is not allowed (does not match any of the allowed patterns [%s])",
                                    category,
                                    allowedCategoryPatterns.stream()
                                            .map(Pattern::pattern)
                                            .collect(Collectors.joining(",")))))
                    .collect(Collectors.toList());
        }
        return null;
    }

    boolean isClientLibrary(@NotNull DocViewNode2 node) {
        return node.getPrimaryType().orElse("").equals("cq:ClientLibraryFolder");
    }

    @Override
    public @Nullable Collection<ValidationMessage> done() {
        return null;
    }
}
