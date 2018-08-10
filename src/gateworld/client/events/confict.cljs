(ns gateworld.client.events.conflict
  (:require
   [gateworld.rules.core :as rules])
  (:require-macros
   [devcards.core :refer [defcard]]))


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
      (update :conflict-state rules/add-char char)
      (update :conflict-state rules/give-char-permanent 1 rock-perm)))


(defn start-conflict-practise
  [state]
  (-> state
      (assoc :story ["Rolling new practise conflict..."])
      (update :story conj " -- You have a Rock and a Blob on the field")
      (update :story conj " -- You have a Rock and a Blob in hand")
      (assoc :conflict-state rules/empty-conflict-state)
      (update :conflict-state rules/give-char-permanent 0 rock-perm)
      (update :conflict-state rules/give-char-permanent 0 blob-perm)
      (update :conflict-state rules/give-char-in-hand 0 rock-card)
      (update :conflict-state rules/give-char-in-hand 0 blob-card)
      (encounter-char (assoc rules/empty-char :name "Jeff"))
      (assoc :actions ["Play Rock"
                       "Play Blob"
                       "Attack with Blob"
                       "Pass"])))


;;


(defcard a)
