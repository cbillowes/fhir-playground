(ns dev.curiousprogrammer.app.api
  (:require [clojure.string :as str]))

(def ^:private api-url "http://localhost:3000/api")


(defn- endpoint
  "Concat any params to api-url separated by /"
  [& params]
  (str/join "/" (cons api-url params)))


(def route-fhir-filters (endpoint "fhir/filters"))
(def route-fhir-patient-search (endpoint "fhir/patients/search"))