(ns dev.curiousprogrammer.app.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx]]
            [ajax.core :refer [json-request-format json-response-format]]
            [clojure.string :as str]
            [day8.re-frame.http-fx]
            [dev.curiousprogrammer.app.router :as router]))


(def api-url "http://localhost:3000/api")


(defn endpoint
  "Concat any params to api-url separated by /"
  [& params]
  (str/join "/" (cons api-url params)))


(def route-fhir-filters (endpoint "fhir/filters"))


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
                :dispatch-n [[:get-fhir-filters]]
                :set-url    {:url "/search"}}))))


;; GET FHIR Filters @ /api/fhir/filters --------------------------------------------------

(reg-event-fx
 :get-fhir-filters
 (fn [{:keys [db]} [_]]
   {:db         (assoc-in db [:loading? :fhir-filters] true)
    :http-xhrio {:method          :get
                 :uri             route-fhir-filters
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:get-fhir-filters-success]
                 :on-failure      [:api-request-error {:request-type :fhir-filters}]}}))


(reg-event-db
 :get-fhir-filters-success
 (fn [db [_ filters]]
   (-> db
       (assoc-in [:loading? :fhir-filters] false)
       (assoc :fhir-filters filters))))


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