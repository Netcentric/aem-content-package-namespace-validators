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

import java.util.Set;
import java.util.regex.Pattern;

import org.apache.jackrabbit.vault.validation.spi.ValidationContext;
import org.apache.jackrabbit.vault.validation.spi.Validator;
import org.apache.jackrabbit.vault.validation.spi.ValidatorFactory;
import org.apache.jackrabbit.vault.validation.spi.ValidatorSettings;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.MetaInfServices;

@MetaInfServices(ValidatorFactory.class)
public class EmbeddedNamespaceValidatorFactory extends AbstractPatternSettingsValidatorFactory {

    public EmbeddedNamespaceValidatorFactory() {
        super("netcentric-embedded-namespace", "allowedBundleSymbolicNamePatterns", false);
    }

    @Override
    protected Validator createValidator(
            @NotNull Set<Pattern> patterns, @NotNull ValidationContext context, @NotNull ValidatorSettings settings) {
        return new EmbeddedNamespaceValidator(settings.getDefaultSeverity(), patterns);
    }
}
