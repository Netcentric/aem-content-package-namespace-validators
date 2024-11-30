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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.jackrabbit.oak.spi.security.user.UserConstants;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.vault.util.DocViewNode2;
import org.apache.jackrabbit.vault.validation.spi.DocumentViewXmlValidator;
import org.apache.jackrabbit.vault.validation.spi.NodeContext;
import org.apache.jackrabbit.vault.validation.spi.ValidationMessage;
import org.apache.jackrabbit.vault.validation.spi.ValidationMessageSeverity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AuthorizableNamespaceValidator implements DocumentViewXmlValidator {

    private final ValidationMessageSeverity severity;
    private final Set<Pattern> allowedAuthorizableIdPatterns;
    private final Set<Pattern> allowedPrincipalNamePatterns;

    private static final Set<String> AUTHORIZABLE_NODE_TYPES = new HashSet<>(
            Arrays.asList(UserConstants.NT_REP_USER, UserConstants.NT_REP_GROUP, UserConstants.NT_REP_SYSTEM_USER));
    private static final @NotNull Name PROPERTY_PRINCIPAL_NAME = NameConstants.REP_PRINCIPAL_NAME;
    private static final @NotNull Name PROPERTY_AUTHORIZABLE_ID =
            NameFactoryImpl.getInstance().create(Name.NS_REP_URI, "authorizableId");

    public AuthorizableNamespaceValidator(
            @NotNull ValidationMessageSeverity severity,
            @NotNull Set<Pattern> allowedPrincipalNamePatterns,
            @NotNull Set<Pattern> allowedAuthorizableIdPatterns) {
        this.severity = severity;
        this.allowedPrincipalNamePatterns = allowedPrincipalNamePatterns;
        this.allowedAuthorizableIdPatterns = allowedAuthorizableIdPatterns;
    }

    @Override
    public @Nullable Collection<ValidationMessage> validate(
            @NotNull DocViewNode2 node, @NotNull NodeContext nodeContext, boolean isRoot) {
        if (isOakAuthorizable(node)) {
            Collection<ValidationMessage> messages = new LinkedList<>();
            String principalName =
                    node.getPropertyValue(PROPERTY_PRINCIPAL_NAME).orElse(null);
            if (allowedPrincipalNamePatterns.stream()
                    .noneMatch(pattern -> pattern.matcher(principalName).matches())) {
                messages.add(new ValidationMessage(
                        severity,
                        String.format(
                                "Principal name '%s' is not allowed (does not match any of the principal name patterns [%s])",
                                principalName,
                                allowedPrincipalNamePatterns.stream()
                                        .map(Pattern::pattern)
                                        .collect(Collectors.joining(",")))));
            }
            if (!allowedAuthorizableIdPatterns.isEmpty()) {
                String authorizableId = node.getPropertyValue(PROPERTY_AUTHORIZABLE_ID)
                        .orElse(node.getName().getLocalName());
                if (allowedAuthorizableIdPatterns.stream()
                        .noneMatch(pattern -> pattern.matcher(authorizableId).matches())) {
                    messages.add(new ValidationMessage(
                            severity,
                            String.format(
                                    "Authorizable ID '%s' is not allowed (does not match any of the authorizable ID patterns [%s])",
                                    authorizableId,
                                    allowedPrincipalNamePatterns.stream()
                                            .map(Pattern::pattern)
                                            .collect(Collectors.joining(",")))));
                }
            }
            return messages;
        }
        return null;
    }

    boolean isOakAuthorizable(@NotNull DocViewNode2 node) {
        return AUTHORIZABLE_NODE_TYPES.contains(node.getPrimaryType().orElse(""));
    }

    @Override
    public @Nullable Collection<ValidationMessage> done() {
        return null;
    }
}
