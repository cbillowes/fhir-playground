(ns dev.curiousprogrammer.app.pages.patients
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]
            [dev.curiousprogrammer.app.ui :as ui]))


(rf/reg-event-db
 :initialize-db
 (fn [_ _]
   {:filters []
    :patients []
    :loading? false
    :error nil}))


(rf/reg-sub
 :filters
 (fn [db _]
   (->> (:filters db)
        (map (fn [filter] [(:name filter) (:value filter)]))
        (into []))))


(rf/reg-sub
 :patients
 (fn [db _]
   (:patients db)))


(rf/reg-sub
 :loading?
 (fn [db _]
   (:loading? db)))


(rf/reg-sub
 :error
 (fn [db _]
   (:error db)))


(rf/reg-event-fx
 :fetch-filters
 (fn [{:keys [db]} _]
   {:db (assoc db :loading? true :error nil)
    :http-xhrio {:method          :get
                 :uri             "http://localhost:3000/api/fhir/filters"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:fetch-filter-success]
                 :on-failure      [:fetch-filter-failure]}}))


(rf/reg-event-db
 :fetch-filter-success
 (fn [db [_ response]]
   (assoc db :loading? false :filters response)))


(rf/reg-event-db
 :fetch-filter-failure
 (fn [db [_ error]]
   (assoc db :loading? false :error error)))


(defn- filter-selectbox
  []
  (let [filters @(rf/subscribe [:filters])
        loading? @(rf/subscribe [:loading?])
        error @(rf/subscribe [:error])]
    [ui/selectbox "Filter type:" filters :title-class "text-red-900"]))


(defn page
  []
  (rf/dispatch-sync [:initialize-db])
  [ui/layout
   "🧑‍😷 Search FHIR Patients"
   [:<>
    [:p "Fast Healthcare Interoperability Resources"]
    [:div {:class "bg-gray-50 p-8 rounded-lg mt-8"}
     [:div {:class "flex gap-4 mb-4"} [ui/textbox "Filter value:" :title-class "text-red-900"] [filter-selectbox]]
     [ui/button "Search" :class "bg-yellow-400 text-yellow-900 hover:bg-blue-400 hover:text-blue-900 cursor-pointer"]]]])