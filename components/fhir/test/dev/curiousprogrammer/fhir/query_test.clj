(ns dev.curiousprogrammer.fhir.query-test
  (:require [clojure.test :as test :refer :all]
            [dev.curiousprogrammer.fhir.query :as sut]))

(deftest build-patient-query-test
  (testing "Should build a query for a patient"
    (is (= {}
           (sut/build-patient-query))
        "Query is not empty")

    (is (= {"_language" "fr"}
           (sut/build-patient-query :language "fr"))
        "_language is not set")

    (is (= {"_lastUpdated" "2018-01-01"}
           (sut/build-patient-query :last-updated "2018-01-01"))
        "_lastUpdated not set")

    (is (= {"link" "123"}
           (sut/build-patient-query :link "123"))
        "link not set")

    (is (= {"identifier" "123"}
           (sut/build-patient-query :identifier "123"))
        "identifier not set")

    (is (= {"active" "true"}
           (sut/build-patient-query :active? true))
        "active not set")

    (is (= {"name" "John"}
           (sut/build-patient-query :partial-name "John"))
        "name not set")

    (is (= {"given" "John"}
           (sut/build-patient-query :given-name "John"))
        "given not set")

    (is (= {"phonetic" "John"}
           (sut/build-patient-query :phonetic-name "John"))
        "phonetic not set")

    (is (= {"birthdate" "1985-10-10"}
           (sut/build-patient-query :date-of-birth "1985-10-10"))
        "birthdate not set")

    (is (= {"deceased" "true"}
           (sut/build-patient-query :deceased? true))
        "deceased not set")

    (is (= {"address" "123 Line Str"}
           (sut/build-patient-query :partial-address "123 Line Str"))
        "address not set")

    (is (= {"address-city" "Seattle"}
           (sut/build-patient-query :city "Seattle"))
        "address-city not set")

    (is (= {"address-state" "WA"}
           (sut/build-patient-query :state "WA"))
        "address-state not set")

    (is (= {"address-country" "USA"}
           (sut/build-patient-query :country "USA"))
        "address-country not set")

    (is (= {"address-postalcode" "000000"}
           (sut/build-patient-query :postal-code "000000"))
        "address-postalcode not set")

    (is (= {"gender" "male"}
           (sut/build-patient-query :gender "male"))
        "gender not set")

    (is (= {"death-date" "1995-01-01"}
           (sut/build-patient-query :death-date "1995-01-01"))
        "death-date not set")

    (is (= {"telecom" "0987654321"}
           (sut/build-patient-query :telecom "0987654321"))
        "telecom not set")

    (is (= {"email" "john@icare.com"}
           (sut/build-patient-query :email "john@icare.com"))
        "email not set")

    (is (= {"phone" "0987654321"}
           (sut/build-patient-query :phone "0987654321"))
        "phone not set")

    (is (= {"general-practitioner" "Dr Jennifer Meyers"}
           (sut/build-patient-query :general-practitioner "Dr Jennifer Meyers"))
        "general-practitioner not set")

    (is (= {"organization" "Peter Pan Inc."}
           (sut/build-patient-query :organization "Peter Pan Inc."))
        "organization not set")))