(ns ^:figwheel-hooks dev.curiousprogrammer.core
  (:require [goog.dom :as gdom]
            [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]
            [dev.curiousprogrammer.page-patients :as patients]))


;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"}))

(defn get-app-element []
  (gdom/getElement "app"))


(defn layout [heading component]
  [:div {:class "my-8 w-1/2 mx-auto text-center"}
   [:div.bg-red-400.p-8.rounded-lg.shadow-lg
    [:h1.text-5xl.font-bold.mb-8.text-red-900 heading]
    component]])


(defn page []
  (patients/patients-page layout))

(defn mount [el]
  (rdom/render [page] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
