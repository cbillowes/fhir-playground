(ns dev.curiousprogrammer.fhir.transform-test
  (:require [clojure.test :as test :refer :all]
            [dev.curiousprogrammer.fhir.transform :as sut]))


(defn- sample-response
  [patients]
  {:entry (map (fn [patient] {:resource patient}) patients)})


(defn- sample-patient
  [& {:keys [id name family gender birth-date address communication resource-type]}]
  (let [{:keys [lines city state postal-code country]} address
        {:keys [method value]} communication]
    (cond-> {}
      id (assoc :id id)
      (or name family) (assoc :name [{:family family :given [name]}])
      gender (assoc :gender gender)
      birth-date (assoc :birthDate birth-date)
      address (assoc :address [{:use (:use address) :line lines :city city :state state :postalCode postal-code :country country}])
      communication (assoc :telecom [{:system method :value value :use (:use communication)}])
      resource-type (assoc :resourceType resource-type))))


(defn- transform-and-select
  [keys data]
  (-> (sut/transform-patients data)
      (first)
      (select-keys keys)))


(deftest transform-test
  (testing "xxx"
    (is (= {:id "123"}
           (->> (sample-response [(sample-patient :id "123")])
                (transform-and-select [:id]))))

    (is (= {:name "John"}
           (->> (sample-response [(sample-patient :name "John")])
                (transform-and-select [:name]))))

    (is (= {:surname "Smith"}
           (->> (sample-response [(sample-patient :family "Smith")])
                (transform-and-select [:surname]))))

    (is (= {:gender "male"}
           (->> (sample-response [(sample-patient :gender "male")])
                (transform-and-select [:gender]))))

    (is (= {:date-of-birth "1995-01-01"}
           (->> (sample-response [(sample-patient :birth-date "1995-01-01")])
                (transform-and-select [:date-of-birth]))))

    (is (= {:address "Smith Rd, Seattle, WA, 000000, USA"}
           (->> (sample-response [(sample-patient :address {:use "work" :lines ["Smith Rd"] :city "Seattle" :state "WA" :postal-code "000000" :country "USA"})])
                (transform-and-select [:address]))))))



  #_(testing "Should extract all relevant fields from a patient"
    (let [patient {:address
                   [{:use "work",
                     :line ["AAAAAAAAAAAAAAAAAAAAA "],
                     :city "Seattle",
                     :state "WA",
                     :postalCode "000000",
                     :country "USA"}],
                   :meta
                   {:versionId "1",
                    :lastUpdated "2020-02-02T04:47:22.062+00:00",
                    :source "#bGpxjGKj6e084WBp"},
                   :name [{:family "Yang", :given ["Dave"]}],
                   :birthDate "1995-08-25",
                   :resourceType "Patient",
                   :id "596571",
                   :identifier [{:value "Tyang358"}],
                   :telecom [{:system "phone", :value "2232231111", :use "home"}],
                   :gender "male",
                   :text
                   {:status "generated",
                    :div
                    "<div xmlns=\"http://www.w3.org/1999/xhtml\"><div class=\"hapiHeaderText\">Dave <b>YANG </b></div><table class=\"hapiPropertyTable\"><tbody><tr><td>Identifier</td><td>Tyang358</td></tr><tr><td>Address</td><td><span>AAAAAAAAAAAAAAAAAAAAA </span><br/><span>Seattle </span><span>WA </span><span>USA </span></td></tr><tr><td>Date of birth</td><td><span>25 August 1995</span></td></tr></tbody></table></div>"}}]
      (is (= {:id "596571",
              :name "Dave",
              :surname "Yang",
              :gender "male",
              :date-of-birth "1995-08-25"}
             (sut/simplify-patient patient)))))


