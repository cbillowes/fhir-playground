(ns dev.curiousprogrammer.app.api
  (:require [clojure.string :as str]))

(def ^:private api-url "http://localhost:3000/api")


(defn- endpoint
  "Concat any params to api-url separated by /"
  [& params]
  (str/join "/" (cons api-url params)))


(def route-fhir-filters (endpoint "fhir/filters"))

(def route-fhir-patient-search (endpoint "fhir/patient-search"))


(defn get-csrf-token []
  (.-content (.querySelector js/document "meta[name='csrf-token']")))


(defn get-csrf-header []
  {"X-Csrf-Token" (get-csrf-token)})