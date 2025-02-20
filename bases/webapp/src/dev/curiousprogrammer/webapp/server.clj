(ns dev.curiousprogrammer.webapp.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [nrepl.server :as nrepl]
            [taoensso.timbre :as logger]))

(def port 3000)


(defonce *server (atom nil))
(defonce *repl-server (atom nil))


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
  (when @*server
    (http/stop @*server)
    (reset! *server nil)
    (logger/info "⛔ Webserver stopped"))

  (when @*repl-server
    (nrepl/stop-server @*repl-server)
    (reset! *repl-server nil)
    (logger/info "⛔ nREPL stopped")))


(defn start-server []
  (logger/info "Trying to start server...")
  (if (nil? @*server)
    (do
      (reset! *server (http/create-server service))
      (http/start @*server)
      (logger/info (str "🚀 Server started on" port))
      (.addShutdownHook (Runtime/getRuntime)
                        (Thread. #(stop))))
    (logger/info "Server already running.")))


(defn start-repl []
  (when (nil? @*repl-server)
    (let [server (nrepl/start-server :port 7000)]
      (reset! *repl-server server)
      (logger/info "🔁 nREPL started on port 7000"))))


(def ring-handler
  (http/create-servlet service))


(defn -main
  [& args]
  (start-server)
  (start-repl))