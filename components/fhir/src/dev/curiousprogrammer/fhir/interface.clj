(ns dev.curiousprogrammer.fhir.interface
  (:require [dev.curiousprogrammer.fhir.api :as api]
            [dev.curiousprogrammer.fhir.transform :as transform]))


(defn fetch-patients
  "Searches for paged patient instances in the FHIR server.
   Returns a transformed list of patients.

   * `page`: The page number (offset) to fetch.
   * `page-size`: The number of patients to fetch per page.

   https://hapi.fhir.org/baseR4/swagger-ui/?page=Patient#/Patient/get_Patient"
  [page page-size]
  (->> (api/fetch-patients page page-size)
       (transform/transform-patients)))
