(ns dev.curiousprogrammer.webapp.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.ring-middlewares :as middlewares]
            [taoensso.timbre :as logger]
            [clojure.java.io :as io]))


(defonce server-instance (atom nil))


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
   ::http/port 3000})


(defn stop []
  (when @server-instance
    (http/stop @server-instance)
    (reset! server-instance nil)
    (logger/info "Server stopped gracefully.")))


(defn start []
  (when (nil? @server-instance)
    (let [server (http/start (http/create-server service))]
      (reset! server-instance server)
      (logger/info "Server started on port 3000")
      (.addShutdownHook (Runtime/getRuntime)
                        (Thread. #(stop))))))


(start)
