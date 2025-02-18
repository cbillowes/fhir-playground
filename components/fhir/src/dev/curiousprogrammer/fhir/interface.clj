(ns dev.curiousprogrammer.fhir.interface
  (:require [dev.curiousprogrammer.fhir.api :as api]
            [dev.curiousprogrammer.fhir.transform :as transform]))


(defn fetch-patients []
  (->> (api/fetch-patients)
       (transform/transform-patients)))
