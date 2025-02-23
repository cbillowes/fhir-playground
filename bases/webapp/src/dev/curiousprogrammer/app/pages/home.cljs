(ns dev.curiousprogrammer.app.pages.home)


(defn page []
  [:div {:class "my-8 max-w-4xl w-2/3 mx-auto text-center text-white leading-relaxed"}
   [:h1.font-bold.my-2.text-4xl "Welcome to the FHIR playground"]
   [:p.mb-2 "FHIR is a standard for exchanging healthcare information electronically. It is a set of rules that allow healthcare systems to work together and share information. It stands for Fast Healthcare Interoperability Resources."]
   [:p "This application uses the " [:a.underline.text-yellow-400.hover:text-gray-400 {:href "https://hapi.fhir.org/baseR4" :target "_blank"} "HAPI API"] " to fetch data."]])