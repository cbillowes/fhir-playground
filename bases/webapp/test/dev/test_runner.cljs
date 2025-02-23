;; This test runner is intended to be run from the command line
(ns dev.test-runner
   ;; require all the namespaces that you want to test
  (:require [dev.curiousprogrammer.app.core]
            [figwheel.main.testing :refer [run-tests-async]]))

(defn -main [& args]
  (run-tests-async 5000))
