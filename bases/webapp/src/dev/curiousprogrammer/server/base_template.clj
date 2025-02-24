(ns dev.curiousprogrammer.server.base-template
  (:require [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [hiccup.core :as h]))


(defn base-template []
  (h/html
   [:html
    [:head
     [:title "FHIR Database"]
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     (let [csrf-token (force *anti-forgery-token*)]
       [:meta {:name "csrf-token"
               :content csrf-token}])
     [:link {:rel "stylesheet" :href "/css/style.min.css" :type "text/css"}]]
    [:body.bg-gray-800
     (anti-forgery-field)
     [:div#app]
     [:script {:src "/js/main_bundle.js" :type "text/javascript"}]]]))
