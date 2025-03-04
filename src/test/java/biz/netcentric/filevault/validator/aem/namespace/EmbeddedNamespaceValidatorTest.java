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

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddedNamespaceValidatorTest {

    @Test
    void testIsEmbeddedBundle() {
        assertTrue(EmbeddedNamespaceValidator.isEmbeddedBundle(Paths.get("apps", "myapp", "install", "mybundle.jar")));
        // with run modes
        assertTrue(EmbeddedNamespaceValidator.isEmbeddedBundle(
                Paths.get("apps", "myapp", "install.author", "mybundle.jar")));
        assertTrue(EmbeddedNamespaceValidator.isEmbeddedBundle(
                Paths.get("apps", "myapp", "install.author.test", "mybundle.jar")));
        // outside /apps
        assertFalse(EmbeddedNamespaceValidator.isEmbeddedBundle(Paths.get("conf", "myapp", "install", "mybundle.jar")));
        // at max depth
        assertTrue(EmbeddedNamespaceValidator.isEmbeddedBundle(
                Paths.get("apps", "myapp", "child", "child", "install", "mybundle.jar")));
        // below max depth
        assertFalse(EmbeddedNamespaceValidator.isEmbeddedBundle(
                Paths.get("apps", "myapp", "child", "child", "child", "child", "install", "mybundle.jar")));
        // no jar
        assertFalse(EmbeddedNamespaceValidator.isEmbeddedBundle(Paths.get("apps", "myapp", "install", "mybundle.zip")));
    }
}
