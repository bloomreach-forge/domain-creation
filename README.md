[![Build Status](https://travis-ci.org/onehippo-forge/domain-creation.svg?branch=develop)](https://travis-ci.org/onehippo-forge/domain-creation)

# Hippo Domain Creation Plugin

This plugin is used to automatically create simple domain rules for your project dynamically based on configuration options.

For reference, see the page [repository authorization and permissions](http://www.onehippo.org/library/concepts/security/repository-authorization-and-permissions.html)

## Installation
Add this repository definition to the main project pom.xml:

```
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

Add to your CMS pom.xml:
(for v12, there's a slight catch. Exclusion is needed because com.github.jknack:handlebars:1.3.1 introduces the same jar
with different version)
```xml
<dependency>
    <groupId>org.onehippo.forge</groupId>
    <artifactId>domain-creation</artifactId>
    <version>1.01.01</version>
    <exclusions>
    <!--because of com.github.jknack:handlebars:1.3.1 -->
      <exclusion>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```
Also remember to add the plugin as a dependency in your v12 project at: /repository-data/application/src/main/resources/hcm-module.yaml

See the "after":
```yaml
group: 
  name: myhippoproject
  after: [hippo-cms, domain-creation]
project: myhippoproject
module:
  name: myhippoproject-repository-data-application
```

## Usage

1. Create a new document of type 'authorization'.
2. Select if you would like to create: author, editor and or admin groups.
3. Select folders which the domain should have access to.
4. Publish the document and the groups will be autocreated for you in the group management.
5. Create a new user and assign the newly created group to the user.
6. Login with user and behold result of autocreated domain.

## Release notes
_26-09-2014 Release 1.01.01_

- Initial release for Hippo 11
  

## Documentation 

No documentation site available at [onehippo-forge.github.io/](https://onehippo-forge.github.io/) for this plugin.
