(ns gateworld.dev.animals
  (:require
   [reagent.core :as rg]
   [goog.string :as goog-string]
   [goog.crypt :as goog-crypt]
   [goog.crypt.Sha256 :as Sha256])
  (:import
   [goog.testing PseudoRandom])
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


(defn string->bytes
  [s]
  (goog-crypt/stringToUtf8ByteArray s))


(defn bytes->hex
  [bytes]
  (goog-crypt/byteArrayToHex bytes))


(defn digest
  [hasher bytes]
  (.update hasher bytes)
  (.digest hasher))


(defn hash256
  [s]
  (let [bytes (digest (goog.crypt.Sha256.) (string->bytes s))]
    (str "0x" (bytes->hex bytes))))


(defn pseudo-random-int
  [seed max]
  (let [rng (PseudoRandom. (goog-string/parseInt seed))]
    (int (* max (.random rng)))))


(defn pseudo-random-nth
  [seed coll]
  (let [idx (pseudo-random-int seed (count coll))]
    (nth coll idx)))


;;


(defcard hash-test
  [
   (hash256 "1")
   (hash256 "2")
   (hash256 "3")])


(defcard pseudo-random-nth-test
  (let [x [:a :b :c :d]]
    [
     (pseudo-random-nth (hash256 "a") x)
     (pseudo-random-nth (hash256 "b") x)
     (pseudo-random-nth (hash256 "c") x)
     (pseudo-random-nth (hash256 "d") x)
     (pseudo-random-nth (hash256 "e") x)
     (pseudo-random-nth (hash256 "f") x)
     (pseudo-random-nth (hash256 "g") x)
     (pseudo-random-nth (hash256 "h") x)]))


;;


(def peck-card
  {:name "Peck"
   :fx {}})
(def fly-card
  {:name "Fly"
   :fx {}})
(def speak-card
  {:name "Speak"
   :fx {}})
(def crow-card
  {:name "Crow"
   :fx {:gain-random-cards {:cards [peck-card fly-card speak-card]
                            :num 2}}})
(def goat-card
  {:name "Goat"
   :fx {}})
(def rat-card
  {:name "Rat"
   :fx {}})


;;


(defn select-outcome
  [state possible-outcomes]
  (let [story-str (pr-str (:story state))]
    (pseudo-random-nth (hash256 story-str) possible-outcomes)))


(defn gain-random-cards
  [state {:keys [gain-random-cards]}]
  (let [{:keys [cards num]} gain-random-cards
        ;; TODO pick num * cards
        card (select-outcome state cards)]
    (update state :cards conj card)))


(defn resolve-card-fx
  [state idx]
  (let [fx (-> state :cards (nth idx) :fx)]
    (cond-> state
            (:gain-random-cards fx) (gain-random-cards fx))))


(defn pick-card
  [state idx]
  (-> state
      (update-in [:story :card-picks] conj idx)
      (resolve-card-fx idx)))


;;


(def state-atom
  (reagent.core/atom
   {:cards [crow-card goat-card rat-card]
    :story {:seed (hash256 "abc")
            :card-picks '()}}))


(defn animals-component
  []
  [:div
   (into [:div {:style {:display "flex"}}]
         (map-indexed
          (fn [idx card]
            [:div {:style {:background "green"
                           :margin 10
                           :padding 10}
                   :on-click #(swap! state-atom pick-card idx)}
             (:name card)])
          (:cards @state-atom)))])


(defcard-rg animals
  animals-component
  state-atom
  {:inspect-data true
   :history true})
