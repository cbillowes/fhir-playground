(ns dev.curiousprogrammer.app.pages.patients
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]
            [dev.curiousprogrammer.app.ui :as ui]
            [dev.curiousprogrammer.app.api :as api]))


(rf/reg-sub
 :fhir/filters
 (fn [db _]
   (:fhir/filters db)))


(rf/reg-sub
 :fhir/filter-value-field
 (fn [db _]
   (:fhir/filter-value db)))


(rf/reg-sub
 :fhir/filter-by-field
 (fn [db _]
   (:fhir/filter-by db)))


(rf/reg-sub
 :fhir/filter-list
 (fn [db _]
   (->> (:fhir/filter-list db)
        (map (fn [filter] {:value (:key filter) :option (:value filter)})))))


(rf/reg-sub
 :fhir/patients
 (fn [db _]
   (:fhir/patients db)))


(rf/reg-sub
 :fhir/loading?
 (fn [db _]
   (:fhir/loading? db)))


(rf/reg-sub
 :fhir/error
 (fn [db _]
   (:fhir/error db)))


(rf/reg-event-fx
 :fhir/fetch-filters
 (fn [{:keys [db]} _]
   {:db (assoc db :fhir/loading? true :fhir/error nil)
    :http-xhrio {:method          :get
                 :uri             api/route-fhir-filters
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:fhir/fetch-filter-success]
                 :on-failure      [:fhir/fetch-filter-failed]}}))


(rf/reg-event-db
 :fhir/fetch-filter-success
 (fn [db [_ filters]]
   (assoc db :fhir/loading? false :fhir/filter-list filters)))


(rf/reg-event-db
 :fhir/fetch-filter-failed
 (fn [db [_ _]]
   (assoc db :fhir/loading? false :fhir/filter-list [])))


(rf/reg-event-db
 :fhir/filter-value
 (fn [db [_ value]]
   (assoc db :fhir/filter-value value)))


(rf/reg-event-db
 :fhir/filter-by
 (fn [db [_ value]]
   (assoc db :fhir/filter-by value)))


(rf/reg-event-db
 :fhir/clear-filter-fields
 (fn [db _]
   (dissoc db :fhir/filter-by :fhir/filter-value)))


(rf/reg-event-db
 :fhir/store-filter
 (fn [db [_ _]]
   (js/console.log db)
   (when (and (:fhir/filter-by db)
              (:fhir/filter-value db))
     (assoc db :fhir/filters (-> (:fhir/filters db)
                                 (conj {:by (:fhir/filter-by db) :value (:fhir/filter-value db)})
                                 (distinct))))))


(rf/reg-event-db
 :fhir/remove-filter
  (fn [db [_ filter]]
    (assoc db :fhir/filters (remove #(= filter %) (:fhir/filters db)))))



(defn page
  []
  (let [filter-list @(rf/subscribe [:fhir/filter-list])
        filters @(rf/subscribe [:fhir/filters])
        filter-by-field @(rf/subscribe [:fhir/filter-by-field])
        filter-value-field @(rf/subscribe [:fhir/filter-value-field])]
    [:div {:class "my-8 max-w-4xl w-2/3 mx-auto text-center text-white leading-relaxed"}
     [:h1.font-bold.my-2.text-4xl "🤒 Search for Patients"]
     [:p "This application is using the " [:a.underline.text-yellow-400.hover:text-gray-400 {:href "https://hapi.fhir.org/baseR4" :target "_blank"} "HAPI API"] " to fetch data."]
     [ui/layout
      [:<>
       [:div.flex.mb-4.items-stretch
        [:div.grow
         [:div.flex.gap-4.w-full
          (when (seq filter-list)
            [ui/selectbox "Search by:" filter-list
             :title-class "text-red-900"
             :class "w-1/2"
             :selected-value filter-by-field
             :on-change #(rf/dispatch [:fhir/filter-by %])])
          [ui/textbox "Search value:"
           :class "w-1/2"
           :title-class "text-red-900"
           :default-value filter-value-field
           :on-change #(rf/dispatch [:fhir/filter-value %])]]]
        (when (seq filter-list)
          [:div.flex
           [ui/button "➕"
            :class "ml-4 w-16 bg-green-400 text-green-900 hover:bg-blue-400 hover:text-blue-900 cursor-pointer flex-none block"
            :title "Add filter to search criteria"
            :on-click #(rf/dispatch [:fhir/store-filter])]
           [ui/button "✖️"
            :class "ml-4 w-16 bg-slate-400 text-slate-900 hover:bg-blue-400 hover:text-blue-900 cursor-pointer flex-none block"
            :title "Clear filters"
            :on-click #(rf/dispatch [:fhir/clear-filter-fields])]])]
       [ui/button "Search" :class "w-full bg-red-800 text-red-200 hover:bg-yellow-400 hover:text-yellow-900 cursor-pointer"]
       (when (seq filters)
         (js/console.log filters)
         [:div.mt-2.flex.flex-wrap
          (for [filter filters]
            [:div.text-sm.rounded-md.bg-gray-200.px-2.mr-2.mb-2.bg-slate-800
             (:by filter) " : " (:value filter)
             [ui/button "❌" :class "cursor-pointer" :on-click #(rf/dispatch [:fhir/remove-filter filter])]])])]]]))