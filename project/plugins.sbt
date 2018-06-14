addSbtPlugin("com.dwijnand" % "sbt-dynver" % "3.0.0")
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.5.1")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.0.0")
addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.5.0")
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.3.2")
addSbtPlugin("com.lightbend" % "sbt-whitesource" % "0.1.10")
addSbtPlugin("com.thoughtworks.sbt-api-mappings" % "sbt-api-mappings" % "2.1.0")
addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.4")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.3.0")
addSbtPlugin("lt.dvim.paradox" % "sbt-paradox-local" % "0.1")
// latest version with https://github.com/ehsanyou/sbt-docker-compose/pull/3
addSbtPlugin("com.github.ehsanyou" % "sbt-docker-compose" % "926a4d83")
resolvers += Resolver.bintrayIvyRepo("2m", "sbt-plugins")

addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.10.0-20180605-26e7708-streams")
resolvers += Resolver.url("lightbend-commercial",
  url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)
