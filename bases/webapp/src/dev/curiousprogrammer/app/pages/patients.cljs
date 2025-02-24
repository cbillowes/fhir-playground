(ns dev.curiousprogrammer.app.pages.patients
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]
            [dev.curiousprogrammer.app.ui :as ui]
            [dev.curiousprogrammer.app.api :as api]))


(rf/reg-sub
 :fhir/custom-filters
 (fn [db _]
   (map
    (fn [[k v]]
      [k v])
    (:fhir/custom-filters db))))


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
 :fhir/searched-for-patients
 (fn [db _]
   (:fhir/searched-for-patients db)))


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
   (when (empty? (:fhir/available-filters db))
     {:db (assoc db :fhir/loading? true :fhir/error nil)
      :http-xhrio {:method          :get
                   :uri             api/route-fhir-filters
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:fhir/fetch-filter-success]
                   :on-failure      [:fhir/fetch-filter-failed]}})))


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
   (assoc db :fhir/filter-by (-> (:fhir/available-filters db)
                                 (first)
                                 (:key)))))


(rf/reg-event-db
 :fhir/clear-filter-value
 (fn [db _]
   (dissoc db :fhir/filter-value)))


(rf/reg-event-db
 :fhir/store-filter
 (fn [db [_ type value]]
   (assoc-in db [:fhir/custom-filters type] value)))


(rf/reg-event-db
 :fhir/remove-filter
 (fn [db [_ key]]
   (assoc db :fhir/custom-filters (dissoc (:fhir/custom-filters db) key))))


(rf/reg-event-fx
 :fhir/search-patients
 (fn [{:keys [db]} _]
   (let [key (:fhir/filter-by db)
         value (:fhir/filter-value db)]
     (if (and key value)
       {:db (assoc db :fhir/loading? true :fhir/error nil)
        :http-xhrio {:method          :post
                     :uri             api/route-fhir-patient-search
                     :params          {:filters (:fhir/custom-filters db)}
                     :headers         (api/get-csrf-header)
                     :format          (ajax/json-request-format)
                     :response-format (ajax/json-response-format {:keywords? true})
                     :on-success      [:fhir/search-patients-success]
                     :on-failure      [:fhir/search-patients-failed]}}
       {:db (assoc db :fhir/error "Required fields are missing.")
        :dispatch-n [:fhir/search-patients-failed]}))))


(rf/reg-event-db
 :fhir/search-patients-success
 (fn [db [_ {:keys [data]}]]
   (assoc db :fhir/loading? false :fhir/patients data :fhir/searched-for-patients true)))


(rf/reg-event-db
 :fhir/search-patients-failed
 (fn [db [_ error]]
   (assoc db :fhir/loading? false :fhir/error error)))


(defn page
  []
  (let [available-filters @(rf/subscribe [:fhir/available-filters])
        custom-filters @(rf/subscribe [:fhir/custom-filters])
        filter-by @(rf/subscribe [:fhir/filter-by-field])
        filter-value @(rf/subscribe [:fhir/filter-value-field])
        patients @(rf/subscribe [:fhir/patients])
        searched-for-patients @(rf/subscribe [:fhir/searched-for-patients])
        loading? @(rf/subscribe [:fhir/loading?])
        error-message @(rf/subscribe [:fhir/error])]
    [:div {:class "my-8 max-w-4xl w-2/3 mx-auto text-center text-white leading-relaxed"}
     [:h1.font-bold.my-2.text-4xl "🤒 Search for Patients"]
     [:p "This application is using the " [:a.underline.text-yellow-400.hover:text-gray-400 {:href "https://hapi.fhir.org/baseR4" :target "_blank"} "HAPI API"] " to fetch data."]
     [ui/layout
      [:<>
       [:div.flex.mb-4.items-stretch
        [:div.flex.gap-4.w-full
         (when (seq available-filters)
           [ui/selectbox "Search by:" available-filters
            :title-class "text-red-900"
            :class "w-1/2"
            :required? true
            :selected-value filter-by
            :on-clear-value #(rf/dispatch [:fhir/clear-filter-by])
            :on-change (fn [value]
                         (rf/dispatch [:fhir/filter-by value])
                         (rf/dispatch [:fhir/store-filter value filter-value]))])
         [ui/textbox "Search value:"
          :class "w-1/2"
          :title-class "text-red-900"
          :default-value filter-value
          :required? true
          :on-clear-value #(rf/dispatch [:fhir/clear-filter-value])
          :on-change (fn [value]
                       (rf/dispatch [:fhir/filter-value value])
                       (rf/dispatch [:fhir/store-filter filter-by value]))]]]
       [ui/button (if loading? [:div.flex.justify-center [ui/spinner] "Fetching patients..."] "🔍 Search")
        :disabled? loading?
        :class "w-full bg-red-800 text-red-200 hover:bg-yellow-400 hover:text-yellow-900 cursor-pointer disabled:opacity-50 disabled:hover:bg-red-800 disabled:hover:text-red-800"
        :on-click #(rf/dispatch [:fhir/search-patients])]
       (when error-message
         [:div.mt-2.text-red-800.bg-red-200.rounded-md.p-2
          [:div error-message]])
       (when custom-filters
         [:div.mt-2.flex.flex-wrap
          (for [[key value] custom-filters]
            [:div.text-sm.rounded-md.bg-gray-200.px-2.mr-2.mb-2.bg-slate-800
             key " : " value [ui/button "❌" :class "cursor-pointer" :on-click #(rf/dispatch [:fhir/remove-filter key])]])])]]
       [:<>
        (when searched-for-patients
          (if (seq patients)
            [:div.mt-4
             [:table.w-full.bg-red-100.rounded-lg.overflow-hidden
              [:thead
               [:tr
                [:th {:class "text-left px-4 py-2"} "ID"]
                [:th {:class "text-left px-4 py-2"} "Patient"]
                [:th {:class "text-left px-4 py-2"} "Gender"]
                [:th {:class "text-left px-4 py-2"} "Date of Birth"]]]
              [:tbody
               (for [patient patients]
                 [:tr
                  [:td {:class "text-left px-4 py-2"} (:id patient)]
                  [:td {:class "text-left px-4 py-2"} (:surname patient) ", " (:name patient)]
                  [:td {:class "text-left px-4 py-2"} (:gender patient)]
                  [:td {:class "text-left px-4 py-2"} (:date-of-birth patient)]])]]]
            [:div "No patients found."]))]]))