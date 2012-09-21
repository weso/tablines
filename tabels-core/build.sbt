name := "Tabels core"

version := "0.4-SNAPSHOT"

organization := "es.ctic.tabels"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

//libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.5.8"

libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "0.6.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "it,test"

libraryDependencies += "junit" % "junit" % "4.8.1" % "it,test"

libraryDependencies += "net.sourceforge.jexcelapi" % "jxl" % "2.6.12"

libraryDependencies += "net.sf.opencsv" % "opencsv" % "2.0"

//libraryDependencies += "net.sourceforge.nekohtml" % "nekohtml" % "1.9.15"

libraryDependencies += "nu.validator.htmlparser" % "htmlparser" % "1.2.1"

libraryDependencies += "com.hp.hpl.jena" % "jena" % "2.6.4"

libraryDependencies += "com.hp.hpl.jena" % "arq" % "2.8.8"

libraryDependencies += "com.hp.hpl.jena" % "tdb" % "0.8.10"

libraryDependencies += "commons-cli" % "commons-cli" % "1.2"

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "commons-io" % "commons-io" % "2.1"

libraryDependencies += "commons-configuration" % "commons-configuration" % "1.7"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "6.0.3"

libraryDependencies += "org.apache.lucene" % "lucene-core" % "3.4.0"

libraryDependencies += "org.apache.lucene" % "lucene-analyzers" % "3.4.0"

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.0"

libraryDependencies += "com.linuxense" % "javadbf" % "0.4.0"

// Geotools dependencies

libraryDependencies += "org.geotools" % "gt-shapefile" % "8.0-RC2"

libraryDependencies += "org.geotools" % "gt-swing" % "8.0-RC2" // FIXME Im not sure we will need this

libraryDependencies += "org.geotools" % "gt-epsg-hsql" % "8.0-RC2"

libraryDependencies += "org.geotools" % "gt-opengis" % "8.0-RC2"

libraryDependencies += "org.geotools.xsd" % "gt-xsd-kml" % "8.0-RC2"

// maplab-geotools

libraryDependencies += "es.ctic.maplab" % "maplab-geotools" % "0.4"

//managedStyle := ManagedStyle.Maven

publishTo := Some(Resolver.file("My local maven repo", file(Path.userHome + "/.m2/repository")))

//publishTo <<= (version) { version: String =>
//  val nexus = "http://wopr.fundacionctic.org:8081/nexus/content/repositories/"
//  if (version.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "snapshots/") 
//  else                                   Some("releases"  at nexus + "releases/")
//}

//credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")



libraryDependencies += "org.odftoolkit" % "odfdom-java" % "0.8.7"

libraryDependencies += "xerces" % "xercesImpl" % "2.9.1"

resolvers += "3rd party repo" at
"http://wopr.fundacionctic.org:8081/nexus/content/repositories/thirdparty/"

// maplab-geotools resolver
resolvers += "CTIC releases" at
"http://wopr.fundacionctic.org:8081/nexus/content/repositories/releases/"

// Geotools resolver
resolvers += "Open Source Geospatial Foundation Repository" at
"http://download.osgeo.org/webdav/geotools/"

