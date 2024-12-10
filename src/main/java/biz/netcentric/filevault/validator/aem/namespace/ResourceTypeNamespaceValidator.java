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
import org.apache.sling.api.SlingConstants;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceTypeNamespaceValidator implements DocumentViewXmlValidator {

    private static final Name PROPERTY_NAME_RESOURCE_TYPE = NameFactoryImpl.getInstance()
            .create(JcrResourceConstants.SLING_NAMESPACE_URI, SlingConstants.PROPERTY_RESOURCE_TYPE);
    private static final Name PROPERTY_NAME_RESOURCE_SUPER_TYPE = NameFactoryImpl.getInstance()
            .create(JcrResourceConstants.SLING_NAMESPACE_URI, SlingConstants.PROPERTY_RESOURCE_SUPER_TYPE);

    private final ValidationMessageSeverity severity;
    private final Set<Pattern> allowedTypePatterns;
    private final Set<Pattern> allowedSuperTypePatterns;

    public ResourceTypeNamespaceValidator(
            ValidationMessageSeverity severity,
            Set<Pattern> allowedTypePatterns,
            Set<Pattern> allowedSuperTypePatterns) {
        super();
        this.severity = severity;
        this.allowedTypePatterns = allowedTypePatterns;
        this.allowedSuperTypePatterns = allowedSuperTypePatterns;
    }

    @Override
    public @Nullable Collection<ValidationMessage> validate(
            @NotNull DocViewNode2 node, @NotNull NodeContext nodeContext, boolean isRoot) {
        Collection<ValidationMessage> messages = new LinkedList<>();
        if (!allowedTypePatterns.isEmpty()) {
            String type = node.getPropertyValue(PROPERTY_NAME_RESOURCE_TYPE).orElse(null);
            if (type != null
                    && allowedTypePatterns.stream()
                            .noneMatch(pattern -> pattern.matcher(type).matches())) {
                messages.add(new ValidationMessage(
                        severity,
                        String.format(
                                "Resource is using type '%s' which is not allowed (does not match any of the allowed patterns [%s])",
                                type,
                                allowedTypePatterns.stream()
                                        .map(Pattern::pattern)
                                        .collect(Collectors.joining(",")))));
            }
        }
        if (!allowedSuperTypePatterns.isEmpty()) {
            String superType =
                    node.getPropertyValue(PROPERTY_NAME_RESOURCE_SUPER_TYPE).orElse(null);
            if (superType != null
                    && allowedSuperTypePatterns.stream()
                            .noneMatch(pattern -> pattern.matcher(superType).matches())) {
                messages.add(new ValidationMessage(
                        severity,
                        String.format(
                                "Resource is using super type '%s' which is not allowed (does not match any of the allowed patterns [%s])",
                                superType,
                                allowedSuperTypePatterns.stream()
                                        .map(Pattern::pattern)
                                        .collect(Collectors.joining(",")))));
            }
        }
        return messages;
    }

    @Override
    public @Nullable Collection<ValidationMessage> done() {
        return null;
    }
}
