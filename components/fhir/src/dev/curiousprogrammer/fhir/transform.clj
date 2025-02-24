(ns dev.curiousprogrammer.fhir.transform
  (:require [clojure.string :as str]))


(def ^:private work-address "work")
(def ^:private home-address "home")


(defn- extract-addresses
  [addresses type]
  (let [addresses (filter #(= type (:use %)) addresses)]
    (->> addresses
         (map
          (fn [{:keys [lines city state postalCode country] :as address}]
            (when address
              (format "%s, %s, %s, %s, %s" (str/join ", " lines) city state postalCode country))))
         (filter identity))))


(defn- extract-phones
  [telecoms]
  (map
   (fn [telecom]
     {:value (:value telecom)
      :type (:system telecom)
      :text (format "%s (%s)" (:value telecom) (:use telecom))})
   telecoms))


(defn- transform-patient [patient]
  (let [name (-> patient :name first)
        address (:address patient)]
    (->> {:id (:id patient)
          :link (when (:id patient)
                  (format "http://hapi.fhir.org/baseR4/Patient/%s/$summary" (:id patient)))
          :identifiers (str/join ", " (map :value (:identifier patient)))
          :name (str/join ", " (:given name))
          :surname (:family name)
          :gender (:gender patient)
          :date-of-birth (when (:birthDate patient)
                          (-> (:birthDate patient)
                              (str/split #"T")
                              (first)))
          :work-address (extract-addresses address work-address)
          :home-address (extract-addresses address home-address)
          :phone (extract-phones (:telecom patient))
          :marital-status (str/join ", " (map :display (get-in patient [:maritalStatus :coding])))
          :type (:resourceType patient)
          :last-updated (get-in patient [:meta :lastUpdated])
          :tags (str/join ", " (map :display (get-in patient [:meta :tag])))}
         (map (fn [[k v]] [k (when (string? v)
                               (or (str/trim (or v "")) "N/A")
                               v)]))
         (into {}))))


(defn transform-patients [fhir-response]
  (->> fhir-response
       (:entry)
       (map :resource)
       (map transform-patient)))
