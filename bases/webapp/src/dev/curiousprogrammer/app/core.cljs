(ns ^:figwheel-hooks dev.curiousprogrammer.app.core
  (:require [goog.dom :as gdom]
            [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [dev.curiousprogrammer.app.ui :as ui]
            [dev.curiousprogrammer.app.router :as router]
            [dev.curiousprogrammer.app.pages.home :as home]
            [dev.curiousprogrammer.app.pages.patients :as patients]
            [dev.curiousprogrammer.app.pages.not-found :as not-found]
            ;; Load when evaluating this ns
            [dev.curiousprogrammer.app.events]
            [dev.curiousprogrammer.app.subs]))


(defn- get-app-element []
  (gdom/getElement "app"))


(defn- pages
  []
  (let [page-name @(rf/subscribe [:active-page])]
    (case page-name
      :home [home/page]
      :search [patients/page]
      [not-found/page])))


(defn app
  []
  [:<>
   [ui/header]
   [pages]
   [ui/footer]])


(defn mount [el]
  (rdom/render app el))


(defn init []
  (router/start!)
  (let [initialized? @(rf/subscribe [:initialized?])]
    (when (false? initialized?)
      (rf/dispatch-sync [:initialize-db])
      (rf/dispatch-sync [:set-active-page {:page router/default-route}])))
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(init)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (init))
