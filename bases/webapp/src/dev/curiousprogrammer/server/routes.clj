(ns dev.curiousprogrammer.server.routes
  (:require [compojure.core :refer [defroutes GET POST context]]
            [compojure.route :as route]
            [ring.util.response :as response :refer [resource-response]]
            [dev.curiousprogrammer.fhir.interface :as fhir]))


(defroutes web-routes
  (GET "/*" [] (resource-response "index.html" {:root "public"}))
  (route/not-found "Not Found"))


(defroutes api-routes
  (context "/api" []
    (GET "/health" []
      (response/response {:status "ok"}))

    (GET "/fhir/filters" []
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

    (POST "/fhir/patients/search" []
      (let [res (fhir/fetch-patients 1 10)]
        (if (empty? res)
          (response/response {:status "error" :params "No patients found."})
          (response/response {:status "ok" :params res}))))))
