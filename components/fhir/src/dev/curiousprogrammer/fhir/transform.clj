(ns dev.curiousprogrammer.fhir.transform
  (:require [cheshire.core :as json]))


(defn- simplify-patient [patient]
  (let [name (-> patient :name first)]
    {:id (:id patient)
     :name (first (:given name))
     :surname (:family name)
     :gender (:gender patient)
     :date-of-birth (:birthDate patient)
     :address ()
     :type "Patient"}))


(defn transform-patients [fhir-response]
  (->> fhir-response
       (:entry)
       (map :resource)
       (map simplify-patient)))
