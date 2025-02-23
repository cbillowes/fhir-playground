(ns dev.curiousprogrammer.fhir.query)

(defn build-patient-query-for-search
  "Builds a query for a patient search endpoint.

* language: Language of the resource content
* last-updated: The date when the resource content was last change
* language-code: Language code (irrespective of use value)
* link: All patients linked to the given patient
* identifier: A patient identifier
* active?: Whether the patient record is activ
* partial-name: A server defined search that may match any of the string fields in the HumanName, including family, give, prefix, suffix, suffix, and/or text
* given-name: A portion of the given name of the patient
* family-name: A portion of the family name of the patient
* phonetic-name: A portion of either family or given name using some kind of phonetic matching algorithm
* date-of-birth: The patient's date of birth
* deceased?: This patient has been marked as deceased, or as a death date entered
* partial-address: A server defined search that may match any of the string fields in the Address, including line, city, district, state, country, postalCode, and/or text
* city: A city specified in an address
* state: A state specified in an address
* country: A country specified in an address
* postal-code: A postal code specified in an address
* gender: Gender of the patient
* death-date: The date of death has been provided and satisfies this search value
* telecom: The value in any kind of telecom details of the patient
* email: A value in an email contact
* phone: A value in a phone contact
* organization: The organization that is the custodian of the patient record
* general-practitioner: Patient's nominated general practitioner, not the organization that manages the record"
  [data]
  (let [kvs (->> data
                 (filter (comp some? val))
                 (map (fn [[k v]] [(keyword k) v]))
                 (into {}))
        {:keys [language last-updated
                language-code link identifier active?
                partial-name given-name family-name phonetic-name date-of-birth gender
                death-date deceased?
                partial-address city state country postal-code
                telecom email phone
                general-practitioner organization]} kvs]
    (cond-> {}
      language (assoc "_language" language)
      last-updated (assoc "_lastUpdated" last-updated)
      language-code (assoc "language" language-code)
      link (assoc "link" link)
      identifier (assoc "identifier" identifier)
      (true? active?) (assoc "active" (str active?))
      partial-name (assoc "name" partial-name)
      given-name (assoc "given" given-name)
      family-name (assoc "family" family-name)
      phonetic-name (assoc "phonetic" phonetic-name)
      date-of-birth (assoc "birthdate" date-of-birth)
      (true? deceased?) (assoc "deceased" (str deceased?))
      partial-address (assoc "address" partial-address)
      city (assoc "address-city" city)
      state (assoc "address-state" state)
      country (assoc "address-country" country)
      postal-code (assoc "address-postalcode" postal-code)
      gender (assoc "gender" gender)
      death-date (assoc "death-date" death-date)
      telecom (assoc "telecom" telecom)
      email (assoc "email" email)
      phone (assoc "phone" phone)
      general-practitioner (assoc "general-practitioner" general-practitioner)
      organization (assoc "organization" organization))))


(comment

  (build-patient-query-for-search {"language" "zulu"})

  )