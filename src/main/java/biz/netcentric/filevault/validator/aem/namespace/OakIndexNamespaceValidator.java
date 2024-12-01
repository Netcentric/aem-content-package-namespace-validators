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

public class OakIndexNamespaceValidator implements DocumentViewXmlValidator {

    private static final Name PROPERTY_NAME_TYPE = NameFactoryImpl.getInstance().create(Name.NS_DEFAULT_URI, "type");
    private static final Name PROPERTY_NAME_INCLUDED_PATHS =
            NameFactoryImpl.getInstance().create(Name.NS_DEFAULT_URI, "includedPaths");

    private final ValidationMessageSeverity severity;
    private final Set<Pattern> allowedPathPatterns;

    public OakIndexNamespaceValidator(ValidationMessageSeverity severity, Set<Pattern> allowedPathPatterns) {
        super();
        this.severity = severity;
        this.allowedPathPatterns = allowedPathPatterns;
    }

    @Override
    public @Nullable Collection<ValidationMessage> validate(
            @NotNull DocViewNode2 node, @NotNull NodeContext nodeContext, boolean isRoot) {
        if (isOakIndexDefinition(node)) {
            String type = node.getPropertyValue(PROPERTY_NAME_TYPE).orElse("");
            Collection<String> includedPaths = null;
            switch (type) {
                case "lucene":
                case "property":
                    // https://jackrabbit.apache.org/oak/docs/query/lucene.html#include-exclude
                    // https://jackrabbit.apache.org/oak/docs/query/property-index.html
                    includedPaths = node.getPropertyValues(PROPERTY_NAME_INCLUDED_PATHS);
                    break;
                default:
                    return Collections.singleton(new ValidationMessage(
                            ValidationMessageSeverity.WARN,
                            "Unsupported Oak Query Index of type " + type + " found. Skip evaluation!"));
            }
            if (includedPaths.isEmpty()) {
                return Collections.singleton(new ValidationMessage(
                        severity,
                        "Oak Query Index does not have includedPaths property (is not restricted to specific paths)"));
            }
            return includedPaths.stream()
                    .filter(includedPath -> allowedPathPatterns.stream()
                            .noneMatch(pattern -> pattern.matcher(includedPath).matches()))
                    .map(includedPath -> new ValidationMessage(
                            severity,
                            String.format(
                                    "Oak Query Index uses path restriction '%s' which is not allowed (does not match any of the allowed patterns [%s])",
                                    includedPath,
                                    allowedPathPatterns.stream()
                                            .map(Pattern::pattern)
                                            .collect(Collectors.joining(",")))))
                    .collect(Collectors.toList());
        }
        return null;
    }

    boolean isOakIndexDefinition(@NotNull DocViewNode2 node) {
        return node.getPrimaryType().orElse("").equals("oak:QueryIndexDefinition");
    }

    @Override
    public @Nullable Collection<ValidationMessage> done() {
        return null;
    }
}
