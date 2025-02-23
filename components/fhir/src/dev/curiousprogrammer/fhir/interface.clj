(ns dev.curiousprogrammer.fhir.interface
  (:require [dev.curiousprogrammer.fhir.api :as api]
            [dev.curiousprogrammer.fhir.transform :as transform]
            [dev.curiousprogrammer.fhir.query :as query]))


(defn fetch-patients
  "Searches for paged patient instances in the FHIR server.
   Returns a transformed list of patients.

* `page`: (int) The page number (offset) to fetch.
* `page-size`: (int) The number of patients to fetch per page.
* `query` (map) (optional): Additional query parameters to filter the search.
  see: `dev.curiousprogrammer.fhir.query/build-patient-query` for attributes.
  example: {:given-name \"John\" :family-name \"Doe\"}

Returns an empty list if no patients are found.

Docs: https://hapi.fhir.org/baseR4/swagger-ui/?page=Patient#/Patient/get_Patient"
  {:refs {:arg 'query
          :description "A map of query parameters to filter the search."
          :ns 'dev.curiousprogrammer.fhir.query/build-patient-query-for-search}}
  [page page-size & [query]]
  (->> (query/build-patient-query-for-search query)
       (api/fetch-patients page page-size)
       (transform/transform-patients)))


(comment

  (fetch-patients 1 10)

  :rcf)