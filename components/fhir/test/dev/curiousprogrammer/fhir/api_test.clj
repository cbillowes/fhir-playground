(ns dev.curiousprogrammer.fhir.api-test
  (:require [clojure.test :as test :refer :all]
            [clj-http.client :as client]
            [dev.curiousprogrammer.fhir.api :as sut]
            [dev.curiousprogrammer.fhir.query :as q]))


(deftest api-test
  (testing "Should validate page index & size"
    (is
     (thrown-with-msg?
      java.lang.AssertionError
      #"^.*Page index \(-10\) must be greater than or equal to 0.*"
      (let [page-index -10]
        (sut/fetch-patients page-index 10)))
     "Page index cannot be less than 0.")

    (is
     (thrown-with-msg?
      java.lang.AssertionError
      #"^.*Page size \(-10\) must be greater than 0.*"
      (let [page-size -10]
        (sut/fetch-patients 0 page-size)))
     "Page size cannot be less than 0.")

    (is
     (thrown-with-msg?
      java.lang.AssertionError
      #"^.*Page size \(10000\) must be less than or equal to 1000.*"
      (let [page-size 10000]
        (sut/fetch-patients 0 page-size)))
     "Page size is higher than the upper limit.")

    (with-redefs [client/get (constantly {:body []})]
      (is (= []
             (let [page-size 1000]
               (sut/fetch-patients 0 page-size)))
          "Page size of <1000 is accepted.")))

  (testing "Should request the first 1000 patients from the api"
    (let [*api-called? (atom false)
          page 1
          page-size 1000]
      (with-redefs [client/get (fn [url opts]
                                 (is (= "https://hapi.fhir.org/baseR4/Patient" url))
                                 (is (= {"_count" page-size "_offset" page} (:query-params opts)))
                                 (reset! *api-called? true)
                                 {:body []})]
        (let [actual (sut/fetch-patients page page-size)]
          (is (true? @*api-called?) "API was not called as expected.")
          (is (= [] actual)
              "Page size of 1000 is accepted."))))

     (let [*api-called? (atom false)
           page 1
           page-size 1000]
      (with-redefs [client/get (fn [url opts]
                                 (is (= "https://hapi.fhir.org/baseR4/Patient" url))
                                 (is (= {"_count" page-size "_offset" page "address" "123 Jameson Str" "given" "John"} (:query-params opts)))
                                 (reset! *api-called? true)
                                 {:body []})]
        (let [query (q/build-patient-query-for-search :partial-address "123 Jameson Str" :given-name "John")
              actual (sut/fetch-patients page page-size query)]
          (is (true? @*api-called?) "API was not called as expected.")
          (is (= [] actual)
              "Page size of 1000 is accepted."))))))


