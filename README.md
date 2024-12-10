# AEM FileVault Content Package Namespace Validators

[![Build Status](https://img.shields.io/github/actions/workflow/status/Netcentric/aem-content-package-namespace-validators/maven.yml?branch=main)](https://github.com/Netcentric/aem-content-package-namespace-validators/actions)
[![License](https://img.shields.io/badge/License-EPL%202.0-red.svg)](https://opensource.org/licenses/EPL-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/biz.netcentric.filevault.validator/aem-content-package-namespace-validators)](https://central.sonatype.com/artifact/biz.netcentric.filevault.validator/aem-content-package-namespace-validators)
[![SonarCloud Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Netcentric_aem-content-package-namespace-validators&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Netcentric_aem-content-package-namespace-validators)
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=Netcentric_aem-content-package-namespace-validators&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Netcentric_aem-content-package-namespace-validators)

## Overview

Validates that FileVault content packages stick to certain namespacing rules. This is helpful to make sure that separate AEM applications may run in parallel on the same server without stepping on each other toes. This is particularly useful with [multiple teams working on the same AEM environment](https://experienceleague.adobe.com/en/docs/experience-manager-learn/assets/deployment/multitenancy-concurrent-article-understand) (also outlined in [Considerations for a multi-team setup](https://experienceleague.adobe.com/en/docs/experience-manager-cloud-service/content/implementing/using-cloud-manager/managing-code/enterprise-team-dev-setup#considerations)).

There are several validators included in this artifact, all relate to namespacing rules for certain aspects of AEM:

1. [FileVault Content Package Filter][filevault-workspace-filter] (`root` attribute of each `filter` element)
1. [FileVault Content Package ID][filevault-package-id] (both `group` and optionally `name` package properties)
1. [Oak Authorizables][oak-authorizables](`rep:principalName` and optionally `rep:authorizableId` properties of users/groups)
1. [Oak Query Index Definition][oakindex] (path restrictions for [Lucene][oakindex-lucene-pathrestrictions] or [Property][oakindex-property-pathrestrictions] index definitions)
1. [OSGi Configuration][osgi-installer-configurations]
1. [Sling Resource Type and Resource Super Type][sling-resource-type] (`sling:resourceType` and `sling:resourceSuperType` properties)
1. [AEM Client Library][aem-clientlibrary] (`categories` property)

Namespacing has been explicitly mentioned in [Achim Koch's Blog: Hosting Multiple Tenants on AEM](https://blog.developer.adobe.com/hosting-multiple-tenants-on-aem-815c8ed0c9f9) but obviously namespacing is just one of multiple aspects to consider for multi-tenant AEM environments.

## Implementation

This artifact provides multiple validator implementations for the [FileVault Validation Module][filevault-validation] and can be used for example with the [filevault-package-maven-plugin][filevault-p-m-p] like outlined below.


## Settings

The following options are supported apart from the default settings mentioned in [FileVault validation][filevault-validation].
Leaving the validators with the default options will not emit validation issues at all, i.e. none of the options are mandatory.


Validator ID | Option | Description
--- | --- | --- 
`netcentric-filter-namespace` | `allowedPathPatterns` | Comma-separated list of regular expression patterns. Each package filter `root` must match at least one of the given patterns.
`netcentric-packageid-namespace` | `allowedGroupPatterns` | Comma-separated list of regular expression patterns. The package's group must match at least one of the given patterns.
`netcentric-packageid-namespace` | `allowedNamePatterns` | Comma-separated list of regular expression patterns. The package's name must match at least one of the given patterns.
`netcentric-authorizable-namespace` | `allowedPrincipalNamePatterns` | Comma-separated list of regular expression patterns. The authorizable's `rep:principalName` must match at least one of the given patterns.
`netcentric-authorizable-namespace` | `allowedAuthorizableIdPatterns` | Comma-separated list of regular expression patterns. The authorizable's `rep:authorizableId` or its node name (if the property does not exist( must match at least one of the given patterns.
`netcentric-authorizable-namespace` | `allowedAuthorizableIdPatterns` | Comma-separated list of regular expression patterns. The authorizable's `rep:authorizableId` or its node name (if the property does not exist) must match at least one of the given patterns.
`netcentric-oakindex-namespace` | `allowedPathPatterns` | Comma-separated list of regular expression patterns. Each Oak index definition's path restriction (for `lucene` index types][oakindex-lucene-pathrestrictions] or [`property` index types][oakindex-property-pathrestrictions]) must match at least one of the given patterns.
`netcentric-osgiconfig-namespace` | `allowedPidPatterns` | Comma-separated list of regular expression patterns. Each (non-factory) configuration name given via the [OSGi Installer][osgi-installer-configurations] must have a PID matching at least one of the given patterns.
`netcentric-osgiconfig-namespace` | `allowedFactoryPidNames` | Comma-separated list of regular expression patterns. Each factory configuration name given via the [OSGi Installer][osgi-installer-configurations] must have a name matching at least one of the given patterns.
`netcentric-osgiconfig-namespace` | `restrictFactoryConfigurationsToAllowedPidPatterns` | Boolean flag, `false` by default. If set to `true` each factory configuration PID given via the [OSGi Installer][osgi-installer-configurations] must also matching at least one of the given patterns from `allowedPidPatterns`.
`netcentric-resourcetype-namespace` | `allowedTypePatterns` | Comma-separated list of regular expression patterns. Each `sling:resourceType` property of arbitrary JCR nodes must match at least one of the given patterns.
`netcentric-resourcetype-namespace` | `allowedSuperTypePatterns` | Comma-separated list of regular expression patterns. Each `sling:resourceSuperType` property of arbitrary JCR nodes must match at least one of the given patterns.
`netcentric-clientlibrary-namespace` | `allowedCategoryPatterns` | Comma-separated list of regular expression patterns. Each [client library's `categories` value][aem-clientlibrary] must match at least one of the given patterns.

*Due to the use of comma-separated strings it is not possible to use a comma within the regular expressions. However, as those are matched against names/paths (which don't allow a comma anyhow) using the comma inside the regular expressions shouldn't be necessary anyhow.*

## Fix Violations

Make the relevant attribute value/name/property value match one of the given patterns.

## Usage with Maven

You can use this validator with the [FileVault Package Maven Plugin][filevault-p-m-p] in version 1.4.0 or higher like this

```
<plugin>
  <groupId>org.apache.jackrabbit</groupId>
  <artifactId>filevault-package-maven-plugin</artifactId>
  <configuration>
    <validatorsSettings>
      <netcentric-authorizable-namespace>
        <options>
            <allowedPrincipalNamePatterns>mytenant-.*</allowedPrincipalNamePatterns>
            <allowedAuthorizableIdPatterns>mytenant-.*</allowedAuthorizableIdPatterns>
        </options>
      </netcentric-authorizable-namespace>
      <netcentric-clientlibrary-namespace>
        <options>
            <allowedCategoryPatterns>mytenant-.*</allowedCategoryPatterns>
        </options>
      </netcentric-clientlibrary-namespace>
     <netcentric-filter-namespace>
        <options>
            <allowedFilterRootPatterns>/apps/mytenant(/.*)?,/conf/mytenant(/.*)?,/home/users/mytenant(/.*)?,/oak:index/mytenant-(.*)</allowedFilterRootPatterns>
        </options>
      </netcentric-filter-namespace>
      <netcentric-oakindex-namespace>
        <options>
            <allowedPathPatterns>/content/mytenant(/.*)?</allowedPathPatterns>
        </options>
      </netcentric-oakindex-namespace>
      <netcentric-osgiconfig-namespace>
        <options>
            <allowedPidPatterns>com\.example\.mytenant\..*</allowedPidPatterns>
            <allowedFactoryPidNames>name.*</allowedFactoryPidNames>
            <restrictFactoryConfigurationsToAllowedPidPatterns>true</restrictFactoryConfigurationsToAllowedPidPatterns>
        </options>
      </netcentric-osgiconfig-namespace>
      <netcentric-packageid-namespace>
        <options>
             <allowedGroupPatterns>biz\.netcentric\.filevault\.validator\.aem\.namespace\.it</allowedGroupPatterns>
             <allowedNamePatterns>.*-package</allowedNamePatterns>
        </options>
      </netcentric-packageid-namespace>
      <netcentric-resourcetype-namespace>
        <options>
            <allowedSuperTypePatterns>/apps/mytenant2/components/.*</allowedSuperTypePatterns>
            <allowedTypePatterns>/apps/mytenant2/components/.*</allowedTypePatterns>
        </options>
      </netcentric-resourcetype-namespace>
    </validatorsSettings>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>biz.netcentric.filevault.validators</groupId>
      <artifactId>aem-namespacing-content-package-validator</artifactId>
      <version><latestversion></version>
    </dependency>
  </dependencies>
</plugin>
```

Adobe, and AEM are either registered trademarks or trademarks of Adobe in the United States and/or other countries.

[aemanalyser-maven-plugin]: https://github.com/adobe/aemanalyser-maven-plugin/tree/main/aemanalyser-maven-plugin
[filevault-validation]: https://jackrabbit.apache.org/filevault/validation.html
[filevault-p-m-p]: https://jackrabbit.apache.org/filevault-package-maven-plugin/index.html
[filevault-workspace-filter]: https://jackrabbit.apache.org/filevault/filter.html
[oakindex]: https://jackrabbit.apache.org/oak/docs/query/indexing.html#index-defnitions
[oakindex-lucene-pathrestrictions]: https://jackrabbit.apache.org/oak/docs/query/lucene.html#path-restrictions
[oakindex-property-pathrestrictions]: https://jackrabbit.apache.org/oak/docs/query/property-index.html
[aem-clientlibrary]: https://experienceleague.adobe.com/en/docs/experience-manager-cloud-service/content/implementing/developing/full-stack/clientlibs#clientlib-folders
[osgi-installer-configurations]: https://sling.apache.org/documentation/bundles/configuration-installer-factory.html#applying-of-configurations
[filevault-package-id]: https://jackrabbit.apache.org/filevault/properties.html
[sling-resource-type]: https://sling.apache.org/documentation/the-sling-engine/resources.html#resource-types
[oak-authorizables]: https://jackrabbit.apache.org/oak/docs/security/user/default.html#representation-in-the-repository

