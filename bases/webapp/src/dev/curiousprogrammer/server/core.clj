(ns dev.curiousprogrammer.server.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [routes GET]]
            [compojure.route :as route]))


(defn handler [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello from the backend!"})


(defn -main [& _]
  (println "Starting backend on port 3000...")
  (run-jetty handler {:port 3000 :join? false}))
