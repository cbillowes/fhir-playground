(ns dev.curiousprogrammer.fhir.api
  (:require [clj-http.client :as client]
            [dev.curiousprogrammer.fhir.guard :as guard]))

;; https://hapi.fhir.org/baseR4/swagger-ui/
(def ^:private fhir-base-url "https://hapi.fhir.org/baseR4")


;; https://hapi.fhir.org/baseR4/api-docs
;; /Composition/{id}/$document: get
(defn fetch-patients [page page-size]
  (gaurd/validate-page-index page)
  (guard/validate-page-size page-size)
  (let [url (str fhir-base-url "/Patient")
        response (client/get url
                             {:query-params {"_count" page-size
                                             "_offset" page}
                              :as :json})]
    (:body response)))