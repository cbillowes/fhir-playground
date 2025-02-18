(ns dev.curiousprogrammer.fhir.api
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))

;; https://hapi.fhir.org/baseR4/swagger-ui/
(def fhir-base-url "https://hapi.fhir.org/baseR4")


(defn fetch-patients []
  (let [url (str fhir-base-url "/Patient?_count=5")
        response (client/get url {:as :json})]
    (:body response)))

