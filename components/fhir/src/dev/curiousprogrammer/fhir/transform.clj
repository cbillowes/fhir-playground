(ns dev.curiousprogrammer.fhir.transform
  (:require [clojure.string :as str]))


(def ^:private work-address "work")
(def ^:private home-address "home")


(defn- extract-address
  [addresses type]
  (let [filtered (filter #(= type (:use %)) addresses)
        {:keys [lines city state postalCode country] :as address} (first filtered)]
    (when address
      (format "%s, %s, %s, %s, %s" (str/join ", " lines) city state postalCode country))))


(defn- transform-patient [patient]
  (let [name (-> patient :name first)
        address (:address patient)]
    {:id (:id patient)
     :name (first (:given name))
     :surname (:family name)
     :gender (:gender patient)
     :date-of-birth (:birthDate patient)
     :work-address (extract-address address work-address)
     :home-address (extract-address address home-address)
     :type "Patient"}))


(defn transform-patients [fhir-response]
  (->> fhir-response
       (:entry)
       (map :resource)
       (map transform-patient)))
