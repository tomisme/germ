(ns gateworld.rules.core
  (:require
   [gateworld.utils :refer [vec-without]])
  (:require-macros
   [devcards.core :refer [defcard]]))


(def conflict-move-types
  #{:activate-ability
    :pass-priority})


(def empty-char
  {:permanents []
   :inventory []
   :in-hand []})


(def empty-conflict-state
  {:chars [empty-char]
   :active-char 0
   :fx []})


;;


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


(def effect-attributes
  #{:targetable})


(defn add-effect
  [state effect]
  (update-in state [:fx] conj effect))


(defn sacrifice-permanent
  [state {:keys [char-idx perm-idx]}]
  (update-in state
             [:chars char-idx :permanents]
             vec-without
             perm-idx))


(defn resolve-next-effect
  [state]
  (let [effect (-> state :fx peek)
        popped-state (update-in state [:fx] pop)]
    (condp = (:type effect)
           :sac-perm (sacrifice-permanent popped-state effect))))


;; dev


(defcard empty-char empty-char)
(defcard empty-conflict-state empty-conflict-state)

(defcard conflict-move-types conflict-move-types)
(defcard effect-types effect-types)
(defcard effect-attributes effect-attributes)
