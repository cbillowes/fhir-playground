(ns dev.curiousprogrammer.app.ui)


(defn layout [component]
  [:div {:class "my-8 w-full mx-auto text-center"}
   [:div {:class "bg-red-400 p-8 rounded-lg shadow-lg"}
    component]])


(defn textbox [title & {:keys [title-class]
                        :or {title-class ""}}]
  [:div {:class "w-full bg-gray-200 p-2 rounded-lg"}
   [:p {:class (str "mb-2 text-xs text-left " title-class)} title]
   [:input {:type "text" :class "w-full p-2 outline-none focus:bg-gray-100 focus:rounded-lg"}]])


(defn selectbox [title options & {:keys [title-class]
                                  :or {title-class ""}}]
  [:div {:class "w-full bg-gray-200 p-2 rounded-lg"}
   [:p {:class (str "mb-2 text-xs text-left " title-class)} title]
   [:select {:class "w-full p-2 outline-none focus:bg-gray-100 focus:rounded-lg"}
    (for [{:keys [value option disabled?]} options]
      [:option {:value value :disabled disabled?} option])]])


(defn button [display & {:keys [class]
                         :or {class ""}}]
  [:button {:class (str "p-2 rounded-lg w-full " class)
            :type "button"}
   display])