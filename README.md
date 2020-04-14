2020-04-14 Christoph Knabe

# ZIO-Trial

This project tries ZIO according to the instruction [Getting Started with ZIO](https://zio.dev/docs/getting_started.html). But the artifact `zio` is taken from the vendor `zio` instead of former `org.scalaz`.

Additionally it demonstrates in package `zio_layer` how to combine a `Logging` `ZLayer` from a `Console` `ZLayer` and a `Clock` `ZLayer` as it is recommended from ZIO 1.0.0 RC 18.