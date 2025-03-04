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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Spliterators;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.jackrabbit.vault.validation.spi.GenericJcrDataValidator;
import org.apache.jackrabbit.vault.validation.spi.ValidationMessage;
import org.apache.jackrabbit.vault.validation.spi.ValidationMessageSeverity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmbeddedNamespaceValidator implements GenericJcrDataValidator {

    private final ValidationMessageSeverity severity;
    private final Set<Pattern> allowedBundleSymbolicNamePatterns;

    public EmbeddedNamespaceValidator(
            ValidationMessageSeverity severity, Set<Pattern> allowedBundleSymbolicNamePatterns) {
        super();
        this.severity = severity;
        this.allowedBundleSymbolicNamePatterns = allowedBundleSymbolicNamePatterns;
    }

    @Override
    public @Nullable Collection<ValidationMessage> validateJcrData(
            @NotNull InputStream input,
            @NotNull Path filePath,
            @NotNull Path basePath,
            @NotNull Map<String, Integer> nodePathsAndLineNumbers)
            throws IOException {
        try (JarInputStream jarInputStream = new JarInputStream(input)) {
            String bundleSymbolicName = getBundleSymbolicName(jarInputStream.getManifest());
            if (bundleSymbolicName == null) {
                return Collections.singleton(new ValidationMessage(
                        ValidationMessageSeverity.WARN,
                        "Either no manifest or no Bundle-SymbolicName header found in manifest. Skip evaluation!"));
            }
            if (allowedBundleSymbolicNamePatterns.stream()
                    .noneMatch(pattern -> pattern.matcher(bundleSymbolicName).matches())) {
                return Collections.singleton(new ValidationMessage(
                        severity,
                        String.format(
                                "Bundle-SymbolicName '%s' does not match any of the allowed patterns [%s]",
                                bundleSymbolicName,
                                allowedBundleSymbolicNamePatterns.stream()
                                        .map(Pattern::pattern)
                                        .collect(Collectors.joining(",")))));
            }
        }
        return null;
    }

    String getBundleSymbolicName(Manifest manifest) {
        if (manifest == null) {
            return null;
        }
        return manifest.getMainAttributes().getValue("Bundle-SymbolicName");
    }

    @Override
    public boolean shouldValidateJcrData(@NotNull Path filePath, @NotNull Path basePath) {
        return isEmbeddedBundle(filePath);
    }

    static boolean isEmbeddedBundle(@NotNull Path filePath) {
        if (!filePath.getName(0).toString().equals("apps")) {
            return false;
        }
        if (!filePath.getFileName().toString().endsWith(".jar")) {
            return false;
        }
        return StreamSupport.stream(Spliterators.spliterator(filePath.iterator(), filePath.getNameCount(), 0), false)
                .limit(5) // max depth
                .map(Path::getFileName)
                .map(Path::toString)
                .anyMatch(name -> name.startsWith("install.") || name.equals("install"));
    }

    @Override
    public @Nullable Collection<ValidationMessage> done() {
        return null;
    }
}
