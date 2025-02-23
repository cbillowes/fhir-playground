(ns dev.curiousprogrammer.app.ui)


(defn layout [component]
  [:div {:class "my-8 w-full mx-auto text-center"}
   [:div {:class "bg-red-400 p-8 rounded-lg shadow-lg"}
    component]])



(defn label
  [text & {:keys [title-class value on-clear-value]}]
  [:p {:class (str "relative mx-2 text-xs text-left flex space-between " title-class)} text
   (when (and value on-clear-value) [:button.cursor-pointer {:on-click on-clear-value} "✖️"])])


(defn textbox [text & {:keys [title title-class class on-change default-value on-clear-value]
                       :or {title-class ""
                            class ""
                            on-change (fn [_])}}]
  [:div {:class (str "w-full bg-gray-200 p-2 rounded-lg border-8" class)}
   [label text :title-class title-class :value default-value :on-clear-value on-clear-value]
   [:input {:title title
            :type "text"
            :class "w-full p-2 outline-none border-b border-transparent focus:border-red-800 text-black"
            :value default-value
            :on-change #(on-change (.. % -target -value))}]])


(defn selectbox [text options & {:keys [title title-class class on-change selected-value on-clear-value]
                                 :or {title-class ""
                                      class ""
                                      on-change (fn [_])}}]
  [:div {:class (str "w-full bg-gray-200 p-2 rounded-lg border-8" class)}
   [label text :title-class title-class :value selected-value :on-clear-value on-clear-value]
   [:select {:title title
             :value selected-value
             :class "w-full p-2 outline-none border-b border-transparent focus:border-red-800 text-black" :on-change #(on-change (.. % -target -value))}
    [:option {:value "" :disabled true :selected true} "Select an option"]
    (for [{:keys [value option disabled?]} options]
      ^{:key value} [:option {:value value :disabled disabled? :selected (= selected-value value)} option])]])


(defn button [display & {:keys [class on-click]
                         :or {class ""}}]
  [:button {:class (str "p-2 rounded-lg " class)
            :on-click on-click
            :type "button"}
   display])