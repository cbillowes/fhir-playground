(ns dev.curiousprogrammer.service.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [cheshire.core :as json]
            [dev.curiousprogrammer.fhir.interface :as fhir]))


(def routes
  (route/expand-routes
   #{["/health" :get (fn [_] {:status 200 :body "OK"}) :route-name :health]
     ["/patients"
      :get (fn [_]
             {:status 200
              :headers {"Content-Type" "application/json"}
              :body (json/generate-string
                     (fhir/fetch-patients))})
      :route-name :patients]}))


(def service
  {:env :prod
   ::http/routes routes
   ::http/type :jetty
   ::http/port 4334})


(defn start []
  (http/start (http/create-server service)))


(start)