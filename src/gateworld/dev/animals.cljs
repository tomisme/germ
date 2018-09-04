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
(defn d [x] (js/console.log x) x)

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

(def card-defs
  {:peck
   {:name "Peck"}

   :fly
   {:name "Fly"}

   :speak
   {:name "Speak"}

   :crow
   {:name "Crow"
    :fx (list [:discard-all]
              [:gain-random-cards {:cards [:peck :fly :speak]
                                   :n 2}])}
   :goat
   {:name "Goat"}

   :rat
   {:name "Rat"}})


;;


(defn select-outcome
  ([state possibilities]
   (select-outcome state possibilities 1))
  ([state possibilities n]
   (loop [i n
          result '()]
     (if (= i 0)
       result
       (let [input (-> state :story (assoc :nonce i) pr-str)
             outcome (pseudo-random-nth (hash256 input) possibilities)]
         (recur (dec i) (conj result outcome)))))))

;;


(defn gain-random-cards
  [state [_ {:keys [cards n]}]]
  (let [to-add (map (fn [k]
                      {:k k})
                    (select-outcome state cards n))]
    (update state :cards #(into [] (concat % to-add)))))


(defn discard-all
  [state _]
  (assoc state :cards []))


(defn resolve-effect
  [state [k :as effect]]
  (condp = k
         :discard-all (discard-all state effect)
         :gain-random-cards (gain-random-cards state effect)))


(defn resolve-card-fx
  [state idx]
  (let [k (get-in state [:cards idx :k])
        fx (get-in card-defs [k :fx])]
    (reduce resolve-effect state fx)))


(defn pick-card
  [state idx]
  (-> state
      (update-in [:story :card-picks] conj idx)
      (resolve-card-fx idx)))


;;


(defonce state-atom
  (reagent.core/atom
   {:cards [{:k :crow} {:k :goat} {:k :rat}]
    :story {:seed (hash256 "abc")
            :card-picks '()}}))


(defn animals-component
  []
  [:div
   (into [:div {:style {:display "flex"
                        :flex-wrap "wrap"}}]
         (map-indexed
          (fn [idx card]
            (let [{:keys [name]} (get card-defs (:k card))]
              [:div {:style {:background "green"
                             :margin 10
                             :padding 10}
                     :on-click #(swap! state-atom pick-card idx)}
               name]))
          (:cards @state-atom)))])


(defcard-rg animals
  animals-component
  state-atom
  {:inspect-data true
   :history true})
