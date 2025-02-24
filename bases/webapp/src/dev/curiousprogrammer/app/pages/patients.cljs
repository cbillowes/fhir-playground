(ns dev.curiousprogrammer.app.pages.patients
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]
            [dev.curiousprogrammer.app.ui :as ui]
            [dev.curiousprogrammer.app.api :as api]))

(def ^:private default-page-size "15")
(def ^:private required-message "Required fields are missing.")


(rf/reg-event-fx
 :fhir/initialize
 (fn [{:keys [db]} _]
   {:db (assoc db
               :fhir/page-size default-page-size
               :fhir/error required-message)
    :dispatch-n [[:fhir/fetch-filters]]}))


(defn- required-fields-valid?
  [db]
  (and (:fhir/page-size db)
       (:fhir/filter-by db)
       (:fhir/filter-value db)))


(rf/reg-sub
 :fhir/custom-filters
 (fn [db _]
   (map
    (fn [[k v]]
      [k v])
    (:fhir/custom-filters db))))


(rf/reg-sub
 :fhir/page-size-field
 (fn [db _]
   (:fhir/page-size db)))


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
 :fhir/page-size
 (fn [db [_ value]]
   (assoc db
          :fhir/page-size value
          :fhir/error (when (not (required-fields-valid? (assoc db :fhir/page-size value)))
                        required-message))))


(rf/reg-event-db
 :fhir/filter-value
 (fn [db [_ value]]
   (assoc db
          :fhir/filter-value value
          :fhir/error (when (not (required-fields-valid? (assoc db :fhir/filter-value value)))
                        required-message))))


(rf/reg-event-db
 :fhir/filter-by
 (fn [db [_ value]]
   (assoc db
          :fhir/filter-by value
          :fhir/error (when (not (required-fields-valid? (assoc db :fhir/filter-by value)))
                        required-message))))


(rf/reg-event-db
 :fhir/clear-page-size
 (fn [db _]
   (-> db
       (dissoc :fhir/page-size)
       (assoc :fhir/error required-message))))


(rf/reg-event-db
 :fhir/clear-filter-by
 (fn [db _]
   (-> db
       (dissoc :fhir/filter-by)
       (assoc :fhir/error required-message))))


(rf/reg-event-db
 :fhir/clear-filter-value
 (fn [db _]
   (-> db
       (dissoc :fhir/filter-value)
       (assoc :fhir/error required-message))))


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
   (let [page-size (:fhir/page-size db)
         key (:fhir/filter-by db)
         value (:fhir/filter-value db)]
     (if (and page-size key value)
       {:db (assoc db :fhir/loading? true :fhir/error nil)
        :http-xhrio {:method          :post
                     :uri             api/route-fhir-patient-search
                     :params          {:filters (:fhir/custom-filters db)
                                       :page-size (:fhir/page-size db)}
                     :headers         (api/get-csrf-header)
                     :format          (ajax/json-request-format)
                     :response-format (ajax/json-response-format {:keywords? true})
                     :on-success      [:fhir/search-patients-success]
                     :on-failure      [:fhir/search-patients-failed "Something went wrong on the server."]}}
       {:db db
        :dispatch-n [:fhir/search-patients-failed required-message]}))))


(rf/reg-event-db
 :fhir/search-patients-success
 (fn [db [_ {:keys [data message]}]]
   (assoc db
          :fhir/loading? false
          :fhir/patients data
          :fhir/error message
          :fhir/searched-for-patients true)))


(rf/reg-event-db
 :fhir/search-patients-failed
 (fn [db [_ error]]
   (assoc db :fhir/loading? false :fhir/error error)))


(defn page
  []
  (let [available-filters @(rf/subscribe [:fhir/available-filters])
        custom-filters @(rf/subscribe [:fhir/custom-filters])
        page-size @(rf/subscribe [:fhir/page-size-field])
        filter-by @(rf/subscribe [:fhir/filter-by-field])
        filter-value @(rf/subscribe [:fhir/filter-value-field])
        patients @(rf/subscribe [:fhir/patients])
        searched-for-patients @(rf/subscribe [:fhir/searched-for-patients])
        loading? @(rf/subscribe [:fhir/loading?])
        error-message @(rf/subscribe [:fhir/error])]
    [:<>
     [:div {:class "my-8 max-w-4xl w-2/3 mx-auto text-center text-white leading-relaxed"}
      [:h1.font-bold.my-2.text-4xl "🤒 Search for Patients"]
      [:p "This application is using the " [:a.underline.text-yellow-400.hover:text-gray-400 {:href "https://hapi.fhir.org/baseR4" :target "_blank"} "HAPI API"] " to fetch data."]
      [ui/layout
       [:<>
        [:div.flex.mb-4.items-stretch
         [:div.flex.gap-4.w-full
          [ui/selectbox
           "Page size:"
           (map
            #(hash-map :option (str %) :value (str %))
            (sort
             (concat
              (range 5 50 5)
              (range 50 250 50))))
           :class "w-1/2"
           :title-class "text-red-900"
           :selected-value page-size
           :required? true
           :on-clear-value #(rf/dispatch [:fhir/clear-page-size])
           :on-change (fn [value]
                        (rf/dispatch [:fhir/page-size value]))]]]
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
         :class "w-full bg-red-800 text-red-200 hover:bg-yellow-400 hover:text-yellow-900 cursor-pointer disabled:opacity-50 disabled:hover:bg-red-800 disabled:hover:text-red-800 disabled:text-black!"
         :on-click #(rf/dispatch [:fhir/search-patients])]
        (when error-message
          [:div.mt-2.text-red-800.bg-red-200.rounded-md.p-2
           [:div error-message]])
        (when (or page-size custom-filters)
          [:div.mt-2.flex.flex-wrap
           (when page-size
             [:div.text-sm.rounded-md.bg-gray-200.px-2.mr-2.mb-2.bg-slate-700
              "page-size : " page-size [ui/button "❌" :class "cursor-pointer" :on-click #(rf/dispatch [:fhir/clear-page-size])]])
           (for [[key value] custom-filters]
             [:div.text-sm.rounded-md.bg-gray-200.px-2.mr-2.mb-2.bg-slate-800
              key " : " value [ui/button "❌" :class "cursor-pointer" :on-click #(rf/dispatch [:fhir/remove-filter key])]])])]]]
     [:div {:class "text-white flex flex-wrap justify-center items-center"}
      (for [patient patients]
        [:div {:class "w-1/5 rounded-lg bg-slate-900 p-4 m-1 text-left"}
         [:a {:class "text-sm"
              :href (:link patient)} (:id patient)]
         [:h2.text-center.font-bold.text-xl.uppercase (:name patient) " " (:surname patient)]
         [:div {:class "my-4 leading-relaxed"}
          [:p (cond
                (= (:gender patient) "male") "👨"
                (= (:gender patient) "female") "👩"
                :else "🧑") " "
           (:gender patient)]
          [:p "🎂 " (:date-of-birth patient)]
          [:p "💍 " (:marital-status patient)]]])]]))