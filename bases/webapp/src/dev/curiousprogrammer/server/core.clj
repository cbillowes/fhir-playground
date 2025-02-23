(ns dev.curiousprogrammer.server.core
  (:require [compojure.core :refer [routes]]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [taoensso.timbre :as logger]
            [dev.curiousprogrammer.server.api :as api]))


(defonce server (atom nil))
(def ^:private port 3000)

(def app
  (-> (routes
       api/api-routes
       (route/not-found {:error "Not Found"}))
      wrap-json-body
      wrap-json-response))


(defn start-server []
  (logger/info "Starting backend on port" port "...")
  (reset! server (run-jetty app {:port port :join? false})))


(defn stop-server []
  (when @server
    (logger/info "Stopping backend...")
    (.stop @server)
    (reset! server nil)))


(defn -main [& _]
  (start-server)
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server)))


(-main)