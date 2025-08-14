(ns cloud-first-step
  (:require [datomic.client.api :as d]))
;=> nil
(def cfg {:server-type :cloud                               ; aqui a gente define serve type, podendo ser :cloud, :ion, etc. / Here we define the type of service, which can be :cloud, :ion, etc.
          :region "us-east-1"                               ;
          :system "gabi-gol-storage"                        ; aqui precisamos colocar o mesmo nome que colocamos no sistema da AWS / Here we need to put the same name that we put in the AWS system
          :endpoint "https://xxxxxxxxx.execute-api.us-east-1.amazonaws.com"}) ; aqui colocamos o endpoint que pegamos da máquina na AWS / Here we put the endpoint that we got from the machine in AWS

;=> #'cloud-first-step/cfg
;
(def client (d/client cfg))
;=> #'cloud-first-step/client
(d/create-database client {:db-name "gabi-gol-storage"})
;=> true
(d/list-databases client {})
;=> ("gabi-gol-storage")
(def conn (d/connect client {:db-name "gabi-gol-storage"}))
;=> #'cloud-first-step/conn


(def schema-tx-data
  [{:db/ident       :user/name                              ; identifica o atributo / identifies the attribute
    :db/valueType   :db.type/string                         ; tipo do valor / value type
    :db/cardinality :db.cardinality/one                     ; qual a cardinalidade / what is its cardinality
    :db/doc         "name"}                                 ; a descrição dele / his description
   {:db/ident       :user/year
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc         "year of birth"}
   {:db/ident       :user/sport
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "favorite sport"
    }
   {:db/ident       :user/band
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "favorite band"
    }
   ])
;=> #'cloud-first-step/schema-tx-data

(d/transact conn {:tx-data schema-tx-data})
;=>
;{:db-before {:database-id "7d6064ac-4e06-41fd-a20d-0aeb9091a91c",
;             :db-name "gabi-gol-storage",
;             :t 6,
;             :next-t 7,
;             :type :datomic.client/db},
; :db-after {:database-id "7d6064ac-4e06-41fd-a20d-0aeb9091a91c",
;            :db-name "gabi-gol-storage",
;            :t 7,
;            :next-t 8,
;            :type :datomic.client/db},
; :tx-data [#datom[13194139533319 50 #inst"2025-08-14T21:17:38.672-00:00" 13194139533319 true]],
; :tempids {},
; :tx-id #uuid"4cb90f30-127d-4b9e-b7cc-d56911e78c8e",
; :db-id "7d6064ac-4e06-41fd-a20d-0aeb9091a91c"}
;

(def gabi
  {:user/name "gabi"
   :user/year 2000
   :user/sport "sleep"
   :user/band "slipknot"})
;=> #'cloud-first-step/gabi

(def draco
  {:user/name "draco"
   :user/year 2024
   :user/sport "Race"
   :user/band "Metallica"})
;=> #'cloud-first-step/draco

(d/transact conn {:tx-data [gabi draco]})
;=>
;{:db-before {:database-id "7d6064ac-4e06-41fd-a20d-0aeb9091a91c",
;             :db-name "gabi-gol-storage",
;             :t 7,
;             :next-t 8,
;             :type :datomic.client/db},
; :db-after {:database-id "7d6064ac-4e06-41fd-a20d-0aeb9091a91c",
;            :db-name "gabi-gol-storage",
;            :t 8,
;            :next-t 9,
;            :type :datomic.client/db},
; :tx-data [#datom[13194139533320 50 #inst"2025-08-14T21:25:33.357-00:00" 13194139533320 true]
;           #datom[87960930222157 73 "gabi" 13194139533320 true]
;           #datom[87960930222157 74 2000 13194139533320 true]
;           #datom[87960930222157 75 "sleep" 13194139533320 true]
;           #datom[87960930222157 76 "slipknot" 13194139533320 true]
;           #datom[87960930222158 73 "draco" 13194139533320 true]
;           #datom[87960930222158 74 2024 13194139533320 true]
;           #datom[87960930222158 75 "Race" 13194139533320 true]
;           #datom[87960930222158 76 "Metallica" 13194139533320 true]],
; :tempids {},
; :tx-id #uuid"ea7350ec-0d8c-420e-8a89-e69f6c33c538",
; :db-id "7d6064ac-4e06-41fd-a20d-0aeb9091a91c"}

(d/q '[:find ?name
       :where
       [?e :user/name ?name]
       (not [?e :user/name "gabi"])]
     (d/db conn))
; => [["draco"]]


