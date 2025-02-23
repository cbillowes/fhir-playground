(ns dev.curiousprogrammer.app.pages.patients
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]
            [dev.curiousprogrammer.app.ui :as ui]
            [dev.curiousprogrammer.app.api :as api]))


(rf/reg-sub
 :fhir/custom-filters
 (fn [db _]
   (:fhir/custom-filters db)))


(rf/reg-sub
 :fhir/filter-value-field
 (fn [db _]
   (:fhir/filter-value db)))


(rf/reg-sub
 :fhir/filter-by-field
 (fn [db _]
   (:fhir/filter-by db)))


(rf/reg-sub
 :fhir/available-filters
 (fn [db _]
   (->> (:fhir/available-filters db)
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
   (assoc db :fhir/loading? false :fhir/available-filters filters)))


(rf/reg-event-db
 :fhir/fetch-filter-failed
 (fn [db [_ _]]
   (assoc db :fhir/loading? false :fhir/available-filters [])))


(rf/reg-event-db
 :fhir/filter-value
 (fn [db [_ value]]
   (assoc db :fhir/filter-value value)))


(rf/reg-event-db
 :fhir/filter-by
 (fn [db [_ value]]
   (assoc db :fhir/filter-by value)))


(rf/reg-event-db
 :fhir/clear-filter-by
 (fn [db _]
   (dissoc db :fhir/filter-by)))


(rf/reg-event-db
 :fhir/clear-filter-value
 (fn [db _]
   (dissoc db :fhir/filter-value)))


(rf/reg-event-db
 :fhir/store-filter
 (fn [db [_ type value]]
   (when (and (= type :by)
              (not (nil? value)))
     (let [filters (->> (:fhir/custom-filters db)
                        (filter #(= value (get % type)))
                        (distinct))]
       (assoc db :fhir/custom-filters (conj filters {:by (:fhir/filter-by db) :value (:fhir/filter-value db)}))))))


(rf/reg-event-db
 :fhir/remove-filter
  (fn [db [_ filter]]
    (assoc db :fhir/custom-filters (remove #(= filter %) (:fhir/custom-filters db)))))


(rf/reg-event-fx
 :fhir/search-patients
 (fn [{:keys [db]} _]
   {:db (assoc db :fhir/loading? true :fhir/error nil)
    :http-xhrio {:method          :post
                 :uri             api/route-fhir-patient-search
                 :response-format (ajax/json-response-format {:keywords? true})
                 #_#_#_#_:on-success      [:fhir/fetch-filter-success]
                 :on-failure      [:fhir/fetch-filter-failed]}}))



(defn page
  []
  (let [available-filters @(rf/subscribe [:fhir/available-filters])
        custom-filters @(rf/subscribe [:fhir/custom-filters])
        filter-by @(rf/subscribe [:fhir/filter-by-field])
        filter-value @(rf/subscribe [:fhir/filter-value-field])]
    [:div {:class "my-8 max-w-4xl w-2/3 mx-auto text-center text-white leading-relaxed"}
     [:h1.font-bold.my-2.text-4xl "🤒 Search for Patients"]
     [:p "This application is using the " [:a.underline.text-yellow-400.hover:text-gray-400 {:href "https://hapi.fhir.org/baseR4" :target "_blank"} "HAPI API"] " to fetch data."]
     [ui/layout
      [:<>
       [:div.flex.mb-4.items-stretch
        [:div.grow
         [:div.flex.gap-4.w-full
          (when (seq available-filters)
            [ui/selectbox "Search by:" available-filters
             :title-class "text-red-900"
             :class "w-1/2"
             :selected-value filter-by
             :on-clear-value #(rf/dispatch [:fhir/clear-filter-by])
             :on-change (fn [value]
                          (rf/dispatch [:fhir/filter-by value])
                          (rf/dispatch [:fhir/store-filter :by value]))])
          [ui/textbox "Search value:"
           :class "w-1/2"
           :title-class "text-red-900"
           :default-value filter-value
           :on-clear-value #(rf/dispatch [:fhir/clear-filter-value])
           :on-change (fn [value]
                        (rf/dispatch [:fhir/filter-value value])
                        (rf/dispatch [:fhir/store-filter :value value]))]]]
        (when (seq available-filters)
          [ui/button "➕"
           :class "ml-4 w-8 bg-green-400 text-green-900 hover:bg-blue-400 hover:text-blue-900 cursor-pointer flex-none block"
           :title "Add filter to search criteria"
           :on-click #(rf/dispatch [:fhir/store-filter])])]
       [ui/button "Search" :class "w-full bg-red-800 text-red-200 hover:bg-yellow-400 hover:text-yellow-900 cursor-pointer"]
       (when (seq custom-filters)
         [:div.mt-2.flex.flex-wrap
          (for [filter custom-filters]
            [:div.text-sm.rounded-md.bg-gray-200.px-2.mr-2.mb-2.bg-slate-800
             (:by filter) " : " (:value filter)
             [ui/button "❌" :class "cursor-pointer" :on-click #(rf/dispatch [:fhir/remove-filter filter])]])])]]]))