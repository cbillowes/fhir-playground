(ns dev.curiousprogrammer.ui)


(defn layout [heading component]
  [:div {:class "my-8 w-2/3 mx-auto text-center"}
   [:div.bg-red-400.p-8.rounded-lg.shadow-lg
    [:h1.text-5xl.font-bold.text-red-900 heading]
    component
    [:p {:class "mt-4 text-sm text-red-900"} "Powered by ClojureScript"]]
   [:p {:class "mt-4 text-sm text-gray-400"} "Copyright © 2025 Curious Programmer"]])


(defn textbox [title & {:keys [title-class]
                        :or {title-class ""}}]
  [:div.w-full.bg-gray-200.p-2.rounded-lg
   [:p {:class (str "mb-2 text-xs text-left " title-class)} title]
   [:input {:type "text" :class "w-full p-2 outline-gray-100"}]])


(defn selectbox [title options & {:keys [title-class]
                                  :or {title-class ""}}]
  [:div.w-full.bg-gray-200.p-2.rounded-lg
   [:p {:class (str "mb-2 text-xs text-left " title-class)} title]
   [:select {:class "bg-gray-200 p-2 rounded-lg w-full outline-gray-100"}
    (for [{:keys [value option disabled?]} options]
      [:option {:value value :disabled disabled?} option])]])


(defn button [display & {:keys [class]
                         :or {class ""}}]
  [:button {:class (str "p-2 rounded-lg w-full " class)
            :type "button"}
   display])