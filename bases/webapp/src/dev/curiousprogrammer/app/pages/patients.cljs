(ns dev.curiousprogrammer.app.pages.patients
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]
            [dev.curiousprogrammer.app.ui :as ui]
            [dev.curiousprogrammer.app.api :as api]))


(rf/reg-sub
 :fhir/filters
 (fn [db _]
   (->> (:fhir/filters db)
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
                 :on-failure      [:api-request-error {:request-type :fhir/filters}]}}))


(rf/reg-event-db
 :fhir/fetch-filter-success
 (fn [db [_ filters]]
   (assoc db :fhir/loading? true :fhir/filters filters)))


(defn- filter-selectbox
  [& {:keys [class]}]
  (let [filters @(rf/subscribe [:fhir/filters])
        loading? @(rf/subscribe [:fhir/loading?])
        error @(rf/subscribe [:fhir/error])]
    [ui/selectbox "Filter type:" filters :title-class "text-red-900" :class class]))


(defn page
  []
  [:div {:class "my-8 max-w-4xl w-2/3 mx-auto text-center text-white leading-relaxed"}
   [:h1.font-bold.my-2.text-4xl "🤒 Search for Patients"]
   [:p "This application is using the " [:a.underline.text-yellow-400.hover:text-gray-400 {:href "https://hapi.fhir.org/baseR4" :target "_blank"} "HAPI API"] " to fetch data."]
   [ui/layout
    [:<>
     [:div.flex.mb-4.items-stretch
      [:div.grow
       [:div.flex.gap-4.w-full
        [filter-selectbox :class "w-1/2"]
        [ui/textbox "Filter value:" :class "w-1/2" :title-class "text-red-900"]]]
      [ui/button "➕" :class "ml-4 bg-green-400 text-green-900 hover:bg-blue-400 hover:text-blue-900 cursor-pointer flex-none block"]]
     [ui/button "Search" :class "w-full bg-yellow-400 text-yellow-900 hover:bg-blue-400 hover:text-blue-900 cursor-pointer"]]]])