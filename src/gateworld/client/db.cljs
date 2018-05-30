(ns gateworld.client.db
  (:require
   [re-frame.db]
   [gateworld.rules.core :as rules])
  (:require-macros
   [devcards.core :refer [defcard]]))


(defcard app-db
  @re-frame.db/app-db)


(def initial-db
  {:view :intro})
(defcard initial-db initial-db)
