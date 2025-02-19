(ns dev.curiousprogrammer.webapp.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.ring-middlewares :as middlewares]
            [clojure.java.io :as io]))


(def static-content-interceptor
  (middlewares/resource "/public"))


(def routes
  (route/expand-routes
   #{["/" :get (fn [_] {:status 200
                        :headers {"Content-Type" "text/html"}
                        :body (slurp (io/resource "public/index.html"))})
      :route-name :home]
     ;; This allows any file under `resources/public/` to be served directly
     ["/*" :get static-content-interceptor]}))


(def service
  {:env :prod
   ::http/routes routes
   ::http/type :jetty
   ::http/port 8081})


;; Start the server
(defn start []
  (http/start (http/create-server service)))


(defn stop []
  (http/stop (http/create-server service)))


(start)
;; Call (start) in the REPL or add `(start)` at the bottom of the file
