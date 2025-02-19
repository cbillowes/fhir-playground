(ns dev.curiousprogrammer.webapp.server-test
  (:require [clojure.test :refer :all]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :refer [response-for]]
            [com.stuartsierra.component :as component]
            [dev.curiousprogrammer.webapp.routes :as routes]
            [dev.curiousprogrammer.webapp.system :as system]))

(def url-for (route/url-for-routes
              (route/expand-routes routes/routes)))


(defn service-fn
  [system]
  (get-in system [:pedestal :service ::http/service-fn]))


(defmacro with-system
  [[bound-var binding-expr] & body]
  `(let [~bound-var (component/start ~binding-expr)]
     (try
       ~@body
       (finally
         (component/stop ~bound-var)))))


(deftest landing-page-test
  (with-system [sut (system/new-system :test)]                   ;; <1>
    (let [service               (service-fn sut)                 ;; <2>
          {:keys [status body]} (response-for service
                                              :get
                                              (url-for :home))] ;; <3>
      (is (= 200 status))                                        ;; <4>
      (is (= "Hello, world!" body)))))