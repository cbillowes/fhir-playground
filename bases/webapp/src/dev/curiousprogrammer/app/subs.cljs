(ns dev.curiousprogrammer.app.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub
 :active-page
 (fn [db _]
   (or (:active-page db) :home)))

