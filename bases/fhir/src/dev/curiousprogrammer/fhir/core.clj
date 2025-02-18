(ns dev.curiousprogrammer.fhir.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]))



(def routes
  (route/expand-routes
   #{["health" :get (fn [_] {:status 200 :body "OK"}) :route-name :health]}))


(def service
  {:env :prod
   ::http/routes routes
   ::http/type :jetty
   ::http/port 4334})


(defn start []
  (http/start (http/create-server service)))
