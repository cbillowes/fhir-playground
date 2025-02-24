(ns dev.curiousprogrammer.app.ui
  (:require [re-frame.core :as rf]))


(defn- header-menu-item
  [name page]
  [:a.cursor-pointer.inline-block.rounded-md.px-4.py-1.my-4.bg-slate-700.hover:bg-yellow-400.hover:text-black
   {:on-click #(rf/dispatch [:set-active-page {:page page}])} name])


(defn spinner
  []
  [:svg {:class "mr-3 -ml-1 size-5 animate-spin text-white" :xmlns "http://www.w3.org/2000/svg" :fill "none" :viewBox "0 0 24 24"}
   [:circle {:class "opacity-25" :cx "12" :cy "12" :r "10" :stroke "currentColor" :stroke-width "4"}]
   [:path {:class "opacity-75" :fill "currentColor" :d "M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"}]])


(defn header
  []
  [:header {:class "max-w-6xl mx-auto"}
   [:nav
    [:ul.flex.text-white.gap-4
     [:li [header-menu-item "Home" :home]]
     [:li [header-menu-item "Search patients" :search]]]]])


(defn footer
  []
  [:footer
   [:p.text-center.text-gray-400.p-4.mt-4.text-sm
    (str "Copyright © " (-> (js/Date.) .getFullYear) " Curious Programmer")]])


(defn layout [component]
  [:div {:class "my-8 w-full mx-auto text-center"}
   [:div {:class "bg-red-400 p-8 rounded-lg shadow-lg"}
    component]])


(defn label
  [text & {:keys [title-class value on-clear-value required?]}]
  [:div {:class (str "relative text-xs text-left flex justify-between w-full " title-class)}
   text
   [:div.flex.items-center
    (when required? [:p.text-red-800 "*"])
    (when (and value on-clear-value) [:button.cursor-pointer {:on-click on-clear-value} "✖️"])]])


(defn textbox [text & {:keys [title title-class class on-change default-value on-clear-value
                              required?]
                       :or {title-class ""
                            class ""
                            on-change (fn [_])}}]
  [:div {:class (str "w-full bg-gray-200 p-2 rounded-lg border-8" class)}
   [label text :title-class title-class :value default-value :on-clear-value on-clear-value :required? required?]
   [:input {:title title
            :type "text"
            :class "w-full p-2 outline-none border-b border-transparent focus:border-red-800 text-black"
            :value default-value
            :on-change #(on-change (.. % -target -value))}]])


(defn selectbox [text options & {:keys [title title-class class on-change selected-value on-clear-value
                                        required?]
                                 :or {title-class ""
                                      class ""
                                      on-change (fn [_])}}]
  [:div {:class (str "w-full bg-gray-200 p-2 pr-4 rounded-lg border-8" class)}
   [label text :title-class title-class :value selected-value :on-clear-value on-clear-value :required? required?]
   [:select {:title title
             :value selected-value
             :class "w-full p-2 outline-none border-b border-transparent focus:border-red-800 text-black" :on-change #(on-change (.. % -target -value))}
    [:option {:value "" :disabled true :selected true} "Select an option"]
    (for [{:keys [value option disabled?]} options]
      ^{:key value} [:option {:value value :disabled disabled? :selected (= selected-value value)} option])]])


(defn button [display & {:keys [class disabled? on-click]
                         :or {class "" disabled? false}}]
  [:button {:class (str "p-2 rounded-lg " class)
            :on-click on-click
            :disabled disabled?
            :type "button"}
   display])