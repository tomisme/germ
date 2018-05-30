(ns gateworld.rules.effects
  (:require
   [gateworld.utils :refer [vec-without]])
  (:require-macros
   [devcards.core :refer [defcard]]))


(defn add-char
  [state char]
  (update-in state [:chars] conj char))


(defn give-char-permanent
  [state char-index permanent]
  (update-in state [:chars char-index :permanents] conj permanent))


(defn give-char-in-hand
  [state char-index thing]
  (update-in state [:chars char-index :in-hand] conj thing))


;;


(def effect-types
  #{:sacrifice})
(defcard effect-types effect-types)


(defn add-effect
  [state effect]
  (update-in state [:fx] conj effect))


(defn sacrifice-permanent
  [state {:keys [char permanent-index]}]
  (update-in state
             [:chars char :permanents]
             vec-without
             permanent-index))


(defn resolve-next-effect
  [state]
  (let [effect (-> state :fx peek)
        popped-state (update-in state [:fx] pop)]
    (condp = (:type effect)
           :sacrifice (sacrifice-permanent popped-state effect))))
