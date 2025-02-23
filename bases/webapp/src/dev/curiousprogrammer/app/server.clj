(ns dev.curiousprogrammer.app.server
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [resource-response]]))

  (defroutes app-routes
    (GET "/*" [] (resource-response "index.html" {:root "public"}))
    (route/not-found "Not Found"))

  (def app
    (wrap-defaults app-routes site-defaults))