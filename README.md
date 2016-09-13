# mateu-ui

mateu-ui is an imperative UI framework.

It is based on a core project containing the base classes to define your user interface in a tech agnostic way and several ports to run your app using different technologies.

Here some features:

- it is for building bussiness applications (not for building websites. For doing that please check mateu-cms)
- it is for developers
- you only need to know basic java (not html, no javascript, no ...)
- define your screens using fluent style, plain java
- take advantage of java (inheritance, polymorphism, anonymous classes, ...) when building your ui


It aims to reach several goals:

- protect you against technologies trends
- let you focus on bussiness problems, not on ui
- hide client-server communication implementation
- limit developer freedom (yes, I know it does not sound nice, but it is very important) by forcing good practices

I hope you like it ;)

Building blocks
---

For every user interface framework you have the building blocks (aka components).

These are ours:

- app
- views
- components

And here are some class diagrams:

![alt text](http://yuml.me/3fb671cf, "app class diagram")

![alt text](http://yuml.me/43de1929, "views class diagram")

![alt text](http://yuml.me/a48f9dec, "components class diagram")

