# NetpbmImageIO

Java ImageIO plugin that adds support for Netpbm image files.

## Netpbm Image Files 

[Netpbm](https://netpbm.sourceforge.net/) is a toolkit for 
manipulation of graphics images. It used to be popular on Unix 
systems to do image manipulation such as scaling, cropping, 
creating sequences, etc.

Although still supported by most Unix systems the Toolkit itself
is considered a bit obsolete. But the simple image formats that
come with the toolkit might still be useful. These image files
are not very economical when it comes to their storage size but
their simple structure might have some benefits over other file
formats. For instance, the plain text versions of these files
are human readable, and the raw formats are basically just a 
a binary stream of the bitmaps image data. This makes them still
be useful as an intermediate format or when developing software
for example.

## The Netpbm ImageIO plugin
 
The Netpbm ImageIO plugin currently provides read support for
the three most common Netpbm image file formats, which are:

- [pbm or portable bitmap](https://netpbm.sourceforge.net/doc/pbm.html) (monochrome bitmap)
- [pgm or portable graymap](https://netpbm.sourceforge.net/doc/pgm.html) (grayscale bitmap)
- [ppm or portable pixelmap](https://netpbm.sourceforge.net/doc/ppm.html) (RGB bitmap)

Both raw/binary and plain text formats are supported.

## How to use the plugin

The project is an [implementation of an ImageIO plugin](https://docs.oracle.com/javase/8/docs/technotes/guides/imageio/spec/extending.fm1.html). 
This means that once built, the resulting jar only needs to be 
available on your project's classpath. If so, java ImageIO will 
automatically add support for Netpbm image files. For instance, if 
you are are using Maven:

1) Build the plugin so that it will be available in your local maven repository:

```sh
	mvn clean install
```

2) Add the dependency to your project's pom.xml:

```xml
	...
	<dependencies>
		<dependency>
			<groupId>org.scrivo.imageio</groupId>
			<artifactId>imageio-netpbm</artifactId>
			<version>1.0.0</version>
		</dependency>
		...
	</dependencies>
	...
```

2) Then use it as follows:

```java
	BufferedImage img = ImageIO.read("/home/geert/image.bpm");

```

That should be all there is to it.

## Housekeeping

The build in Eclipse code formatter was used with the exception that
it was set to maintain line breaks in code.

The code passes a SonarLint check based on SonarLint's default settings.

## Acknowledgments

- The people of the [Netpbm project](https://netpbm.sourceforge.net/) (current maintainer: Bryan Henderson)
- The plugin code was adopted from [Writing Image I/O Plug-ins](https://docs.oracle.com/javase/8/docs/technotes/guides/imageio/spec/extending.fm1.html)
- Sample images were used from [Wikipedia](https://en.wikipedia.org/wiki/Netpbm), [The Gimp](www.gimp.org) and [Potrace](https://potrace.sourceforge.net/)

## License

[BSD](LICENSE.md)

