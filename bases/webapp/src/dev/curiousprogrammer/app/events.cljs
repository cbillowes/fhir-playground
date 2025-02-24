(ns dev.curiousprogrammer.app.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx]]
            [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [dev.curiousprogrammer.app.router :as router]))


(reg-fx
 :set-url
 (fn [{:keys [url]}]
   (router/set-token! url)))


(reg-event-fx
 :initialize-db
 (fn [_ _]
   {:db {:active-page :home}}))


(reg-event-fx
 :set-active-page
 (fn [{:keys [db]} [_ {:keys [page]}]]
   (let [set-page (assoc db :active-page page)]
     (case page
       :home   {:db         set-page
                :set-url    {:url "/"}}

       :search {:db          set-page
                :dispatch-n [[:fhir/initialize]]
                :set-url    {:url "/search"}}))))


#_(reg-event-fx
 :get-fhir-filters-success
 (fn [{user :db} [filters]]
   {:db         (-> (assoc-in [:loading :login] false))
    :dispatch-n [[:get-feed-articles {:tag nil :author nil :offset 0 :limit 10}]
                 [:set-active-page {:page :home}]]}))

;; Generic failure --------------------------------------------------

(reg-event-db
 :api-request-error
 (fn [db [_ {:keys [request-type loading?]} response]]
   (-> db
       (assoc-in [:errors request-type] (get-in response [:response :errors]))
       (assoc-in [:loading? (or loading? request-type)] false))))