(ns dev.curiousprogrammer.app.router
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :refer [dispatch]]))

(def ^{:doc "Defined routes for the application."}
  routes
  ["/" {""       :home
        "search" :search
        true     :not-found}])


(def ^{:doc "The history object manages the browser's history stack as users use the back & forwards button on their browsers to navigate through history."}
  history
  (let [dispatch #(dispatch [:set-active-page {:page (:handler %)}])
        match #(bidi/match-route routes %)]
    (pushy/pushy dispatch match)))

;; -- Router Start ------------------------------------------------------------
;;
(defn start!
  "Starts the router and configures the browser to use nice looking URLs."
  []
  (pushy/start! history))


(def ^{:doc "Generates a URL for a given route. Example: `(url-for :search)` returns `/search`."}
  url-for (partial bidi/path-for routes))


(defn set-token!
  "Updates the browser's URL to the given token without reloading the page.
   It manages the browser's history and updates the URL in the SPA without triggering a full
   page reload.

   Parameters:
   - token: The new URL token to be set.

   Example usage:
   (set-token! \"/new-page\")"
  [token]
  (pushy/set-token! history token))