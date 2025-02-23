(ns dev.curiousprogrammer.app.subs
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [dev.curiousprogrammer.app.router :as router]))


(rf/reg-sub
 :active-page
 (fn [db _]
   (let [pathname (.-pathname js/window.location)
         page-name (-> (str/split pathname #"/")
                       (second)
                       (keyword))]
     (or (:active-page db) page-name router/default-route))))

