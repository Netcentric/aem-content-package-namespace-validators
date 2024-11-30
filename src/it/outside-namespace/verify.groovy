String buildLog = new File(basedir, 'build.log').text

// FIXME: will also convert escape sequences in pattern
// normalize file separator
if (File.separator ==  '\\') {
    buildLog = buildLog.replaceAll('\\', '/')
}

// application-package
assert buildLog.contains('''[ERROR] ValidationViolation: Filter root '/apps/mytenant' is not allowed (does not match any of the allowed patterns [/apps/mytenant2(/.*)?,/conf/mytenant2(/.*)?,/home/users/mytenant2(/.*)?,/oak:index/mytenant2-(.*)]) @ META-INF/vault/filter.xml, validator: netcentric-filter-namespace
[ERROR] ValidationViolation: Filter root '/oak:index/mytenant-custom-1' is not allowed (does not match any of the allowed patterns [/apps/mytenant2(/.*)?,/conf/mytenant2(/.*)?,/home/users/mytenant2(/.*)?,/oak:index/mytenant2-(.*)]) @ META-INF/vault/filter.xml, validator: netcentric-filter-namespace
[ERROR] ValidationViolation: Package group 'biz.netcentric.filevault.validator.aem.namespace.it' is not allowed (does not match any of the group patterns [invalid-group]) @ META-INF/vault/properties.xml, validator: netcentric-packageid-namespace
[ERROR] ValidationViolation: Package name 'application-package' is not allowed (does not match any of the name patterns [invalid-name]) @ META-INF/vault/properties.xml, validator: netcentric-packageid-namespace
[ERROR] ValidationViolation: Oak Query Index uses path restriction '/content/mytenant/something' which is not allowed (does not match any of the allowed patterns [/content/mytenant2(/.*)?]) @ jcr_root/_oak_index/mytenant-custom-1/.content.xml, line 10, column 57, validator: netcentric-oakindex-namespace, JCR node path: /oak:index/mytenant-custom-1
[ERROR] ValidationViolation: Client Library's categories contains 'mytenant-librarya' which is not allowed (does not match any of the allowed patterns [mytenant2-.*]) @ jcr_root/apps/mytenant/clientlibrary1/.content.xml, line 4, column 57, validator: netcentric-clientlibrary-namespace, JCR node path: /apps/mytenant/clientlibrary1
[ERROR] ValidationViolation: Client Library's categories contains 'mytenant-libraryb' which is not allowed (does not match any of the allowed patterns [mytenant2-.*]) @ jcr_root/apps/mytenant/clientlibrary1/.content.xml, line 4, column 57, validator: netcentric-clientlibrary-namespace, JCR node path: /apps/mytenant/clientlibrary1
[ERROR] ValidationViolation: Resource is using type '/apps/othertenant/components/component2' which is not allowed (does not match any of the allowed patterns [/apps/mytenant2/components/.*]) @ jcr_root/apps/mytenant/components/component1/.content.xml, line 5, column 72, validator: netcentric-resourcetype-namespace, JCR node path: /apps/mytenant/components/component1
[ERROR] ValidationViolation: Resource is using super type '/apps/othertenant/components/component1' which is not allowed (does not match any of the allowed patterns [/apps/mytenant2/components/.*]) @ jcr_root/apps/mytenant/components/component1/.content.xml, line 5, column 72, validator: netcentric-resourcetype-namespace, JCR node path: /apps/mytenant/components/component1''') : 'application-package'


// content-package
assert buildLog.contains('''[ERROR] ValidationViolation: Filter root '/home/users/mytenant' is not allowed (does not match any of the allowed patterns [/apps/mytenant2(/.*)?,/conf/mytenant2(/.*)?,/home/users/mytenant2(/.*)?,/oak:index/mytenant2-(.*)]) @ META-INF/vault/filter.xml, validator: netcentric-filter-namespace
[ERROR] ValidationViolation: Filter root '/conf/mytenant' is not allowed (does not match any of the allowed patterns [/apps/mytenant2(/.*)?,/conf/mytenant2(/.*)?,/home/users/mytenant2(/.*)?,/oak:index/mytenant2-(.*)]) @ META-INF/vault/filter.xml, validator: netcentric-filter-namespace
[ERROR] ValidationViolation: Package group 'biz.netcentric.filevault.validator.aem.namespace.it' is not allowed (does not match any of the group patterns [invalid-group]) @ META-INF/vault/properties.xml, validator: netcentric-packageid-namespace
[ERROR] ValidationViolation: Package name 'content-package' is not allowed (does not match any of the name patterns [invalid-name]) @ META-INF/vault/properties.xml, validator: netcentric-packageid-namespace
[ERROR] ValidationViolation: Principal name 'mytenant-myuser' is not allowed (does not match any of the principal name patterns [mytenant2-.*]) @ jcr_root/home/users/mytenant/myuser/.content.xml, line 8, column 41, validator: netcentric-authorizable-namespace, JCR node path: /home/users/mytenant/myuser
[ERROR] ValidationViolation: Authorizable ID 'mytenant-myuser' is not allowed (does not match any of the authorizable ID patterns [mytenant2-.*]) @ jcr_root/home/users/mytenant/myuser/.content.xml, line 8, column 41, validator: netcentric-authorizable-namespace, JCR node path: /home/users/mytenant/myuser''') : 'content-package'


// container-package
assert buildLog.contains('''[ERROR] ValidationViolation: Filter root '/apps/mytenant/config' is not allowed (does not match any of the allowed patterns [/apps/mytenant2(/.*)?,/conf/mytenant2(/.*)?,/home/users/mytenant2(/.*)?,/oak:index/mytenant2-(.*)]) @ META-INF/vault/filter.xml, validator: netcentric-filter-namespace
[ERROR] ValidationViolation: Package group 'biz.netcentric.filevault.validator.aem.namespace.it' is not allowed (does not match any of the group patterns [invalid-group]) @ META-INF/vault/properties.xml, validator: netcentric-packageid-namespace
[ERROR] ValidationViolation: Package name 'container-package' is not allowed (does not match any of the name patterns [invalid-name]) @ META-INF/vault/properties.xml, validator: netcentric-packageid-namespace
[ERROR] ValidationViolation: OSGi configuration PID 'com.example.mytenant.MyComponent2' is not allowed to be configured (does not match any of the allowed patterns [com\\.example\\.mytenant2\\..*]) @ jcr_root/apps/mytenant/config/com.example.mytenant.MyComponent2.cfg.json, validator: jackrabbit-osgiconfigparser
[ERROR] ValidationViolation: OSGi configuration PID 'com.example.mytenant.MyComponent' is not allowed to be configured (does not match any of the allowed patterns [com\\.example\\.mytenant2\\..*]) @ jcr_root/apps/mytenant/config/com.example.mytenant.MyComponent~name.cfg.json, validator: jackrabbit-osgiconfigparser
[ERROR] ValidationViolation: OSGi factory configuration PID 'com.example.mytenant.MyComponent' is not allowed with the given subname 'name' (does not match any of the allowed patterns [othername.*]) @ jcr_root/apps/mytenant/config/com.example.mytenant.MyComponent~name.cfg.json, validator: jackrabbit-osgiconfigparser''') : 'container-package'

return true