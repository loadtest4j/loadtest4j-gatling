# Gatling private API wrappers

Welcome to the **Danger Zone**!

Gatling does not (yet) expose all the functionality we need, so wrapper classes have been created in this specific package to give them access to Gatling's package-private classes and APIs. The Gatling project (like any other project) has no obligation to keep its package-private APIs stable, so expect to see breakages here during version upgrades of Gatling. 