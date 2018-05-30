(ns gateworld.rules.core
  (:require
   [gateworld.rules.effects :as effects])
  (:require-macros
   [devcards.core :refer [defcard]]))


(def combat-move-types
  #{:activate-ability
    :pass-priority})
(defcard combat-move-types combat-move-types)


(def empty-char
  {:permanents []
   :inventory []
   :in-hand []})
(defcard empty-char empty-char)


(def empty-combat-state
  {:chars []
   :active-char 0
   :fx []})
(defcard empty-combat-state empty-combat-state)
