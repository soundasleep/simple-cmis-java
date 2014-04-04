simple-cmis-java
================

A simple Java library for interacting with CMIS repositories, acts as a wrapper around the Apache CMIS bindings. Supports a handful of the most common actions, for example:

### Browsing folders

```java
CMISInterface cmis = new CMISInterface(getConfig());
CMISFolder folder = cmis.getFolderByPath("/Sites/afghanistan-one-health-hub/documentLibrary");
for (CMISObject obj : folder.getChildren()) {
  System.out.println("Found " + obj.getName());
}
for (CMISFolder obj : folder.getSubfolders()) {
  System.out.println("Found subfolder " + obj.getName());
}
```

### Uploading files

```java
CMISInterface cmis = new CMISInterface(getConfig());
CMISFolder folder = cmis.getFolderByPath("/Sites/afghanistan-one-health-hub/documentLibrary");
CMISDocument doc = folder.upload("test.txt", "Hello, World!".getBytes("UTF-8"), "text/plain", "This is a test file");

// or we can overwrite it
doc.replaceContents("Goodbye, world!".getBytes("UTF-8"), "text/plain");
```

## Usage

You can build using the provided Ant script, or use the recent JAR in `/deploy`.

You can also add this as a SVN external: `https://github.com/soundasleep/simple-cmis-java/trunk/deploy`
