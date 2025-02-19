(ns dev.curiousprogrammer.ui)


(defn textbox [title & {:keys [title-class]
                        :or {title-class ""}}]
  [:div.w-full
   [:p {:class (str "mb-2 text-sm text-left " title-class)} title]
   [:input {:class "bg-gray-200 p-2 rounded-lg w-full"
           :type "text"}]])


(defn selectbox [title options & {:keys [title-class]
                                  :or {title-class ""}}]
  [:div.w-full
   [:p {:class (str "mb-2 text-sm text-left " title-class)} title]
   [:select {:class "bg-gray-200 p-2 rounded-lg w-full"}
    (for [{:keys [value option disabled?]} options]
      [:option {:value value :disabled disabled?} option])]])


(defn button [display & {:keys [class]
                         :or {class ""}}]
  [:button {:class (str "p-2 rounded-lg w-full " class)
            :type "button"}
   display])