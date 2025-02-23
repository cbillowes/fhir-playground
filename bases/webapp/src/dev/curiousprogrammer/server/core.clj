(ns dev.curiousprogrammer.server.core
  (:require [compojure.core :refer [routes defroutes GET]]
            [compojure.route :as route]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer [resource-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [taoensso.timbre :as logger]
            [dev.curiousprogrammer.server.routes :as r]))


(defonce server (atom nil))
(def ^:private port 3000)


(defn wrap-logging [handler]
  (fn [request]
    (logger/info "Request:" (:uri request) (:request-method request))
    (handler request)))


(def app
  ;; Note that the API routes need to go first because web-routes have a capture all GET /*
  (-> (-> (routes r/api-routes)
          (wrap-json-body)
          (wrap-json-response))
      (-> (routes r/web-routes)
          (wrap-defaults site-defaults)
          (wrap-file "resources/public"))
      (wrap-logging)))



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


;;(-main)

(comment

 (do
   (stop-server)
   (-main))


  )
