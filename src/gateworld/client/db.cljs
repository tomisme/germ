(ns gateworld.client.db
  (:require
   [re-frame.db])
  (:require-macros
   [devcards.core :refer [defcard]]))


(def initial-db
  {:view :intro})


;;


(defcard app-db
  @re-frame.db/app-db)


(defcard initial-db initial-db)
