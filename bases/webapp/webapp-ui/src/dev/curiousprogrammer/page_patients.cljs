(ns dev.curiousprogrammer.page-patients
  (:require [dev.curiousprogrammer.ui :as ui]))


(defn- filter-selectbox
  []
  [ui/selectbox "Filter type" [{:value "value" :option "Option"}] :title-class "text-red-900"])


(defn patients-page
  [layout]
  (layout
   "🧑‍😷 Search FHIR Patients"
   [:div
    [:div.bg-gray-50.p-8.rounded-lg.shadow-lg
     [:p.text-red-900.text-sm.mb-4 "Create your filters below, then click Search to find patients."]
     [:div.flex.gap-4.mb-4 [ui/textbox "Filter value" :title-class "text-red-900"] [filter-selectbox]]
     [ui/button "Search" :class "bg-yellow-400 text-yellow-900"]]]))