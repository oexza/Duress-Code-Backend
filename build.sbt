name := """durress-code"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  
  
  "com.mashape.unirest"    %  "unirest-java"        % "1.4.9"
)
