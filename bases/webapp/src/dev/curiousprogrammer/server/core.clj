(ns dev.curiousprogrammer.server.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [routes GET]]
            [compojure.route :as route]
            [taoensso.timbre :as logger]))


(defonce server (atom nil))
(def ^:private port 3000)


(defn handler [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello from the backend!"})


(defn start-server []
  (logger/info "Starting backend on port" port "...")
  (reset! server (run-jetty handler {:port port :join? false})))


(defn stop-server []
  (when @server
    (logger/info "Stopping backend...")
    (.stop @server)
    (reset! server nil)))


(defn -main [& _]
  (start-server)
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server)))
