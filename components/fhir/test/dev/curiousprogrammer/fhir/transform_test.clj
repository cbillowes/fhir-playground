(ns dev.curiousprogrammer.fhir.transform-test
  (:require [clojure.test :as test :refer :all]
            [dev.curiousprogrammer.fhir.transform :as sut]))


(defn- sample-response
  [patients]
  {:entry (map (fn [patient] {:resource patient}) patients)})


(defn- sample-address
  [& {:keys [use lines city state postal-code country]}]
  {:use use
   :lines lines
   :city city
   :state state
   :postalCode postal-code
   :country country})


(defn- sample-telecom
  [& {:keys [system value]}]
  {:system system :value value})


(defn- sample-patient
  [& {:keys [id name family gender birth-date address telecom resource-type]}]
  (cond-> {}
    id (assoc :id id)
    (or name family) (assoc :name [{:family family :given [name]}])
    gender (assoc :gender gender)
    birth-date (assoc :birthDate birth-date)
    address (assoc :address address)
    telecom (assoc :telecom telecom)
    resource-type (assoc :resourceType resource-type)))


(defn- transform-and-select
  [keys data]
  (-> (sut/transform-patients data)
      (first)
      (select-keys keys)))


(deftest transform-test
  (testing "Should transform fields for a patient"
    (is (= {:id "123"}
           (->> (sample-response [(sample-patient :id "123")])
                (transform-and-select [:id])))
        "ID not correctly transformed.")

    (is (= {:name "John"}
           (->> (sample-response [(sample-patient :name "John")])
                (transform-and-select [:name])))
        "Name not correctly transformed.")

    (is (= {:surname "Smith"}
           (->> (sample-response [(sample-patient :family "Smith")])
                (transform-and-select [:surname])))
        "Surname not correctly transformed.")

    (is (= {:gender "male"}
           (->> (sample-response [(sample-patient :gender "male")])
                (transform-and-select [:gender])))
        "Gender not correctly transformed.")

    (is (= {:date-of-birth "1995-01-01"}
           (->> (sample-response [(sample-patient :birth-date "1995-01-01")])
                (transform-and-select [:date-of-birth])))
        "Date of birth not correctly transformed.")

    (is (= {:work-address "Smith Rd, Seattle, WA, 000000, USA"}
           (->> (sample-response [(sample-patient :address [(sample-address :use "work" :lines ["Smith Rd"] :city "Seattle" :state "WA" :postal-code "000000" :country "USA")])])
                (transform-and-select [:work-address])))
        "Work address not correctly transformed.")

    (is (= {:home-address "Smith Rd, Seattle, WA, 000000, USA"}
           (->> (sample-response [(sample-patient :address [(sample-address :use "home" :lines ["Smith Rd"] :city "Seattle" :state "WA" :postal-code "000000" :country "USA")])])
                (transform-and-select [:home-address])))
        "Home address not correctly transformed.")

    (is (= {:home-address nil
            :work-address nil}
           (->> (sample-response [(sample-patient :address [])])
                (transform-and-select [:home-address :work-address])))
        "Work and home addresses should remain blank when not specified.")))


