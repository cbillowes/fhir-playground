(ns dev.curiousprogrammer.server.core
  (:require [compojure.core :refer [routes]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.adapter.jetty :refer [run-jetty]]
            [taoensso.timbre :as logger]
            [dev.curiousprogrammer.server.routes :as r]))


(defonce server (atom nil))
(def ^:private port 3000)


(defn wrap-logging [handler]
  (fn [request]
    #_(logger/info "Request:" (:uri request) (:request-method request))
    (handler request)))


(defn app
  []
  (-> (routes
       ;; Note that the API routes need to go first
       ;; because web-routes have a capture all GET /*
       (-> (routes r/api-routes)
           (wrap-anti-forgery)
           (wrap-json-body)
           (wrap-json-response)
           (wrap-keyword-params))
       (-> (routes r/web-routes)
           (wrap-file "resources/public")
           (wrap-anti-forgery)))
      (wrap-defaults site-defaults)
      (wrap-session)
      (wrap-logging)))


(defn stop! []
  (when @server
    (logger/info "Stopping backend...")
    (.stop @server)
    (reset! server nil)))


(defn start! []
  (logger/info "Starting backend on port" port "...")
  (reset! server (run-jetty (app) {:port port :join? false}))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop!)))



(defn restart! []
  (stop!)
  (start!))


(defn -main [& _]
  (start!))


(comment

 (restart!)

  :rcf)
