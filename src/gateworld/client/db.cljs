(ns gateworld.client.db
  (:require
   [re-frame.db]
   [gateworld.rules.core :as rules])
  (:require-macros
   [devcards.core :refer [defcard]]))


(def initial-db
  {:view :intro})


;;


(defcard app-db
  @re-frame.db/app-db)
