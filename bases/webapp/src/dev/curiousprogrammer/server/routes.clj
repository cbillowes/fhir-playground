(ns dev.curiousprogrammer.server.routes
  (:require [compojure.core :refer [defroutes GET POST context]]
            [compojure.route :as route]
            [ring.util.response :as response]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [hiccup.core :refer [html]]
            [dev.curiousprogrammer.fhir.interface :as fhir]))


(defn base-template []
  [:html
   [:head
    [:title "FHIR Database"]
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    (let [csrf-token (force *anti-forgery-token*)]
      [:meta {:name "csrf-token"
              :content csrf-token}])
    [:link {:rel "stylesheet" :href "/css/style.min.css" :type "text/css"}]]
   [:body.bg-gray-800
    (anti-forgery-field)
    [:div#app]
    [:script {:src "/js/main_bundle.js" :type "text/javascript"}]]])


(defroutes web-routes
  (GET "/*" [] (html (base-template)))
  (route/not-found "Not Found"))


(defroutes api-routes
  (context "/api" []
    (GET "/health" _
      (response/response {:status "ok"}))

    (GET "/fhir/filters" _
      (response/response [{:key "*" :value "Any"}
                          {:key "partial-name" :value "Partial Name"}
                          {:key "given-name" :value "Given Name"}
                          {:key "family-name" :value "Family Name"}
                          {:key "phonetic-name" :value "Phonetic Name"}
                          {:key "partial-address" :value "Partial Address"}
                          {:key "date-of-birth" :value "Date of Birth"}
                          {:key "gender" :value "Gender"}
                          {:key "city" :value "City"}
                          {:key "state" :value "State"}
                          {:key "country" :value "Country"}
                          {:key "postal-code" :value "Postal Code"}
                          {:key "email" :value "Email"}
                          {:key "phone" :value "Phone"}
                          {:key "last-updated" :value "Last Updated"}
                          {:key "language" :value "Language"}
                          {:key "language-code" :value "Language Code"}
                          {:key "active?" :value "Active?"}
                          {:key "deceased?" :value "Deceased?"}
                          {:key "telecom" :value "Telecom"}
                          {:key "death-date" :value "Death Date"}
                          {:key "organization" :value "Organization"}
                          {:key "general-practitioner" :value "General Practitioner"}
                          {:key "identifier" :value "Identifier"}
                          {:key "link" :value "Link"}]))

    (POST "/fhir/patient-search" req
      (let [{:keys [body]} req
            filters (get body "filters")
            res (fhir/fetch-patients 1 10 filters)]
        (if (empty? res)
          (response/response {:status "error" :data "No patients found." :params body})
          (response/response {:data res :params body}))))))
