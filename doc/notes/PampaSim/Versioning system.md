# Policy
release tags follow the default maven-release-plugin format:
`PampaSim-MAJOR.MINOR.PATCH-QUALIFIER`
version 2.3.0-SNAPSHOT is represented by
`PampaSim-2.3.0-SNAPSHOT`, for example.
Tags are only needed for releases, really.

## SemVer
We don't really follow semantic versioning, major vs. minor bumps are vibes based. If the graphical interface ever gets a major overhaul, that'd warrant a major bump. All minor version bumps for now.

# Bumping versions
`$ mvn release:prepare` should be enough to bump patch or minor version numbers. Once a feature branch reaches its end, one should 

Relevant pages:
https://stackoverflow.com/questions/9554688/maven-versioning-best-practices
https://www.baeldung.com/maven-versioning