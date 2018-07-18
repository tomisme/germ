(ns gateworld.client.events.combat
  (:require
   [gateworld.rules.core :as rules]))


(def rock-card {:name "Rock"})
(def blob-card {:name "Blob"
                :attack 1
                :toughness 1})
(def rock-perm {:name "Rock"})
(def blob-perm {:name "Blob"
                :attack 1
                :toughness 1})


(defn encounter-char
  [state char]
  (-> state
      (update :story conj (str "Encountered "
                               (:name char)
                               "!"))
      (update :story conj " -- Appears to have a Rock in play and nothing in hand")
      (update :combat-state rules/add-char char)
      (update :combat-state rules/give-char-permanent 1 rock-perm)))


(defn start-combat-practise
  [state]
  (-> state
      (assoc :story ["Rolling new practise combat..."])
      (update :story conj " -- You have a Rock and a Blob on the field")
      (update :story conj " -- You have a Rock and a Blob in hand")
      (assoc :combat-state rules/empty-combat-state)
      (update :combat-state rules/give-char-permanent 0 rock-perm)
      (update :combat-state rules/give-char-permanent 0 blob-perm)
      (update :combat-state rules/give-char-in-hand 0 rock-card)
      (update :combat-state rules/give-char-in-hand 0 blob-card)
      (encounter-char (assoc rules/empty-char :name "Jeff"))
      (assoc :actions ["Play Rock"
                       "Play Blob"
                       "Attack with Blob"
                       "Pass"])))
