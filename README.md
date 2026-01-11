
<div align="center">

<img src="YumeLira.jpg" width="96" alt="YumeMow Logo" />

## YumeMow

**A Windows folder icon batch configuration tool.**

</div>

### Features

- Batch assign folder icons by dragging folders onto icon cards  
- Built-in icon pack download support  
- Icon search and filtering for efficient navigation  

### Install

#### Option 1: Download from Release (Recommended)

Prebuilt binaries are available on the **Releases** page.  
Download the latest release and extract it to any directory, then run the executable.

> This is the recommended approach for most users.

#### Option 2: Build from Source

If you prefer to build the application manually, ensure that **JDK 17 or later** is installed, then execute the following commands in the project root directory.

##### Build distributable

```bash
./gradlew packageDistributable
````

The generated application can be found at:

```
composeApp/build/compose/binaries/main/app/
```

##### Run locally (development only)

```bash
./gradlew run
```

### Icon Packs

* Icon packs are downloaded to `~/.yume/IcoPacks/` by default
* Custom `.ico` files can be placed directly into this directory
* The application automatically scans and loads available icons from the directory

### Credits

* [https://github.com/icon11-community](https://github.com/icon11-community)
* [https://blog.cls.ink/2024/07/27/Set-Folder-Icon-and-Refresh-Cache-Immediately](https://blog.cls.ink/2024/07/27/Set-Folder-Icon-and-Refresh-Cache-Immediately)
