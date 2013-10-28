# servel

A Clojure program that scraps Servel's database. It uses `pmap` with 10000 RUTs in batch, so as not to choke on memory.

## Usage

This assumes that you have a MongoDB instance up and running.

`lein run`

## License

Copyright © 2013 Sergio Campamá

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
