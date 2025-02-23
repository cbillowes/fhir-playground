(ns dev.curiousprogrammer.app.pages.home
  (:require [dev.curiousprogrammer.app.router :refer [url-for]]))

(defn page []
  [:div.text-white.bg-red-400
   [:h1 "Welcome to the Curious Programmer"]
   [:p "This is a simple web application to demonstrate the use of Clojure and ClojureScript in a full-stack application."]
   [:a {:href (url-for :home)} "Go home"][:br]
   [:a {:href (url-for :search)} "Search for something"]])