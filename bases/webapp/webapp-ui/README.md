# Webapp UI

## From scratch

[Figwheel Tutorial](https://figwheel.org/tutorial)

```bash
# Install clj-new https://github.com/seancorfield/clj-new
clojure -Ttools install-latest :lib com.github.seancorfield/clj-new :as clj-new
```

```bash
# Create the template https://rigsomelight.com/figwheel-main-template/
clojure -Tclj-new create :template figwheel-main :name dev.curiousprogrammer/webapp-ui :args '["+deps", "+npm-bundle", "--reagent"]'
```

```bash
clojure -M:fig:build
```

```bash
# To clean all compiled files:
rm -rf target/public
```

Initial tests are in the test directory.
You will have tests that are updated live with each file change if you open a tab to http://localhost:9500/figwheel-extra-main/auto-testing

```bash
clj -M:fig:test
```

## License

Copyright © 2025 Clarice Bouwer

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
