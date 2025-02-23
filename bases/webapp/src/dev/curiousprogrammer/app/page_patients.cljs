(ns dev.curiousprogrammer.app.page-patients
  (:require [dev.curiousprogrammer.app.ui :as ui]))


(defn- filter-selectbox
  []
  [ui/selectbox "Filter type:" [{:value "value" :option "Option"}] :title-class "text-red-900"])


(defn patients-page
  []
  [ui/layout
   "🧑‍😷 Search FHIR Patients"
   [:<>
    [:p "Fast Healthcare Interoperability Resources"]
    [:div {:class "bg-gray-50 p-8 rounded-lg mt-8"}
     [:div {:class "flex gap-4 mb-4"} [ui/textbox "Filter value:" :title-class "text-red-900"] [filter-selectbox]]
     [ui/button "Search" :class "bg-yellow-400 text-yellow-900 hover:bg-blue-400 hover:text-blue-900 cursor-pointer"]]]])