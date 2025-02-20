(ns dev.curiousprogrammer.webapp.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [taoensso.timbre :as logger]))

(def port 3000)


(defonce *server (atom nil))


(def routes
  (route/expand-routes
   #{["/api/" :get (fn [_] {:status 200
                        :headers {"Content-Type" "text/plain"}
                        :body "OK"})
      :route-name :home]}))


(def service
  {:env :prod
   ::http/routes routes
   ::http/type :jetty
   ::http/port port
   #_#_::http/middlewares [(wrap-cors
                        :access-control-allow-origin [#".*"]
                        :access-control-allow-methods [:get :post :put :delete]
                        :access-control-allow-headers ["Content-Type" "Authorization"])]})


(defn stop []
  (logger/info "Trying to stop server...")
  (if @*server
   (do
     (reset! *server nil)
     (http/stop service)
     (logger/info "Server stopped gracefully."))
    (logger/info "Server already stopped.")))


(defn start []
  (logger/info "Trying to start server...")
  (if (nil? @*server)
    (do
      (reset! *server (http/create-server service))
      (http/start @*server)
      (logger/info "Server already started.")
      (.addShutdownHook (Runtime/getRuntime)
                        (Thread. #(stop))))
    (logger/info "Server already running.")))


(defn -main
  [& args]
  (start))