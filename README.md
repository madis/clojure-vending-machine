## Vending machine API in Clojure

The application is deployed at http://mad.is:3939
> The endpoints there have additional `/vending` prefix, e.g. `POST /vending/user`

### Running

There are scripts for convenience under `bin`
1. Run the tests: `./bin/kaocha` (or with `--watch` for development)
2. Start server: `./bin/start-server`
  - then access at `http://localhost:3939`
  - Swagger API documentation is available from the root url
3. For REPL tinkering: `./bin/repl`

## Corner cases and simplifications

Below are descriptions some examples of corner cases and simplifications that I'm aware of and should be addressed in a real world API of that kind.

- `POST /product` endpoint: filling the machine should consider the size of the item. So the machine would have internal map of the slots, in a simplified case (ignoring the height and width), the depth of the item slot. So when stocking particular product, its depth would be considered and error given if the available space has been filled

## TODO

- implement authentication (currently all requests are allowed)
- implement `/buy`, `/deposit` and `/reset` endpoints
