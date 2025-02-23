(ns dev.curiousprogrammer.server.routes
  (:require [compojure.core :refer [defroutes GET context]]
            [compojure.route :as route]
            [ring.util.response :as response :refer [resource-response]]))


(defroutes web-routes
  (GET "/*" [] (resource-response "index.html" {:root "public"}))
  (route/not-found "Not Found"))


(defroutes api-routes
  (context "/api" []
    (GET "/health" []
      (response/response {:status "ok"}))

    (GET "/fhir/filters" []
      (response/response [{:key "language" :value "Language"}
                          {:key "last-updated" :value "Last Updated"}
                          {:key "language-code" :value "Language Code"}
                          {:key "link" :value "Link"}
                          {:key "identifier" :value "Identifier"}
                          {:key "active?" :value "Active?"}
                          {:key "partial-name" :value "Partial Name"}
                          {:key "given-name" :value "Given Name"}
                          {:key "family-name" :value "Family Name"}
                          {:key "phonetic-name" :value "Phonetic Name"}
                          {:key "date-of-birth" :value "Date of Birth"}
                          {:key "deceased?" :value "Deceased?"}
                          {:key "partial-address" :value "Partial Address"}
                          {:key "city" :value "City"}
                          {:key "state" :value "State"}
                          {:key "country" :value "Country"}
                          {:key "postal-code" :value "Postal Code"}
                          {:key "gender" :value "Gender"}
                          {:key "death-date" :value "Death Date"}
                          {:key "telecom" :value "Telecom"}
                          {:key "email" :value "Email"}
                          {:key "phone" :value "Phone"}
                          {:key "organization" :value "Organization"}
                          {:key "general-practitioner" :value "General Practitioner"}]))))
