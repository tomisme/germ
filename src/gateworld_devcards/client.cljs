(ns gateworld-devcards.client
  (:require
   [devcards.core]
   [gateworld.client.core]
   [re-frame.core :as rf]
   [re-frame.db])
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


(defonce start
  (rf/dispatch-sync [:initialise]))

#_(rf/dispatch-sync [:initialise])


(defcard-rg ui-el
  [gateworld.client.core/ui-el])


(defcard app-db
  @re-frame.db/app-db)
