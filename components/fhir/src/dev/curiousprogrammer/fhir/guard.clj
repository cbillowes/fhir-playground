(ns dev.curiousprogrammer.fhir.guard)

(def ^:private max-page-size 1000)


(defn validate-page-index [value]
  (assert (>= value 0) (format "Page index (%s) must be greater than or equal to 0" value))
  true)


(defn validate-page-size [value]
  (assert (pos? value) (format "Page size (%s) must be greater than 0" value))
  (assert (>= max-page-size value) (format "Page size (%s) must be less than or equal to %s" value max-page-size))
  true)

