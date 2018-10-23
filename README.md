[![Build Status](https://travis-ci.org/onehippo-forge/domain-creation.svg?branch=develop)](https://travis-ci.org/onehippo-forge/domain-creation)

# Hippo Domain Creation Plugin

This plugin is used to automatically create simple domain rules for your project dynamically based on configuration options.

For reference, see the page [repository authorization and permissions](http://www.onehippo.org/library/concepts/security/repository-authorization-and-permissions.html)

## Installation
Make sure to have this repository definition to the main project pom.xml:

``` xml
<repository>
  <id>hippo-forge</id>
  <name>Hippo Forge maven 2 repository.</name>
  <url>https://maven.onehippo.com/maven2-forge/</url>
  <snapshots>
    <enabled>false</enabled>
  </snapshots>
  <releases>
    <updatePolicy>never</updatePolicy>
  </releases>
  <layout>default</layout>
</repository>
```

Add this dependency your CMS's pom.xml:
``` xml
<dependency>
    <groupId>org.onehippo.forge</groupId>
    <artifactId>domain-creation</artifactId>
    <version>2.0.0</version>
</dependency>
```

On Hippo 12, be sure your project's application HCM module bootstraps after "hippo-forge":
``` yaml
group: 
  name: myhippoproject
  after: [hippo-cms, hippo-forge]
project: myhippoproject
module:
  name: myhippoproject-repository-data-application
```

## Usage
1. Create a new document of type 'authorization'. Its name will be used as prefix to the generated user groups and security domains.
2. Select if you would like to create: author, editor and/or admin groups.
3. Select folders which the domain should have access to.
4. Publish the document and the user groups and security domains will be autocreated.
5. Create a new user and assign the newly created group to the user.
6. Login with user and behold result of autocreated domain.

## Release notes
### 23 October 2018 Release 2.0.1
- In domains, replace hippo:paths with jcr:path
- Add path-specific domain rules for hippo:request nodes
- Fix issue with not being able to select 2+ folders in authorization document
- Use deamon module's session instead of local admin/admin

### 25 May 2018 Release 2.0.0
- Upgrade to Hippo CMS 12

### 26 Sept 2014 Release 1.01.01
- Initial release for Hippo CMS 11
 
## Documentation 

No documentation site available at [onehippo-forge.github.io/](https://onehippo-forge.github.io/) for this plugin.
