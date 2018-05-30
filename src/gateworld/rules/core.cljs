(ns gateworld.rules.core
  (:require
   [gateworld.rules.effects :as effects])
  (:require-macros
   [devcards.core :refer [defcard]]))


(def combat-move-types
  #{:activate-ability
    :pass-priority})
(defcard combat-move-types combat-move-types)


;;


(def empty-char
  {:permanents []
   :inventory []
   :in-hand []})


(def empty-combat-state
  {:chars []
   :active-char 0
   :fx []})


;;


(defcard sacrifice-permanent
  (let [char (assoc-in empty-char
                       [:permanents]
                       [{:name "SacRock"} {:name "NoSacRock"}])
        s1 (effects/add-char empty-combat-state char)
        s2 (effects/add-effect s1 {:type :sacrifice
                                   :char 0
                                   :permanent-index 0})]
    (effects/resolve-next-effect s2)))
