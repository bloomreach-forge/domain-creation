### Hippo Domain Creation Plugin

## Introduction
This plugin is used to automatically create simple domain rules for your project dynamically based on configuration options.

http://www.onehippo.org/library/concepts/security/repository-authorization-and-permissions.html

## Checkout trunk

## build trunk with mvn clean install

## Installation:

Add to you cms pom.xml:

<dependency>
    <groupId>org.onehippo.forge</groupId>
    <artifactId>domain-creation</artifactId>
    <version>1.01.02-SNAPSHOT</version>
</dependency>

## Usage:

1. Create a new document of type: 'authorization'

2. Select if you would like to create: author, editor and or admin groups

3. Select folders which the domain should have access to.

Publish document and the groups will be autocreated for you in the group management

Continue further with creating a new user and assigning group created group to the user.

Login with user and behold result of autocreated domain.


